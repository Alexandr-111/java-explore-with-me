package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.CategoryRepository;

import static ru.practicum.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует в базе");
        }
        Category category = new Category();
        category.setName(dto.getName());
        Category savedCategory = categoryRepository.save(category);
        return toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void removeCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Категория с id " + catId + " не найдена"));

        long eventsCount = eventRepository.countByCategoryId(catId);
        if (eventsCount > 0) {
            throw new ConflictException("Невозможно удалить категорию - существуют события, связанные с ней");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto dto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Категория с id " + catId + " не найдена"));
        if (dto.getName().equals(category.getName())) {
            return toCategoryDto(category);
        }
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует в базе");
        }
        category.setName(dto.getName());
        Category updatedCategory = categoryRepository.save(category);

        return toCategoryDto(updatedCategory);
    }
}