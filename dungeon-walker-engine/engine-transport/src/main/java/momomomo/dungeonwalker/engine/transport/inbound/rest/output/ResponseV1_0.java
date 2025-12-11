package momomomo.dungeonwalker.engine.transport.inbound.rest.output;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class ResponseV1_0 {

    private String message;
    private LocalDateTime timestamp;

}
