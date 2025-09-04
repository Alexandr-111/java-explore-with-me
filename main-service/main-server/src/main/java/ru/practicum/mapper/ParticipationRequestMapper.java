package ru.practicum.mapper;

import ru.practicum.model.ParticipationRequest;
import ru.practicum.participation.ParticipationRequestDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.service.apiprivate.PrivateEventServiceImpl.FORMATTER;

public class ParticipationRequestMapper {

    public static List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        Objects.requireNonNull(request, "Запрос на участие (ParticipationRequest) не должен быть null");

        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated().format(FORMATTER));
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(request.getStatus().name());

        return dto;
    }
}