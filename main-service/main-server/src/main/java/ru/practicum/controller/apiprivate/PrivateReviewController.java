package ru.practicum.controller.apiprivate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.review.ChangeEventReviewDto;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.review.NewEventReviewDto;
import ru.practicum.service.apiprivate.PrivateReviewService;

import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/private/reviews/{userId}")
@RequiredArgsConstructor
public class PrivateReviewController {
    private final PrivateReviewService service;

    @PostMapping
    public ResponseEntity<EventReviewFullDto> createReview(@PathVariable Long userId,
                                                           @RequestBody NewEventReviewDto dto) {
        log.debug("PrivateReviewController. Создание отзыва пользователем {}. Получен ChangeEventReviewDto {}",
                userId, dto);
        EventReviewFullDto savedDto = service.createReview(userId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedDto);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<EventReviewFullDto> updateReview(@PathVariable Long userId,
                                                           @PathVariable Long reviewId,
                                                           @RequestBody ChangeEventReviewDto dto) {
        log.debug("PrivateReviewController. Обновление отзыва с id {}. Получен ChangeEventReviewDto {}", reviewId, dto);
        return ResponseEntity.ok(service.updateReview(userId, reviewId, dto));
    }

    @GetMapping
    public ResponseEntity<List<EventReviewFullDto>> getOnlyOwnReviews(@PathVariable Long userId) {
        log.debug("PrivateReviewController. Получение списка своих отзывов пользователем {}", userId);
        List<EventReviewFullDto> list = service.getOnlyOwnReviews(userId);
        return ResponseEntity.ok(list);
    }
}