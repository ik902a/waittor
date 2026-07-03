package by.klihal.waittor.aiapi.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

    private static final PromptTemplate SYSTEM_PROMPT_HTML_CLIENT = new PromptTemplate(
            """
                             Ты — специализированный робот-верстальщик Email-писем.
                                  Входные данные: JSON-объекты.
                                  ЗАДАЧА: Создать лаконичную готовую к использованию HTML страницу в светло синих тонах, макс. ширина 600px, выравнивание по центру.
                                  ВСЕ стили пиши строго через атрибут style="...".
                                  ВЫВЕДИ СРАЗУ ГОТОВЫЙ HTML. Никаких пояснений.
                                  СТРОГО ЗАПРЕЩЕНО использовать шаблонизатор.
                    """
    );

    @Bean("htmlClient")
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors()
                .defaultOptions(getOllamaOptions())
                .defaultSystem(SYSTEM_PROMPT_HTML_CLIENT.render())
                .build();
    }

    private ChatOptions getOllamaOptions() {
        return OllamaChatOptions.builder()
                .model("qwen2.5-coder:1.5b")
                .temperature(0.4)
                .topP(0.9)
                .build();
    }
}
