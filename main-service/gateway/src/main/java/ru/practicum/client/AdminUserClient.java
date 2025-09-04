package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/admin/users";
    private static final String PATH_WITH_ID = "/admin/users/{userId}";

    @Autowired
    public AdminUserClient(@Value("${ewm-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<UserDto>> getUsers(List<Long> ids, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        addParamIfNotEmpty(parameters, "ids", ids);
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return getPageList(path, parameters, null);
    }

    public ResponseEntity<UserDto> createUser(NewUserRequest dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return post(path, dto, UserDto.class);
    }

    public ResponseEntity<Void> removeUser(Long userId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(userId)
                .toUriString();

        return delete(path);
    }
}