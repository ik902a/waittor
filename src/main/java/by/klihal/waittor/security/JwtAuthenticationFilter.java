package by.klihal.waittor.security;

import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtils jwtUtil;

    public JwtAuthenticationFilter(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Достаем куку с названием "jwt-token"
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt-token");

        if (cookie != null) {
            String token = cookie.getValue();

            // 2. Валидируем токен (логика внутри вашего jwtUtil)
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                List<GrantedAuthority> authorities = jwtUtil.extractAuthorities(token);

                // 3. Создаем объект аутентификации
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 4. Ключевой момент: кладем аутентификацию в реактивный контекст
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            }
        }

        // Если токена нет или он просрочен — просто идем дальше (Spring Security сам решит, пускать ли дальше)
        return chain.filter(exchange);
    }
}
