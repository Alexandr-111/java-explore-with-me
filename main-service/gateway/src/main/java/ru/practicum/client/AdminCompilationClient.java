package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;

@Service
public class AdminCompilationClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/admin/compilations";
    private static final String PATH_WITH_ID = "/admin/compilations/{compId}";

    @Autowired
    public AdminCompilationClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<CompilationDto> createCompilation(NewCompilationDto dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return post(path, dto, CompilationDto.class);
    }

    public ResponseEntity<Void> removeCompilation(Long compId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(compId)
                .toUriString();

        return delete(path);
    }

    public ResponseEntity<CompilationDto> updateCompilation(Long compId, UpdateCompilationRequest dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(compId)
                .toUriString();

        return patch(path, dto, CompilationDto.class);
    }
}