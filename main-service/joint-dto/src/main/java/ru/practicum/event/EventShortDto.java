package ru.practicum.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;

    @NotBlank(message = "Краткое описание обязательно")
    private String annotation;

    @NotNull(message = "Категория обязательна")
    private CategoryDto category;

    private Long confirmedRequests;

    @NotBlank(message = "Дата события обязательна")
    private String eventDate;

    @NotNull(message = "Инициатор события обязателен")
    private UserShortDto initiator;

    @NotNull(message = "Информация о том, нужно ли оплачивать участие - обязательна")
    private Boolean paid;

    @NotBlank(message = "Заголовок обязателен")
    private String title;

    private Long views;
}