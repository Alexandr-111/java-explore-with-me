package ru.practicum.service.apipublic;

import org.springframework.http.ResponseEntity;
import ru.practicum.PageResponse;
import ru.practicum.compilation.CompilationDto;

public interface PublicCompilationService {
    ResponseEntity<PageResponse<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<CompilationDto> getCompilationById(Long compId);
}