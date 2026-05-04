package by.klihal.waittor.service;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.mapper.TorMapper;
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
    private final TorMapper torMapper;

    public TorService(TorRepository repository, TorMapper torMapper) {
        this.repository = repository;
        this.torMapper = torMapper;
    }

    @Transactional
    public List<Torrent> findAll() {
        return repository.findAll();
    }

    @Transactional
    public List<TorDto> findAllDto() {
        return torMapper.toDtoList(repository.findAll());
    }

    @Transactional
    public void plusSeries(Long id) {
        Torrent torrent = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torrent with id " + id + " not found"));

        torrent.setSeries(Optional.ofNullable(torrent.getSeries())
                .map(s -> s + 1)
                .orElse(1));
    }

    public void save(TorDto tor) {
        repository.save(
                torMapper.toEntity(tor)
        );
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
