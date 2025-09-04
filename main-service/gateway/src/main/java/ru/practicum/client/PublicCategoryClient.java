package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.category.CategoryDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicCategoryClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/categories";
    private static final String PATH_WITH_ID = "/categories/{catId}";

    @Autowired
    public PublicCategoryClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return getPageList(path, parameters, null);
    }

    public ResponseEntity<CategoryDto> getCategoryById(Long catId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(catId)
                .toUriString();

        return get(path, CategoryDto.class);
    }
}