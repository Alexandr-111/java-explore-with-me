package ru.practicum.stats;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDtoResponse saveHit(EndpointHitDto dto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}