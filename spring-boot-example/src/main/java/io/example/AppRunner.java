package io.example;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AppRunner implements CommandLineRunner {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AppRunner.class);

    private final ProducerService producerService;

    public AppRunner(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Sending messages..");
        producerService.send().thenRun(() -> {
            logger.info("Messages sent");
        }).join();
    }
}
