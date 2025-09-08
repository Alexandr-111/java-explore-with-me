package ru.practicum.controller.apipublic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.PublicEventClient;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final PublicEventClient client;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsWithFiltering(
            @RequestParam(required = false)
            @Size(min = 1, max = 400, message = "Текст должен быть от 1 до 400 символов") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @Min(0) @RequestParam(defaultValue = "0") Integer from,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        {
            log.debug("PublicEventController. Получение событий с возможностью фильтрации.");
            log.debug("Client IP: {}", request.getRemoteAddr());
            log.debug("Endpoint path: {}", request.getRequestURI());
            String remoteAddr = request.getRemoteAddr();
            String requestURI = request.getRequestURI();
            return client.getEventsWithFiltering(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, sort, from, size, remoteAddr, requestURI);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventWithDetails(@Positive @PathVariable Long id,
                                                            HttpServletRequest request) {
        log.debug("PublicEventController. Получение подробной информации о событии с ID {}", id);
        log.debug("Client IP: {}", request.getRemoteAddr());
        log.debug("Endpoint path: {}", request.getRequestURI());
        String remoteAddr = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        return client.getEventWithDetails(id, remoteAddr, requestURI);
    }
}