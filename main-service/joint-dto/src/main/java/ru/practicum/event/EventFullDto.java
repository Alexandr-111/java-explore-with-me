package ru.practicum.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.Location;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    @NotBlank(message = "Краткое описание обязательно")
    private String annotation;

    @NotNull(message = "Категория обязательна")
    private CategoryDto category;

    private Long confirmedRequests;

    private String createdOn;

    private String description;

    @NotBlank(message = "Дата и время события обязательны")
    private String eventDate;

    private Long id;

    @NotNull(message = "Инициатор события обязателен")
    private UserShortDto initiator;

    @NotNull(message = "Местоположение обязательно")
    private Location location;

    @NotNull(message = "Поле paid обязательно")
    private Boolean paid;

    private Integer participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private EventState state;

    @NotBlank(message = "Заголовок обязателен")
    private String title;

    private Long views;
}