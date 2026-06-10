package by.klihal.waittor.data.router;

import by.klihal.waittor.data.handler.TorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TorRouter {

    @Bean
    public RouterFunction<ServerResponse> torRoutes(TorHandler handler) {
        return route()
                .path("/api/tors", builder -> builder
                        .GET("", accept(MediaType.APPLICATION_JSON), handler::showPage)
                        .POST("/add", contentType(MediaType.APPLICATION_JSON), handler::addTor)
                        .GET("/check", handler::checkTorrents)
                        .DELETE("/delete/{id}", handler::deleteTor)
                )
                .build();
    }
}
