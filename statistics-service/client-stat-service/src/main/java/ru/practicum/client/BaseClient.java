package ru.practicum.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.exception.NetworkException;
import ru.practicum.exception.ServerResponseException;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BaseClient {
    protected final RestTemplate rest;
    protected final ObjectMapper objectMapper;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    protected <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {
        try {
            return rest.postForEntity(url, body, responseType);
        } catch (HttpStatusCodeException e) {
            throw new ServerResponseException("HTTP ошибка", e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NetworkException("Ошибка соединения во время POST-запроса");
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка шлюза", e);
        }
    }

    protected <T> ResponseEntity<List<T>> getList(String url, Class<T> responseType, Map<String, Object> parameters) {
        log.info("Отправление GET-запроса на URL: {}", url);
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

            if (parameters != null) {
                parameters.forEach((key, value) -> {
                    if (value instanceof LocalDateTime) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        builder.queryParam(key, ((LocalDateTime) value).format(formatter));
                    } else if (value instanceof Collection) {
                        ((Collection<?>) value).forEach(item -> builder.queryParam(key, item.toString()));
                    } else {
                        builder.queryParam(key, value.toString());
                    }
                });
            }

            URI uri = builder.build().toUri();
            ParameterizedTypeReference<List<T>> typeRef = new ParameterizedTypeReference<List<T>>() {
            };

            return rest.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    typeRef
            );
        } catch (HttpStatusCodeException e) {
            throw new ServerResponseException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NetworkException("Ошибка подключения: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка шлюза", e);
        }
    }
}