package ru.practicum.service.admin;

import ru.practicum.PageResponse;
import ru.practicum.review.EventAttendanceDto;
import ru.practicum.review.EventReviewFullDto;

import java.util.List;

public interface AdminReviewService {
    EventReviewFullDto removeCommentReview(Long reviewId);

    PageResponse<EventReviewFullDto> getReviews(List<Long> ids, Integer from, Integer size);

    EventReviewFullDto getOneReview(Long reviewId);

    EventAttendanceDto confirmAttendance(Long requestId);
}