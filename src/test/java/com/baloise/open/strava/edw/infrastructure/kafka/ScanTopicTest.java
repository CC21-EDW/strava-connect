package com.baloise.open.strava.edw.infrastructure.kafka;

import com.baloise.open.edw.infrastructure.kafka.Config;
import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

@EnableKafka
@EnableKafkaStreams
@Slf4j
@Disabled
public class ScanTopicTest extends AbstractKafkaTest {

    private static final String TOPIC_NAME = "lz.edw.strava-connect.activity";

    @Test
    @Disabled("Currently turned of as we will invest time in kafka streams later")
    public void readPartition() {
        KafkaConsumer<String, ActivityDto> consumer = new KafkaConsumer<>(getConsumerProperties());

        Set<TopicPartition> topicPartitions = getTopicPartition(consumer, TOPIC_NAME);
        consumer.assign(topicPartitions);

        ArrayList<ActivityDto> activities = new ArrayList<>();
        consumer.seekToBeginning(topicPartitions);
        ConsumerRecords<String, ActivityDto> records = consumer.poll(Duration.ofDays(1));
        records.forEach((record) -> {
            try {
                ActivityDto activity = record.value();
                log.info("Record: {}", activity);
                activities.add(activity);
            } catch (Throwable e) {
                log.error("Issue converting AVRO", e);
                Assertions.fail("An exception occurred - please look into console.");
            }
        });

        //provide a clear
        for (ActivityDto currentActivity : activities) {
            log.info("Entity: {}", currentActivity);
        }
    }

    /**
     * Provides the collection of topic partitions we need to brows a topic.
     *
     * @param consumer  the consumer we use to access the topic.
     * @param topicName the topic we want to access.
     * @return the collection, must not be null.
     */
    private Set<TopicPartition> getTopicPartition(KafkaConsumer<?, ?> consumer, String topicName) {
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
        return partitionInfos
                .stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
                .collect(Collectors.toSet());
    }

    @Test
    @Disabled("Currently turned of as we will invest time in kafka streams later")
    public void checkActivityTopic() {
        StreamsBuilder builder = new StreamsBuilder();

        StoreBuilder<KeyValueStore<String, ActivityDto>> store =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("inmemory"),
                        Serdes.String(),
                        getActivityAvroSerde((String) getProducerProperties().get(Config.SCHEMA_SERVER_CONFIG_KEY)));
        builder.addStateStore(store);

        KStream<String, ActivityDto> sourceStream = builder.stream(TOPIC_NAME, Consumed.with(Serdes.String(),
                getActivityAvroSerde((String) getProducerProperties().get(Config.SCHEMA_SERVER_CONFIG_KEY))));
        sourceStream.print(Printed.toSysOut());

        KTable<String, ActivityDto> textLinesTable =
                builder.table(TOPIC_NAME, Consumed.with(Serdes.String(),
                        getActivityAvroSerde((String) getProducerProperties().get(Config.SCHEMA_SERVER_CONFIG_KEY))));

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, getProducerProperties());
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Provides the specific AVRO serializer/ deserializer (SerDe) we need using KafkaStreams.
     *
     * @param schemaUrl URL we need to access the schema.
     * @return the specific AVRO serializer/ deserializer.
     */
    private SpecificAvroSerde<ActivityDto> getActivityAvroSerde(String schemaUrl) {
        SpecificAvroSerde<ActivityDto> movieAvroSerde = new SpecificAvroSerde<>();

        Map<String, String> serdeConfig;
        serdeConfig = new HashMap<>();
        serdeConfig.put(SCHEMA_REGISTRY_URL_CONFIG, schemaUrl);
        movieAvroSerde.configure(
                serdeConfig, false);
        return movieAvroSerde;
    }


    /**
     * Provides the configuration we use specifically for producing events.
     *
     * @return the properties ready to be used.
     */
    private Properties getProducerProperties() {

        Properties properties = getTestContainerProperties();
        properties.put("application.id", "test-query");
        return properties;
    }

    /**
     * Provides the configuration we use specifically for consuming events.
     *
     * @return the properties ready to be used.
     */
    private Properties getConsumerProperties() {
        Properties properties = getTestContainerProperties();
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return properties;
    }
}
