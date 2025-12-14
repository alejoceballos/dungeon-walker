package momomomo.dungeonwalker.engine.core.service;

import akka.cluster.sharding.typed.javadsl.EntityRef;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.actor.ClusterShardingManager;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WakeUp;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerCommand;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerStateReply;
import momomomo.dungeonwalker.engine.core.actor.walker.command.WalkerStateRequest;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.domain.model.dungeon.placing.SpiralStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovementStrategy;
import momomomo.dungeonwalker.engine.domain.model.walker.state.Asleep;
import momomomo.dungeonwalker.engine.domain.model.walker.state.WalkerState;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static akka.actor.typed.javadsl.AskPattern.ask;

@Slf4j
@RequiredArgsConstructor
public abstract class WalkerService {

    private static final String LABEL = "---> [SERVICE - Walker]";

    // When having several dungeon levels, the placing strategy will depend on the level
    private static final DungeonPlacingStrategy PLACING_STRATEGY = new SpiralStrategy();

    private final IdentityService identityService;
    protected final ClusterShardingManager cluster;

    public void enterTheDungeon(@NonNull final String walkerId) {
        log.debug("{} Entering the dungeon: {}", LABEL, walkerId);

        final var walkerRef = getWalkerRef(walkerId);
        final var state = askForState(walkerRef).value();

        if (onEnterTheDungeonWhenInAsleepState(walkerRef, state)) {
            log.debug("{} Walker \"{}\" is Asleep", LABEL, walkerId);
            return;
        }

        if (onEnterTheDungeonWhenInStoppedState(walkerRef, state)) {
            log.debug("{} Walker \"{}\" is Stopped", LABEL, walkerId);
        }
    }

    protected boolean onEnterTheDungeonWhenInAsleepState(
            final EntityRef<WalkerCommand> walkerRef,
            final Class<? extends WalkerState> state) {
        if (!Asleep.class.equals(state)) {
            return false;
        }

        log.debug("{} Waking up walker \"{}\"", LABEL, walkerRef.getEntityId());

        walkerRef.tell(new WakeUp(
                identityService.dungeonId(1),
                PLACING_STRATEGY,
                getMovementStrategy()));

        return true;
    }

    protected boolean onEnterTheDungeonWhenInStoppedState(
            final EntityRef<WalkerCommand> walkerRef,
            final Class<? extends WalkerState> state) {
        return true;
    }

    private WalkerStateReply askForState(final EntityRef<WalkerCommand> walkerRef) {
        try {
            return ask(
                    walkerRef,
                    WalkerStateRequest::new,
                    Duration.ofSeconds(1L),
                    cluster.getActorSystem().scheduler())
                    .toCompletableFuture()
                    .get();

        } catch (final InterruptedException | ExecutionException e) {
            throw new DungeonServiceException("Unable to ask dungeon for its state", e);
        }
    }

    protected abstract EntityRef<WalkerCommand> getWalkerRef(String walkerId);

    protected abstract WalkerMovementStrategy getMovementStrategy();

}
