package ru.practicum.controller.apipublic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.PublicCompilationClient;
import ru.practicum.compilation.CompilationDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final PublicCompilationClient client;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @Min(0) @RequestParam(defaultValue = "0") Integer from,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("PublicCompilationController. Получение подборок событий. Получено pinned {}, from {}, size {}",
                pinned, from, size);
        return client.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@Positive @PathVariable Long compId) {
        log.debug("PublicCompilationController. Получение подборки по id. Получено compId {}", compId);
        return client.getCompilationById(compId);
    }
}