package ru.practicum.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    @NotNull(message = "Идентификатор пользователя обязателен")
    private Long id;

    @NotBlank(message = "Имя пользователя обязательно")
    private String name;
}