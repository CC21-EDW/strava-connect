package com.baloise.open.strava.edw.infrastructure.kafka;

import com.baloise.open.edw.infrastructure.kafka.Producer;
import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import com.baloise.open.strava.client.model.ActivityTypeDto;
import com.baloise.open.strava.client.model.SummaryActivityDto;
import com.baloise.open.strava.edw.infrastructure.kafka.mapper.ActivityDtoMapper;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

@Testcontainers
public class PushActivityEventTest extends AbstractKafkaTest {
    /**
     * Provides the TEST topic in order not to interfere with production.
     */
    private static final String TOPIC_NAME = "lz.edw.test.strava-connect.activity";

    /**
     * Provides the TEST system ID to be able to distinguish things.
     */
    private static final String SYSTEM_ID = "strava-connect";


    @Test
    public void pushActivity() throws Exception {
        SummaryActivityDto activity = new SummaryActivityDto();
        activity.name("A hilarious walk");
        activity.distance(13546.0F);
        activity.movingTime(6200);
        activity.elapsedTime(6498);
        activity.startDate(OffsetDateTime.now());
        activity.timezone("(GMT+01:00) Europe/Vienna");
        activity.type(ActivityTypeDto.HIKE);

        ActivityDto kafkaActivityDto = ActivityDtoMapper.INSTANCE.map(activity);
        //push
        Producer activityProducer = Producer.create(getTestContainerProperties(), TOPIC_NAME, SYSTEM_ID);
        activityProducer.pushEvent(kafkaActivityDto);
    }
}
