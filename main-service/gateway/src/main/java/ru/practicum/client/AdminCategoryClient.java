package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;

@Service
public class AdminCategoryClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/admin/categories";
    private static final String PATH_WITH_ID = "/admin/categories/{catId}";

    @Autowired
    public AdminCategoryClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<CategoryDto> createCategory(NewCategoryDto dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return post(path, dto, CategoryDto.class);
    }

    public ResponseEntity<Void> removeCategory(Long catId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(catId)
                .toUriString();

        return delete(path);
    }

    public ResponseEntity<CategoryDto> updateCategory(Long catId, NewCategoryDto dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(catId)
                .toUriString();

        return patch(path, dto, CategoryDto.class);
    }
}