package ru.practicum.service.apipublic;

import ru.practicum.review.EventReviewPublicDto;
import ru.practicum.review.RatingDto;

import java.util.List;

public interface PublicReviewService {
    List<EventReviewPublicDto> getLastReviews(Long eventId, Integer size);

    RatingDto getRating(Long eventId);
}