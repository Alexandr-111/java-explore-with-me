package ru.practicum.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Long eventId;
    private BigDecimal ratingEvent;
    private Long totalEventReviews; // Общее количество отзывов на событие
    private Long userId;
    private BigDecimal ratingOrganizer;
    private Long totalEventsOrganizerReviews; // Общее количество отзывов на все события организатора
}