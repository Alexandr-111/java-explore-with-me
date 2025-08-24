package ru.practicum.hitclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.BaseClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;

@Service
public class HitClient extends BaseClient {
    private final String serverUrl;

    @Autowired
    public HitClient(@Value("${stats-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public EndpointHitDtoResponse sendHit(EndpointHitDto endpointHitDto) {
        String url = serverUrl + "/hit";
        ResponseEntity<EndpointHitDtoResponse> response = post(url, endpointHitDto, EndpointHitDtoResponse.class);
        return response.getBody();
    }
}