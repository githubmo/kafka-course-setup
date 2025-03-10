package io.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Config {

    private final Properties commonProperties;
    private final Properties consumerProperties;
    private final Properties producerProperties;
    private final Properties streamProperties;

    public Config() throws IOException {
        var commonRootPath = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath();

        var commonConfigPath = commonRootPath + "common.properties";
        commonProperties = new Properties();
        commonProperties.load(new FileInputStream(commonConfigPath));

        var consumerConfigPath = commonRootPath + "consumer.properties";
        consumerProperties = new Properties();
        consumerProperties.load(new FileInputStream(consumerConfigPath));
        consumerProperties.putAll(commonProperties);

        var producerConfigPath = commonRootPath + "producer.properties";
        producerProperties = new Properties();
        producerProperties.load(new FileInputStream(producerConfigPath));
        producerProperties.putAll(commonProperties);

        var streamConfigPath = commonRootPath + "stream.properties";
        streamProperties = new Properties();
        streamProperties.load(new FileInputStream(streamConfigPath));
        streamProperties.putAll(commonProperties);
    }

    public Properties getCommonConsumerProperties() {
        return consumerProperties;
    }

    public Properties getCommonProducerProperties() {
        return producerProperties;
    }

    public Properties getCommonStreamProperties() {
        return streamProperties;
    }
}
