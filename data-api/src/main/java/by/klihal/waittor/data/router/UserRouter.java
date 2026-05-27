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
                .path("/users", builder -> builder
                        .GET("", accept(MediaType.APPLICATION_JSON), handler::findAll)
                        .POST("/save" , accept(MediaType.APPLICATION_JSON), handler::save)
                        .PUT("/update/{id:\\d+}", accept(MediaType.APPLICATION_JSON), handler::update)
                        .DELETE("/delete/{id}", handler::delete))
                .build();
    }
}
