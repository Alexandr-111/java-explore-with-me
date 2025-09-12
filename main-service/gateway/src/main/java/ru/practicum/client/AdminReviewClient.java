package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.review.EventAttendanceDto;
import ru.practicum.review.EventReviewFullDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminReviewClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/admin/reviews";
    private static final String PATH_WITH_ID = "/admin/reviews/{reviewId}";
    private static final String PATH_FOR_CONFIRM = "/admin/reviews/confirm/{requestId}";

    @Autowired
    public AdminReviewClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<EventReviewFullDto> removeCommentReview(Long reviewId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(reviewId)
                .toUriString();

        return patch(path, null, EventReviewFullDto.class);
    }

    public ResponseEntity<List<EventReviewFullDto>> getReviews(List<Long> ids, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        addParamIfNotEmpty(parameters, "ids", ids);
        parameters.put("from", from);
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .toUriString();

        return getPageList(path, parameters, null);
    }

    public ResponseEntity<EventReviewFullDto> getOneReview(Long reviewId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(reviewId)
                .toUriString();

        return get(path, EventReviewFullDto.class);
    }

    public ResponseEntity<EventAttendanceDto> confirmAttendance(Long requestId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_FOR_CONFIRM)
                .buildAndExpand(requestId)
                .toUriString();

        return post(path, (Object) null, EventAttendanceDto.class);
    }
}