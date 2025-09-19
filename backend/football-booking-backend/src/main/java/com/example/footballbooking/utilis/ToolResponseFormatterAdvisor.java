package com.example.footballbooking.utilis;

import com.example.footballbooking.factory.FormatterFactory;
import com.example.footballbooking.parser.ToolResponseParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ToolResponseFormatterAdvisor implements CallAroundAdvisor {

    private final FormatterFactory formatterFactory;
    private final ToolResponseParser toolResponseParser;

    public ToolResponseFormatterAdvisor(FormatterFactory formatterFactory, ToolResponseParser toolResponseParser) {
        this.formatterFactory = formatterFactory;
        this.toolResponseParser = toolResponseParser;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedResponse originalResponse = chain.nextAroundCall(advisedRequest);
        ChatResponse chatResponse = originalResponse.response();
        String content = chatResponse.getResult().getOutput().getText();

        System.out.println("AI answer: " + content);

        String formattedContent = formatResponse(content);

        if (!formattedContent.equals(content)) {
            ChatResponse newChatResponse = createFormattedResponse(chatResponse, formattedContent);
            return new AdvisedResponse(newChatResponse, originalResponse.adviseContext());
        }

        return originalResponse;
    }

    private String formatResponse(String content) {
        Optional<ToolResponseParser.ToolResponsePayload> payloadOptional = toolResponseParser.parse(content);

        return payloadOptional.map(payload -> formatterFactory
                .getFormatter(payload.toolUsed())
                .format(payload.data(), payload.message(), content))
                .orElse(content);
    }

    private ChatResponse createFormattedResponse(ChatResponse originalResponse, String newResponse) {
        Generation newGeneration = new Generation(new AssistantMessage(newResponse), originalResponse.getResult().getMetadata());
        return new ChatResponse(List.of(newGeneration), originalResponse.getMetadata());
    }

    /*private String formatJSONResponseIfNeeded(String content) {
        System.out.println("AI answer: " + content);

        String cleanedContent = cleanupAIResponse(content);
        System.out.println("Cleaned content: " + cleanedContent);
        try {
            if (content.trim().startsWith("{") && content.trim().endsWith("}")) {
                JsonNode jsonResponse = objectMapper.readTree(content);

                if (jsonResponse.has("toolUsed") && jsonResponse.has("data")) {
                    String toolUsed = jsonResponse.get("toolUsed").asText();
                    JsonNode data = jsonResponse.get("data");
                    String message = jsonResponse.has("message") ? jsonResponse.get("message").asText() : "";

                    return formatBasedOnTool(toolUsed, data, message, content);
                }
            }
            else {
                System.out.println("Answer not JSON"); // Debug
            }
        } catch(Exception e) {
            return content;
        }
        return content;
    }

    private String cleanupAIResponse(String content) {
        // Ako odgovor sadrži JSON, izvuci samo JSON deo
        String jsonPart = extractJsonFromResponse(content);
        if (jsonPart != null) {
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonPart);
                if (jsonNode.has("toolUsed") && jsonNode.has("data")) {
                    System.out.println("Found JSON, returning only JSON");
                    return jsonPart; // Vraćaj samo čisti JSON
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        return content; // Ako nema JSON-a, vrati original
    }

    private String extractJsonFromResponse(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        // Regularni izraz za pronalaženje JSON objekta
        // Traži `{...}` par zagrada, uključujući ugniježđene strukture
        String jsonRegex = "\\{.*\\}";

        Pattern pattern = Pattern.compile(jsonRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            // Vraća pronađeni JSON string
            return matcher.group();
        }

        // Ako JSON nije pronađen, vraća null
        return null;
    }

    private String formatBasedOnTool(String toolName, JsonNode data, String message, String originalContent) {
        return switch (toolName) {
            case "getAllAvailableFields" -> formatSlotsFromJson(data, message);
            case "getMyReservations" -> formatMyReservations(data, message);
            default -> originalContent;
        };
    }

    private String formatSlotsFromJson(JsonNode data, String message) {
        StringBuilder tableRows = new StringBuilder();

        if (data.isArray()) {
            for (JsonNode slot : data) {
                String field = slot.has("fieldName") ? slot.get("fieldName").asText() : "N/A";
                String startTime = slot.has("startTime") ? slot.get("startTime").asText() : "N/A";
                String endTime = slot.has("endTime") ? slot.get("endTime").asText() : "N/A";

                tableRows.append(String.format("| %s | %s | %s |\n", field, startTime, endTime));
            }

        }

        return String.format("""
        ## 📅 Available slots for booking
        
        %s
        
        | 🏟️ Field | ⏰ Start | ⏰ End |
        |----------|-----------|----------|
        %s
        
        💡 **For reservation just enter wanted field, date and time!**
        """, message, tableRows);
    }

    private String formatMyReservations(JsonNode data, String message) {
        StringBuilder reservationsText = new StringBuilder();

        System.out.println("JSON structure: " + data.toPrettyString());

        if (data.isArray() && !data.isEmpty()) {
            for (JsonNode reservation : data) {
                String field = reservation.has("fieldName") ? reservation.get("fieldName").asText() : "N/A";

                String date = reservation.has("date") ? reservation.get("date").asText() : "N/A";
                String startTime = reservation.has("time") ? reservation.get("time").asText() : "N/A";
                if (startTime.equals("N/A"))
                    startTime = reservation.has("startTime") ? reservation.get("startTime").asText() : "N/A";

                String endTime = "";
                if (startTime.equals("N/A")) {
                    endTime = "N/A";
                }
                else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime time = LocalTime.parse(startTime, formatter);
                    LocalTime newTime = time.plusHours(1);
                    endTime = newTime.format(formatter);
                }

                reservationsText.append(String.format("""
                **🏟️ Field:** %s  
                **📅 Date:** %s
                **⏰ Start:** %s
                **⏰ End:** %s
                ---
                """, field, date, startTime, endTime));
            }
        } else {
            reservationsText.append("There are no reservations made.");
        }


        return String.format("""
        ## 🎯 %s
        
        %s
        """, message, reservationsText);
    }*/

    @Override
    public String getName() {
        return "formatAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
