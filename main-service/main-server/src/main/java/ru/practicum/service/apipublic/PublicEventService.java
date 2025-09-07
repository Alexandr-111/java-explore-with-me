package ru.practicum.service.apipublic;

import org.springframework.http.ResponseEntity;
import ru.practicum.PageResponse;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Map;

public interface PublicEventService {

    ResponseEntity<PageResponse<EventShortDto>> getEventsWithFiltering(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            String clientIp,
            String requestUri);

    ResponseEntity<EventFullDto> getEventWithDetails(Long id, String clientIp, String requestUri);

    Map<Long, Long> getEventsViews(List<Event> events);

    Long getEventViews(Event event);
}