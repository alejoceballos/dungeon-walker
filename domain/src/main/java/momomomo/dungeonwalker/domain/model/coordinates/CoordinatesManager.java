package momomomo.dungeonwalker.domain.model.coordinates;

import lombok.NonNull;

import static java.lang.Math.negateExact;
import static momomomo.dungeonwalker.domain.model.coordinates.Coordinates.Axis.X;
import static momomomo.dungeonwalker.domain.model.coordinates.Coordinates.Axis.Y;

public class CoordinatesManager {

    private Coordinates coordinates;

    public CoordinatesManager(@NonNull final Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public static CoordinatesManager of(@NonNull final Coordinates coordinates) {
        return new CoordinatesManager(coordinates);
    }

    public CoordinatesManager moveRight(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(steps, X);
        return this;
    }

    public CoordinatesManager moveLeft(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(negateExact(steps), X);
        return this;
    }

    public CoordinatesManager moveUp(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(negateExact(steps), Y);
        return this;
    }

    public CoordinatesManager moveDown(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(steps, Y);
        return this;
    }

    public Coordinates coordinates() {
        return coordinates;
    }

    private void validateSteps(final int steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("Steps cannot be negative.");
        }
    }

}
