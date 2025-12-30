package momomomo.dungeonwalker.wsserver.transport.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.contract.client.ClientRequestProto.ClientRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<@NonNull String, @NonNull ClientRequest> producerFactory(
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
    public KafkaTemplate<@NonNull String, @NonNull ClientRequest> kafkaTemplate(
            final ProducerFactory<@NonNull String, @NonNull ClientRequest> producerFactory
    ) {
        log.debug("---> [CONFIG - Kafka] Creating kafka template");
        return new KafkaTemplate<>(producerFactory);
    }

}
