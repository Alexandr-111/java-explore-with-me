package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.CompilationDto;
import ru.practicum.compilation.NewCompilationDto;
import ru.practicum.compilation.UpdateCompilationRequest;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.HashSet;
import java.util.Set;

import static ru.practicum.mapper.CompilationMapper.toCompilationDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        if (compilationRepository.existsByTitle(dto.getTitle())) {
            throw new ConflictException("Подборка с заголовком '" + dto.getTitle() + "' уже существует");
        }
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
            compilation.setEvents(events);
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        return toCompilationDto(savedCompilation);
    }

    @Override
    @Transactional
    public void removeCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new DataNotFoundException("Подборка с id " + compId + " не найдена");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Подборка с id " + compId + " не найдена"));

        if (dto.getTitle() != null) {
            // Проверяем уникальность нового заголовка (если он изменился)
            if (!dto.getTitle().equals(compilation.getTitle()) &&
                    compilationRepository.existsByTitle(dto.getTitle())) {
                throw new ConflictException("Подборка с заголовком '" + dto.getTitle() + "' уже существует");
            }
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        return toCompilationDto(updatedCompilation);
    }
}