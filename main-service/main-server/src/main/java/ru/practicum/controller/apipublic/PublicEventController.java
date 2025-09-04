package ru.practicum.controller.apipublic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.PageResponse;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.service.apipublic.PublicEventService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final PublicEventService service;

    @GetMapping
    public ResponseEntity<PageResponse<EventShortDto>> getEventsWithFiltering(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader("X-Client-IP") String clientIp,
            @RequestHeader("X-Request-URI") String requestUri) {
        log.debug("PublicEventController. Получение событий с возможностью фильтрации.");
        return service.getEventsWithFiltering(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, clientIp, requestUri);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventWithDetails(
            @PathVariable Long id,
            @RequestHeader("X-Client-IP") String clientIp,
            @RequestHeader("X-Request-URI") String requestUri) {
        log.debug("PublicEventController. Получение подробной информации о событии с ID {}", id);
        return service.getEventWithDetails(id, clientIp, requestUri);
    }
}