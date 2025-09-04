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
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    public ResponseEntity<PageResponse<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<Compilation> compilationsPage;

        if (pinned != null) {
            compilationsPage = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilationsPage = compilationRepository.findAll(pageable);
        }

        List<CompilationDto> compilationDtos = compilationsPage.getContent().stream()
                .map(CompilationMapper::toCompilationDto)
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
            return compilationRepository.findById(compId)
                    .map(compilation -> ResponseEntity.ok(CompilationMapper.toCompilationDto(compilation)))
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении  подборки по ID", e);
        }
    }
}