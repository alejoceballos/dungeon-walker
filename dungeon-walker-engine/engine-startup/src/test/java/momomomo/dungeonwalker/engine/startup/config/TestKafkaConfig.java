package momomomo.dungeonwalker.engine.startup.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;
import java.util.Properties;

import static java.util.Collections.singletonList;

@Slf4j
@Configuration
public class TestKafkaConfig {

    @Bean
    public ProducerFactory<@NonNull String, @NonNull ClientRequest> testProducerFactory(
            @Value("${spring.kafka.producer.bootstrap-servers}") final String bootstrapServers,
            @Value("${spring.kafka.producer.key-serializer}") final Class<?> keySerializer,
            @Value("${test.kafka.producer.value-serializer}") final Class<?> valueSerializer,
            @Value("${spring.kafka.producer.acks}") final String acks,
            @Value("${spring.kafka.producer.retries}") final int retries
    ) {
        log.debug("---> [TEST CONFIG - Kafka] Creating producer factory: {}, {}, {}, {}, {}"
                , bootstrapServers, keySerializer, valueSerializer, acks, retries);
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer,
                ProducerConfig.ACKS_CONFIG, acks,
                ProducerConfig.RETRIES_CONFIG, retries));
    }

    @Bean
    public KafkaTemplate<@NonNull String, @NonNull ClientRequest> testKafkaTemplate(
            final ProducerFactory<@NonNull String, @NonNull ClientRequest> testProducerFactory
    ) {
        log.debug("---> [TEST CONFIG - Kafka] Creating kafka template");
        return new KafkaTemplate<>(testProducerFactory);
    }

    @Bean
    public KafkaConsumer<String, EngineMessage> testKafkaConsumer(
            @Value("${spring.kafka.consumer.bootstrap-servers}") final String bootstrapServers,
            @Value("${test.kafka.consumer.group-id}") final String groupId,
            @Value("${spring.kafka.consumer.key-deserializer}") final Class<?> keyDeserializer,
            @Value("${test.kafka.consumer.value-deserializer}") final Class<?> valueDeserializer,
            @Value("${test.kafka.consumer.enable-auto-commit}") final String enableAutoCommit,
            @Value("${kafka.topic.game-engine.outbound}") final String topic
    ) {
        final var props = new Properties();

        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", enableAutoCommit);
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);

        final var consumer = new KafkaConsumer<String, EngineMessage>(props);
        consumer.subscribe(singletonList(topic));

        return consumer;
    }

}
