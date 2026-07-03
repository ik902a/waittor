package by.klihal.waittor.aiapi.service;

import by.klihal.waittor.common.dto.GroupedMovie;
import by.klihal.waittor.common.dto.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(EmailGeneratorService.class);
    private static final String USER_PROMPT_TEMPLATE = "Вот JSON-данные:\n";

    @Qualifier("htmlClient")
    private final ChatClient chatHtmlClient;
    private final ObjectMapper objectMapper;

    public EmailGeneratorService(ChatClient chatHtmlClient, ObjectMapper objectMapper) {
        this.chatHtmlClient = chatHtmlClient;
        this.objectMapper = objectMapper;
    }

    public String generateHtmlEmail(List<GroupedMovie> groupedMovies) {
        String data = groupedMovies.stream()
                .map(gm -> makeJsonFragments(gm.movies()))
                .collect(Collectors.joining("\n"));

        return askLlm(USER_PROMPT_TEMPLATE + data);
    }

    private String makeJsonFragments(List<Movie> movies) {
        return movies.stream()
                .map(this::makeJsonFragment)
                .collect(Collectors.joining("\n"));
    }

    private String makeJsonFragment(Movie movie) {
        try {
            return objectMapper.writeValueAsString(movie);
        } catch (JsonProcessingException e) {
            log.error("Ошибка при выполнении парсинга объекта в строку: {}", e.getMessage(), e);
            return "";
        }
    }

    private String askLlm(String userPrompt) {
        System.out.println(userPrompt);
        String llmAnswer = chatHtmlClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
        return llmAnswer != null ?
                llmAnswer
                        .replaceAll("```html", "")
                        .replaceAll("```", "")
                        .trim()
                : "";
    }
}
