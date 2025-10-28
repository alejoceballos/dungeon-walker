package momomomo.dungeonwalker.wsserver.core.handler;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import momomomo.dungeonwalker.wsserver.core.validator.ValidationError;

import java.util.List;

@Builder
public record HandlingResult(@NonNull type type, @Nullable List<ValidationError> errors) {

    public enum type {
        SUCCESS,
        FAILURE,
        IGNORED
    }

}
