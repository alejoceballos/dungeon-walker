package momomomo.dungeonwalker.history.startup.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.history.HistoryLog;
import momomomo.dungeonwalker.history.startup.DungeonWalkerHistoryIntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.join;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.split;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.trim;
import static org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.upperCase;

@Slf4j
public class DungeonWalkerHistoryStepsDef extends DungeonWalkerHistoryIntegrationTests {

    private static final String LABEL = "--> [INTEGRATION TEST]";

    private static final String FROM_ENGINE_PATH = "data/inbound/engine/%s.json";
    private static final String EXCHANGE_BINDING_NAME = "consumeHistoryLog-out-0";

    @Autowired
    private ObjectMapper testJsonMapper;

    @Autowired
    private StreamBridge testRabbitMqProducer;

    @Autowired
    private JdbcTemplate testJdbcTemplate;

    private final Map<String, Object> contextVariables = new HashMap<>();
    private final List<String> contextMessages = new ArrayList<>();

    @Before
    public void scenarioSetUp() {
        contextVariables.clear();
        contextMessages.clear();
    }

    @Given("the engine sends the following history log(s):")
    public void prepareHistoryLogsToSend(final List<String> messageTitles) {
        contextMessages.clear();
        contextMessages
                .addAll(messageTitles
                        .stream()
                        .map(this::messageTitleToFilePath)
                        .map(this::readResourceAsString)
                        .peek(json -> log.info("{} Prepared history log JSON: {}", LABEL, json))
                        .toList());
    }

    @When("the history service receives the history logs")
    public void sendHistoryLogs() {
        final var results = contextMessages
                .stream()
                .map(this::toHistoryLogObject)
                .map(historyLog -> {
                    log.info("{} Sending {} to {}", LABEL, historyLog.getClass().getSimpleName(), EXCHANGE_BINDING_NAME);

                    try {
                        return testRabbitMqProducer.send(EXCHANGE_BINDING_NAME, historyLog);

                    } catch (final Exception e) {
                        log.error("{} Failed to send history log: {}", LABEL, historyLog, e);
                        throw new RuntimeException("Failed to send history log: " + historyLog, e);
                    }
                })
                .toList();

        log.info("{} {} messages were successfully sent", LABEL, results.stream().filter(Boolean::booleanValue).count());
    }

    @Then("the following record(s) (is)(are) saved in the database table {string}:")
    public void checkDatabaseRecords(final String tableName, final DataTable expectedValues) {
        final var expectedValuesMapList = expectedValues.asMaps();
        log.info("{} Checking database records for table {}: {}", LABEL, tableName, expectedValuesMapList);

        final var expectedDbColumnNamesAndTypes = expectedValuesMapList
                .getFirst()
                .keySet()
                .stream()
                .collect(Collectors.toMap(
                        recordKey -> recordKey,
                        recordKey -> {
                            final var parts = split(recordKey, "->");
                            return new Metadata(trim(parts[0]), trim(parts[1]));
                        }));

        final var dbColumnNames = expectedDbColumnNamesAndTypes
                .values()
                .stream()
                .map(Metadata::dbColumnName)
                .toList();

        final var sql = "SELECT %s FROM %s".formatted(join(dbColumnNames, ","), tableName);

        testJdbcTemplate.query(sql, rs -> {
            final var allRowsAssertions = new ArrayList<List<RowAssertions>>();

            for (final var expectedValuesMap : expectedValuesMapList) {
                final var rowAssertions = new ArrayList<RowAssertions>();

                for (final var expectedValuesEntry : expectedValuesMap.entrySet()) {
                    final var expectedValueType = expectedDbColumnNamesAndTypes.get(expectedValuesEntry.getKey()).expectedType();
                    final var expectedDbColumnName = expectedDbColumnNamesAndTypes.get(expectedValuesEntry.getKey()).dbColumnName();
                    final var actualValue = getRsValue(rs, expectedDbColumnName);

                    final var expectedValue = isExpectedValueFromContext(expectedValuesEntry)
                            ? handleExpectedValueFromContext(expectedValuesEntry, actualValue)
                            : convert(expectedValuesEntry.getValue(), expectedValueType);

                    rowAssertions.add(new RowAssertions(expectedDbColumnName, expectedValueType, actualValue, expectedValue));
                }

                allRowsAssertions.add(rowAssertions);

                if (!rs.next()) {
                    break;
                }
            }

            assertThat(expectedValuesMapList).hasSize(allRowsAssertions.size());

            for (final var rowAssertions : allRowsAssertions) {
                for (final var rowAssertion : rowAssertions) {
                    assertThat(rowAssertion.actual)
                            .describedAs("""
                                    Value "%s" of type "%s" from column "%s" does not match expected value "%s"
                                    """.formatted(
                                    rowAssertion.actual,
                                    rowAssertion.type,
                                    rowAssertion.columnName,
                                    rowAssertion.expected))
                            .isEqualTo(rowAssertion.expected);
                }
            }
        });
    }

    @Then("after {int} {string}")
    public void afterSeconds(final int value, final String unitStr) {
        final var timeUnit = Optional
                .of(upperCase(unitStr))
                .map(upperUnit -> upperUnit.endsWith("S") ? upperUnit : upperUnit + "S")
                .map(TimeUnit::valueOf)
                .get();

        waitFor(value, timeUnit);
    }

    private Object handleExpectedValueFromContext(
            final Map.Entry<String, String> recordEntry,
            final Object rsValue
    ) {
        final var recordAction = recordEntry
                .getValue()
                .replace("{{", "")
                .replace("}}", "")
                .trim();
        final var actionParts = recordAction.split("->");
        final var action = actionParts[0];
        final var variableName = actionParts[1];

        if (equalsIgnoreCase(action, "PUT")) {
            contextVariables.put(variableName, rsValue);
            return rsValue;

        } else if (equalsIgnoreCase(action, "GET")) {
            return contextVariables.get(variableName);
        }

        return null;
    }

    private String messageTitleToFilePath(final String messageTitle) {
        return FROM_ENGINE_PATH.formatted(messageTitle)
                .replace(":", "")
                .replace(" ", "-");
    }

    private HistoryLog toHistoryLogObject(final String json) {
        log.info("{} Creating HistoryLog object from: {}", LABEL, json);
        try {
            return testJsonMapper.readValue(json, HistoryLog.class);

        } catch (final Exception e) {
            throw new RuntimeException("Failed to parse JSON message: " + json, e);
        }
    }

    private static boolean isExpectedValueFromContext(final Map.Entry<String, String> expectedValuesEntry) {
        return expectedValuesEntry.getValue().contains("{{")
                && expectedValuesEntry.getValue().contains("}}")
                && expectedValuesEntry.getValue().contains("->");
    }

    private Object getRsValue(final ResultSet rs, final String columnName) {
        try {
            return rs.getObject(columnName);

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object convert(final String strValue, final String expectedType) {
        return switch (expectedType) {
            case "Timestamp" -> convertToTimestamp(strValue);
            case "String" -> convertToString(strValue);
            case "Integer" -> convertToInteger(strValue);
            case "Long" -> convertToLong(strValue);
            case null, default -> null;
        };
    }

    private Long convertToLong(final String strValue) {
        return isExpectedNull(strValue) ? null : Long.parseLong(strValue);
    }

    private Integer convertToInteger(final String strValue) {
        return isExpectedNull(strValue) ? null : Integer.parseInt(strValue);
    }

    private String convertToString(final String strValue) {
        return isExpectedNull(strValue) ? null : strValue;
    }

    private Timestamp convertToTimestamp(final String strValue) {
        if (isExpectedNull(strValue)) {
            return null;
        }

        final var format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final var converted = LocalDateTime.parse(strValue, format).toInstant(UTC);

        return Timestamp.from(converted);
    }

    private static boolean isExpectedNull(final String strValue) {
        return "{{NULL}}".equalsIgnoreCase(strValue);
    }

    private record Metadata(
            String dbColumnName,
            String expectedType
    ) {
    }

    private record RowAssertions(
            String columnName,
            String type,
            Object actual,
            Object expected
    ) {
    }

}
