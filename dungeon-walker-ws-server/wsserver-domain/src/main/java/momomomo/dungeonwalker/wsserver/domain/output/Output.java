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
                        name = HEARTBEAT)
        })
        @NonNull OutputData data
) {

    public static final String HEARTBEAT = "heartbeat";

    public static Output of(final ServerHeartbeat heartbeat) {
        return new Output(HEARTBEAT, heartbeat);
    }

}
