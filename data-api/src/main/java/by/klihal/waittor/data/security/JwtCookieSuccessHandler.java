package by.klihal.waittor.data.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

public class JwtCookieSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtUtils jwtUtil;

    public JwtCookieSuccessHandler(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        UserDetails user = (UserDetails) authentication.getPrincipal();

        // Генерируем токен
        String token = jwtUtil.generateToken(user.getUsername(), user.getAuthorities());

        // Создаем куку
        ResponseCookie cookie = ResponseCookie.from("jwt-token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Strict")
                .build();

        exchange.getResponse().addCookie(cookie);

        // Редирект после логина
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/tors"));
        return exchange.getResponse().setComplete();
    }
}
