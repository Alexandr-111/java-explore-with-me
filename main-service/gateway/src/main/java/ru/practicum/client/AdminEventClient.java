package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.UpdateEventAdminRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminEventClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/admin/events";
    private static final String PATH_WITH_ID = "/admin/events/{eventId}";
    private static final String PATH_FOR_FINISH = "/admin/events/{eventId}/finish";

    @Autowired
    public AdminEventClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<EventFullDto>> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {
        Map<String, Object> parameters = new HashMap<>();

        addParamIfNotEmpty(parameters, "users", users);
        addParamIfNotEmpty(parameters, "states", states);
        addParamIfNotEmpty(parameters, "categories", categories);
        if (from != null) {
            parameters.put("rangeStart", rangeStart);
        }
        if (from != null) {
            parameters.put("rangeEnd", rangeEnd);
        }
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return getPageList(path, parameters, null);
    }

    public ResponseEntity<EventFullDto> updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(eventId)
                .toUriString();

        return patch(path, dto, EventFullDto.class);
    }

    public ResponseEntity<EventFullDto> finishEvent(Long eventId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_FOR_FINISH)
                .buildAndExpand(eventId)
                .toUriString();

        return post(path, (Object) null, EventFullDto.class);
    }
}