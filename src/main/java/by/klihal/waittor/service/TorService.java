package by.klihal.waittor.service;

import by.klihal.waittor.model.Torrent;
import by.klihal.waittor.repo.TorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TorService {

    private final TorRepository repository;

    public TorService(TorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Torrent> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void plusSeries(Long id) {
        Torrent torrent = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torrent with id " + id + " not found"));

        torrent.setSeries(Optional.ofNullable(torrent.getSeries())
                .map(s -> s + 1)
                .orElse(1));
    }
}
