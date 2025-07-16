package com.example.footballbooking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    private Long fieldId;
    private LocalDateTime startTime;
}
