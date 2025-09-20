package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component("getAvailableSlotsInDateRange")
public class GetAvailableSlotsInDateRangeFormatter implements Formatter {
    @Override
    public String format(JsonNode data, String message, String originalContent) {
        StringBuilder tableRows = new StringBuilder();

        if (data.isArray()) {
            for (JsonNode slot : data) {
                String field = slot.has("fieldName") ? slot.get("fieldName").asText() : "N/A";
                String date = slot.has("date") ? slot.get("date").asText() : "N/A";
                String startTime = slot.has("startTime") ? slot.get("startTime").asText() : "N/A";
                String endTime = slot.has("endTime") ? slot.get("endTime").asText() : "N/A";

                tableRows.append(String.format("| %s | %s | %s | %s |\n", date, field, startTime, endTime));
            }
        }

        if (tableRows.isEmpty()) {
            return String.format("""
            ## 📅 Available slots in date range
            
            %s
            
            ❌ **No available slots found in the selected date range.**
            
            💡 **Try extending the date range or check individual dates!**
            """, message);
        }

        return String.format("""
        ## 📅 Available slots in date range
        
        %s
        
        | 📅 Date | 🏟️ Field | ⏰ Start | ⏰ End |
        |----------|-----------|-----------|----------|
        %s
        
        💡 **Found multiple options! Pick your preferred date, field and time for booking.**
        """, message, tableRows);
    }
}
