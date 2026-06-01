package momomomo.dungeonwalker.engine.core.setup;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class DungeonIdentity {

    private static final NumberFormat LEVEL_FORMATTER = new DecimalFormat("000");

    public String id(final int level) {
        if (level < 1 || level > 999) {
            throw new InvalidDungeonLevelException("Level must be between 1 and 999");
        }

        return "LVL-" + LEVEL_FORMATTER.format(level);
    }

    public int level(final String id) {
        if (isBlank(id) || !id.matches("LVL-\\d{3}")) {
            throw new InvalidDungeonIdException("Dungeon ID must be in the format 'LVL-XXX' where XXX is a three-digit number");
        }

        return Integer.parseInt(id.substring(4));
    }

}
