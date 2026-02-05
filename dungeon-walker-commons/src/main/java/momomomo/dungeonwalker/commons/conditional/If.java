package momomomo.dungeonwalker.commons.conditional;

import momomomo.dungeonwalker.commons.conditional.evaluation.Evaluator;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionExecutor;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionGetter;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionTest;
import momomomo.dungeonwalker.commons.conditional.immutable.Operator;

public class If {

    private final Evaluator evaluator = new Evaluator();

    If(final ConditionTest test) {
        evaluator.addCondition(Operator.AND, test);
    }

    public If and(final ConditionTest test) {
        evaluator.addCondition(Operator.AND, test);
        return this;
    }

    public If or(final ConditionTest test) {
        evaluator.addCondition(Operator.OR, test);
        return this;
    }

    public ThenExecute thenExecute(final ConditionExecutor result) {
        return new ThenExecute(result, evaluator);
    }

    public <T> ThenGet<T> thenGet(final ConditionGetter<T> result) {
        return new ThenGet<>(result, evaluator);
    }

}
