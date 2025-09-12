package ru.practicum.controller.apiprivate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.client.PrivateReviewClient;
import ru.practicum.review.ChangeEventReviewDto;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.review.NewEventReviewDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/private/reviews/{userId}")
@RequiredArgsConstructor
public class PrivateReviewController {
    private final PrivateReviewClient client;

    @PostMapping
    public ResponseEntity<EventReviewFullDto> createReview(@Positive @PathVariable Long userId,
                                                           @Valid @RequestBody NewEventReviewDto dto) {
        log.debug("PrivateReviewController. Создание отзыва. Получен NewEventReviewDto {}", dto);
        return client.createReview(userId, dto);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<EventReviewFullDto> updateReview(@Positive @PathVariable Long userId,
                                                           @Positive @PathVariable Long reviewId,
                                                           @Valid @RequestBody ChangeEventReviewDto dto) {
        log.debug("PrivateReviewController. Обновление отзыва с id {}. Получен ChangeEventReviewDto {}", reviewId, dto);
        return client.updateReview(userId, reviewId, dto);
    }

    @GetMapping
    public ResponseEntity<List<EventReviewFullDto>> getOnlyOwnReviews(@Positive @PathVariable Long userId) {
        log.debug("PrivateReviewController. Получение списка своих отзывов пользователем.");
        return client.getOnlyOwnReviews(userId);
    }
}