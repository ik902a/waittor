package by.klihal.waittor.data.repo;

import by.klihal.waittor.data.model.RefreshToken;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Long> {

    Mono<RefreshToken> findByToken(String token);

    @Modifying
    Mono<Void> deleteByUserId(Long userId);
}
