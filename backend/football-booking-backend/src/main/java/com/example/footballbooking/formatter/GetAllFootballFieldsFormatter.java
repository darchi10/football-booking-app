package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component("getAllFootballFields")
public class GetAllFootballFieldsFormatter implements Formatter{
    @Override
    public String format(JsonNode data, String message, String originalContent) {
        StringBuilder tableRows = new StringBuilder();

        if (data.isArray()) {
            for (JsonNode field : data) {
                String name = field.has("fieldName") ? field.get("fieldName").asText() : "N/A";
                String address = field.has("address") ? field.get("address").asText() : "N/A";
                String pricePerHour = field.has("pricePerHour") ? field.get("pricePerHour").asText() + "€" : "N/A";

                tableRows.append(String.format("| %s | %s | %s |\n", name, address, pricePerHour));
            }
        }

        if (tableRows.isEmpty()) {
            return String.format("""
            ## ⚽ Football Fields
            
            %s
            
            ❌ **No football fields found.**
            """, message);
        }

        return String.format("""
        ## ⚽ Available Football Fields
        
        %s
        
        | 🏟️ Field Name | 📍 Address | 💰 Price/Hour |
        |---------------|-------------|----------------|
        %s
        
        💡 **Want to check availability for any of these fields? Just ask!**
        """, message, tableRows);
    }
}
