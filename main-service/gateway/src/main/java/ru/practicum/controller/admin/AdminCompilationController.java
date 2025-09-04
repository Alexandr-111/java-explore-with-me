package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.client.AdminCompilationClient;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;

@Slf4j
@Controller
@Validated
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final AdminCompilationClient client;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto dto) {
        log.debug("AdminCompilationController. Создание новой подборки. Получен объект NewCompilationDto {}", dto);
        return client.createCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> removeCompilation(@Positive @PathVariable Long compId) {
        log.debug("AdminCompilationController. Удаление подборки с id {}", compId);
        return client.removeCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @Positive @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest dto) {
        log.debug("AdminCompilationController. Обновление подборки с ID {}. Получен объект UpdateCompilationRequest {}",
                compId, dto);
        return client.updateCompilation(compId, dto);
    }
}