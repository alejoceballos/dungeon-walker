package momomomo.dungeonwalker.wsserver.core.handler;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
public record HandlingResult(@NonNull HandlingResult.Type type, @NonNull List<String> errors) {

    public enum Type {
        SUCCESS,
        FAILURE,
        IGNORED
    }

}
