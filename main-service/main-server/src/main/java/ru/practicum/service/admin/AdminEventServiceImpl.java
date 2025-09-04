package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.PageResponse;
import ru.practicum.event.AdminStateAction;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventState;
import ru.practicum.event.UpdateEventAdminRequest;
import ru.practicum.exception.BadInputException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventSpecifications;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResponseEntity<PageResponse<EventFullDto>> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<EventState> eventStates = parseEventStates(states);
        LocalDateTime startTime = parseDateTime(rangeStart);
        LocalDateTime endTime = parseDateTime(rangeEnd);

        Specification<Event> spec = Specification.allOf(
                EventSpecifications.hasUsers(users),
                EventSpecifications.hasStates(eventStates),
                EventSpecifications.hasCategories(categories),
                EventSpecifications.afterStartTime(startTime),
                EventSpecifications.beforeEndTime(endTime)
        );

        Page<Event> eventsPage = eventRepository.findAll(spec, pageRequest);

        List<EventFullDto> eventFullDtos = eventsPage.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        PageResponse<EventFullDto> response = PageResponse.<EventFullDto>builder()
                .content(eventFullDtos)
                .page(eventsPage.getNumber())
                .size(eventsPage.getSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<EventFullDto> updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Событие не найдено"));

        validateEventUpdate(event, dto);
        updateEventFields(event, dto);

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(EventMapper.toEventFullDto(updatedEvent));
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            String decodedDateTime = URLDecoder.decode(dateTimeStr, StandardCharsets.UTF_8);
            return LocalDateTime.parse(decodedDateTime, FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный формат даты: " + dateTimeStr, e);
        }
    }

    private void validateEventUpdate(Event event, UpdateEventAdminRequest dto) {
        if (dto.getEventDate() != null) {
            LocalDateTime newEventDate = parseDateTime(dto.getEventDate());
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new BadInputException("Дата события не может быть в прошлом");
            }
        }

        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Событие можно публиковать только из состояния PENDING");
                }
                LocalDateTime eventDateToCheck = dto.getEventDate() != null ?
                        parseDateTime(dto.getEventDate()) : event.getEventDate();

                if (eventDateToCheck.isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new BadInputException("Дата события должна быть не ранее чем через час от текущего момента");
                }
            } else if (dto.getStateAction() == AdminStateAction.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Нельзя отклонить уже опубликованное событие");
                }
            }
        }
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest dto) {
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new DataNotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(parseDateTime(dto.getEventDate()));
        }
        if (dto.getLocation() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction() == AdminStateAction.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            }
        }
    }

    private List<EventState> parseEventStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return null;
        }
        return states.stream()
                .map(String::toUpperCase)
                .map(EventState::valueOf)
                .collect(Collectors.toList());
    }
}