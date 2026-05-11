package by.klihal.waittor.service;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.mapper.TorMapper;
import by.klihal.waittor.model.Torrent;
import by.klihal.waittor.repo.TorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TorService {

    private final TorRepository repository;
    private final TorMapper torMapper;

    public TorService(TorRepository repository, TorMapper torMapper) {
        this.repository = repository;
        this.torMapper = torMapper;
    }

    @Transactional(readOnly = true)
    public Flux<TorDto> findAll() {
        return repository.findAll()
                .map(torMapper::toDto);
    }

    @Transactional
    public Mono<Void> plusSeries(Long id) {
        repository.findById(id)
                .flatMap(tor -> {
                    tor.setSeries(Optional.ofNullable(tor.getSeries())
                            .map(s -> s + 1)
                            .orElse(1));
                    return repository.save(tor);
                });
        return Mono.empty();
    }

    @Transactional
    public Mono<TorDto> save(TorDto tor) {
        return repository.save(
                        torMapper.toEntity(tor)
                ).map(torMapper::toDto);
    }

    public Mono<TorDto> update(Long id, TorDto userDto) {
        return repository.findById(id)
                .flatMap(existingUser -> {
                    Torrent updatedEntity = new Torrent();
                    return repository.save(updatedEntity);
                })
                .map(torMapper::toDto);
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}
