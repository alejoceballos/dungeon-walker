package momomomo.dungeonwalker.history.transport.mapper.map;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import momomomo.dungeonwalker.history.domain.model.map.Coordinates;

@ToString(
        includeFieldNames = false,
        doNotUseGetters = true
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CoordinatesMapper implements Coordinates {

    @Column(name = "X_COORD", nullable = false)
    private int x;

    @Column(name = "Y_COORD", nullable = false)
    private int y;

}
