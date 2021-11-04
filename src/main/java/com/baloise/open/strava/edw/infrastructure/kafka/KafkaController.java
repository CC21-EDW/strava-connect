package com.baloise.open.strava.edw.infrastructure.kafka;

import com.baloise.open.edw.infrastructure.kafka.Producer;
import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import com.baloise.open.strava.edw.infrastructure.kafka.config.KafkaConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class KafkaController {

    @Autowired
    private KafkaConfiguration configuration;

    private Producer activityProducer;

    @PostConstruct
    public void initActivityProducer() throws ExecutionException, InterruptedException {
        Properties specificProperties = new Properties();
        specificProperties.put("value.serializer", configuration.getActivityValueSerializer());
        specificProperties.put("schema.registry.url", configuration.getSchemaRegistryUrl());

        activityProducer = new Producer(specificProperties, configuration.getActivityTopicName(), configuration.getSystemId());
    }

    public void pushActivities(List<ActivityDto> activities) {
        for (ActivityDto currentActivity : activities) {
            activityProducer.pushEvent(configuration.getActivityTopicName(), "tbd-id", currentActivity);
        }
    }

    @PostConstruct
    public void connectToWorkflow() {
        log.info("Connected system with ID '{}' to workflow.", configuration.getSystemId());
    }

    @PreDestroy
    public void disconnectFromWorkflow() {
        log.info("Disconnected system with ID '{}' from workflow.", configuration.getSystemId());
    }
}