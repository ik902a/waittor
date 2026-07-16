package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.CreatedTorDto;
import by.klihal.waittor.common.dto.PageResponse;
import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.data.mapper.TorMapper;
import by.klihal.waittor.data.model.Torrent;
import by.klihal.waittor.data.repo.TorRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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

    @Transactional(readOnly = true)
    public Mono<PageResponse<TorDto>> findAll(String search, String sortBy, String order, int pageNumber, int size) {
        // 1. Создаем объект-заглушку и пишем в поисковые поля одну и ту же строку
        Torrent probe = new Torrent();
        if (!search.isBlank()) {
            probe.setName(search);
        }

        // 2. Настраиваем правила сопоставления (аналог Specification)
        ExampleMatcher matcher = ExampleMatcher.matchingAny() // matchingAny() делает связь через OR
                .withIgnoreCase()                             // Игнорировать регистр
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING); // Поиск по подстроке (LIKE %...%)

        Example<Torrent> example = Example.of(probe, matcher);

        // 3. Формируем пагинацию и сортировку
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        return repository.findBy(example, query -> query.as(Torrent.class).page(pageable))
                .map(page -> {
                    List<TorDto> dtos =page.getContent().stream()
                            .map(torMapper::toDto) // или movieMapper::toDto
                            .toList();

                    return new PageResponse<>(
                            dtos,
                            page.getTotalElements(),
                            page.getTotalPages(),
                            page.getNumber(),
                            page.getSize()
                    );
                });
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
