package momomomo.dungeonwalker.engine.domain.model.walker.moving;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Direction {

    EAST("E"),
    WEST("W"),
    NORTH("N"),
    SOUTH("S"),
    SOUTHWEST("SW"),
    SOUTHEAST("SE"),
    NORTHWEST("NW"),
    NORTHEAST("NE");

    @Getter
    private final String acronym;

    public static Direction of(@NonNull final String direction) {
        return Arrays.stream(values())
                .filter(value -> value.acronym.equalsIgnoreCase(direction) || value.name().equalsIgnoreCase(direction))
                .findFirst()
                .orElse(null);
    }

}
