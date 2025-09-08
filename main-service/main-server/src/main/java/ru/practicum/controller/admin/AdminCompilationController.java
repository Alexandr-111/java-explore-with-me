package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;
import ru.practicum.service.admin.AdminCompilationService;

import java.net.URI;

@Slf4j
@Controller
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final AdminCompilationService service;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody NewCompilationDto dto) {
        log.debug("AdminCompilationController. Создание новой подборки. Получен объект NewCompilationDto {}", dto);
        CompilationDto savedDto = service.createCompilation(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> removeCompilation(@PathVariable Long compId) {
        log.debug("AdminCompilationController. Удаление подборки с id {}", compId);
        service.removeCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @RequestBody UpdateCompilationRequest dto) {
        log.debug("AdminCompilationController. Обновление подборки с ID {}. Получен объект UpdateCompilationRequest {}",
                compId, dto);
        CompilationDto updatedDto = service.updateCompilation(compId, dto);
        return ResponseEntity.ok(updatedDto);
    }
}