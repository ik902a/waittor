package by.klihal.waittor.data.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebFluxOpenApiConfig {

    // Базовое описание API
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Waittor API")
                        .version("1.0")
                        .description("Документация API для приложения Waittor"))
                // 1. Добавляем глобальное требование авторизации для всех эндпоинтов
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 2. Описываем, как именно устроен механизм авторизации (Bearer JWT)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Введите ваш JWT токен в формате: Bearer <токен>")));
    }

    @Bean
    public RouterFunction<ServerResponse> swaggerResourcesRouter() {
        return RouterFunctions
                .resources("/swagger-ui/**", new ClassPathResource("META-INF/resources/webjars/swagger-ui/"));
    }
}
