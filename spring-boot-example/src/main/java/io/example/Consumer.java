package io.example;

import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = "spring-boot-example")
    public void listen(String message) {
        logger.info("Message received: {}", message);
    }
}
