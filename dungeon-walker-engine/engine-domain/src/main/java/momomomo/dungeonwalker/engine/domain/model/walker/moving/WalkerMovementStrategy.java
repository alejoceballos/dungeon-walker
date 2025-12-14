package momomomo.dungeonwalker.engine.domain.model.walker.moving;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SameDirectionOrRandomOtherwise.class, name = "SAME_DIR_OR_RANDOM"),
        @JsonSubTypes.Type(value = UserMovementStrategy.class, name = "USER")
})
public interface WalkerMovementStrategy {

    @Nonnull
    List<Coordinates> nextCoordinates(
            @Nullable Coordinates previousCoordinates,
            @Nonnull Coordinates currentCoordinates);

}
