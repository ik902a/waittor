package by.klihal.waittor.data.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtils jwtUtil;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtil, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // Пропускаем эндпоинты аутентификации без проверки заголовка
        if (request.getURI().getPath().contains("/api/auth")) {
            return chain.filter(exchange);
        }

        final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        final String jwt = authHeader.substring(7);

        return Mono.fromCallable(() -> jwtUtil.extractUsername(jwt))
                // Перехватываем ошибку парсинга синхронного метода jwtUtil и превращаем её в Mono.error
                .onErrorResume(Exception.class, e -> {
                    log.error("JWT parsing failed: {}", e.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token parsing error"));
                })
                // Принудительно переводим цепочку из Mono<String> в Mono<Void>
                .flatMap(login -> {
                    if (login == null) {
                        return chain.filter(exchange);
                    }

                    return ReactiveSecurityContextHolder.getContext()
                            .log("SecurityContextCheck")
                            .map(SecurityContext::getAuthentication)
                            // Если контекст уже есть, просто пропускаем запрос дальше
                            .flatMap(authentication -> chain.filter(exchange))
                            // Если контекста нет (switchIfEmpty), выполняем валидацию и авторизацию
                            .switchIfEmpty(Mono.defer(() ->
                                    userDetailsService.findByUsername(login)
                                            .filter(userDetails -> jwtUtil.isTokenValid(jwt, userDetails))
                                            .map(userDetails -> new UsernamePasswordAuthenticationToken(
                                                    userDetails,
                                                    null,
                                                    userDetails.getAuthorities()
                                            ))
                                            // Важно: пишем в контекст и продолжаем фильтр
                                            .flatMap(authToken -> chain.filter(exchange)
                                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken)))
                                            // Если пользователя нет в БД или токен невалиден
                                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is expired or invalid")))
                            ));
                })
                // Ловим все сгенерированные выше Mono.error в одном месте и отдаем ваш кастомный handleUnauthorized
                .onErrorResume(ResponseStatusException.class, e -> handleUnauthorized(exchange, e.getReason()))
                .onErrorResume(e -> handleUnauthorized(exchange, "Token process error"));
    }

    // Реактивная отправка 401 ошибки для React-клиента
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(message.getBytes())));
    }
}
