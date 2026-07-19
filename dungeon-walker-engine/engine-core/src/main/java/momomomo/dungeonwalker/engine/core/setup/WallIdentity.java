package momomomo.dungeonwalker.engine.core.setup;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Component
public class WallIdentity {

    private static final NumberFormat LEVEL_FORMATTER = new DecimalFormat("000");

    public String id(final int level) {
        if (level < 1 || level > 999) {
            throw new InvalidDungeonLevelException("Level must be between 1 and 999");
        }

        return "W-" + LEVEL_FORMATTER.format(level);
    }

}
