package momomomo.dungeonwalker.wsserver.startup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import momomomo.dungeonwalker.wsserver.startup.steps.DungeonWalkerWsServerStepsDef;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SuppressWarnings("java:S2187")
@Slf4j
@DirtiesContext
@Testcontainers
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DungeonWalkerWsServerIntegrationTests {

    @Autowired
    protected ObjectMapper jsonMapper;

    @LocalServerPort
    protected Integer port;

    public static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"))
                    .withExposedPorts(9092)
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false")
                    .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");

    static {
        kafka.setPortBindings(List.of("9092:9092"));
        kafka.start();

        final var bootstrapServers = kafka.getBootstrapServers();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", bootstrapServers);

        try {
            createTopics(bootstrapServers);

        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeycloakContainer keycloak =
            new KeycloakContainer("quay.io/keycloak/keycloak:26.6.1")
                    .withRealmImportFile("keycloak-realm-dungeon-walker.json");

    static {
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(final DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/dungeon-walker-realm");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloak.getAuthServerUrl() + "/realms/dungeon-walker-realm/protocol/openid-connect/certs");
    }

    /**
     * Creates Kafka topics that the tests need.
     * Define your topic names here based on your application's requirements.
     */
    private static void createTopics(final String bootstrapServers) throws ExecutionException, InterruptedException {
        final var adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (final var adminClient = AdminClient.create(adminProps)) {
            final var topics = List.of(
                    new NewTopic("game-engine-consumer-topic", 1, (short) 1),
                    new NewTopic("game-engine-producer-topic", 1, (short) 1)
            );

            final var createResult = adminClient.createTopics(topics);

            // Wait for topic creation to complete
            createResult.all().get();
        }
    }

    protected static String requestToken() {
        final var restTemplate = new RestTemplate();

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final var requestBody = new LinkedMultiValueMap<>();
        requestBody.put("grant_type", Collections.singletonList("password"));
        requestBody.put("client_id", Collections.singletonList("dungeon-walker-api"));
        requestBody.put("username", Collections.singletonList("user1"));
        requestBody.put("password", Collections.singletonList("password1"));

        final var requestEntity = new HttpEntity<>(requestBody, headers);

        final var response = restTemplate.exchange(
                keycloak.getAuthServerUrl() + "/realms/dungeon-walker-realm/protocol/openid-connect/token",
                HttpMethod.POST,
                requestEntity,
                String.class);

        return response.getBody();
    }

    protected <T> T readResourceAsInputStream(
            final String resourcePath,
            final Function<InputStreamReader, T> converter) {
        try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assert resource != null;

            try (final var content = new InputStreamReader(resource)) {
                return converter.apply(content);

            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected JsonNode readResourceAsJson(final String resourcePath) {
        return readResourceAsInputStream(
                resourcePath,
                inputStream -> {
                    try {
                        return jsonMapper.readTree(inputStream);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected String readResourceAsString(final String resourcePath) {
        return readResourceAsInputStream(
                resourcePath,
                inputStream -> {
                    try {
                        return inputStream.readAllAsString();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected void waitFor(final long amount, final TimeUnit timeUnit) {
        try (final var executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.schedule(DungeonWalkerWsServerStepsDef.DoNothing::new, amount, timeUnit);
        }
    }

    protected static class DoNothing implements Runnable {
        @Override
        public void run() {
            // Do nothing
        }
    }

}