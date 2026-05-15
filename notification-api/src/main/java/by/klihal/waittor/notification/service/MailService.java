package by.klihal.waittor.notification.service;

import by.klihal.waittor.common.dto.Movie;
import com.google.common.collect.Multimap;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${tor.email.target}")
    private String targetEmail;

    public MailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendLetter(Multimap<String, Movie> tables) {
        try {
            sendSimpleEmail(tables);
            System.out.println("Letter was send [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "]");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSimpleEmail(Multimap<String, Movie> movies) throws MessagingException {
        Context context = new Context();
        context.setVariable("movies", movies);
        String letter = templateEngine.process("email-template", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(targetEmail);
        helper.setSubject("New movies");
        helper.setText(letter, true);
        emailSender.send(message);
    }
}
