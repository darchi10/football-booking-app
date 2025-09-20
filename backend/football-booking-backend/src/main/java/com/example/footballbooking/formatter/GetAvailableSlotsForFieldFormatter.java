package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component("getAvailableSlotsForField")
public class GetAvailableSlotsForFieldFormatter implements Formatter{
    @Override
    public String format(JsonNode data, String message, String originalContent) {
        StringBuilder tableRows = new StringBuilder();
        String fieldName = "N/A";

        if (data.isArray()) {
            for (JsonNode slot : data) {
                fieldName = slot.has("fieldName") ? slot.get("fieldName").asText() : "N/A";
                String startTime = slot.has("startTime") ? slot.get("startTime").asText() : "N/A";
                String endTime = slot.has("endTime") ? slot.get("endTime").asText() : "N/A";

                tableRows.append(String.format("| %s | %s |\n", startTime, endTime));
            }
        }

        if (tableRows.isEmpty()) {
            return String.format("""
            ## 🏟️ Available slots for field
            
            %s
            
            ❌ **No available slots found for this field on the selected date.**
            
            💡 **Try checking another date or field!**
            """, message);
        }

        return String.format("""
        ## 🏟️ Available slots for field: %s
        
        %s
        
        | ⏰ Start | ⏰ End |
        |-----------|----------|
        %s
        
        💡 **Ready to book? Just tell me the time you prefer!**
        """, fieldName, message, tableRows);
    }
}
