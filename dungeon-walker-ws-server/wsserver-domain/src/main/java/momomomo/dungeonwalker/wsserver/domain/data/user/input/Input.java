package momomomo.dungeonwalker.wsserver.domain.data.user.input;

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
                        value = UserAuthentication.class,
                        name = AUTHENTICATION),
                @JsonSubTypes.Type(
                        value = UserHeartbeat.class,
                        name = HEARTBEAT),
                @JsonSubTypes.Type(
                        value = UserMovement.class,
                        name = MOVEMENT),
                @JsonSubTypes.Type(
                        value = UserAbandon.class,
                        name = ABANDON)
        })
        @NonNull InputData data
) {

    public static final String AUTHENTICATION = "authentication";
    public static final String HEARTBEAT = "heartbeat";
    public static final String MOVEMENT = "movement";
    public static final String ABANDON = "abandon";

    public static Input of(final UserAuthentication inputData) {
        return new Input(AUTHENTICATION, inputData);
    }

    public static Input of(final UserHeartbeat inputData) {
        return new Input(HEARTBEAT, inputData);
    }

    public static Input of(final UserMovement inputData) {
        return new Input(MOVEMENT, inputData);
    }

    public static Input of(final UserAbandon inputData) {
        return new Input(ABANDON, inputData);
    }

}
