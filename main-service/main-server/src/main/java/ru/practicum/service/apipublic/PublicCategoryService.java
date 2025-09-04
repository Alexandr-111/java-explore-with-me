package ru.practicum.service.apipublic;

import org.springframework.http.ResponseEntity;
import ru.practicum.PageResponse;
import ru.practicum.category.CategoryDto;

public interface PublicCategoryService {

    ResponseEntity<PageResponse<CategoryDto>> getCategories(Integer from, Integer size);

    ResponseEntity<CategoryDto> getCategoryById(Long catId);
}