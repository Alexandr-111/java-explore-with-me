package ru.practicum.controller.apipublic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.PageResponse;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.service.apipublic.PublicCompilationService;

@Slf4j
@Controller
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final PublicCompilationService service;

    @GetMapping
    public ResponseEntity<PageResponse<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("PublicCompilationController. Получение подборок событий. Получено pinned {}, from {}, size {}",
                pinned, from, size);
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        log.debug("PublicCompilationController. Получение подборки по id. Получено compId {}", compId);
        return service.getCompilationById(compId);
    }
}