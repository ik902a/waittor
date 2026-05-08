package by.klihal.waittor.repo;

import by.klihal.waittor.model.Torrent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorRepository extends ReactiveCrudRepository<Torrent, Long> {
}
