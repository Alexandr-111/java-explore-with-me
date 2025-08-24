package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadInputException;
import ru.practicum.hit.EndpointHit;
import ru.practicum.hit.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.hit.EndpointHitMapper.toEndpointHit;
import static ru.practicum.hit.EndpointHitMapper.toEndpointHitDtoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository repository;

    @Override
    @Transactional
    public EndpointHitDtoResponse saveHit(EndpointHitDto dto) {
        log.info("StatsServiceImpl. Получен hit: app={}, uri={}, ip={}, timestamp={}",
                dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
        EndpointHit entity = toEndpointHit(dto);
        EndpointHit savedEntity = repository.save(entity);
        return toEndpointHitDtoResponse(savedEntity);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateTimeRange(start, end);

        List<EndpointHit> hits;
        if (uris == null || uris.isEmpty()) {
            hits = repository.findAllByTimestampBetween(start, end);
        } else {
            hits = repository.findByTimestampBetweenAndUriIn(start, end, uris);
        }
        return aggregateStats(hits, unique);
    }

    private List<ViewStatsDto> aggregateStats(List<EndpointHit> hits, boolean unique) {
        // Вспомогательный класс для агрегации данных
        class Stats {
            int total = 0;
            final Set<String> uniqueIps = new HashSet<>();
        }

        Map<String, Map<String, Stats>> statsMap = new HashMap<>();

        for (EndpointHit hit : hits) {
            String app = hit.getApp();
            String uri = hit.getUri();
            String ip = hit.getIp();

            Stats stats = statsMap
                    .computeIfAbsent(app, k -> new HashMap<>())
                    .computeIfAbsent(uri, k -> new Stats());

            // Обновляем статистику
            stats.uniqueIps.add(ip);
            stats.total++;
        }

        return statsMap.entrySet().stream()
                .flatMap(appEntry -> appEntry.getValue().entrySet().stream()
                        .map(uriEntry -> {
                            Stats stats = uriEntry.getValue();
                            long hitCount = unique ? stats.uniqueIps.size() : stats.total;
                            return ViewStatsDto.builder()
                                    .app(appEntry.getKey())
                                    .uri(uriEntry.getKey())
                                    .hits(hitCount)
                                    .build();
                        }))
                .sorted(Comparator.comparingLong(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BadInputException("Дата начала должна быть перед датой окончания");
        }
        if (start.equals(end)) {
            throw new BadInputException("Дата начала и дата окончания должны быть разными");
        }
    }
}