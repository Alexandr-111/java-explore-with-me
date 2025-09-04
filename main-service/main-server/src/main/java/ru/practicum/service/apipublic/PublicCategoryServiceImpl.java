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
import ru.practicum.category.CategoryDto;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<PageResponse<CategoryDto>> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryDto> categoryDtos = categoryPage.getContent()
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        PageResponse<CategoryDto> response = PageResponse.<CategoryDto>builder()
                .content(categoryDtos)
                .page(categoryPage.getNumber())
                .size(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CategoryDto> getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Категория с id " + catId + " не найдена"));

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(category);
        return ResponseEntity.ok(categoryDto);
    }
}