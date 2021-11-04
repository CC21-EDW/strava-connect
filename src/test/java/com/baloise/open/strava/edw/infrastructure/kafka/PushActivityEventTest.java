package com.baloise.open.strava.edw.infrastructure.kafka;

import com.baloise.open.edw.infrastructure.kafka.Config;
import com.baloise.open.edw.infrastructure.kafka.Producer;
import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import com.baloise.open.strava.client.model.ActivityTypeDto;
import com.baloise.open.strava.client.model.SummaryActivityDto;
import com.baloise.open.strava.edw.infrastructure.kafka.mapper.ActivityDtoMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.OffsetDateTime;
import java.util.Properties;

/**
 * Test disabled to be able to run from IDE but have no dependencies in CI/ CD.
 */
@Testcontainers
public class PushActivityEventTest {
    /**
     * Provides the TEST topic in order not to interfere with production.
     */
    private static final String TOPIC_NAME = "lz.edw.test.strava-connect.activity";

    /**
     * Provides the TEST system ID to be able to distinguish things.
     */
    private static final String SYSTEM_ID = "strava-connect-test";

    /**
     * Provides the TEST system ID to be able to distinguish things.
     */
    private static final String KAFKA_DOCKER_IMG = "confluentinc/cp-kafka:5.3.2";

    @Container
    public static KafkaContainer kafkaTestContainer = new KafkaContainer(DockerImageName.parse(KAFKA_DOCKER_IMG));

    private Properties getTestContainerProperties() {
        Properties properties = new Properties();
        properties.put(Config.KAFKA_SERVER_CONFIG_KEY, kafkaTestContainer.getBootstrapServers());
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaTestContainer.getBootstrapServers());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        properties.put(Config.SCHEMA_SERVER_CONFIG_KEY, "http://localhost:9012/");
        return properties;
    }

    @Test
    public void pushActivity() throws Exception {
        SummaryActivityDto activity = new SummaryActivityDto();
        activity.distance(13546.0F);
        activity.movingTime(6200);
        activity.elapsedTime(6498);
        activity.startDate(OffsetDateTime.now());
        activity.timezone("(GMT+01:00) Europe/Vienna");
        activity.type(ActivityTypeDto.HIKE);

        ActivityDto kafkaActivityDto = ActivityDtoMapper.INSTANCE.map(activity);
        //push
        Producer activityProducer = new Producer(getTestContainerProperties(), TOPIC_NAME, SYSTEM_ID);
        activityProducer.pushEvent(TOPIC_NAME, "tbd-id", kafkaActivityDto);
    }
}
