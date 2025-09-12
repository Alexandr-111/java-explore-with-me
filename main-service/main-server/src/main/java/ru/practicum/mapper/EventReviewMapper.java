package ru.practicum.mapper;

import ru.practicum.model.Event;
import ru.practicum.model.EventReview;
import ru.practicum.model.User;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.review.EventReviewPublicDto;
import ru.practicum.review.NewEventReviewDto;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.mapper.UserMapper.toUserDto;

public class EventReviewMapper {
    public static EventReviewFullDto toEventReviewFullDto(EventReview eventReview) {
        Objects.requireNonNull(eventReview, "Отзыв на событие (EventReview) не должен быть null");
        Objects.requireNonNull(eventReview.getEvent(), "Событие в отзыве не должно быть null");
        Objects.requireNonNull(eventReview.getUser(), "Пользователь в отзыве не должен быть null");

        return EventReviewFullDto.builder()
                .id(eventReview.getId())
                .eventId(eventReview.getEvent().getId())
                .userDto(toUserDto(eventReview.getUser()))
                .rating(eventReview.getRating())
                .comment(eventReview.getComment())
                .createdAt(eventReview.getCreatedAt())
                .build();
    }

    public static EventReview toEventReview(User user, Event event, NewEventReviewDto dto) {
        Objects.requireNonNull(dto, "Дто (NewEventReviewDto) не должен быть null");
        Objects.requireNonNull(event, "Событие (Event) не должно быть null");
        Objects.requireNonNull(user, "Пользователь (User) не должен быть null");

        return EventReview.builder()
                .event(event)
                .user(user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static EventReviewPublicDto toEventReviewPublicDto(EventReview eventReview) {
        Objects.requireNonNull(eventReview, "Отзыв на событие (EventReview) не должен быть null");
        Objects.requireNonNull(eventReview.getEvent(), "Событие в отзыве не должно быть null");
        Objects.requireNonNull(eventReview.getUser(), "Пользователь в отзыве не должен быть null");

        return EventReviewPublicDto.builder()
                .eventId(eventReview.getEvent().getId())
                .rating(eventReview.getRating())
                .comment(eventReview.getComment())
                .authorName("Участник #" + eventReview.getUser().getId())
                .createdAt(eventReview.getCreatedAt())
                .build();
    }
}