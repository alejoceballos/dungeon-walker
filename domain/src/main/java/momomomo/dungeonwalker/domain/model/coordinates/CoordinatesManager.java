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

    public CoordinatesManager moveNorth(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(negateExact(steps), Y);
        return this;
    }

    public CoordinatesManager moveEast(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(steps, X);
        return this;
    }

    public CoordinatesManager moveWest(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(negateExact(steps), X);
        return this;
    }

    public CoordinatesManager moveNortheast(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates
                .adjust(steps, X)
                .adjust(negateExact(steps), Y);
        return this;
    }

    public CoordinatesManager moveNorthwest(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates
                .adjust(negateExact(steps), X)
                .adjust(negateExact(steps), Y);
        return this;
    }

    public CoordinatesManager moveSouth(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates.adjust(steps, Y);
        return this;
    }

    public CoordinatesManager moveSoutheast(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates
                .adjust(steps, X)
                .adjust(steps, Y);
        return this;
    }

    public CoordinatesManager moveSouthwest(final int steps) {
        validateSteps(steps);
        this.coordinates = coordinates
                .adjust(negateExact(steps), X)
                .adjust(steps, Y);
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
