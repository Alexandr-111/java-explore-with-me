package ru.practicum.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.PageResponse;
import ru.practicum.exception.NetworkException;
import ru.practicum.exception.ServerResponseException;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BaseClient {
    protected final RestTemplate rest;

    protected BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T, R> ResponseEntity<R> get(String path, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, null, null, responseType);
    }

    protected <T, R> ResponseEntity<R> get(String path, Class<R> responseType, HttpHeaders headers) {
        return makeAndSendRequest(HttpMethod.GET, path, responseType, headers);
    }

    protected <T, R> ResponseEntity<R> post(String path, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path, @Nullable Map<String, Object> parameters,
                                            Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, null, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.PATCH, path, null, body, responseType);
    }

    protected ResponseEntity<Void> delete(String path) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null, Void.class);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method,
            String path,
            @Nullable Map<String, Object> parameters,
            @Nullable T body,
            Class<R> responseType) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);

        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((key, value) -> {
                if (value instanceof Collection) {
                    builder.queryParam(key, (Collection<?>) value);
                } else {
                    builder.queryParam(key, value);
                }
            });
        }
        URI uri = builder.encode().build().toUri();

        try {
            ResponseEntity<R> response = rest.exchange(uri, method, requestEntity, responseType);
            // Создаем новый ResponseEntity с корректными заголовками
            // В тестах без этого у меня возникают ошибки
            HttpHeaders newHeaders = new HttpHeaders();
            newHeaders.putAll(response.getHeaders());
            // Удаляем проблемные заголовки и устанавливаем новые
            newHeaders.remove("transfer-encoding");
            newHeaders.remove("connection");
            newHeaders.setContentType(MediaType.APPLICATION_JSON);
            newHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            return new ResponseEntity<>(response.getBody(), newHeaders, response.getStatusCode());
        } catch (HttpStatusCodeException e) {
            throw new ServerResponseException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NetworkException("Ошибка подключения: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка шлюза", e);
        }
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method,
            String path,
            @Nullable Map<String, Object> parameters,
            ParameterizedTypeReference<R> responseType) {

        HttpEntity<T> requestEntity = new HttpEntity<>(null, defaultHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);

        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((key, value) -> {
                if (value instanceof Collection) {
                    builder.queryParam(key, (Collection<?>) value);
                } else {
                    builder.queryParam(key, value);
                }
            });
        }
        URI uri = builder.encode().build().toUri();
        try {
            return rest.exchange(uri, method, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            throw new ServerResponseException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NetworkException("Ошибка подключения: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка шлюза", e);
        }
    }

    protected <T> List<T> getListFromPageResponse(String path, Map<String, Object> parameters) {
        ParameterizedTypeReference<PageResponse<T>> typeRef = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<PageResponse<T>> response = makeAndSendRequest(HttpMethod.GET, path, parameters, typeRef);
        return response.getBody() != null ? response.getBody().getContent() : Collections.emptyList();
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method,
            String path,
            Class<R> responseType,
            @Nullable HttpHeaders customHeaders) {

        HttpHeaders headers = defaultHeaders();
        if (customHeaders != null) {
            headers.putAll(customHeaders);
        }
        HttpEntity<T> requestEntity = new HttpEntity<>(null, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);

        URI uri = builder.encode().build().toUri();
        try {
            return rest.exchange(uri, method, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            throw new ServerResponseException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NetworkException("Ошибка подключения: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка шлюза", e);
        }
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method,
            String path,
            @Nullable Map<String, Object> parameters,
            ParameterizedTypeReference<R> responseType,
            @Nullable HttpHeaders customHeaders) {

        HttpHeaders headers = defaultHeaders();
        if (customHeaders != null) {
            headers.putAll(customHeaders);
        }
        HttpEntity<T> requestEntity = new HttpEntity<>(null, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);

        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((key, value) -> {
                if (value instanceof Collection) {
                    builder.queryParam(key, (Collection<?>) value);
                } else {
                    builder.queryParam(key, value);
                }
            });
        }
        URI uri = builder.encode().build().toUri();
        try {
            return rest.exchange(uri, method, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            throw new ServerResponseException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new NetworkException("Ошибка подключения: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Внутренняя ошибка шлюза", e);
        }
    }

    protected <T> ResponseEntity<List<T>> getPageList(
            String path,
            @Nullable Map<String, Object> parameters,
            HttpHeaders headers
    ) {
        ParameterizedTypeReference<PageResponse<T>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<PageResponse<T>> response = makeAndSendRequest(
                HttpMethod.GET,
                path,
                parameters,
                responseType,
                headers
        );
        PageResponse<T> pageResponse = response.getBody();
        if (pageResponse == null) {
            throw new RuntimeException("Внутренняя ошибка шлюза: Тело ответа null");
        }
        if (pageResponse.getContent() != null) {
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(pageResponse.getContent());
        } else {
            throw new RuntimeException("Внутренняя ошибка шлюза. Content в PageResponse null");
        }
    }

    protected void addParamIfNotEmpty(Map<String, Object> parameters, String key, List<?> values) {
        if (values != null && !values.isEmpty()) {
            String joinedValues = values.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            parameters.put(key, joinedValues);
        }
    }

    protected <T> ResponseEntity<List<T>> getList(String path) {
        ParameterizedTypeReference<List<T>> responseType = new ParameterizedTypeReference<>() {
        };
        return makeAndSendRequest(HttpMethod.GET, path, null, responseType);
    }
}