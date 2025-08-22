package ru.practicum.hit;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitDtoResponse;

import java.util.Objects;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        Objects.requireNonNull(endpointHitDto, "ДТО (EndpointHitDto) не должен быть null");

        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static EndpointHitDtoResponse toEndpointHitDtoResponse(EndpointHit endpointHit) {
        Objects.requireNonNull(endpointHit, "Объект (hit) не должен быть null");

        return EndpointHitDtoResponse.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}