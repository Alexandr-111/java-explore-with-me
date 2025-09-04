package ru.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Почтовый адрес обязателен")
    @Email(message = "Некорректный формат email")
    @Size(min = 6, max = 254, message = "Email должен содержать от 6 до 254 символов")
    private String email;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 250, message = "Имя должно содержать от 2 до 250 символов")
    private String name;
}