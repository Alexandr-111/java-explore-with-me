package ru.practicum.service.apiprivate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.participation.ParticipationRequestDto;
import ru.practicum.participation.ParticipationRequestStatus;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.mapper.ParticipationRequestMapper.toDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getUserParticipationRequest(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        List<ParticipationRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        List<ParticipationRequestDto> requestDtos = requests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(requestDtos);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Невозможно принять участие в неопубликованном событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может принять участие в его собственном событии");
        }

        Optional<ParticipationRequest> existingRequest = requestRepository
                .findByRequesterIdAndEventId(userId, eventId);

        if (existingRequest.isPresent()) {
            throw new ConflictException("Заявка уже есть для пользователя " + userId + " и события " + eventId);
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников уже достигнут для события " + eventId);
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(ParticipationRequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        ParticipationRequest savedRequest = requestRepository.save(request);
        return toDto(savedRequest);
    }

    @Override
    @Transactional
    public ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Запрос с id " + requestId + " не найден"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new DataNotFoundException("Запрос с id " + requestId + " не найден");
        }

        if (request.getStatus() == ParticipationRequestStatus.CANCELED) {
            throw new ConflictException("Этот запрос уже отменен");
        }

        if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        ParticipationRequest canceledRequest = requestRepository.save(request);

        return ResponseEntity.ok(toDto(canceledRequest));
    }
}