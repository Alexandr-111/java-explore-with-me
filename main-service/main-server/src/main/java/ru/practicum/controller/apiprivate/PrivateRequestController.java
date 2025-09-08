package ru.practicum.controller.apiprivate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.participation.ParticipationRequestDto;
import ru.practicum.service.apiprivate.PrivateRequestService;

import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final PrivateRequestService service;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserParticipationRequest(
            @PathVariable Long userId) {
        log.debug("PrivateRequestController. Получение запросов на участие в событиях для пользователя с id {}",
                userId);
        return service.getUserParticipationRequest(userId);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        log.debug("PrivateRequestController. Создание запроса на участие в событии {} пользователем {}", eventId, userId);
        ParticipationRequestDto savedDto = service.createParticipationRequest(userId, eventId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        log.debug("PrivateRequestController. Отмена запроса {} на участие в событии, пользователем с id {}",
                requestId, userId);
        return service.cancelParticipationRequest(userId, requestId);
    }
}