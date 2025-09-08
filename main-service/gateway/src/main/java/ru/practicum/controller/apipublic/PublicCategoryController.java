package ru.practicum.controller.apipublic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.category.CategoryDto;
import ru.practicum.client.PublicCategoryClient;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final PublicCategoryClient client;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @Min(0) @RequestParam(defaultValue = "0") Integer from,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("PublicCategoryController. Получение категорий. Получено from {}, size {}", from, size);
        return client.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@Positive @PathVariable Long catId) {
        log.debug("PublicCategoryController. Получение категории по ID. Получено catId {}", catId);
        return client.getCategoryById(catId);
    }
}