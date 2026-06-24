package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.AuthenticationRequest;
import by.klihal.waittor.common.dto.AuthenticationResponse;
import by.klihal.waittor.data.model.RefreshToken;
import by.klihal.waittor.data.security.JwtUtils;
import by.klihal.waittor.data.security.RefreshTokenService;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final ReactiveUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService; // Предполагается, что он возвращает Mono/Flux
    private final UserService userService;

    public AuthService(ReactiveAuthenticationManager authenticationManager,
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

    public Mono<ResponseEntity<AuthenticationResponse>> login(AuthenticationRequest request, ServerWebExchange exchange) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.username(), request.password())
                )
                .flatMap(auth -> userDetailsService.findByUsername(request.username()))
                // 3. Генерируем токены (предполагаем, что создание рефреш-токена в БД — это реактивная операция, возвращающая Mono<String>)
                .flatMap(user -> generateAccessToken(user, exchange));
    }

    private Mono<ResponseEntity<AuthenticationResponse>> generateAccessToken(UserDetails user, ServerWebExchange exchange) {
        String accessToken = jwtUtils.generateAccessToken(user);

        return refreshTokenService.createRefreshToken(user.getUsername())
                .map(refreshToken -> {
                    addRefreshCookie(refreshToken, exchange, Duration.ofDays(7));
                    return generateAuthenticationResponse(accessToken);
                });
    }

    private void addRefreshCookie(String refreshToken, ServerWebExchange exchange, Duration duration) {
        // 4. Создаем безопасную куку для Refresh Token
        ResponseCookie refreshCookie = buildRefreshCoockie(refreshToken, duration);
        // Добавляем куку в реактивный ответ
        exchange.getResponse().addCookie(refreshCookie);
    }

    private ResponseCookie buildRefreshCoockie(String refreshToken, Duration duration) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // Для localhost можно выставить false
                .path("/api/auth/refresh")
                .maxAge(duration)
                .sameSite("Strict")
                .build();
    }

    private ResponseEntity<AuthenticationResponse> generateAuthenticationResponse(String accessToken) {
        return ResponseEntity.ok(new AuthenticationResponse(accessToken));
    }

    public Mono<ResponseEntity<AuthenticationResponse>> refresh(ServerWebExchange exchange) {
        String refreshToken = getRefreshToken(exchange);
        if (refreshToken == null) {
            return Mono.error(new RuntimeException("Refresh token is missing"));
        }

        return refreshTokenService.findByToken(refreshToken)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")))
                .flatMap(this::generateRefreshTokenResponse);
    }

    private Mono<ResponseEntity<AuthenticationResponse>> generateRefreshTokenResponse(RefreshToken tokenEntity) {
        if (refreshTokenService.isExpired(tokenEntity)) {
            return refreshTokenService.delete(tokenEntity)
                    .then(Mono.error(new RuntimeException("Refresh token has expired. Please login again")));
        }

        return generateRefreshToken(tokenEntity.getUserId());
    }

    private Mono<ResponseEntity<AuthenticationResponse>> generateRefreshToken(Long userId) {
        return userService.findById(userId)
                .flatMap(user -> userDetailsService.findByUsername(user.login()))
                .flatMap(user -> {
                    String newAccessToken = jwtUtils.generateAccessToken(user);
                    return Mono.just(generateAuthenticationResponse(newAccessToken));
                });
    }

    private String getRefreshToken(ServerWebExchange exchange) {
        return Optional.ofNullable(getHttpCookie(exchange))
                .map(HttpCookie::getValue)
                .orElse(null);
    }

    private HttpCookie getHttpCookie(ServerWebExchange exchange) {
        return exchange.getRequest().getCookies().getFirst("refreshToken");
    }

    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        String refreshToken = getRefreshToken(exchange);
        if (refreshToken == null) {
            return Mono.just(ResponseEntity.ok().build());
        }

        return refreshTokenService.findByToken(refreshToken)
                .flatMap(refreshTokenService::delete)
                .onErrorResume(e -> {
                    System.err.println("Ошибка удаления токена из БД при logout: " + e.getMessage());
                    return Mono.empty();
                })
                .then(Mono.fromSupplier(() -> {
                    addRefreshCookie("", exchange, Duration.ZERO);

                    return ResponseEntity.ok().build();
                }));
    }
}
