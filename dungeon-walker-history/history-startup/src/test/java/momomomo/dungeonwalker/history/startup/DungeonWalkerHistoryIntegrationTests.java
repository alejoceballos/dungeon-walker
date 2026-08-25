package momomomo.dungeonwalker.history.startup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.history.startup.steps.DungeonWalkerHistoryStepsDef;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SuppressWarnings({"resource", "java:S2187"})
@Slf4j
@DirtiesContext
@Testcontainers
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DungeonWalkerHistoryIntegrationTests {

    @Autowired
    protected ObjectMapper jsonMapper;

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:18-alpine"))
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432)
            .withInitScript("init-db.sql");

    static {
        POSTGRES.setPortBindings(List.of("5432:5432"));
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    public static final RabbitMQContainer RABBIT_MQ =
            new RabbitMQContainer("rabbitmq:4-management")
                    .withExposedPorts(5672);

    static {
        RABBIT_MQ.setPortBindings(List.of("5672:5672"));
        RABBIT_MQ.start();
    }

    @AfterAll
    static void stopContainers() {
        RABBIT_MQ.stop();
        RABBIT_MQ.close();

        POSTGRES.stop();
        POSTGRES.close();
    }

    protected <T> T readResourceAsInputStream(
            final String resourcePath,
            final Function<InputStreamReader, T> converter) {
        try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assert resource != null;

            try (final var content = new InputStreamReader(resource)) {
                return converter.apply(content);

            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected JsonNode readResourceAsJson(final String resourcePath) {
        return readResourceAsInputStream(
                resourcePath,
                inputStream -> {
                    try {
                        return jsonMapper.readTree(inputStream);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected String readResourceAsString(final String resourcePath) {
        return readResourceAsInputStream(
                resourcePath,
                inputStream -> {
                    try {
                        return inputStream.readAllAsString();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected void waitFor(final long amount, final TimeUnit timeUnit) {
        try (final var executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.schedule(DungeonWalkerHistoryStepsDef.DoNothing::new, amount, timeUnit);
        }
    }

    protected static class DoNothing implements Runnable {
        @Override
        public void run() {
            // Do nothing
        }
    }

}
