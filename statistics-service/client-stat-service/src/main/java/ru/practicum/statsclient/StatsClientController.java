package ru.practicum.statsclient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.hitclient.HitClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
public class StatsClientController {
    private final StatsClient statsClient;
    private final HitClient hitClient;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDtoResponse> saveHit(
            @Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.debug("StatsController. Получен hit для сохранения: app={}, uri={}, ip={}, timestamp={}",
                endpointHitDto.getApp(), endpointHitDto.getUri(),
                endpointHitDto.getIp(), endpointHitDto.getTimestamp());
        EndpointHitDtoResponse savedHit = hitClient.sendHit(endpointHitDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedHit.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedHit);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.debug("StatsController. Запрос статистики. Получены объекты start {}, end {}, uris {} и unique {}",
                start, end, uris, unique);
        return statsClient.fetchStats(start, end, uris, unique);
    }
}