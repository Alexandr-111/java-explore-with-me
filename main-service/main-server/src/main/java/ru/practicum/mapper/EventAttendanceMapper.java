package ru.practicum.mapper;

import ru.practicum.model.EventAttendance;
import ru.practicum.review.EventAttendanceDto;

import java.util.Objects;

public class EventAttendanceMapper {

    public static EventAttendanceDto toEventAttendanceDto(EventAttendance attendance) {
        Objects.requireNonNull(attendance, "Участие в событии (EventAttendance) не должно быть null");
        return EventAttendanceDto.builder()
                .id(attendance.getId())
                .requestId(attendance.getRequest().getId())
                .attended(attendance.getAttended())
                .confirmedAt(attendance.getConfirmedAt())
                .build();
    }
}