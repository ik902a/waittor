package by.klihal.waittor.data.router;

import by.klihal.waittor.data.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route()
                .path("/api/users", builder -> builder
                        .GET("/api", accept(MediaType.APPLICATION_JSON), handler::findAll)
                        .POST("/api/save" , accept(MediaType.APPLICATION_JSON), handler::save)
                        .PUT("/api/update/{id:\\d+}", accept(MediaType.APPLICATION_JSON), handler::update)
                        .DELETE("/api/delete/{id}", handler::delete))
                .build();
    }
}
