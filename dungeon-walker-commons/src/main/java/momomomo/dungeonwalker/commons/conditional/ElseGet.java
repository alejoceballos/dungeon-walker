package momomomo.dungeonwalker.commons.conditional;

import momomomo.dungeonwalker.commons.conditional.evaluation.Evaluator;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionGetter;

public class ElseGet<T> {

    private final Evaluator evaluator;
    private final ConditionGetter<T> thenResult;
    private final ConditionGetter<T> elseResult;

    ElseGet(
            final ConditionGetter<T> result,
            final ConditionGetter<T> thenResult,
            final Evaluator evaluator) {
        this.elseResult = result;
        this.thenResult = thenResult;
        this.evaluator = evaluator;
    }

    public T evaluate() {
        return (evaluator.evaluate() ? thenResult : elseResult).get();
    }

}
