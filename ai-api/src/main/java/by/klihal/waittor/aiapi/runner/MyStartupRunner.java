package by.klihal.waittor.aiapi.runner;

import by.klihal.waittor.aiapi.service.EmailGeneratorService;
import by.klihal.waittor.common.dto.Movie;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyStartupRunner implements CommandLineRunner {

    private final EmailGeneratorService emailGeneratorService;

    public MyStartupRunner(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ваш метод или логика
        startMyMethod();
    }

    private void startMyMethod() {
        Multimap<String, Movie> multiMovies = ArrayListMultimap.create();
        multiMovies.put("Паук Нуар", new Movie("Паук Нуар", "4Gb", "https://ollama.com/search?c=cloud&page=2&q=coder"));
        multiMovies.put("Паук Нуар", new Movie("Паук Нуар", "8Gb", "https://ollama.com/library/qwen3-next"));

        String email = emailGeneratorService.generateHtmlEmail(multiMovies);
        System.out.println("EMAIL:\n" + email);
    }
}
