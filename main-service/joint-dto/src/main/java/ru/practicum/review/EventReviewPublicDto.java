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
public class EventReviewPublicDto {
    private Long eventId;
    private Short rating;
    private String comment;
    private String authorName;
    private LocalDateTime createdAt;
}