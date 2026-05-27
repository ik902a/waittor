package by.klihal.waittor.data.handler;

import by.klihal.waittor.common.dto.CreateUserDto;
import by.klihal.waittor.common.dto.UserDto;
import by.klihal.waittor.data.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private final UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), UserDto.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CreateUserDto.class)
                .flatMap(service::save)
                .flatMap(dto -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CreateUserDto.class)
                .flatMap(dto -> service.update(id, dto))
                .flatMap(dto -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
//        switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return service.delete(id)
                .then(ServerResponse.ok().bodyValue(""));
    }
}
