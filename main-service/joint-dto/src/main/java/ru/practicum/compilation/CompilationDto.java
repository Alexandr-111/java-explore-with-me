package ru.practicum.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.EventShortDto;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;

    private Boolean pinned;

    @NotBlank(message = "Заголовок подборки должен быть обязательно")
    @Size(min = 1, max = 50, message = "Длина заголовка подборки должна быть от 1 до 50 символов")
    private String title;

    private Set<EventShortDto> events = new HashSet<>();
}