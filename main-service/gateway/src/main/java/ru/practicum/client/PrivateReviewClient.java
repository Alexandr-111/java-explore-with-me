package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.review.ChangeEventReviewDto;
import ru.practicum.review.EventReviewFullDto;
import ru.practicum.review.NewEventReviewDto;

import java.util.List;

@Service
public class PrivateReviewClient extends BaseClient {
    private final String serverUrl;
    private static final String RESOURCE_PATH = "/private/reviews/{userId}";
    private static final String PATH_FOR_UPDATE = "/private/reviews/{userId}/{reviewId}";

    @Autowired
    public PrivateReviewClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<EventReviewFullDto> createReview(Long userId, NewEventReviewDto dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        return post(path, dto, EventReviewFullDto.class);
    }

    public ResponseEntity<EventReviewFullDto> updateReview(Long userId, Long reviewId, ChangeEventReviewDto dto) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_FOR_UPDATE)
                .buildAndExpand(userId, reviewId)
                .toUriString();

        return patch(path, dto, EventReviewFullDto.class);
    }

    public ResponseEntity<List<EventReviewFullDto>> getOnlyOwnReviews(Long userId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(RESOURCE_PATH)
                .buildAndExpand(userId)
                .toUriString();

        return getList(path);
    }
}