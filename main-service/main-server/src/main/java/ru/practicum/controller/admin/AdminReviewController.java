package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.PageResponse;
import ru.practicum.review.EventAttendanceDto;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.service.admin.AdminReviewService;

import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final AdminReviewService service;

    // Администратор заменяет только текст отзыва, оценка события (в баллах) не удаляется
    @PatchMapping("/{reviewId}")
    public ResponseEntity<EventReviewFullDto> removeCommentReview(@PathVariable Long reviewId) {
        log.debug("AdminReviewController. Удаление администратором комментария из отзыва с id {}", reviewId);
        return ResponseEntity.ok(service.removeCommentReview(reviewId));
    }

    // Администратор подтверждает участие в событии, для дальнейшей валидации оценок события
    @PostMapping("/confirm/{requestId}")
    public ResponseEntity<EventAttendanceDto> confirmAttendance(@PathVariable Long requestId) {
        log.debug("AdminReviewController. Подтверждение администратором участия пользователя в событии.");
        EventAttendanceDto savedDto = service.confirmAttendance(requestId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventReviewFullDto>> getReviews(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("AdminReviewController. Получение списка отзывов администратором.");
        PageResponse<EventReviewFullDto> response = service.getReviews(ids, from, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<EventReviewFullDto> getOneReview(@PathVariable Long reviewId) {
        log.debug("AdminReviewController. Получение администратором отзыва с id {}", reviewId);
        return ResponseEntity.ok(service.getOneReview(reviewId));
    }
}