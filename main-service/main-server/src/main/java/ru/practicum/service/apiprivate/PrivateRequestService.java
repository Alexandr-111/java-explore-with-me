package ru.practicum.service.apiprivate;

import org.springframework.http.ResponseEntity;
import ru.practicum.participation.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {

    ResponseEntity<List<ParticipationRequestDto>> getUserParticipationRequest(Long userId);

    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(Long userId, Long requestId);
}