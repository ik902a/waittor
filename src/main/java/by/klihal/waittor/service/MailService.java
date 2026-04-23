package by.klihal.waittor.service;

import by.klihal.waittor.model.Movie;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailService {

    private final JavaMailSender emailSender;

    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendLetter(String tables) {
        String letter = buildLetter(tables);
        System.out.println("Letter\n" + letter);
        try {
            sendSimpleEmail(letter);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSimpleEmail(String letter) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo("ik902a@gmail.com");
        helper.setSubject("New movies");
        helper.setText(letter, true);
        emailSender.send(message);
    }

    private String buildLetter(String tables) {
        String htmlHeader = """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333;">
                        <h1 style="color: #007bff;">Привет, найдены новые раздачи!</h1>
                        <hr>""";
        String htmlFotter = """
                    </body>
                </html>""";
        return htmlHeader + tables + htmlFotter;
    }

    public String buildTable(String name, List<Movie> movies) {
        String htmlTableHeader = String.format("""
                <h3 style="color: #007bff;">%s</h3>
                <table style="width: 100%%; border-collapse: collapse; margin: 20px 0; font-family: Arial, sans-serif;">
                    <tbody>
                """, name);
        String htmlTableFooter = """
                    </tbody>
                </table>
                <hr>
                """;
        String middlePart = movies.stream()
                .map(movie -> String.format("""
                        <tr>
                               <td style="padding: 12px; border: 1px solid #ddd;">%s</td>
                               <td style="padding: 12px; border: 1px solid #ddd;">%s</td>
                               <td style="padding: 12px; border: 1px solid #ddd;">%s</td>
                        </tr>""", movie.title(), movie.size(), movie.link()))
                .collect(Collectors.joining("\n"));

        return htmlTableHeader + middlePart + htmlTableFooter;
    }
}
