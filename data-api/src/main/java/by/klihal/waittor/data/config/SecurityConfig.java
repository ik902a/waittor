package by.klihal.waittor.data.config;

import by.klihal.waittor.data.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    // В WebFlux вместо AuthenticationProvider используется ReactiveAuthenticationManager
    private final ReactiveUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, ReactiveUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключаем CSRF (для Stateless архитектуры)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // 1. КРИТИЧЕСКОЕ ИЗМЕНЕНИЕ: Явно отключаем модалку
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // В WebFlux сессии по умолчанию отключены или управляются через WebSession, STATELESS поведение сохраняется
                // КРИТИЧЕСКИ ДЛЯ JWT: Отключаем сохранение сессий (Stateless) в WebFlux
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                .authenticationManager(authenticationManager(userDetailsService, passwordEncoder()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll() // Разрешаем вход, регистрацию и рефреш
                        .anyExchange().authenticated()
                )
                // КРИТИЧЕСКОЕ ДОБАВЛЕНИЕ: Отключаем всплывающее окно браузера для неавторизованных запросов
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, denied) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
                            // Явно удаляем или затираем любые попытки выставить заголовки WWW-Authenticate
                            exchange.getResponse().getHeaders().remove("WWW-Authenticate");
                        }))
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {

        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001")); // URL вашего React приложения
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Разрешает передачу HttpOnly кук

        // ВАЖНО: Используем реактивную реализацию UrlBasedCorsConfigurationSource
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
