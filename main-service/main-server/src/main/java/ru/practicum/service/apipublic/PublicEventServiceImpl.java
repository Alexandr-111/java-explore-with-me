package ru.practicum.service.apipublic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.EndpointHitDtoResponse;
import ru.practicum.PageResponse;
import ru.practicum.ViewStatsDto;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.EventState;
import ru.practicum.exception.BadInputException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventSpecifications;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.service.apiprivate.PrivateEventServiceImpl.FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    private static final String STATS_SERVICE_URL = "http://client-stat:9090";

    @Override
    public ResponseEntity<PageResponse<EventShortDto>> getEventsWithFiltering(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            String clientIp,
            String requestUri) {
        LocalDateTime startDateTime = parseDateTime(rangeStart);
        LocalDateTime endDateTime = parseDateTime(rangeEnd);

        if (startDateTime == null && endDateTime == null) {
            startDateTime = LocalDateTime.now();
        }
        if (startDateTime != null && endDateTime != null) {
            if (startDateTime.isAfter(endDateTime)) {
                throw new BadInputException("Начало события не может быть позже конца события");
            }
        }
        Sort sorting = getSort(sort);
        Pageable pageable = PageRequest.of(from / size, size, sorting);

        Specification<Event> spec = EventSpecifications.isPublished()
                .and(EventSpecifications.containsText(text))
                .and(EventSpecifications.hasCategories(categories))
                .and(EventSpecifications.hasPaid(paid))
                .and(EventSpecifications.hasStartDateTime(startDateTime))
                .and(EventSpecifications.hasEndDateTime(endDateTime))
                .and(EventSpecifications.isAvailable(onlyAvailable));

        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        Map<Long, Long> viewsForEvents = getEventsViews(eventsPage.getContent());

        List<EventShortDto> eventDtos = eventsPage.getContent().stream()
                .map(event -> {
                    Long views = viewsForEvents.getOrDefault(event.getId(), 0L);
                    return EventMapper.toEventShortDto(event, views);
                })
                .collect(Collectors.toList());

        PageResponse<EventShortDto> pageResponse = PageResponse.<EventShortDto>builder()
                .content(eventDtos)
                .page(eventsPage.getNumber())
                .size(eventsPage.getSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .build();

        saveEndpointHit(clientIp, requestUri);
        return ResponseEntity.ok(pageResponse);
    }

    private void saveEndpointHit(String clientIp, String requestUri) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(requestUri)
                .ip(clientIp)
                .timestamp(LocalDateTime.now())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hitDto, headers);

        String hitUrl = UriComponentsBuilder.fromUriString(STATS_SERVICE_URL + "/hit")
                .build()
                .toUriString();

        restTemplate.postForEntity(
                hitUrl,
                requestEntity,
                EndpointHitDtoResponse.class
        );
    }

    @Override
    public ResponseEntity<EventFullDto> getEventWithDetails(Long id, String clientIp, String requestUri) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new DataNotFoundException("Событие " + id + " не найдено или не опубликовано"));

        saveEndpointHit(clientIp, requestUri);
        Long views = getEventViews(event);
        EventFullDto eventDto = EventMapper.toEventFullDto(event, views);
        return ResponseEntity.ok(eventDto);
    }

    public Long getEventViews(Event event) {
        String eventUri = "/events/" + event.getId();

        LocalDateTime start = LocalDateTime.now().minusYears(2);
        LocalDateTime end = LocalDateTime.now();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(STATS_SERVICE_URL + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("uris", eventUri)
                .queryParam("unique", true);

        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                builder.toUriString(),
                ViewStatsDto[].class
        );
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null &&
                response.getBody().length > 0) {
            return response.getBody()[0].getHits();
        }
        return 0L;
    }

    private Sort getSort(String sort) {
        if ("EVENT_DATE".equals(sort)) {
            return Sort.by("eventDate").descending();
        } else if ("VIEWS".equals(sort)) {
            return Sort.by("views").descending();
        }
        return Sort.unsorted();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, FORMATTER);
        } catch (Exception e) {
            String errorMessage = String.format("Поле должно быть в формате 'yyyy-MM-dd HH:mm:ss'. Значение: %s",
                    dateTimeStr);
            throw new BadInputException(errorMessage);
        }
    }

    @Override
    public Map<Long, Long> getEventsViews(List<Event> events) {
        if (events.isEmpty()) {
            return new HashMap<>();
        }
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        LocalDateTime start = LocalDateTime.now().minusYears(2);
        LocalDateTime end = LocalDateTime.now();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(STATS_SERVICE_URL + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", false);

        if (!uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }
        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                builder.toUriString(),
                ViewStatsDto[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Long> viewsByUri = Arrays.stream(response.getBody())
                    .collect(Collectors.toMap(
                            ViewStatsDto::getUri,
                            ViewStatsDto::getHits,
                            (existing, replacement) -> existing
                    ));
            Map<Long, Long> eventViews = new HashMap<>();
            for (Event event : events) {
                String eventUri = "/events/" + event.getId();
                eventViews.put(event.getId(), viewsByUri.getOrDefault(eventUri, 0L));
            }
            return eventViews;
        }
        return events.stream().collect(Collectors.toMap(Event::getId, event -> 0L));
    }
}