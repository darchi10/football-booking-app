package com.example.footballbooking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FreeSlotDTO {
    private String description;
    private String fieldName;
    private String startTime;
    private String endTime;

    public FreeSlotDTO(String description, String name, String s, String s1) {
        this.description = description;
        this.fieldName = name;
        this.startTime = s;
        this.endTime = s1;
    }
}
