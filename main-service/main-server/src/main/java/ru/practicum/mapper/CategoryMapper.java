package ru.practicum.mapper;

import ru.practicum.category.CategoryDto;
import ru.practicum.model.Category;

import java.util.Objects;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        Objects.requireNonNull(category, "Категория (Category) не должна быть null");
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}