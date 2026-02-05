package momomomo.dungeonwalker.commons.conditional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionalTest {

    private static final String SUCCESS = "success";
    private static final String ALSO_SUCCESS = "also success";
    private static final String FAILURE = "failure";
    private static final String ALSO_FAILURE = "also failure";

    private String executeResult;
    private String alsoExecuteResult;

    @BeforeEach
    void setUp() {
        executeResult = null;
        alsoExecuteResult = null;
    }

    public static Stream<Arguments> simpleThenValuesProvider() {
        return Stream.of(
                Arguments.of(true, SUCCESS),
                Arguments.of(false, null));
    }

    @ParameterizedTest
    @MethodSource("simpleThenValuesProvider")
    void testThenExecute(
            final boolean ifCondition,
            final String expectedResult
    ) {
        Conditional.on(() -> ifCondition).thenExecute(() -> setExecuteResult(SUCCESS)).evaluate();
        assertThat(executeResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("simpleThenValuesProvider")
    void testThenGet(
            final boolean ifCondition,
            final String expectedResult
    ) {
        final var actual = Conditional.on(() -> ifCondition).thenGet(() -> SUCCESS).evaluate();
        assertThat(actual).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> andConditionalValuesProvider() {
        return Stream.of(
                Arguments.of(true, true, SUCCESS, ALSO_SUCCESS),
                Arguments.of(true, false, FAILURE, ALSO_FAILURE),
                Arguments.of(false, true, FAILURE, ALSO_FAILURE),
                Arguments.of(false, false, FAILURE, ALSO_FAILURE));
    }

    @ParameterizedTest
    @MethodSource("andConditionalValuesProvider")
    void testAndConditionalExecute(
            final boolean ifCondition,
            final boolean andCondition,
            final String expectedResult,
            final String alsoExpectedResult
    ) {
        Conditional
                .on(() -> ifCondition)
                .and(() -> andCondition)
                .thenExecute(() -> setExecuteResult(SUCCESS))
                .alsoExecute(() -> setAlsoExecuteResult(ALSO_SUCCESS))
                .orElseExecute(() -> setExecuteResult(FAILURE))
                .alsoExecute(() -> setAlsoExecuteResult(ALSO_FAILURE))
                .evaluate();

        assertThat(executeResult).isEqualTo(expectedResult);
        assertThat(alsoExecuteResult).isEqualTo(alsoExpectedResult);
    }

    @ParameterizedTest
    @MethodSource("andConditionalValuesProvider")
    void testAndConditionalGet(
            final boolean ifCondition,
            final boolean andCondition,
            final String expectedResult
    ) {
        final var actual = Conditional
                .on(() -> ifCondition)
                .and(() -> andCondition)
                .thenGet(() -> SUCCESS)
                .orElseGet(() -> FAILURE)
                .evaluate();

        assertThat(actual).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> orConditionalValuesProvider() {
        return Stream.of(
                Arguments.of(true, true, SUCCESS),
                Arguments.of(true, false, SUCCESS),
                Arguments.of(false, true, SUCCESS),
                Arguments.of(false, false, FAILURE));
    }

    @ParameterizedTest
    @MethodSource("orConditionalValuesProvider")
    void testOrConditionalExecute(
            final boolean ifCondition,
            final boolean orCondition,
            final String expectedResult
    ) {
        Conditional
                .on(() -> ifCondition)
                .or(() -> orCondition)
                .thenExecute(() -> setExecuteResult(SUCCESS))
                .orElseExecute(() -> setExecuteResult(FAILURE))
                .evaluate();

        assertThat(executeResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("orConditionalValuesProvider")
    void testOrConditionalGet(
            final boolean ifCondition,
            final boolean orCondition,
            final String expectedResult
    ) {
        final var actual = Conditional
                .on(() -> ifCondition)
                .or(() -> orCondition)
                .thenGet(() -> SUCCESS)
                .orElseGet(() -> FAILURE)
                .evaluate();

        assertThat(actual).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> conditionalComparisonValuesProvider() {
        return Stream.of(
                Arguments.of(true, true, true, true, SUCCESS),

                Arguments.of(false, true, true, true, SUCCESS),
                Arguments.of(true, false, true, true, SUCCESS),
                Arguments.of(true, true, false, true, SUCCESS),
                Arguments.of(true, true, true, false, SUCCESS),

                Arguments.of(false, false, true, true, SUCCESS),
                Arguments.of(false, true, false, true, SUCCESS),
                Arguments.of(false, true, true, false, SUCCESS),
                Arguments.of(true, false, false, true, SUCCESS),
                Arguments.of(true, true, false, false, FAILURE),
                Arguments.of(true, false, true, false, SUCCESS),

                Arguments.of(true, false, false, false, FAILURE),
                Arguments.of(false, true, false, false, FAILURE),
                Arguments.of(false, false, true, false, FAILURE),
                Arguments.of(false, false, false, true, SUCCESS),

                Arguments.of(false, false, false, false, FAILURE));
    }

    @ParameterizedTest
    @MethodSource("conditionalComparisonValuesProvider")
    void conditionalComparisonValuesProvider(
            final boolean condition1,
            final boolean condition2,
            final boolean condition3,
            final boolean condition4,
            final String expectedResult
    ) {
        final var actual = Conditional
                .on(() -> condition1)
                .or(() -> condition2)
                .and(() -> condition3)
                .or(() -> condition4)
                .thenGet(() -> SUCCESS)
                .orElseGet(() -> FAILURE)
                .evaluate();

        assertThat(actual).isEqualTo(expectedResult);
    }

    private void setExecuteResult(final String result) {
        this.executeResult = result;
    }

    private void setAlsoExecuteResult(final String result) {
        this.alsoExecuteResult = result;
    }

}