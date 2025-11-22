package momomomo.dungeonwalker.engine.ui.console;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Console {

    @PostConstruct
    public void init() {
        log.debug("---> [CONSOLE] Start");
    }

}
