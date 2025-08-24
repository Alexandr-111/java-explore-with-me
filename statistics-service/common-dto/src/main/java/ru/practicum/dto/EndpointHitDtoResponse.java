package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDtoResponse {

    private Long id;     // Идентификатор записи
    private String app;  // Идентификатор сервиса
    private String uri;  // URI для которого был осуществлен запрос
    private String ip;   // IP-адрес пользователя
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}