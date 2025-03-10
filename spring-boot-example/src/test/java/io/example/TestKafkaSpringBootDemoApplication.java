package io.example;

import org.springframework.boot.SpringApplication;

public class TestKafkaSpringBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(KafkaSpringBootDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
