package io.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.io.IOException;
import java.util.Properties;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        runProducer();
        runConsumer();
        runStreams();
    }

    private static void runProducer() throws ExecutionException, InterruptedException, TimeoutException {
        var stringProducerProps = new Properties();
        stringProducerProps.putAll(config.getCommonProducerProperties());
        stringProducerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        stringProducerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // wrap in a try for cleanup as KafkaProducer is a Closeable 
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(stringProducerProps)) {
            IntStream.range(0, 10).forEach(i -> {
                System.out.println("Sending message " + i);
                try {
                    producer.send(new ProducerRecord<>("example-topic", "" + i, "Message " + i)).get(100, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void runConsumer() {
        Properties stringConsumerProps = new Properties();
        stringConsumerProps.putAll(config.getCommonConsumerProperties());
        stringConsumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        stringConsumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(stringConsumerProps);

        try (consumer) {
            consumer.subscribe(Collections.singletonList("example-topic"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            records.forEach(record -> System.out.println("Consumed: " + record.value()));
        }
    }

    private static void runStreams() {
        Properties streamsProps = new Properties();
        streamsProps.putAll(config.getCommonStreamProperties());
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.Serdes$StringSerde");
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.Serdes$StringSerde");

        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream("example-topic")
                .mapValues((key, value) -> {
                    System.out.println("Processing message " + value);
                    return value;
                })
                .to("output-topic");

        KafkaStreams streams = new KafkaStreams(builder.build(), streamsProps);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static final Config config;

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
