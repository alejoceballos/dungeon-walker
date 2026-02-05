package momomomo.dungeonwalker.commons.conditional.immutable;

import momomomo.dungeonwalker.commons.conditional.functional.ConditionTest;

public record Condition(Operator operator, ConditionTest test) {
}
