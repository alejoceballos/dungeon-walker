package momomomo.dungeonwalker.wsserver.transport.inbound;

import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import momomomo.dungeonwalker.wsserver.domain.inbound.Consumer;
import momomomo.dungeonwalker.wsserver.domain.inbound.ConsumerFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class WsServerKafkaConsumerFactory implements ConsumerFactory<EngineMessage> {

    private final String bootstrapServers;
    private final Class<?> keyDeserializer;
    private final Class<?> valueDeserializer;
    private final String enableAutoCommit;
    private final String topic;

    public WsServerKafkaConsumerFactory(
            @Value("${spring.kafka.consumer.bootstrap-servers}") final String bootstrapServers,
            @Value("${spring.kafka.consumer.key-deserializer}") final Class<?> keyDeserializer,
            @Value("${spring.kafka.consumer.value-deserializer}") final Class<?> valueDeserializer,
            @Value("${spring.kafka.consumer.enable-auto-commit}") final String enableAutoCommit,
            @Value("${kafka.topic.game-engine.inbound}") final String topic) {
        this.bootstrapServers = bootstrapServers;
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
        this.enableAutoCommit = enableAutoCommit;
        this.topic = topic;
    }

    @Override
    public Consumer<EngineMessage> create(final String groupId) {
        final var props = new Properties();

        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", enableAutoCommit);
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);

        return new WsServerKafkaConsumer(new KafkaConsumer<>(props), topic);
    }

}
