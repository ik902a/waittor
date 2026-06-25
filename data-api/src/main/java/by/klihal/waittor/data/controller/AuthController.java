package by.klihal.waittor.data.controller;

import by.klihal.waittor.common.dto.AuthenticationRequest;
import by.klihal.waittor.common.dto.AuthenticationResponse;
import by.klihal.waittor.common.dto.CreateUserDto;
import by.klihal.waittor.data.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request,
                                                              ServerWebExchange exchange) {
        return service.login(request, exchange);
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthenticationResponse>> refresh(ServerWebExchange exchange) {
        return service.refresh(exchange);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        return service.logout(exchange);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(@Valid @RequestBody CreateUserDto userDto) {
        return service.register(userDto)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).build());
    }
}
