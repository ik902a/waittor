package by.klihal.waittor.common.dto;

import by.klihal.waittor.common.enums.UserRole;

public record UserDto(Long id,
                      String login,
                      String password,
                      String email,
                      UserRole role) {
}
