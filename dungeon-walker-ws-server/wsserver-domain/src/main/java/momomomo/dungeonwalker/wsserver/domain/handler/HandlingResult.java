package momomomo.dungeonwalker.wsserver.domain.handler;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public record HandlingResult(@NonNull HandlingResult.Type type, @NonNull List<String> errors) {

    public enum Type {
        SUCCESS,
        FAILURE,
        IGNORED
    }

    public String description() {
        switch (type) {
            case SUCCESS -> {
                return "Handling succeeded";
            }
            case FAILURE -> {
                return "Handling failed with errors: " + String.join(", ", errors);
            }
            case IGNORED -> {
                return "Handling ignored with message: " + String.join(", ", errors);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static HandlingResult success() {
        return new HandlingResult(Type.SUCCESS, List.of());
    }

    public static HandlingResult failure() {
        return new HandlingResult(Type.FAILURE, List.of());
    }

    public static HandlingResult failure(@NonNull final List<String> errors) {
        return new HandlingResult(Type.FAILURE, errors);
    }

    public static HandlingResult failure(@NonNull final String... errors) {
        return new HandlingResult(Type.FAILURE, Arrays.asList(errors));
    }

    public static HandlingResult ignored() {
        return new HandlingResult(Type.IGNORED, List.of());
    }

    public static HandlingResult ignored(@NonNull final List<String> errors) {
        return new HandlingResult(Type.IGNORED, errors);
    }

    public static HandlingResult ignored(@NonNull final String... errors) {
        return new HandlingResult(Type.IGNORED, Arrays.asList(errors));
    }

}
