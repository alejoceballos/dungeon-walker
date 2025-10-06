package momomomo.dungeonwalker.presentation;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.domain.DungeonMaster;
import momomomo.dungeonwalker.presentation.event.TerminalDungeonCreated;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Console {

    private final DungeonMaster dungeonMaster;

    @PostConstruct
    public void init() {
        log.debug("[TERMINAL] Start");
        dungeonMaster.addEventListener(new TerminalDungeonCreated());
        dungeonMaster.addWalker("walker-id");
    }

}
