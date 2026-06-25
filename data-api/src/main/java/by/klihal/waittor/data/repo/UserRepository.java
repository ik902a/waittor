package by.klihal.waittor.data.repo;

import by.klihal.waittor.data.model.User;import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByLogin(String login);

    Mono<Boolean> existsByLogin(String login);
}
