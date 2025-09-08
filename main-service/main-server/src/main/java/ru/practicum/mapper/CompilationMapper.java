package ru.practicum.mapper;

import ru.practicum.compilation.CompilationDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, Map<Long, Long> eventViews) {
        Objects.requireNonNull(compilation, "Подборка (Compilation) не должна быть null");
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(mapEventsToShortDto(compilation.getEvents(), eventViews))
                .build();
    }

    private static Set<EventShortDto> mapEventsToShortDto(Set<Event> events, Map<Long, Long> eventViews) {
        if (events == null || events.isEmpty()) {
            return Collections.emptySet();
        }
        return events.stream()
                .map(event -> {
                    Long views = eventViews.getOrDefault(event.getId(), 0L);
                    return EventMapper.toEventShortDto(event, views);
                })
                .collect(Collectors.toSet());
    }
}