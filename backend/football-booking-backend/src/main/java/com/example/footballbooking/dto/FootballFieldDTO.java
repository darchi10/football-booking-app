package com.example.footballbooking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FootballFieldDTO {
    private Long id;
    private String name;
    private String address;
    private BigDecimal price_per_hour;
}
