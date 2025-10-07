package momomomo.dungeonwalker.engine;

import akka.actor.typed.ActorSystem;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.domain.DungeonMaster;
import momomomo.dungeonwalker.domain.event.DungeonEvent;
import momomomo.dungeonwalker.domain.event.DungeonEventListener;
import momomomo.dungeonwalker.domain.mapper.DungeonMapper;
import momomomo.dungeonwalker.domain.model.coordinates.Coordinates;
import momomomo.dungeonwalker.engine.guardian.command.AddEventListener;
import momomomo.dungeonwalker.engine.guardian.command.CreateDungeon;
import momomomo.dungeonwalker.engine.guardian.command.EngineCommand;
import momomomo.dungeonwalker.engine.guardian.command.SpawnWalker;
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
        log.debug("[ENGINE - Manager] add walker");
        engine.tell(new SpawnWalker(id));
    }

}
