package ru.practicum;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime convert(String source) {
        try {
            String decoded = URLDecoder.decode(source, StandardCharsets.UTF_8);
            return LocalDateTime.parse(decoded, FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Несоответствующий формат даты: " + source, e);
        }
    }
}