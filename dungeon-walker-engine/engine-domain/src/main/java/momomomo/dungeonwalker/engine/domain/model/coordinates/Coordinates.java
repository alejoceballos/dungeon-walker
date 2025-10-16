package momomomo.dungeonwalker.engine.domain.model.coordinates;

import lombok.NonNull;

public record Coordinates(int x, int y) {

    public enum Axis {
        X, Y
    }

    public static Coordinates of(final int x, final int y) {
        return new Coordinates(x, y);
    }

    public Coordinates adjust(int amount, @NonNull final Axis... axes) {
        var x = this.x;
        var y = this.y;

        for (final var axis : axes) {
            switch (axis) {
                case X -> x += amount;
                case Y -> y += amount;
            }
        }

        return of(x, y);
    }

    @Override
    public @NonNull String toString() {
        return "(%d,%d)".formatted(x, y);
    }
}
