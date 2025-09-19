package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component("getAllAvailableFields")
public class GetAllAvailableFieldsFormatter implements Formatter {
    @Override
    public String format(JsonNode data, String message, String originalContent) {
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
}
