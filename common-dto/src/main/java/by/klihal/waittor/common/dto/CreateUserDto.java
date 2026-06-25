package by.klihal.waittor.common.dto;

import by.klihal.waittor.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @NotBlank(message = "Логин не должен быть пустым")
        @Size(min = 3, max = 20, message = "Логин должен быть от 3 до 20 символов")
        String login,
        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,
        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Некорректный формат email")
        String email,
        UserRole role) {

        public CreateUserDto(String login, String password, String email, UserRole role) {
                this.login = login;
                this.password = password;
                this.email = email;
                this.role = (role == null) ? UserRole.CLIENT : role;
        }
}
