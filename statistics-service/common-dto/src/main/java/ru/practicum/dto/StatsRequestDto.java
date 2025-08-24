package ru.practicum.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsRequestDto {
    private String start;
    private String end;
    private List<String> uris;
    private Boolean unique;
}