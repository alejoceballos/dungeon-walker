package momomomo.dungeonwalker.engine.core.walker;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.domain.model.walker.moving.WalkerMovingStrategy;
import momomomo.dungeonwalker.engine.core.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.core.dungeon.command.MoveWalker;
import momomomo.dungeonwalker.engine.core.dungeon.command.PlaceWalker;
import momomomo.dungeonwalker.engine.core.walker.command.EnterTheDungeon;
import momomomo.dungeonwalker.engine.core.walker.command.GetMoving;
import momomomo.dungeonwalker.engine.core.walker.command.StandStill;
import momomomo.dungeonwalker.engine.core.walker.command.UpdateCoordinates;
import momomomo.dungeonwalker.engine.core.walker.command.WalkerCommand;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class WalkerActor extends AbstractBehavior<WalkerCommand> {

    private final ActorRef<DungeonCommand> dungeonRef;
    private final WalkerMovingStrategy movingStrategy;

    private Coordinates previousCoordinates;
    private Coordinates currentCoordinates;

    private WalkerActor(
            final ActorContext<WalkerCommand> context,
            final ActorRef<DungeonCommand> dungeonRef,
            final WalkerMovingStrategy movingStrategy) {
        log.debug("[ACTOR - Walker] constructor [id: {}]", context.getSelf().path().name());

        super(context);
        this.dungeonRef = dungeonRef;
        this.movingStrategy = movingStrategy;
    }

    public static Behavior<WalkerCommand> create(
            final ActorRef<DungeonCommand> dungeonRef,
            final WalkerMovingStrategy movingStrategy) {
        log.debug("[ACTOR - Walker] create");
        return Behaviors.setup(context -> new WalkerActor(context, dungeonRef, movingStrategy));
    }

    @Override
    public Receive<WalkerCommand> createReceive() {
        log.debug("[ACTOR - Walker] create receive [id: {}]", getContext().getSelf().path().name());
        return created();
    }

    public Receive<WalkerCommand> created() {
        log.debug("[ACTOR - Walker] created state [id: {}]", getContext().getSelf().path().name());
        return newReceiveBuilder()
                .onMessage(EnterTheDungeon.class, this::onEnterDungeon)
                .build();
    }

    public Receive<WalkerCommand> alive() {
        log.debug("[ACTOR - Walker] alive state [id: {}]", getContext().getSelf().path().name());
        return newReceiveBuilder()
                .onMessage(UpdateCoordinates.class, this::onUpdateCoordinates)
                .build();
    }

    public Receive<WalkerCommand> moving() {
        log.debug("[ACTOR - Walker] moving state [id: {}]", getContext().getSelf().path().name());
        return newReceiveBuilder()
                .onMessage(GetMoving.class, this::onMove)
                .onMessage(UpdateCoordinates.class, this::onUpdateCoordinates)
                .onMessage(StandStill.class, this::onStandStill)
                .build();
    }

    private Behavior<WalkerCommand> onEnterDungeon(final EnterTheDungeon command) {
        log.debug("[ACTOR - Walker] on enter dungeon [id: {}]", getContext().getSelf().path().name());
        // Tell the dungeon that you are alive and want to spawn in the coordinates
        // The dungeon will spawn you somewhere near and tell you that later
        dungeonRef.tell(new PlaceWalker(getContext().getSelf()));

        // Go to ALIVE state
        return alive();
    }

    private Behavior<WalkerCommand> onUpdateCoordinates(final UpdateCoordinates command) {
        log.debug("[ACTOR - Walker] on update coordinates [id: {}]", getContext().getSelf().path().name());
        if (!Objects.equals(currentCoordinates, command.coordinates())) {
            previousCoordinates = currentCoordinates;
            currentCoordinates = command.coordinates();
        }

        return startTimerToGetMoving();
    }

    private Behavior<WalkerCommand> onMove(final GetMoving command) {
        log.debug("[ACTOR - Walker] on move [id: {}]", getContext().getSelf().path().name());

        // Calculate new coordinates
        final var nextCoordinates = movingStrategy.nextCoordinates(previousCoordinates, currentCoordinates);

        // Ask to be moved to the new coordinates
        dungeonRef.tell(new MoveWalker(getContext().getSelf(), currentCoordinates, nextCoordinates));

        return Behaviors.same();
    }

    private Behavior<WalkerCommand> onStandStill(final StandStill command) {
        log.debug("[ACTOR - Walker] on stand still [id: {}]", getContext().getSelf().path().name());
        return startTimerToGetMoving();
    }

    private Behavior<WalkerCommand> startTimerToGetMoving() {
        // Start a counter-delay (in the future, based on the walker velocity). When the timer reaches zero, it will
        // send to the dungeon the next movements it wants to take
        return Behaviors.withTimers(timers -> {
            final var timerKey = getContext().getSelf().path().name();

            if (timers.isTimerActive(timerKey)) {
                timers.cancel(timerKey);
            }

            // Wait for some while (it could be the walker speed, the lower the value, the faster it moves) and then
            // send a self-command to start calculating where to go and to really move
            timers.startSingleTimer(
                    getContext().getSelf().path().name(),
                    new GetMoving(),
                    Duration.ofMillis(1000));

            // Go to the MOVING state
            return moving();
        });
    }

}
