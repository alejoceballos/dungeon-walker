package momomomo.dungeonwalker.commons;

import java.util.function.Consumer;

public class Conditional<T> {

    private final T subject;

    private Conditional(T subject) {
        this.subject = subject;
    }

    public static <T> Conditional<T> when(T subject) {
        return new Conditional<>(subject);
    }

    public Conditional<T> isNull(final Consumer<Void> consumer) {
        if (subject == null) {
            consumer.accept(null);
        }

        return this;
    }

    public Conditional<T> isNotNull(final Consumer<T> consumer) {
        if (subject != null) {
            consumer.accept(subject);
        }

        return this;
    }
}
