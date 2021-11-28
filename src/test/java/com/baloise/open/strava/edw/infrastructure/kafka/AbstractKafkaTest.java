package com.baloise.open.strava.edw.infrastructure.kafka;

import com.baloise.open.edw.infrastructure.kafka.Config;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

@Testcontainers
public class AbstractKafkaTest {

  @Container
  public static KafkaContainer kafkaTestContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.3.2"));

  protected Properties getTestContainerProperties() {
    final Properties properties = new Properties();
    properties.put(Config.KAFKA_SERVER_CONFIG_KEY, kafkaTestContainer.getBootstrapServers());
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaTestContainer.getBootstrapServers());
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
    properties.put(Config.SCHEMA_SERVER_CONFIG_KEY, "mock://localhost");
    return properties;
  }
}
