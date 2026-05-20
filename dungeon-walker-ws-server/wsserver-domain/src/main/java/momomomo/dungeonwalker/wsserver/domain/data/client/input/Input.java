package momomomo.dungeonwalker.wsserver.domain.data.client.input;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NonNull;

public record Input(
        @NonNull String type,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type",
                visible = true)
        @JsonSubTypes({
                @JsonSubTypes.Type(
                        value = Authenticate.class,
                        name = AUTHENTICATE),
                @JsonSubTypes.Type(
                        value = ClientHeartbeat.class,
                        name = HEARTBEAT),
                @JsonSubTypes.Type(
                        value = Movement.class,
                        name = MOVEMENT),
                @JsonSubTypes.Type(
                        value = Leave.class,
                        name = LEAVE)
        })
        @NonNull InputData data
) {

    public static final String AUTHENTICATE = "authenticate";
    public static final String HEARTBEAT = "heartbeat";
    public static final String MOVEMENT = "movement";
    public static final String LEAVE = "leave";

    public static Input of(final Authenticate authenticate) {
        return new Input(AUTHENTICATE, authenticate);
    }

    public static Input of(final ClientHeartbeat heartbeat) {
        return new Input(HEARTBEAT, heartbeat);
    }

    public static Input of(final Movement movement) {
        return new Input(MOVEMENT, movement);
    }

    public static Input of(final Leave leave) {
        return new Input(LEAVE, leave);
    }

}
