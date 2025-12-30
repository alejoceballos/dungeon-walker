package momomomo.dungeonwalker.wsserver.domain.output;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NonNull;

public record Output(
        @NonNull String type,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type",
                visible = true)
        @JsonSubTypes({
                @JsonSubTypes.Type(
                        value = ServerHeartbeat.class,
                        name = HEARTBEAT),
                @JsonSubTypes.Type(
                        value = ServerErrors.class,
                        name = SERVER_ERRORS),
                @JsonSubTypes.Type(
                        value = ServerMessage.class,
                        name = SERVER_MESSAGE),
                @JsonSubTypes.Type(
                        value = EngineWalkersCoordinates.class,
                        name = SERVER_MESSAGE)
        })
        @NonNull OutputData data
) {

    public static final String HEARTBEAT = "heartbeat";
    public static final String SERVER_ERRORS = "server-errors";
    public static final String SERVER_MESSAGE = "server-message";
    public static final String WALKERS_COORDINATES = "Walkers-coordinates";

    public static Output of(final ServerHeartbeat heartbeat) {
        return new Output(HEARTBEAT, heartbeat);
    }

    public static Output of(final ServerErrors error) {
        return new Output(SERVER_ERRORS, error);
    }

    public static Output of(final ServerMessage message) {
        return new Output(SERVER_MESSAGE, message);
    }

    public static Output of(final EngineWalkersCoordinates walkersCoordinates) {
        return new Output(WALKERS_COORDINATES, walkersCoordinates);
    }

}
