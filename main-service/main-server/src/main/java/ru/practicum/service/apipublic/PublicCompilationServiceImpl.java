package ru.practicum.service.apipublic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.PageResponse;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final PublicEventService publicEventService;

    @Override
    public ResponseEntity<PageResponse<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<Compilation> compilationsPage;

        if (pinned != null) {
            compilationsPage = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilationsPage = compilationRepository.findAll(pageable);
        }

        List<Event> allEvents = compilationsPage.getContent().stream()
                .map(Compilation::getEvents)
                .filter(events -> events != null && !events.isEmpty())
                .flatMap(Set::stream)
                .collect(Collectors.toList());

        Map<Long, Long> eventViews = publicEventService.getEventsViews(allEvents);
        List<CompilationDto> compilationDtos = compilationsPage.getContent().stream()
                .map(compilation -> CompilationMapper.toCompilationDto(compilation, eventViews))
                .collect(Collectors.toList());

        PageResponse<CompilationDto> pageResponse = PageResponse.<CompilationDto>builder()
                .content(compilationDtos)
                .page(compilationsPage.getNumber())
                .size(compilationsPage.getSize())
                .totalElements(compilationsPage.getTotalElements())
                .totalPages(compilationsPage.getTotalPages())
                .build();

        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<CompilationDto> getCompilationById(Long compId) {
        try {
            Optional<Compilation> compilationOpt = compilationRepository.findById(compId);

            if (compilationOpt.isEmpty()) {
                throw new DataNotFoundException("Подборка с  id" + compId + " не найдена.");
            }
            Compilation compilation = compilationOpt.get();

            Set<Event> events = compilation.getEvents();
            List<Event> eventList = events != null ? new ArrayList<>(events) : Collections.emptyList();

            Map<Long, Long> eventViews = publicEventService.getEventsViews(eventList);
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, eventViews);
            return ResponseEntity.ok(compilationDto);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении подборки по ID", e);
        }
    }
}