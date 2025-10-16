package momomomo.dungeonwalker.engine.core;

import akka.actor.typed.ActorSystem;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.core.guardian.command.AddEventListener;
import momomomo.dungeonwalker.engine.core.guardian.command.CreateDungeon;
import momomomo.dungeonwalker.engine.core.guardian.command.EngineCommand;
import momomomo.dungeonwalker.engine.core.guardian.command.SpawnWalker;
import momomomo.dungeonwalker.engine.domain.DungeonMaster;
import momomomo.dungeonwalker.engine.domain.event.DungeonEvent;
import momomomo.dungeonwalker.engine.domain.event.DungeonEventListener;
import momomomo.dungeonwalker.engine.domain.mapper.DungeonMapper;
import momomomo.dungeonwalker.engine.domain.model.coordinates.Coordinates;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EngineManager implements DungeonMaster {

    public final ActorSystem<EngineCommand> engine;

    public EngineManager(
            final ActorSystem<EngineCommand> engine,
            final String rawMap,
            final DungeonMapper<String> mapper,
            final Coordinates defaultSpawnLocation) {
        log.debug("[ENGINE - Manager] constructor");

        this.engine = engine;
        this.engine.tell(new CreateDungeon(mapper.map(rawMap, defaultSpawnLocation)));
    }

    @Override
    public void addEventListener(final DungeonEventListener<? extends DungeonEvent> eventListener) {
        log.debug("[ENGINE - Manager] add event listener");
        this.engine.tell(new AddEventListener(eventListener));
    }

    @Override
    public void addWalker(@NonNull final String id) {
        log.debug("[ENGINE - Manager] add walker \"{}\"", id);
        engine.tell(new SpawnWalker(id));
    }

}
