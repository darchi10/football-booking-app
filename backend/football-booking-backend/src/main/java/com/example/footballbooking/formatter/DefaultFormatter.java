package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component("defaultFormatter")
public class DefaultFormatter implements Formatter {

    @Override
    public String format(JsonNode data, String message, String originalContent) {
        return originalContent;
    }
}
