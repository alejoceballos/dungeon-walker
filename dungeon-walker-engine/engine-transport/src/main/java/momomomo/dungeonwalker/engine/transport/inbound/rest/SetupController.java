package momomomo.dungeonwalker.engine.transport.inbound.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.engine.domain.manager.DungeonManager;
import momomomo.dungeonwalker.engine.transport.inbound.rest.input.DungeonConfigV1_0;
import momomomo.dungeonwalker.engine.transport.inbound.rest.output.ResponseV1_0;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping(value = "/setup")
@RequiredArgsConstructor
public class SetupController {

    private final DungeonManager dungeonManager;

    @PostMapping(
            path = "/dungeon",
            consumes = "application/json",
            produces = "application/json",
            version = "1.0"
    )
    public ResponseEntity<ResponseV1_0> setupDungeonV1(@RequestBody final DungeonConfigV1_0 dungeon) {
        // Call some injected service
        dungeonManager.setupDungeon(dungeon.getLevel());

        return ResponseEntity
                .ok(ResponseV1_0.builder()
                        .message("Dungeon set up complete")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

}
