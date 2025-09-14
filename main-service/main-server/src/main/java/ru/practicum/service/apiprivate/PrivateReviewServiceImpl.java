package ru.practicum.service.apiprivate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadInputException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.EventReviewMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventAttendance;
import ru.practicum.model.EventReview;
import ru.practicum.model.User;
import ru.practicum.repository.EventAttendanceRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventReviewRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.review.ChangeEventReviewDto;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.review.NewEventReviewDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.mapper.EventReviewMapper.toEventReview;
import static ru.practicum.mapper.EventReviewMapper.toEventReviewFullDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateReviewServiceImpl implements PrivateReviewService {
    private final EventRepository eventRepository;
    private final EventReviewRepository eventReviewRepository;
    private final UserRepository userRepository;
    private final EventAttendanceRepository eventAttendanceRepository;

    @Override
    @Transactional
    public EventReviewFullDto createReview(Long userId, NewEventReviewDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new DataNotFoundException("Событие c id " + dto.getEventId() + " не найдено"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        if (event.getIsFinished() == null || !event.getIsFinished()) {
            throw new BadInputException("Нельзя оставить отзыв — событие ещё не завершено");
        }

        Optional<EventAttendance> attendanceOpt = eventAttendanceRepository.findByEventIdAndUserId(dto.getEventId(),
                userId);
        if (attendanceOpt.isEmpty()) {
            throw new ConflictException("Ваше участие в событии не подтверждено администратором");
        }

        if (eventReviewRepository.existsByEventIdAndUserId(dto.getEventId(), userId)) {
            throw new ConflictException("Вы уже оставляли отзыв на это событие. Вы можете его отредактировать.");
        }

        EventReview review = toEventReview(user, event, dto);
        EventReview savedReview = eventReviewRepository.save(review);
        return toEventReviewFullDto(savedReview);
    }

    @Override
    @Transactional
    public EventReviewFullDto updateReview(Long userId, Long reviewId, ChangeEventReviewDto dto) {
        EventReview eventReview = eventReviewRepository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("Отзыв c id " + reviewId + " не найден"));

        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (!eventReview.getUser().getId().equals(userId)) {
            throw new BadInputException("Вы не можете редактировать чужой отзыв");
        }

        if (dto.getComment() != null) {
            eventReview.setComment(dto.getComment());
        }
        EventReview saved = eventReviewRepository.save(eventReview);

        return toEventReviewFullDto(saved);
    }

    @Override
    public List<EventReviewFullDto> getOnlyOwnReviews(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<EventReview> reviews = eventReviewRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return reviews.stream()
                .map(EventReviewMapper::toEventReviewFullDto)
                .collect(Collectors.toList());
    }
}