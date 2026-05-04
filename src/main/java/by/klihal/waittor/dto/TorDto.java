package by.klihal.waittor.dto;

import by.klihal.waittor.model.TorrentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TorDto(Long id,
                     @NotBlank(message = "Имя не может быть пустым")
                     String name,
                     @NotNull(message = "Необходимо сделать выбор")
                     TorrentType torrentType,
                     LocalDate release) {
}
