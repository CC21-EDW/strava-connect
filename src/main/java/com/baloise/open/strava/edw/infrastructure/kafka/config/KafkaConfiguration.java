package com.baloise.open.strava.edw.infrastructure.kafka.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaConfiguration {
    @Value("${edw.kafka.system-id}")
    private String systemId;

    @Value("${edw.kafka.activity.topic-name}")
    private String activityTopicName;

    @Value("${edw.kafka.activity.value-serializer}")
    private String activityValueSerializer;

    @Value("${edw.kafka.schema-registry-url}")
    private String schemaRegistryUrl;
}
