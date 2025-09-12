package ru.practicum.service.apiprivate;

import ru.practicum.review.ChangeEventReviewDto;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.review.NewEventReviewDto;

import java.util.List;

public interface PrivateReviewService {
    EventReviewFullDto createReview(Long userId, NewEventReviewDto dto);

    EventReviewFullDto updateReview(Long userId, Long reviewId, ChangeEventReviewDto dto);

    List<EventReviewFullDto> getOnlyOwnReviews(Long userId);
}