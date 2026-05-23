package by.klihal.waittor.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MailService {

    private final JavaMailSender emailSender;

    @Value("${tor.email.target}")
    private String targetEmail;

    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendLetter(String letter) {
        try {
            sendSimpleEmail(letter);
            System.out.println("Letter was send [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "]");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSimpleEmail(String letter) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(targetEmail);
        helper.setSubject("New movies");
        helper.setText(letter, true);
        emailSender.send(message);
    }
}
