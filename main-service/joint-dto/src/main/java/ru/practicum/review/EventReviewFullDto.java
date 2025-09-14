package ru.practicum.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReviewFullDto {
    private Long id;
    private Long eventId;
    private UserDto userDto;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;
}