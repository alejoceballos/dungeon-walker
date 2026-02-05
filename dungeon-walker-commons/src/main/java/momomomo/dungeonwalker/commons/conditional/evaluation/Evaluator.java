package momomomo.dungeonwalker.commons.conditional.evaluation;

import momomomo.dungeonwalker.commons.conditional.functional.ConditionTest;
import momomomo.dungeonwalker.commons.conditional.immutable.Condition;
import momomomo.dungeonwalker.commons.conditional.immutable.Operator;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.logicalAnd;
import static java.lang.Boolean.logicalOr;
import static momomomo.dungeonwalker.commons.conditional.immutable.Operator.OR;

public class Evaluator {

    private final List<Condition> conditions = new ArrayList<>();

    public void addCondition(final Operator operator, final ConditionTest test) {
        conditions.add(new Condition(operator, test));
    }

    public boolean evaluate() {
        var result = conditions.getFirst().test().execute();

        for (int index = 1; index < conditions.size(); index++) {
            final var condition = conditions.get(index);
            final var testResult = condition.test().execute();

            result = condition.operator() == OR
                    ? logicalOr(result, testResult)
                    : logicalAnd(result, testResult);
        }

        return result;
    }

}
