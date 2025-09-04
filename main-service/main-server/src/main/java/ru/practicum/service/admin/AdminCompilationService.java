package ru.practicum.service.admin;

import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    void removeCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);
}