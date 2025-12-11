package momomomo.dungeonwalker.engine.core.service;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Service
public class IdentityService {

    private static final NumberFormat LEVEL_FORMATTER = new DecimalFormat("00");

    public String dungeonId(final int level) {
        if (level < 1 || level > 99) {
            throw new InvalidDungeonLevelException("Level must be between 1 and 99");
        }

        return "LVL-" + LEVEL_FORMATTER.format(level);
    }

}
