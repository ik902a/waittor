package by.klihal.waittor.common.dto;

import by.klihal.waittor.common.enums.TorrentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatedTorDto(
        @NotBlank(message = "Имя не может быть пустым")
        String name,
        LocalDate release,
        @NotNull(message = "Необходимо сделать выбор")
        TorrentType torrentType) {
}
