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
import ru.practicum.category.CategoryDto;
import ru.practicum.category.NewCategoryDto;
import ru.practicum.service.admin.AdminCategoryService;

import java.net.URI;

@Slf4j
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService service;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody NewCategoryDto dto) {
        log.debug("AdminCategoryService. Создание категории. Получен объект NewCategoryDto {}", dto);
        CategoryDto savedDto = service.createCategory(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> removeCategory(@PathVariable Long catId) {
        log.debug("AdminCategoryService. Удаление категории. Получен объект Long {}", catId);
        service.removeCategory(catId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId, @RequestBody NewCategoryDto dto) {
        log.debug("AdminCategoryService. Обновление категории id {}. Получен объект NewCategoryDto {}", catId, dto);
        CategoryDto updatedDto = service.updateCategory(catId, dto);
        return ResponseEntity.ok(updatedDto);
    }
}