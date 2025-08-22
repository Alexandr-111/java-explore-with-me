import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ExploreWithMeClientStat;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.hitclient.HitClient;
import ru.practicum.statsclient.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExploreWithMeClientStat.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"STATS_SERVER_URL=http://stats-server:7070"})
class StatsClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StatsClient statsClient;

    @Autowired
    private HitClient hitClient;

    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    void saveHit_ValidRequest_ReturnsCreated() throws Exception {
        EndpointHitDto requestDto = EndpointHitDto.builder()
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.now())
                .build();

        EndpointHitDtoResponse responseDto = EndpointHitDtoResponse.builder()
                .id(1L)
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.now())
                .build();

        mockServer.expect(requestTo("http://stats-server:7070/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.content()
                        .json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.app").value("test-app"));

        mockServer.verify();
    }


    @Test
    void getStats_ValidRequest_ReturnsStats() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
        String startStr = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endStr = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String expectedEncodedStart = startStr.replace(" ", "%20");
        String expectedEncodedEnd = endStr.replace(" ", "%20");

        List<ViewStatsDto> statsResponse = List.of(
                ViewStatsDto.builder().app("app1").uri("/test1").hits(5L).build(),
                ViewStatsDto.builder().app("app2").uri("/test2").hits(3L).build()
        );

        mockServer.expect(requestTo(containsString("http://stats-server:7070/stats")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("start", expectedEncodedStart))
                .andExpect(queryParam("end", expectedEncodedEnd))
                .andExpect(queryParam("unique", "false"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(statsResponse), MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/stats")
                        .param("start", startStr)
                        .param("end", endStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("app1"))
                .andExpect(jsonPath("$[0].hits").value(5))
                .andExpect(jsonPath("$[1].app").value("app2"));

        mockServer.verify();
    }

}