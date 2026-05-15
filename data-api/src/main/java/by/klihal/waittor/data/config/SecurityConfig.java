package by.klihal.waittor.data.config;

import by.klihal.waittor.data.security.JwtAuthenticationFilter;
import by.klihal.waittor.data.security.JwtCookieSuccessHandler;
import by.klihal.waittor.data.security.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtUtils jwtUtil) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключаем для простоты примера
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/error", "/static/css/**", "/js/**", "/images/**", "/favicon.ico", "/.well-known/**").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(new JwtCookieSuccessHandler(jwtUtil))
                        .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/login?error"))
                )
                // Добавляем фильтр для проверки JWT в куках при каждом запросе
                .addFilterAt(new JwtAuthenticationFilter(jwtUtil), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
