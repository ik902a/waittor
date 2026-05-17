package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.Movie;
import by.klihal.waittor.data.producer.MailProducer;
import com.google.common.collect.Multimap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final MailProducer mailProducer;

    public MailService(MailProducer mailProducer) {
        this.mailProducer = mailProducer;
    }

    @Async
    public void sendLetter(Multimap<String, Movie> tables) {
        if (!tables.isEmpty()) {
            System.out.println("Send movies into KAFKA");
            mailProducer.sendMovies(tables);
        }
    }
}
