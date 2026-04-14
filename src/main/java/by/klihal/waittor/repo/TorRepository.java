package by.klihal.waittor.repo;

import by.klihal.waittor.model.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorRepository extends JpaRepository<Torrent, Long> {
}
