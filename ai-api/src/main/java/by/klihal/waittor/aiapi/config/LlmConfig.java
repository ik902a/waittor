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
                          Входные данные: JSON-объект, содержащий списки элементов и параметры письма.
                    
                          ЗАДАЧА: Создать лаконичное адаптивное HTML-письмо в синих тонах.
                    
                          ПРАВИЛА ДИЗАЙНА:
                          - Основной цвет (шапка, кнопки): Глубокий синий #1A56DB
                          - Цвет фона письма: Светло-серый #F3F4F6
                          - Цвет карточек/контента: Белый #FFFFFF
                          - Цвет текста: Темно-серый #1F2937
                          - Шрифты: Arial, sans-serif, размер 14px-16px.
                    
                          ПРАВИЛА ВЕРСТКИ:
                          1. Используй ТОЛЬКО табличную верстку (<table>, <tr>, <td>).
                          2. Никаких Flexbox, CSS Grid или внешних стилей.
                          3. ВСЕ стили пиши строго инлайново через атрибут style="...".
                          4. Письмо должно быть адаптивным (макс. ширина 600px, выравнивание по центру).
                    
                          ОТВЕТ: Выведи ТОЛЬКО чистый HTML-код с использованием входных данных. Не шаблон. Без разметки 
                          markdown (без ```html), без вводных слов и без объяснений. Только код.
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
                .temperature(0.2)
                .topP(0.9)
                .build();
    }
}
