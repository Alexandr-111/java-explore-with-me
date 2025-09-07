package ru.practicum.mapper;

import ru.practicum.Location;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.model.Event;

import java.util.Objects;

import static ru.practicum.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.mapper.UserMapper.toUserShortDto;
import static ru.practicum.service.apiprivate.PrivateEventServiceImpl.FORMATTER;

public class EventMapper {

    public static EventShortDto toEventShortDto(Event event, Long views) {
        Objects.requireNonNull(event, "Событие (Event) не должно быть null");
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views != null ? views : 0L)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, Long views) {
        Objects.requireNonNull(event, "Событие (Event) не должно быть null");
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(toUserShortDto(event.getInitiator()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() != null ? String.valueOf(event.getPublishedOn()) : null)
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views != null ? views : 0L)
                .build();
    }
}