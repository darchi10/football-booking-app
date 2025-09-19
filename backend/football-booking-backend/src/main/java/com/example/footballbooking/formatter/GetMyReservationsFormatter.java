package com.example.footballbooking.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component("getMyReservations")
public class GetMyReservationsFormatter implements Formatter{

    @Override
    public String format(JsonNode data, String message, String originalContent) {
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
    }
}
