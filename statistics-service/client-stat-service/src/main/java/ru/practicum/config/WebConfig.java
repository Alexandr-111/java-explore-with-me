package ru.practicum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.StringToLocalDateTimeConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final StringToLocalDateTimeConverter stringToLocalDateTimeConverter;

    @Autowired
    public WebConfig(StringToLocalDateTimeConverter stringToLocalDateTimeConverter) {
        this.stringToLocalDateTimeConverter = stringToLocalDateTimeConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToLocalDateTimeConverter);
    }
}