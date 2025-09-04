package ru.practicum.controller.admin;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.AdminEventClient;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.UpdateEventAdminRequest;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventClient client;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @Min(0) @RequestParam(defaultValue = "0") Integer from,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("AdminEventController. Получение информации о событиях подходящих под переданные условия");
        return client.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest dto) {
        log.debug("AdminEventController. Обновление события с ID {}. Получен объект UpdateEventAdminRequest {}",
                eventId, dto);
        return client.updateEvent(eventId, dto);
    }
}