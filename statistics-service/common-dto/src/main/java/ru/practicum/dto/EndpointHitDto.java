package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndpointHitDto {
    @NotBlank(message = "Поле app не должно быть пустым")
    private String app;  // Идентификатор сервиса

    @NotBlank(message = "URI не должен быть пустым")
    private String uri;   // URI для которого был осуществлен запрос

    @NotBlank(message = "IP не должен быть пустым")
    private String ip;  // IP-адрес пользователя

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}