package io.example;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

@Service
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<Void> send() {
        var stages = IntStream.range(0, 10).mapToObj(i -> {
            var message = "Message " + i;
            return kafkaTemplate
                    .send("spring-boot-example", message)
                    .thenRun(() -> logger.info("Message sent: {}", message));
        }).toList();

        return CompletableFuture.allOf(
                stages.stream()
                        .map(CompletionStage::toCompletableFuture)
                        .toArray(CompletableFuture[]::new)
        );
    }
}
