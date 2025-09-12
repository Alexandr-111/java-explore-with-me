package ru.practicum.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventReviewDto {
    @Positive
    private Long eventId;

    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    private Short rating;

    @NotBlank(message = "Краткий отзыв обязателен")
    @Size(min = 5, max = 300, message = "Краткий отзыв должен быть от 5 до 500 символов")
    private String comment;
}