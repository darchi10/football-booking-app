package com.example.footballbooking.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ToolResponseParser {

    private final ObjectMapper objectMapper;
    private final Pattern jsonPattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);

    public ToolResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record ToolResponsePayload(String toolUsed, JsonNode data, String message) {}

    public Optional<ToolResponsePayload> parse(String content) {
        return extractJsonString(content)
                .flatMap(this::parseAndValidateJson);
    }

    private Optional<String> extractJsonString(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Optional.empty();
        }
        Matcher matcher = jsonPattern.matcher(content);
        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

    private Optional<ToolResponsePayload> parseAndValidateJson(String jsonString) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            if (rootNode.has("toolUsed") && rootNode.has("data")) {
                String toolUsed = rootNode.get("toolUsed").asText();
                JsonNode data = rootNode.get("data");
                String message = rootNode.has("message") ? rootNode.get("message").asText() : "";
                return Optional.of(new ToolResponsePayload(toolUsed, data, message));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }
}
