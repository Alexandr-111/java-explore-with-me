package ru.practicum.service.apiprivate;

import org.springframework.http.ResponseEntity;
import ru.practicum.PageResponse;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.UpdateEventUserRequest;
import ru.practicum.participation.ParticipationRequestDto;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;

import java.util.List;

public interface PrivateEventService {

    ResponseEntity<PageResponse<EventShortDto>> getUserAllEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto dto);

    ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId);

    ResponseEntity<EventFullDto> updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    ResponseEntity<List<ParticipationRequestDto>> getParticipationRequest(Long userId, Long eventId);

    ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(Long userId, Long eventId,
                                                                       EventRequestStatusUpdateRequest dto);
}