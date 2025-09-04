package ru.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "Email не должен быть пустой")
    @Email(message = "Некорректный формат email")
    private String email;

    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String name;
}