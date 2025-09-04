package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.compilation.CompilationDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicCompilationClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/compilations";
    private static final String PATH_WITH_ID = "/compilations/{compId}";

    @Autowired
    public PublicCompilationClient(@Value("${ewm-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        if (pinned != null) {
            parameters.put("pinned", pinned);
        }
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return getPageList(path, parameters, null);
    }

    public ResponseEntity<CompilationDto> getCompilationById(Long compId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(compId)
                .toUriString();

        return get(path, CompilationDto.class);
    }
}