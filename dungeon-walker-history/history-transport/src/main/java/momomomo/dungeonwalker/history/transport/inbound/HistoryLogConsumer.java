package momomomo.dungeonwalker.history.transport.inbound;

import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.history.domain.inbound.HistoryLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class HistoryLogConsumer {

    @Bean
    public Consumer<HistoryLog> consumeHistoryLog() {
        return historyLog -> log.info("Received HistoryLog: {}", historyLog);
    }

}
