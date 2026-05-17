package by.klihal.waittor.data.producer;

import by.klihal.waittor.common.dto.Movie;
import com.google.common.collect.Multimap;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailProducer {

    private final KafkaTemplate<String, Multimap<String, Movie>> kafkaTemplate;

    public MailProducer(KafkaTemplate<String, Multimap<String, Movie>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMovies(Multimap<String, Movie> tables) {
        kafkaTemplate.send("movies-topic", tables);
    }
}
