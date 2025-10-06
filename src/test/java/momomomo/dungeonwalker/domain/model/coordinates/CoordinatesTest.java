package momomomo.dungeonwalker.domain.model.coordinates;

import org.junit.jupiter.api.Test;

import static momomomo.dungeonwalker.domain.model.coordinates.Coordinates.Axis.X;
import static momomomo.dungeonwalker.domain.model.coordinates.Coordinates.Axis.Y;
import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesTest {

    @Test
    void of() {
        assertThat(Coordinates.of(1, 1))
                .isEqualTo(new Coordinates(1, 1));
    }

    @Test
    void adjust() {
        assertThat(new Coordinates(0, 0).adjust(1, X, Y, X, Y))
                .isEqualTo(new Coordinates(2, 2));
    }

}