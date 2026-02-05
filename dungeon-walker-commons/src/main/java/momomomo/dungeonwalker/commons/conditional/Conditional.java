package momomomo.dungeonwalker.commons.conditional;

import momomomo.dungeonwalker.commons.conditional.functional.ConditionTest;

public class Conditional {

    private Conditional() {
    }
    
    public static If on(final ConditionTest test) {
        return new If(test);
    }

}
