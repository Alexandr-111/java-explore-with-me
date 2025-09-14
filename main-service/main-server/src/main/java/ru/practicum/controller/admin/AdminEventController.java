package ru.practicum.controller.admin;

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
import ru.practicum.PageResponse;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.UpdateEventAdminRequest;
import ru.practicum.service.admin.AdminEventService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService service;

    @GetMapping
    public ResponseEntity<PageResponse<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("AdminEventController. Получение информации о событиях подходящих под переданные условия");
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody UpdateEventAdminRequest dto) {
        log.debug("AdminEventController. Обновление события с ID {}. Получен объект UpdateEventAdminRequest {}",
                eventId, dto);
        return service.updateEvent(eventId, dto);
    }

    @PostMapping("/{eventId}/finish")
    public ResponseEntity<EventFullDto> finishEvent(@PathVariable Long eventId) {
        log.debug("Администратор отмечает как завершившееся, событие с id {}", eventId);
        return ResponseEntity.ok(service.finishEvent(eventId));
    }
}