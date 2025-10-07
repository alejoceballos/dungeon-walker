package momomomo.dungeonwalker.engine.guardian;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.domain.event.DungeonEvent;
import momomomo.dungeonwalker.domain.event.DungeonEventListener;
import momomomo.dungeonwalker.domain.event.DungeonUpdated;
import momomomo.dungeonwalker.domain.model.dungeon.placing.DungeonPlacingStrategy;
import momomomo.dungeonwalker.engine.dungeon.DungeonActor;
import momomomo.dungeonwalker.engine.dungeon.command.DungeonCommand;
import momomomo.dungeonwalker.engine.dungeon.command.SetupDungeon;
import momomomo.dungeonwalker.engine.guardian.command.*;
import momomomo.dungeonwalker.engine.walker.WalkerActor;
import momomomo.dungeonwalker.engine.walker.command.EnterTheDungeon;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.function.Consumer;

import static momomomo.dungeonwalker.commons.reflection.GenericUtils.getGenericTypeArgument;

@Slf4j
@SuppressWarnings("rawtypes")
public class EngineActor extends AbstractBehavior<EngineCommand> {

    private final DungeonPlacingStrategy placingStrategy;

    private ActorRef<DungeonCommand> dungeonRef;
    public HashMap<String, DungeonEventListener> eventListeners = new HashMap<>();

    public EngineActor(
            final ActorContext<EngineCommand> context,
            final DungeonPlacingStrategy placingStrategy) {
        super(context);
        this.placingStrategy = placingStrategy;
    }

    public static Behavior<EngineCommand> create(final DungeonPlacingStrategy placingStrategy) {
        log.debug("[ACTOR - Engine] create");
        return Behaviors.setup(context -> new EngineActor(context, placingStrategy));
    }

    @Override
    public Receive<EngineCommand> createReceive() {
        log.debug("[ACTOR - Engine] create receive");
        return newReceiveBuilder()
                .onMessage(CreateDungeon.class, this::onCreateDungeon)
                .build();
    }

    public Receive<EngineCommand> ready() {
        log.debug("[ACTOR - Engine] ready");
        return newReceiveBuilder()
                .onMessage(SpawnWalker.class, this::onSpawnWalker)
                .onMessage(DungeonReady.class, this::onDungeonReady)
                .onMessage(AddEventListener.class, this::onAddEventListener)
                .onMessage(NotifyDungeonUpdated.class, this::onNotifyDungeonUpdated)
                .build();
    }

    private Behavior<EngineCommand> onCreateDungeon(final CreateDungeon command) {
        log.debug("[ACTOR - Engine] on create dungeon");
        dungeonRef = getContext().spawn(DungeonActor.create(getContext().getSelf(), placingStrategy), "dungeon-level-1");
        dungeonRef.tell(new SetupDungeon(command.dungeon()));
        return ready();
    }

    private Behavior<EngineCommand> onSpawnWalker(final SpawnWalker command) {
        log.debug("[ACTOR - Engine] on spawn walker");
        final var walkerRef = getContext().spawn(WalkerActor.create(dungeonRef), command.id());
        walkerRef.tell(new EnterTheDungeon());
        return Behaviors.same();
    }

    private Behavior<EngineCommand> onDungeonReady(final DungeonReady command) {
        log.debug("[ACTOR - Engine] on dungeon ready");
        return Behaviors.same();
    }

    private Behavior<EngineCommand> onAddEventListener(final AddEventListener command) {
        log.debug("[ACTOR - Engine] on add event listener");
        getGenericTypeArgument(command.eventListener())
                .ifPresent(putEventListener(command.eventListener()));
        return Behaviors.same();
    }

    private Behavior<EngineCommand> onNotifyDungeonUpdated(final NotifyDungeonUpdated command) {
        log.debug("[ACTOR - Engine] on dungeon updated");
        notifyListener(new DungeonUpdated(command.dungeon()));

        return Behaviors.same();
    }

    private Consumer<Type> putEventListener(final DungeonEventListener eventListener) {
        return typedArgument -> eventListeners.put(typedArgument.getTypeName(), eventListener);
    }

    @SuppressWarnings("unchecked")
    private void notifyListener(final DungeonEvent event) {
        eventListeners
                .getOrDefault(
                        event.getClass().getName(),
                        new FakeEventListener())
                .onEvent(event);
    }

    private static class FakeEventListener implements DungeonEventListener<FakeEventListener.FakeEvent> {
        private record FakeEvent() implements DungeonEvent {
        }

        @Override
        public void onEvent(FakeEvent event) {
            log.debug("[ACTOR - Fake Event Listener] on event - discard");
        }
    }

}
