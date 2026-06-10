package by.klihal.waittor.data.handler;

import by.klihal.waittor.common.dto.CreatedTorDto;
import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.common.enums.TorrentType;
import by.klihal.waittor.data.service.DataService;
import by.klihal.waittor.data.service.TorService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@Component
public class TorHandler {

    private final TorService torService;
    private final DataService dataService;

    public TorHandler(TorService torService, DataService dataService) {
        this.torService = torService;
        this.dataService = dataService;
    }

    // GET /tors
    public Mono<ServerResponse> showPage(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(torService.findAll(), TorDto.class);
    }

    // POST /tors/add
    // Примечание: @Valid и @ModelAttribute в функциональном стиле обрабатываются вручную.
    // Ниже показан пример извлечения данных формы. Для полноценной валидации Spring Validator вызывается вручную.
    public Mono<ServerResponse> addTor(ServerRequest request) {
        return request.bodyToMono(CreatedTorDto.class)
                .flatMap(tor -> {
                    return torService.save(tor);
                })
                .flatMap(savedTor -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        // Возвращаем только фрагмент таймлифа
                        .render("index :: tor-table", Map.of("tors", torService.findAll())));
    }

    // GET /tors/check
    public Mono<ServerResponse> checkTorrents(ServerRequest request) {
        return Mono.fromRunnable(dataService::begin)
                .then(ServerResponse.ok().bodyValue(""));
    }

    // DELETE /tors/delete/{id}
    public Mono<ServerResponse> deleteTor(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return torService.delete(id)
                .then(ServerResponse.ok().bodyValue(""));
    }
}
