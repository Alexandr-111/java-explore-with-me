package ru.practicum.service.apipublic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventState;
import ru.practicum.exception.BadInputException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.EventReviewMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventReview;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventReviewRepository;
import ru.practicum.review.EventReviewPublicDto;
import ru.practicum.review.RatingDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicReviewServiceImpl implements PublicReviewService {
    private final EventRepository eventRepository;
    private final EventReviewRepository eventReviewRepository;

    @Override
    public List<EventReviewPublicDto> getLastReviews(Long eventId, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadInputException("Отзывы недоступны для неопубликованного события");
        }
        if (!Boolean.TRUE.equals(event.getIsFinished())) {
            throw new BadInputException("Отзывы недоступны — это событие ещё не завершено");
        }

        Pageable pageable = PageRequest.of(0, Math.min(size, 100));
        List<EventReview> reviews = eventReviewRepository.findLastPublicReviews(eventId, pageable);

        return reviews.stream()
                .map(EventReviewMapper::toEventReviewPublicDto)
                .collect(Collectors.toList());
    }

    @Override
    public RatingDto getRating(Long eventId) {
        Event event = eventRepository.findByIdWithInitiator(eventId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));

        Objects.requireNonNull(event.getInitiator(), "Организатор события не должен быть null");
        User organizer = event.getInitiator();

        // Количество отзывов на данное событие
        Long eventReviewCount = eventReviewRepository.countByEventId(eventId);

        // Вычисляем рейтинг события (как среднее арифметическое)
        BigDecimal eventRating = eventReviewRepository.findAverageRatingByEventId(eventId);
        if (eventRating == null) eventRating = BigDecimal.ZERO;
        eventRating = eventRating.setScale(2, RoundingMode.HALF_UP);

        // Вычисляем рейтинг организатора (как средневзвешенное арифметическое) по формуле
        // Рейтинг_организатора = Σ(Рейтинг_события_i * Вес_события_i) / Σ(Вес_события_i)
        BigDecimal organizerRating = eventReviewRepository.findWeightedAverageRatingByOrganizerId(organizer.getId());
        if (organizerRating == null) organizerRating = BigDecimal.ZERO;
        organizerRating = organizerRating.setScale(2, RoundingMode.HALF_UP);

        // Общее количество отзывов на события организатора
        Long totalOrganizerReviews = eventReviewRepository.countReviewsForRatedEventsByOrganizerId(organizer.getId());

        return RatingDto.builder()
                .eventId(eventId)
                .ratingEvent(eventReviewCount != 0 ? eventRating : BigDecimal.ZERO)
                .totalEventReviews(eventReviewCount)
                .userId(organizer.getId())
                .ratingOrganizer(organizerRating)
                .totalEventsOrganizerReviews(totalOrganizerReviews)
                .build();
    }
}