package momomomo.dungeonwalker.engine.transport.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import momomomo.dungeonwalker.contract.engine.EngineMessageProto.EngineMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<@NonNull String, @NonNull ClientRequest> consumerFactory(
            @Value("${spring.kafka.consumer.bootstrap-servers}") final String bootstrapServers,
            @Value("${spring.kafka.consumer.key-deserializer}") final Class<?> keyDeserializer,
            @Value("${spring.kafka.consumer.value-deserializer}") final Class<?> valueDeserializer
    ) {
        log.debug("---> [KAFKA - Config] Creating consumer factory: {}, {}, {}",
                bootstrapServers, keyDeserializer, valueDeserializer);
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer));
    }

    @Bean
    public KafkaListenerContainerFactory<@NonNull ConcurrentMessageListenerContainer<@NonNull String, @NonNull ClientRequest>>
    kafkaListenerContainerFactory(
            @Value("${spring.kafka.consumer.concurrency}") final int concurrency,
            @Value("${spring.kafka.consumer.poll-timeout}") final long pollTimeout,
            final ConsumerFactory<@NonNull String, @NonNull ClientRequest> consumerFactory
    ) {
        log.debug("---> [KAFKA - Config] Creating kafka listener container factory: {}, {}, {}",
                concurrency, pollTimeout, consumerFactory);
        final var factory = new ConcurrentKafkaListenerContainerFactory<@NonNull String, @NonNull ClientRequest>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public ProducerFactory<@NonNull String, @NonNull EngineMessage> producerFactory(
            @Value("${spring.kafka.producer.bootstrap-servers}") final String bootstrapServers,
            @Value("${spring.kafka.producer.key-serializer}") final Class<?> keySerializer,
            @Value("${spring.kafka.producer.value-serializer}") final Class<?> valueSerializer,
            @Value("${spring.kafka.producer.acks}") final String acks,
            @Value("${spring.kafka.producer.retries}") final int retries
    ) {
        log.debug("---> [CONFIG - Kafka] Creating producer factory: {}, {}, {}, {}, {}"
                , bootstrapServers, keySerializer, valueSerializer, acks, retries);
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer,
                ProducerConfig.ACKS_CONFIG, acks,
                ProducerConfig.RETRIES_CONFIG, retries));
    }

    @Bean
    public KafkaTemplate<@NonNull String, @NonNull EngineMessage> kafkaTemplate(
            final ProducerFactory<@NonNull String, @NonNull EngineMessage> producerFactory
    ) {
        log.debug("---> [CONFIG - Kafka] Creating kafka template");
        return new KafkaTemplate<>(producerFactory);
    }

}
