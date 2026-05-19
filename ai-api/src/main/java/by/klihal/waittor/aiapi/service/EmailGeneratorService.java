package by.klihal.waittor.aiapi.service;

import by.klihal.waittor.common.dto.Movie;
import com.google.common.collect.Multimap;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.Map;

@Service
public class EmailGeneratorService {

    private static final String PROMPT_TEMPLATE = "Вот JSON-данные для размещения в письме: ";

    @Qualifier("htmlClient")
    private final ChatClient chatHtmlClient;
    private final ObjectMapper objectMapper;

    public EmailGeneratorService(ChatClient chatHtmlClient, ObjectMapper objectMapper) {
        this.chatHtmlClient = chatHtmlClient;
        this.objectMapper = objectMapper;
    }

    public String generateHtmlEmail(Multimap<String, Movie> multiMovies) {
        Map<String, Collection<Movie>> standardMap = multiMovies.asMap();
        String jsonContext = objectMapper.writeValueAsString(standardMap);

        String userText = PROMPT_TEMPLATE + jsonContext;

        return chatHtmlClient
                .prompt()
                .user(userText)
                .call()
                .content();
    }
}
