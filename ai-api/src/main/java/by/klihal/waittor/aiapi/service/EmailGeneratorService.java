package by.klihal.waittor.aiapi.service;

import by.klihal.waittor.common.dto.GroupedMovie;
import by.klihal.waittor.common.dto.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailGeneratorService {

    private static final String USER_PROMPT_TEMPLATE = "Вот JSON-данные для размещения в письме: ";

    @Qualifier("htmlClient")
    private final ChatClient chatHtmlClient;
    private final ObjectMapper objectMapper;

    public EmailGeneratorService(ChatClient chatHtmlClient, ObjectMapper objectMapper) {
        this.chatHtmlClient = chatHtmlClient;
        this.objectMapper = objectMapper;
    }

    public String generateHtmlEmail(List<GroupedMovie> groupedMovies) {
        Map<String, List<Movie>> standardMap = groupedMovies.stream()
                .collect(Collectors.toMap(GroupedMovie::name,
                        GroupedMovie::movies));
        String jsonContext = null;
        try {
            jsonContext = objectMapper.writeValueAsString(standardMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String userText = USER_PROMPT_TEMPLATE + jsonContext;
        System.out.println("PROMPT-" + userText);
        return chatHtmlClient
                .prompt()
                .user(userText)
                .call()
                .content();
    }
}
