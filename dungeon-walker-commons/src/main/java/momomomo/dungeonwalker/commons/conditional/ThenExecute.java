package momomomo.dungeonwalker.commons.conditional;

import momomomo.dungeonwalker.commons.conditional.evaluation.Evaluator;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionExecutor;

import java.util.ArrayList;
import java.util.List;

public class ThenExecute {

    private final Evaluator evaluator;
    private final List<ConditionExecutor> thenResults = new ArrayList<>();

    ThenExecute(final ConditionExecutor result, final Evaluator evaluator) {
        thenResults.add(result);
        this.evaluator = evaluator;
    }

    public ThenExecute alsoExecute(final ConditionExecutor result) {
        thenResults.add(result);
        return this;
    }

    public ElseExecute orElseExecute(final ConditionExecutor result) {
        return new ElseExecute(result, thenResults, evaluator);
    }

    public void evaluate() {
        if (evaluator.evaluate()) {
            thenResults.forEach(ConditionExecutor::execute);
        }
    }

}
