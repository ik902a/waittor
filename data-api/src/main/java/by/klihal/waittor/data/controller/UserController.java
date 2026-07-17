package by.klihal.waittor.data.controller;

import by.klihal.waittor.common.dto.CreateUserDto;
import by.klihal.waittor.common.dto.UserDto;
import by.klihal.waittor.data.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление учетными записями пользователей")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получить всех пользователей", description = "Возвращает реактивный поток всех зарегистрированных пользователей")
    public Flux<UserDto> findAll() {
        return service.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать пользователя", description = "Принимает данные нового пользователя и возвращает созданный объект")
    public Mono<UserDto> save(@RequestBody CreateUserDto dto) {
        return service.save(dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED) // Сохранил вашу логику HttpStatus.CREATED из хендлера
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по его ID")
    public Mono<UserDto> update(@PathVariable Long id, @RequestBody CreateUserDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя из системы по его ID")
    public Mono<String> delete(@PathVariable Long id) {
        return service.delete(id)
                .then(Mono.just(""));
    }
}
