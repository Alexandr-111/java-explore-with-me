package ru.practicum.controller.apiprivate;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.PrivateRequestClient;
import ru.practicum.participation.ParticipationRequestDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final PrivateRequestClient client;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserParticipationRequest(
            @Positive @PathVariable Long userId) {
        log.debug("PrivateRequestController. Получение запросов на участие в событиях для пользователя с id {}",
                userId);
        return client.getUserParticipationRequest(userId);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createParticipationRequest(
            @Positive @PathVariable Long userId,
            @Positive @RequestParam Long eventId) {
        log.debug("PrivateRequestController. Создание запроса на участие в событии {} пользователем {}", eventId, userId);
        return client.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long requestId) {
        log.debug("PrivateRequestController. Отмена запроса {} на участие в событии, пользователем с id {}",
                requestId, userId);
        return client.cancelParticipationRequest(userId, requestId);
    }
}