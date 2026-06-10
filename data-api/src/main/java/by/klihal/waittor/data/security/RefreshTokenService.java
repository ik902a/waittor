package by.klihal.waittor.data.security;

import by.klihal.waittor.data.model.RefreshToken;
import by.klihal.waittor.data.repo.RefreshTokenRepository;
import by.klihal.waittor.data.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

//    @Value("${application.security.jwt.refresh-token.expiration}")
//    private long refreshTokenDurationMs; // Срок действия (например, 7 дней в миллисекундах)
    private final static long REFRESH_TOKEN_DURATION = 1000 * 60 * 60 * 24 * 7; // 7 дней в миллисекундах

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Mono<String> createRefreshToken(String login) {
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> refreshTokenRepository.deleteByUserId(user.getId())
                        .then(Mono.defer(() -> {
                            RefreshToken refreshToken = new RefreshToken();
                            refreshToken.setUserId(user.getId());
                            refreshToken.setToken(UUID.randomUUID().toString());
                            refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION));
                            return refreshTokenRepository.save(refreshToken);
                        }))
                )
                .map(RefreshToken::getToken);
    }

    public Mono<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    public boolean isExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }

    public Mono<Void> delete(RefreshToken refreshToken) {
        return refreshTokenRepository.delete(refreshToken);
    }

    // Принудительное удаление сессии (например, при Logout)
    @Transactional
    public Mono<Void> deleteByUsername(String login) {
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> refreshTokenRepository.deleteByUserId(user.getId()));
    }
}
