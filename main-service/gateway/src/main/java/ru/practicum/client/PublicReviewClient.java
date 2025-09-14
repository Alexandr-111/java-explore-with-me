package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.review.EventReviewPublicDto;
import ru.practicum.review.RatingDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicReviewClient extends BaseClient {
    private final String serverUrl;
    private static final String PATH_WITH_ID = "/reviews/{eventId}";
    private static final String PATH_FOR_RATING = "/reviews/rating/{eventId}";

    @Autowired
    public PublicReviewClient(@Value("${ewm-service.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate);
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<EventReviewPublicDto>> getLastReviews(Long eventId, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("size", size);

        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_WITH_ID)
                .buildAndExpand(eventId)
                .toUriString();

        return getPageList(path, parameters, null);
    }

    public ResponseEntity<RatingDto> getRating(Long eventId) {
        String path = UriComponentsBuilder.fromUriString(serverUrl)
                .path(PATH_FOR_RATING)
                .buildAndExpand(eventId)
                .toUriString();

        return get(path, RatingDto.class);
    }
}