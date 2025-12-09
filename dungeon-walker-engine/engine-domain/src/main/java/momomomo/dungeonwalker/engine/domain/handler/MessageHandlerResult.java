package momomomo.dungeonwalker.engine.domain.handler;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class MessageHandlerResult<M> {

    private final M message;
    private Throwable failure;

    public MessageHandlerResult(@NonNull final M message) {
        this.message = message;
    }

    public MessageHandlerResult(@NonNull final M message, @NonNull final Throwable failure) {
        this(message);
        this.failure = failure;
    }

    public MessageHandlerResult<M> onSuccess(@NonNull final Consumer<M> onSuccessConsumer) {
        if (isNull(failure)) {
            onSuccessConsumer.accept(message);
        }

        return this;
    }

    public void onFailure(@NonNull final BiConsumer<M, Throwable> onFailureConsumer) {
        if (nonNull(failure)) {
            onFailureConsumer.accept(message, failure);
        }
    }

}
