package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.participation.ParticipationRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrivateRequestClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/users/{userId}/requests";
    private static final String PATH_CANCEL = "/users/{userId}/requests/{requestId}/cancel";

    @Autowired
    public PrivateRequestClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<ParticipationRequestDto>> getUserParticipationRequest(Long userId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        return getList(path);
    }

    public ResponseEntity<ParticipationRequestDto> createParticipationRequest(Long userId, Long eventId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("eventId", eventId);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        return post(path, parameters, ParticipationRequestDto.class);
    }

    public ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(Long userId, Long requestId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_CANCEL)
                .buildAndExpand(userId, requestId)
                .toUriString();

        return patch(path, null, ParticipationRequestDto.class);
    }
}