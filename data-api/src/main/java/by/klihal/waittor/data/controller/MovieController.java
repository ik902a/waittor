package by.klihal.waittor.data.controller;

import by.klihal.waittor.common.dto.CreatedTorDto;
import by.klihal.waittor.common.dto.PageResponse;
import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.data.service.DataService;
import by.klihal.waittor.data.service.TorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
@Tag(name = "Фильмы (Торренты)", description = "Управление торрентами для фильмов и запуск синхронизации")
public class MovieController {

    private final TorService torService;
    private final DataService dataService;

    public MovieController(TorService torService, DataService dataService) {
        this.torService = torService;
        this.dataService = dataService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получить страницу торрентов", description = "Возвращает список торрентов с фильтрацией, сортировкой и пагинацией")
    public Mono<PageResponse<TorDto>> showPage(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return torService.findAll(search, sort, order, page, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Сохранить торрент", description = "Принимает данные нового торрента и сохраняет его в системе")
    public Mono<Void> save(@RequestBody CreatedTorDto dto) {
        return torService.save(dto).then();
    }

    @PutMapping(value = "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить торрент", description = "Обновляет данные существующего торрента по его ID")
    public Mono<Void> update(@PathVariable Long id, @RequestBody CreatedTorDto dto) {
        return torService.update(id, dto).then();
    }

    @GetMapping("/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Запустить проверку торрентов", description = "Асинхронно запускает процесс проверки торрентов через DataService")
    public Mono<Void> checkTorrents() {
        return Mono.fromRunnable(dataService::begin);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить торрент", description = "Удаляет торрент из системы по его ID")
    public Mono<Void> delete(@PathVariable Long id) {
        return torService.delete(id);
    }
}
