package by.klihal.waittor.data.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public MailProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMovies(String email) {
        kafkaTemplate.send("movies-topic", email);
    }
}
