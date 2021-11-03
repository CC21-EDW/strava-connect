package com.baloise.open.strava.edw.infrastructure.kafka;

import com.baloise.open.edw.infrastructure.kafka.Producer;
import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import com.baloise.open.strava.client.model.ActivityTypeDto;
import com.baloise.open.strava.client.model.SummaryActivityDto;
import com.baloise.open.strava.edw.infrastructure.kafka.mapper.ActivityDtoMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Properties;

/**
 * Test disabled to be able to run from IDE but have no dependencies in CI/ CD.
 */
@Disabled
public class PushActivityEventIntegrationTest {
    /**
     * Provides the TEST topic in order not to interfere with production.
     */
    private static final String TOPIC_NAME = "lz.edw.test.strava-connect.activity";

    /**
     * Provides the TEST system ID to be able to distinguish things.
     */
    private static final String SYSTEM_ID = "strava-connect-test";

    @Test
    public void pushActivity() throws Exception {
        SummaryActivityDto activity = new SummaryActivityDto();
        activity.distance(13546.0F);
        activity.movingTime(6200);
        activity.elapsedTime(6498);
        activity.startDate(OffsetDateTime.now());
        activity.timezone("(GMT+01:00) Europe/Vienna");
        activity.type(ActivityTypeDto.HIKE);

        Properties specificProperties = new Properties();
        specificProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        specificProperties.put("schema.registry.url", "http://localhost:9012/");

        ActivityDto kafkaActivityDto = ActivityDtoMapper.INSTANCE.map(activity);
        //push
        Producer activityProducer = new Producer(specificProperties, TOPIC_NAME, SYSTEM_ID);
        activityProducer.pushEvent(TOPIC_NAME, "tbd-id", kafkaActivityDto);
    }
}
