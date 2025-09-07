package ru.practicum.controller.apiprivate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService service;

    @GetMapping
    public ResponseEntity<PageResponse<EventShortDto>> getUserAllEvents(
            @Positive @PathVariable Long userId,
            @Min(0) @RequestParam(required = false, defaultValue = "0") Integer from,
            @Min(1) @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.debug("PrivateEventController. Получение событий пользователя. Получено id {}, from {}, size {}",
                userId, from, size);
        return service.getUserAllEvents(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@Positive @PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto dto) {
        log.debug("PrivateEventController. Создание события. Получен объект NewEventDto {}", dto);
        EventFullDto savedDto = service.createEvent(userId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(@Positive @PathVariable Long userId,
                                                     @Positive @PathVariable Long eventId) {
        log.debug("PrivateEventController. Получение события пользователя. Получено id {}, eventId {}",
                userId, eventId);
        return service.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUser(@Positive @PathVariable Long userId,
                                                          @Positive @PathVariable Long eventId,
                                                          @Valid @RequestBody UpdateEventUserRequest dto) {
        log.debug("PrivateEventController. Обновление события пользователем. Получено userId {}, eventId {}, dto {}",
                userId, eventId, dto);
        return service.updateEventByUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequest(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId) {
        log.debug("PrivateEventController. Получение запросов на участие в событии. Получено id {}, eventId {}",
                userId, eventId);
        return service.getParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        log.debug("PrivateEventController. Обновление cтатуса заявки на участие. Получено userId {}, eventId {}, dto {}",
                userId, eventId, dto);
        return service.updateRequestStatus(userId, eventId, dto);
    }
}