package ru.practicum.service.admin;

import org.springframework.http.ResponseEntity;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.UpdateEventAdminRequest;
import ru.practicum.PageResponse;

import java.util.List;

public interface AdminEventService {
    ResponseEntity<PageResponse<EventFullDto>> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    ResponseEntity<EventFullDto> updateEvent(Long eventId, UpdateEventAdminRequest dto);
}