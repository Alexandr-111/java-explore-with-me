package ru.practicum.statsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.BaseClient;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private final String serverUrl;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<ViewStatsDto>> fetchStats(LocalDateTime start, LocalDateTime end,
                                                         List<String> uris, boolean unique) {
        String url = serverUrl + "/stats";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", uris);
        }
        return getList(url, ViewStatsDto.class, parameters);
    }
}