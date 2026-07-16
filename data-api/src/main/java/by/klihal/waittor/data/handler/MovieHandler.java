package by.klihal.waittor.data.handler;

import by.klihal.waittor.common.dto.CreatedTorDto;
import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.data.service.DataService;
import by.klihal.waittor.data.service.TorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MovieHandler {

    private final TorService torService;
    private final DataService dataService;

    public MovieHandler(TorService torService, DataService dataService) {
        this.torService = torService;
        this.dataService = dataService;
    }

    public Mono<ServerResponse> showPage(ServerRequest request) {
        String search = request.queryParam("search").orElse("");
        String sortBy = request.queryParam("sort").orElse("id");
        String order = request.queryParam("order").orElse("asc");
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(30);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(torService.findAll(search, sortBy, order, page, size), TorDto.class);
    }

    // Примечание: @Valid и @ModelAttribute в функциональном стиле обрабатываются вручную.
    // Ниже показан пример извлечения данных формы. Для полноценной валидации Spring Validator вызывается вручную.
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CreatedTorDto.class)
                .flatMap(torService::save)
                .flatMap(savedTor -> ServerResponse.status(HttpStatus.CREATED).build());
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(CreatedTorDto.class)
                .flatMap(dto -> torService.update(id, dto))
                .flatMap(updatedTor -> ServerResponse.status(HttpStatus.OK).build());
    }

    public Mono<ServerResponse> checkTorrents(ServerRequest request) {
        return Mono.fromRunnable(dataService::begin)
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return torService.delete(id)
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).build());
    }
}
