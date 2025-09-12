package ru.practicum.controller.apipublic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.PublicReviewClient;
import ru.practicum.review.EventReviewPublicDto;
import ru.practicum.review.RatingDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class PublicReviewController {
    private final PublicReviewClient client;

    @GetMapping("/{eventId}")
    public ResponseEntity<List<EventReviewPublicDto>> getLastReviews(
            @Positive @PathVariable Long eventId,
            @Min(1) @Max(100) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("PublicReviewController. Получение списка последних отзывов на событие {}", eventId);
        return client.getLastReviews(eventId, size);
    }

    @GetMapping("/rating/{eventId}")
    public ResponseEntity<RatingDto> getRating(@Positive @PathVariable Long eventId) {
        log.debug("PublicReviewController. Получение рейтинга события с id {} и его организатора", eventId);
        return client.getRating(eventId);
    }
}