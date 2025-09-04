package ru.practicum.controller.apiprivate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.PageResponse;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.UpdateEventUserRequest;
import ru.practicum.participation.ParticipationRequestDto;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;
import ru.practicum.service.apiprivate.PrivateEventService;

import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService service;

    @GetMapping
    public ResponseEntity<PageResponse<EventShortDto>> getUserAllEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("PrivateEventController. Получение событий пользователя. Получено id {}, from {}, size {}",
                userId, from, size);
        return service.getUserAllEvents(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(
            @PathVariable Long userId,
            @RequestBody NewEventDto dto) {
        log.debug("PrivateEventController. Создание события. Получен объект NewEventDto {}", dto);
        EventFullDto savedDto = service.createEvent(userId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.debug("PrivateEventController. Получение события пользователя. Получено id {}, eventId {}",
                userId, eventId);
        return service.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody UpdateEventUserRequest dto) {
        log.debug("PrivateEventController. Обновление события пользователем. Получено userId {}, eventId {}, dto {}",
                userId, eventId, dto);
        return service.updateEventByUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.debug("PrivateEventController. Получение запросов на участие в событии. Получено id {}, eventId {}",
                userId, eventId);
        return service.getParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest dto) {
        log.debug("PrivateEventController. Обновление cтатуса заявки на участие. Получено userId {}, eventId {}, dto {}",
                userId, eventId, dto);
        return service.updateRequestStatus(userId, eventId, dto);
    }
}