package by.klihal.waittor.common.dto;

import by.klihal.waittor.common.enums.UserRole;

public record CreateUserDto(String login,
                            String password,
                            String email,
                            UserRole role) {
}
