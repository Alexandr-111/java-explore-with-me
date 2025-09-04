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
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;
import ru.practicum.client.AdminCategoryClient;

@Slf4j
@Controller
@Validated
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryClient client;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody NewCategoryDto dto) {
        log.debug("AdminCategoryController. Создание категории. Получен объект NewCategoryDto {}", dto);
        return client.createCategory(dto);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> removeCategory(@Positive @PathVariable Long catId) {
        log.debug("AdminCategoryController. Удаление категории. Получен объект Long {}", catId);
        return client.removeCategory(catId);
    }


    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@Positive @PathVariable Long catId,
                                                      @Valid @RequestBody NewCategoryDto dto) {
        log.debug("AdminCategoryController. Обновление категории с id {}. Получен объект NewCategoryDto {}",
                catId, dto);
        return client.updateCategory(catId, dto);
    }
}