package by.klihal.waittor.data.controller;

import by.klihal.waittor.common.dto.AuthenticationRequest;
import by.klihal.waittor.common.dto.AuthenticationResponse;
import by.klihal.waittor.common.dto.CreateUserDto;
import by.klihal.waittor.data.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Tag(name = "Авторизация", description = "Управление авторизацией и регистрацией")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @Operation(summary = "Залогиниться")
    public Mono<ResponseEntity<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request,
                                                              ServerWebExchange exchange) {
        return service.login(request, exchange);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Получить обновленный токен")
    public Mono<ResponseEntity<AuthenticationResponse>> refresh(ServerWebExchange exchange) {
        return service.refresh(exchange);
    }

    @PostMapping("/logout")
    @Operation(summary = "Выйти")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        return service.logout(exchange);
    }

    @PostMapping("/register")
    @Operation(summary = "Зарегистрироваться")
    public Mono<ResponseEntity<Void>> register(@Valid @RequestBody CreateUserDto userDto) {
        return service.register(userDto)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).build());
    }
}
