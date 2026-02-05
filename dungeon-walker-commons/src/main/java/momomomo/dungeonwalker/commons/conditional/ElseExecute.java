package momomomo.dungeonwalker.commons.conditional;

import momomomo.dungeonwalker.commons.conditional.evaluation.Evaluator;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionExecutor;

import java.util.ArrayList;
import java.util.List;

public class ElseExecute {

    private final Evaluator evaluator;
    private final List<ConditionExecutor> thenResults;
    private final List<ConditionExecutor> elseResults = new ArrayList<>();

    ElseExecute(final ConditionExecutor result, final List<ConditionExecutor> thenResults, final Evaluator evaluator) {
        elseResults.add(result);
        this.thenResults = thenResults;
        this.evaluator = evaluator;
    }

    public ElseExecute alsoExecute(final ConditionExecutor result) {
        elseResults.add(result);
        return this;
    }

    public void evaluate() {
        (evaluator.evaluate() ? thenResults : elseResults).forEach(ConditionExecutor::execute);
    }

}
