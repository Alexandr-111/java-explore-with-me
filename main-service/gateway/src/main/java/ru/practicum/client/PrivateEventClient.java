package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.event.NewEventDto;
import ru.practicum.event.UpdateEventUserRequest;
import ru.practicum.participation.ParticipationRequestDto;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrivateEventClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/users/{userId}/events";
    private static final String EVENT_PATH = "/users/{userId}/events/{eventId}";
    private static final String EVENT_REQUESTS_PATH = "/users/{userId}/events/{eventId}/requests";

    @Autowired
    public PrivateEventClient(@Value("${ewm-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<EventShortDto>> getUserAllEvents(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        List<EventShortDto> list = getListFromPageResponse(path, parameters);
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<EventFullDto> createEvent(Long userId, NewEventDto dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        return post(path, dto, EventFullDto.class);

    }

    public ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(EVENT_PATH)
                .buildAndExpand(userId, eventId)
                .toUriString();

        return get(path, EventFullDto.class);

    }

    public ResponseEntity<EventFullDto> updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(EVENT_PATH)
                .buildAndExpand(userId, eventId)
                .toUriString();

        return patch(path, dto, EventFullDto.class);
    }

    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequest(Long userId, Long eventId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(EVENT_REQUESTS_PATH)
                .buildAndExpand(userId, eventId)
                .toUriString();

        return getList(path);
    }

    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(EVENT_REQUESTS_PATH)
                .buildAndExpand(userId, eventId)
                .toUriString();

        return patch(path, dto, EventRequestStatusUpdateResult.class);
    }
}