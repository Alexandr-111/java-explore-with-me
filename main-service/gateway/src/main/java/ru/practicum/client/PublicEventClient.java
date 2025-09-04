package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventShortDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicEventClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/events";
    private static final String EVENT_BY_ID_PATH = "/events/{id}";

    @Autowired
    public PublicEventClient(@Value("${ewm-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<EventShortDto>> getEventsWithFiltering(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            String remoteAddr,
            String requestURI) {
        Map<String, Object> parameters = new HashMap<>();

        if (text != null) {
            parameters.put("text", text);
        }
        if (categories != null && !categories.isEmpty()) {
            parameters.put("categories", categories);
        }
        if (paid != null) {
            parameters.put("paid", paid);
        }
        if (rangeStart != null) {
            parameters.put("rangeStart", rangeStart);
        }
        if (rangeEnd != null) {
            parameters.put("rangeEnd", rangeEnd);
        }
        if (onlyAvailable != null) {
            parameters.put("onlyAvailable", onlyAvailable);
        }
        if (sort != null) {
            parameters.put("sort", sort);
        }
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Client-IP", remoteAddr);
        headers.set("X-Request-URI", requestURI);

        return getPageList(path, parameters, headers);
    }

    public ResponseEntity<EventFullDto> getEventWithDetails(Long id, String remoteAddr, String requestURI) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(EVENT_BY_ID_PATH)
                .buildAndExpand(id)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Client-IP", remoteAddr);
        headers.set("X-Request-URI", requestURI);

        return get(path, EventFullDto.class, headers);
    }
}