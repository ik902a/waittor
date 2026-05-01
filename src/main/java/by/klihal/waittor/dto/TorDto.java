package by.klihal.waittor.dto;

import by.klihal.waittor.model.TorrentType;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TorDto(
                     @NotBlank(message = "Имя не может быть пустым")
                     String name,
                     @NotBlank(message = "Необходимо сделать выбор")
                     TorrentType torrentType,
                     LocalDate release) {
}
