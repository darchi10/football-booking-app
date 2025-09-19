package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;

public interface Formatter {

    String format(JsonNode data, String message, String originalContent);
}
