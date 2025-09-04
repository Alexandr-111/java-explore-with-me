package ru.practicum.mapper;

import ru.practicum.compilation.CompilationDto;
import ru.practicum.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        Objects.requireNonNull(compilation, "Подборка (Compilation) не должна быть null");
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(mapEventsToShortDto(compilation.getEvents()))
                .build();
    }

    private static Set<EventShortDto> mapEventsToShortDto(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptySet();
        }
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet());
    }
}