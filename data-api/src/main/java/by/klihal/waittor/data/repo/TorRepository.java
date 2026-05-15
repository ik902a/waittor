package by.klihal.waittor.data.repo;

import by.klihal.waittor.data.model.Torrent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface TorRepository extends ReactiveCrudRepository<Torrent, Long> {

    Flux<Torrent> findAllByReleaseBeforeOrReleaseIsNull(LocalDate now);
}
