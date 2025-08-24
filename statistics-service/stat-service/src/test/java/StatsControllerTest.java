import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ExploreWithMeServer;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.hit.EndpointHitRepository;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ExploreWithMeServer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class StatsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EndpointHitRepository repository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveStats() {
        // создаем тестовый запрос
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp("test-app");
        hitDto.setUri("/test");
        hitDto.setIp("192.168.1.1");
        hitDto.setTimestamp(LocalDateTime.now());

        // отправляем запрос на сохранение
        ResponseEntity<EndpointHitDtoResponse> saveResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/hit",
                hitDto,
                EndpointHitDtoResponse.class
        );

        // проверяем сохранение
        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
        assertNotNull(saveResponse.getBody().getId());

        // запрашиваем статистику
        URI statsUri = UriComponentsBuilder.fromUriString("http://localhost:" + port + "/stats")
                .queryParam("start", LocalDateTime.now().minusHours(1).format(formatter))
                .queryParam("end", LocalDateTime.now().plusHours(1).format(formatter))
                .queryParam("uris", "/test")
                .queryParam("unique", false)
                .build()
                .toUri();

        ResponseEntity<List> statsResponse = restTemplate.exchange(
                statsUri,
                HttpMethod.GET,
                null,
                List.class
        );

        assertEquals(HttpStatus.OK, statsResponse.getStatusCode());
        assertFalse(statsResponse.getBody().isEmpty());
    }

    @Test
    void shouldValidateTimeRange() {
        URI invalidUri = UriComponentsBuilder.fromUriString("http://localhost:" + port + "/stats")
                .queryParam("start", LocalDateTime.now().plusHours(1).format(formatter))
                .queryParam("end", LocalDateTime.now().minusHours(1).format(formatter))
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.exchange(
                invalidUri,
                HttpMethod.GET,
                null,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Некорректный запрос"));
        assertTrue(response.getBody().contains("Дата начала должна быть перед датой окончания"));
    }
}