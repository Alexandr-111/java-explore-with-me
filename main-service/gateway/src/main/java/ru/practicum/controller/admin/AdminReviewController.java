package ru.practicum.controller.admin;

import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.AdminReviewClient;
import ru.practicum.review.EventAttendanceDto;
import ru.practicum.review.EventReviewFullDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final AdminReviewClient client;

    // Администратор заменяет текст отзыва, оценка события (в баллах) не удаляется
    @PatchMapping("/{reviewId}")
    public ResponseEntity<EventReviewFullDto> removeCommentReview(@Positive @PathVariable Long reviewId) {
        log.debug("AdminReviewController. Удаление администратором комментария из отзыва с id {}", reviewId);
        return client.removeCommentReview(reviewId);
    }

    // Администратор подтверждает участие в событии, для дальнейшей валидации оценок события
    @PostMapping("/confirm/{requestId}")
    public ResponseEntity<EventAttendanceDto> confirmAttendance(@Positive @PathVariable Long requestId) {
        log.debug("AdminReviewController. Подтверждение администратором участия пользователя в событии.");
        return client.confirmAttendance(requestId);
    }

    @GetMapping
    public ResponseEntity<List<EventReviewFullDto>> getReviews(
            @RequestParam(required = false) List<Long> ids,
            @Min(0) @RequestParam(defaultValue = "0") Integer from,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("AdminReviewController. Получение списка отзывов администратором.");
        return client.getReviews(ids, from, size);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<EventReviewFullDto> getOneReview(@Positive @PathVariable Long reviewId) {
        log.debug("AdminReviewController. Получение администратором отзыва с id {}", reviewId);
        return client.getOneReview(reviewId);
    }
}