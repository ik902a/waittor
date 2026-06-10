package by.klihal.waittor.data.controller;

import by.klihal.waittor.common.dto.AuthenticationRequest;
import by.klihal.waittor.common.dto.AuthenticationResponse;
import by.klihal.waittor.data.security.JwtUtils;
import by.klihal.waittor.data.security.RefreshTokenService;
import by.klihal.waittor.data.service.UserService;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // В WebFlux используется реактивный менеджер аутентификации
    private final ReactiveAuthenticationManager authenticationManager;
    private final ReactiveUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService; // Предполагается, что он возвращает Mono/Flux
    private final UserService userService;

    public AuthController(ReactiveAuthenticationManager authenticationManager,
                          ReactiveUserDetailsService userDetailsService,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request,
            ServerWebExchange exchange
    ) {
        System.out.println("Login>>>>>");
        // 1. Проверяем логин и пароль через реактивный менеджер
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.username(), request.password())
                )
                // 2. Загружаем данные пользователя
                .flatMap(auth -> userDetailsService.findByUsername(request.username()))
                // 3. Генерируем токены (предполагаем, что создание рефреш-токена в БД — это реактивная операция, возвращающая Mono<String>)
                .flatMap(user -> {
                    String accessToken = jwtUtils.generateAccessToken(user);

                    return refreshTokenService.createRefreshToken(user.getUsername()) // Должен возвращать Mono<String>
                            .map(refreshToken -> {

                                // 4. Создаем безопасную куку для Refresh Token
                                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                                        .httpOnly(true)
                                        .secure(false) // Для localhost можно выставить false
                                        .path("/api/auth/refresh")
                                        .maxAge(Duration.ofDays(7))
                                        .sameSite("Strict")
                                        .build();

                                // Добавляем куку в реактивный ответ
                                exchange.getResponse().addCookie(refreshCookie);

                                // Возвращаем Access Token в теле ответа
                                return ResponseEntity.ok(new AuthenticationResponse(accessToken));
                            });
                });
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthenticationResponse>> refresh(ServerWebExchange exchange) {
        // 1. Извлекаем куку из реактивного запроса
        HttpCookie refreshHttpCookie = exchange.getRequest().getCookies().getFirst("refreshToken");
        System.out.println(refreshHttpCookie);

        if (refreshHttpCookie == null) {
            return Mono.error(new RuntimeException("Refresh token is missing"));
        }

        String refreshToken = refreshHttpCookie.getValue();

        // 2. Валидируем токен через БД (предполагается реактивный репозиторий/сервис)
        return refreshTokenService.findByToken(refreshToken) // Должен возвращать Mono<RefreshTokenEntity>
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")))
                .flatMap(tokenEntity -> {
                    if (refreshTokenService.isExpired(tokenEntity)) {
                        return refreshTokenService.delete(tokenEntity) // Должен возвращать Mono<Void>
                                .then(Mono.error(new RuntimeException("Refresh token has expired. Please login again")));
                    }

                    // 3. Генерируем новый Access Token
                    return userService.findById(tokenEntity.getUserId())
                            .flatMap(user -> userDetailsService.findByUsername(user.login()))
                            .flatMap(user -> {
                                String newAccessToken = jwtUtils.generateAccessToken(user);
                                System.out.println("newAccessToken=" + newAccessToken);
                                return Mono.just(ResponseEntity.ok(new AuthenticationResponse(newAccessToken)));
                            });
                });
    }
}
