package by.klihal.waittor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    // В реальном проекте вынесите это в application.yml (минимум 32 символа)
    private final String SECRET_KEY = "my-super-secret-key-for-jwt-authentication-which-is-long-enough";
    private final SecretKey key = Keys.hmacShaKeyFor(Base64.getUrlDecoder().decode(SECRET_KEY));
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 час

    // 1. Генерируем токен
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        // Сохраняем роли в токен как список строк
        claims.put("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(claims)           // Установка пользовательских утверждений
                .subject(username)        // Установка sub
                .issuedAt(new Date())     // Время выпуска
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Время истечения
                .signWith(key, Jwts.SIG.HS256) // Новый способ указания алгоритма
                .compact();
    }

    // 2. Валидация токена
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // Вместо setSigningKey
                    .build()
                    .parseSignedClaims(token); // Вместо parseClaimsJws
            return true;
        } catch (Exception e) {
            // Здесь можно залогировать ошибку (истек, изменен и т.д.)
            return false;
        }
    }

    // 3. Извлечение имени пользователя
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 4. Извлечение ролей
    public List<GrantedAuthority> extractAuthorities(String token) {
        List<String> roles = getClaims(token).get("roles", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)      // Замена setSigningKey
                .build()
                .parseSignedClaims(token) // Замена parseClaimsJws
                .getPayload();
    }
}
