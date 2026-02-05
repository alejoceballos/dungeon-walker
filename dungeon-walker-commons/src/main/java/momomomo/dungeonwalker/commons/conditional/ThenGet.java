package momomomo.dungeonwalker.commons.conditional;

import momomomo.dungeonwalker.commons.conditional.evaluation.Evaluator;
import momomomo.dungeonwalker.commons.conditional.functional.ConditionGetter;

public class ThenGet<T> {

    private final Evaluator evaluator;
    private final ConditionGetter<T> thenResult;

    ThenGet(final ConditionGetter<T> result, final Evaluator evaluator) {
        this.thenResult = result;
        this.evaluator = evaluator;
    }

    public ElseGet<T> orElseGet(final ConditionGetter<T> result) {
        return new ElseGet<>(result, thenResult, evaluator);
    }

    public T evaluate() {
        if (evaluator.evaluate()) {
            return thenResult.get();
        }

        return null;
    }

}
