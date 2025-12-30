package momomomo.dungeonwalker.wsserver.startup.config;

import lombok.NonNull;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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

@Configuration
public class TestKafkaConfig {

    @Bean
    public KafkaConsumer<String, ClientRequest> testKafkaConsumer(
            @Value("${spring.kafka.consumer.bootstrap-servers}") final String bootstrapServers,
            @Value("${test.kafka.consumer.group-id}") final String groupId,
            @Value("${spring.kafka.consumer.key-deserializer}") final Class<?> keyDeserializer,
            @Value("${test.kafka.consumer.value-deserializer}") final Class<?> valueDeserializer,
            @Value("${test.kafka.consumer.enable-auto-commit}") final String enableAutoCommit,
            @Value("${kafka.topic.game-engine.outbound}") final String topic
    ) {
        final var props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);

        final var consumer = new KafkaConsumer<String, ClientRequest>(props);
        consumer.subscribe(singletonList(topic));

        return consumer;
    }

    @Bean
    public ProducerFactory<@NonNull String, @NonNull EngineMessage> testProducerFactory(
            @Value("${spring.kafka.producer.bootstrap-servers}") final String bootstrapServers,
            @Value("${spring.kafka.producer.key-serializer}") final Class<?> keySerializer,
            @Value("${test.kafka.producer.value-serializer}") final Class<?> valueSerializer,
            @Value("${spring.kafka.producer.acks}") final String acks,
            @Value("${spring.kafka.producer.retries}") final int retries
    ) {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer,
                ProducerConfig.ACKS_CONFIG, acks,
                ProducerConfig.RETRIES_CONFIG, retries));
    }

    @Bean
    public KafkaTemplate<@NonNull String, @NonNull EngineMessage> testKafkaTemplate(
            final ProducerFactory<@NonNull String, @NonNull EngineMessage> testProducerFactory
    ) {
        return new KafkaTemplate<>(testProducerFactory);
    }

}
