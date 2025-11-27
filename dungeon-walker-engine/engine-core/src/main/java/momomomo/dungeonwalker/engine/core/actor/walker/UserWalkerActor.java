package momomomo.dungeonwalker.engine.core.actor.walker;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.Effect;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.actor.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.domain.model.walker.Walker;

@Slf4j
public class UserWalkerActor extends WalkerActor {

    private UserWalkerActor(
            final ActorContext<WalkerCommand> context,
            final PersistenceId persistenceId) {
        super(context, persistenceId);
    }

    public static Behavior<WalkerCommand> create(final PersistenceId persistenceId) {
        log.debug("---> [ACTOR - Dungeon][persistenceId: {}] create", persistenceId.toString());
        return Behaviors.setup(context -> new UserWalkerActor(context, persistenceId));
    }

    @Override
    protected Effect<Walker> onUpdateCoordinates(
            final Walker state,
            final UpdateCoordinates command) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Effect<Walker> onStandStill(
            final Walker state,
            final StandStill command) {
        throw new UnsupportedOperationException();
    }

}
