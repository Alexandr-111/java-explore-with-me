package ru.practicum.service.admin;

import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryDto dto);

    void removeCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto dto);
}