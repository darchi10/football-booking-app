package com.example.footballbooking.dto;

import lombok.Data;

@Data
public class FreeSlotDTO {
    private String startTime;
    private String endTime;
    private String fieldName;
    private String message;

    public FreeSlotDTO(String startTime, String endTime, String fieldName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.fieldName = fieldName;
    }

    public FreeSlotDTO(String message) {
        this.message = message;
    }
}
