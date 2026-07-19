package momomomo.dungeonwalker.engine.domain.model.coordinates;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
public class Coordinates {

    @Getter
    @Setter
    private int x;

    @Getter
    @Setter
    private int y;

    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }

    public enum Axis {
        X, Y
    }

    public static Coordinates of(final int x, final int y) {
        return new Coordinates(x, y);
    }

    public Coordinates adjust(final int amount, @NonNull final Axis... axes) {
        var newX = this.x;
        var newY = this.y;

        for (final var axis : axes) {
            switch (axis) {
                case X -> newX += amount;
                case Y -> newY += amount;
            }
        }

        return of(newX, newY);
    }

    @Override
    public @NonNull String toString() {
        return "(%d,%d)".formatted(x, y);
    }

}
