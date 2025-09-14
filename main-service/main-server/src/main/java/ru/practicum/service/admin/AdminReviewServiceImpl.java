package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.PageResponse;
import ru.practicum.exception.BadInputException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.EventReviewMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventAttendance;
import ru.practicum.model.EventReview;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.participation.ParticipationRequestStatus;
import ru.practicum.repository.EventAttendanceRepository;
import ru.practicum.repository.EventReviewRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.review.EventAttendanceDto;
import ru.practicum.review.EventReviewFullDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.mapper.EventAttendanceMapper.toEventAttendanceDto;
import static ru.practicum.mapper.EventReviewMapper.toEventReviewFullDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewServiceImpl implements AdminReviewService {
    private final EventReviewRepository repository;
    private final EventAttendanceRepository attendanceRepository;
    private final ParticipationRequestRepository requestRepository;

    @Override
    @Transactional
    public EventReviewFullDto removeCommentReview(Long reviewId) {
        EventReview review = repository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("Отзыв с id " + reviewId + " не найден"));

        review.setComment("Отзыв удалён модератором из-за нарушения правил");
        EventReview savedReview = repository.save(review);
        return toEventReviewFullDto(savedReview);
    }

    @Override
    @Transactional
    public EventAttendanceDto confirmAttendance(Long requestId) {
        ParticipationRequest request = requestRepository.findByIdWithEvent(requestId)
                .orElseThrow(() -> new DataNotFoundException("Заявка с ID " + requestId + " не найдена"));

        if (request.getStatus() != ParticipationRequestStatus.CONFIRMED) {
            throw new BadInputException("Подтверждать участие можно только для заявок со статусом CONFIRMED");
        }
        Event event = request.getEvent();
        if (!Boolean.TRUE.equals(event.getIsFinished())) {
            throw new BadInputException("Нельзя подтвердить участие — событие ещё не завершено");
        }
        if (attendanceRepository.existsByRequestId(requestId)) {
            throw new ConflictException("Участие в событии по этой заявке - уже подтверждено");
        }
        EventAttendance attendance = EventAttendance.builder()
                .request(request)
                .attended(true)
                .confirmedAt(LocalDateTime.now())
                .build();

        EventAttendance saved = attendanceRepository.save(attendance);
        return toEventAttendanceDto(saved);
    }

    @Override
    public PageResponse<EventReviewFullDto> getReviews(List<Long> ids, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<EventReview> reviewsPage = repository.findByIdIn(ids, pageable);

        List<EventReviewFullDto> dtos = reviewsPage.getContent().stream()
                .map(EventReviewMapper::toEventReviewFullDto)
                .toList();

        return PageResponse.<EventReviewFullDto>builder()
                .content(dtos)
                .page(reviewsPage.getNumber())
                .size(reviewsPage.getSize())
                .totalElements(reviewsPage.getTotalElements())
                .totalPages(reviewsPage.getTotalPages())
                .build();
    }


    @Override
    public EventReviewFullDto getOneReview(Long reviewId) {
        EventReview review = repository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("Отзыв с id " + reviewId + " не найден"));

        return toEventReviewFullDto(review);
    }
}