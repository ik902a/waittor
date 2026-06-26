package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.CreatedTorDto;
import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.data.mapper.TorMapper;
import by.klihal.waittor.data.model.Torrent;
import by.klihal.waittor.data.repo.TorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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

    @Transactional(readOnly = true)
    public Flux<TorDto> findAllActual() {
        return repository.findAllByReleaseBeforeOrReleaseIsNull(LocalDate.now())
                .map(torMapper::toDto);
    }

    @Transactional
    public Mono<Void> plusSeries(Long id) {
        return repository.findById(id)
                .flatMap(tor -> {
                    tor.setSeries(Optional.ofNullable(tor.getSeries())
                            .map(s -> s + 1)
                            .orElse(1));
                    return repository.save(tor);
                })
                .then();
    }

    @Transactional
    public Mono<TorDto> save(CreatedTorDto dto) {
        return repository.save(
                        torMapper.toEntity(dto)
                ).map(torMapper::toDto);
    }

    @Transactional
    public Mono<TorDto> update(Long id, CreatedTorDto dto) {
        return repository.findById(id)
                .flatMap(entity -> {
                    entity.setName(dto.name());
                    entity.setRelease(dto.release());
                    entity.setTorrentType(dto.torrentType().name());
                    return repository.save(entity);
                })
                .map(torMapper::toDto);
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}
