package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.CreateUserDto;
import by.klihal.waittor.common.dto.UserDto;
import by.klihal.waittor.data.mapper.UserMapper;
import by.klihal.waittor.data.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Flux<UserDto> findAll() {
        return repository.findAll()
                .map(mapper::toDto);
    }

    @Transactional
    public Mono<UserDto> save(CreateUserDto dto) {
        return repository.save(
                mapper.toEntity(dto)
        ).map(mapper::toDto);
    }

    @Transactional
    public Mono<UserDto> update(Long id, CreateUserDto dto) {
        return repository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setLogin(dto.login());
//                    existingUser.setPassword(dto.password());
                    existingUser.setEmail(dto.email());
                    existingUser.setRole(dto.role().name());
                    return repository.save(existingUser);
                })
                .map(mapper::toDto);
//                        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }

    @Transactional
    public Mono<UserDto> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Mono<Boolean> existsByLogin(String login) {
        return repository.existsByLogin(login);
    }
}
