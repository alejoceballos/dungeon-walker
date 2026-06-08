package momomomo.dungeonwalker.engine.core.setup;

public class DungeonSetupException extends RuntimeException {

    public DungeonSetupException(final Throwable cause) {
        super(cause);
    }

    public DungeonSetupException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
