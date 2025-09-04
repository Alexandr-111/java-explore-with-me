package ru.practicum.service.apiprivate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.PageResponse;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.EventState;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.UpdateEventUserRequest;
import ru.practicum.exception.BadInputException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.participation.ParticipationRequestDto;
import ru.practicum.participation.ParticipationRequestStatus;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;
import ru.practicum.request.RequestStatusForUpdate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.ParticipationRequestMapper.toDto;
import static ru.practicum.mapper.ParticipationRequestMapper.toDtoList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResponseEntity<PageResponse<EventShortDto>> getUserAllEvents(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> eventsPage = eventRepository.findByInitiatorId(userId, pageable);

        List<EventShortDto> eventDtos = eventsPage.getContent()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        PageResponse<EventShortDto> pageResponse = PageResponse.<EventShortDto>builder()
                .content(eventDtos)
                .page(from / size)
                .size(size)
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new DataNotFoundException("Категория с id " + dto.getCategory() + " не найдена"));

        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), FORMATTER);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadInputException("Дата события должна быть как минимум через 2 часа от текущего момента");
        }

        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(eventDate);
        event.setInitiator(user);
        event.setLat(dto.getLocation().getLat());
        event.setLon(dto.getLocation().getLon());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);
        event.setTitle(dto.getTitle());
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setViews(0L);
        event.setConfirmedRequests(0L);

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));
        return ResponseEntity.ok(EventMapper.toEventFullDto(event));
    }


    @Override
    @Transactional
    public ResponseEntity<EventFullDto> updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Только ожидающие или отмененные события могут быть изменены.");
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(dto.getEventDate(), FORMATTER);
            if (newEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadInputException("Дата события должна быть как минимум через два часа от текущего момента.");
            }
            event.setEventDate(newEventDate);
        }

        updateEventFields(event, dto);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(EventMapper.toEventFullDto(updatedEvent));
    }

    private void updateEventFields(Event event, UpdateEventUserRequest dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new DataNotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataNotFoundException("Событие с id " + eventId + " не найдено");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return ResponseEntity.ok(toDtoList(requests));
    }

    @Override
    @Transactional
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(Long userId, Long eventId,
                                                                              EventRequestStatusUpdateRequest dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new DataNotFoundException("Событие с id " + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя принять участие в неопубликованном событии");
        }

        List<ParticipationRequest> requestsToUpdate = requestRepository.findAllByIdInAndEventIdAndStatus(
                dto.getRequestIds(), eventId, ParticipationRequestStatus.PENDING);

        if (requestsToUpdate.size() != dto.getRequestIds().size()) {
            throw new ConflictException("Некоторые запросы не в статусе PENDING или были не найдены");
        }

        if (dto.getStatus() == RequestStatusForUpdate.CONFIRMED) {
            return confirmRequests(event, requestsToUpdate);
        } else if (dto.getStatus() == RequestStatusForUpdate.REJECTED) {
            return rejectRequests(requestsToUpdate);
        } else {
            throw new BadInputException("Такого статуса не существует");
        }
    }

    private ResponseEntity<EventRequestStatusUpdateResult> confirmRequests(Event event,
                                                                           List<ParticipationRequest> requests) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        long confirmedCount = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();

        if (participantLimit > 0 && confirmedCount >= participantLimit) {
            throw new ConflictException("Лимит на участие в событии уже достигнут");
        }

        for (ParticipationRequest request : requests) {
            if (participantLimit == 0 || confirmedCount < participantLimit) {
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
                requestRepository.save(request);
                result.getConfirmedRequests().add(toDto(request));
                confirmedCount++;
            } else {
                request.setStatus(ParticipationRequestStatus.REJECTED);
                requestRepository.save(request);
                result.getRejectedRequests().add(toDto(request));
            }
        }

        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<EventRequestStatusUpdateResult> rejectRequests(List<ParticipationRequest> requests) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        for (ParticipationRequest request : requests) {
            request.setStatus(ParticipationRequestStatus.REJECTED);
            requestRepository.save(request);
            result.getRejectedRequests().add(toDto(request));
        }
        return ResponseEntity.ok(result);
    }
}