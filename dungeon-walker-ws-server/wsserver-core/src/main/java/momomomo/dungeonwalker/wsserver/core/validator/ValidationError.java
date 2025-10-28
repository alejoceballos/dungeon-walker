package momomomo.dungeonwalker.wsserver.core.validator;

import jakarta.annotation.Nonnull;
import lombok.NonNull;

public record ValidationError(@NonNull String field, @NonNull ValidationError.Type type) {

    public enum Type {
        NOT_NULL("cannot be null"),
        NOT_EMPTY("cannot be empty");

        private final String message;

        Type(final String message) {
            this.message = message;
        }
    }

    @Nonnull
    @Override
    public String toString() {
        return field + " " + type.message;
    }

}
