package by.klihal.waittor.notification.consumer;

import by.klihal.waittor.common.dto.Movie;
import by.klihal.waittor.notification.service.MailService;
import com.google.common.collect.Multimap;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MailConsumer {

    private final MailService mailService;

    public MailConsumer(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = "movies-topic", groupId = "movies-group")
    public void consumeMovies(String email) {
        System.out.println("Получено новое событие");
        mailService.sendLetter(email);
    }
}
