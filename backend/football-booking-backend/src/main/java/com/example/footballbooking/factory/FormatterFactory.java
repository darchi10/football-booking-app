package com.example.footballbooking.factory;

import com.example.footballbooking.formatter.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class FormatterFactory {

    private final Map<String, Formatter> formatters;

    @Autowired
    public FormatterFactory(Map<String, Formatter> formatters) {
        this.formatters = formatters;
    }

    public Formatter getFormatter(String toolName) {
        return Optional.ofNullable(formatters.get(toolName))
                .orElse(formatters.get("defaultFormatter"));
    }
}
