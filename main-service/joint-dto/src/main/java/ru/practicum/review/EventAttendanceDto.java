package ru.practicum.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAttendanceDto {
    private Long id;
    private Long requestId;
    private Boolean attended;
    private LocalDateTime confirmedAt;
}