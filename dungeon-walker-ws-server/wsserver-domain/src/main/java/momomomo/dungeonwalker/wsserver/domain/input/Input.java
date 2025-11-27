package momomomo.dungeonwalker.wsserver.domain.input;

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
                        value = Identity.class,
                        name = IDENTITY),
                @JsonSubTypes.Type(
                        value = ClientHeartbeat.class,
                        name = HEARTBEAT)
        })
        @NonNull InputData data
) {

    public static final String IDENTITY = "identity";
    public static final String HEARTBEAT = "heartbeat";

    public static Input of(final Identity identity) {
        return new Input(IDENTITY, identity);
    }

    public static Input of(final ClientHeartbeat heartbeat) {
        return new Input(HEARTBEAT, heartbeat);
    }

}
