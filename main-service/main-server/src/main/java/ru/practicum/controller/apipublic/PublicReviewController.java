package ru.practicum.controller.apipublic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.review.EventReviewPublicDto;
import ru.practicum.review.RatingDto;
import ru.practicum.service.apipublic.PublicReviewService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class PublicReviewController {
    private final PublicReviewService service;

    @GetMapping("/{eventId}")
    public ResponseEntity<List<EventReviewPublicDto>> getLastReviews(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("PublicReviewController. Получение списка последних отзывов на событие {}", eventId);
        List<EventReviewPublicDto> list = service.getLastReviews(eventId, size);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/rating/{eventId}")
    public ResponseEntity<RatingDto> getRating(@PathVariable Long eventId) {
        log.debug("PublicReviewController. Получение рейтинга события с id {} и его организатора", eventId);
        return ResponseEntity.ok(service.getRating(eventId));
    }
}