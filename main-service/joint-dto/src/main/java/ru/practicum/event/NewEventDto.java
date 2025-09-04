package ru.practicum.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.Location;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Краткое описание события обязательно")
    @Size(min = 20, max = 2000, message = "Краткое описание должно быть от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "Категория события обязательна")
    private Long category;

    @NotBlank(message = "Полное описание события обязательно")
    @Size(min = 20, max = 7000, message = "Полное описание должно быть от 20 до 7000 символов")
    private String description;

    @NotBlank(message = "Дата и время события обязательны")
    private String eventDate;

    @NotNull(message = "Местоположение события обязательно")
    private Location location;

    private Boolean paid = false;

    @Min(value = 0, message = "Лимит участников не может быть отрицательным")
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank(message = "Заголовок события обязателен")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
}