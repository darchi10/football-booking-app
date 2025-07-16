package com.example.footballbooking.service;

import com.example.footballbooking.dto.FootballFieldDTO;

import java.util.List;

public interface FootballFieldService {
    FootballFieldDTO createField(FootballFieldDTO fieldDTO);
    List<FootballFieldDTO> getAllFields();
    FootballFieldDTO getFieldById(Long id);
    FootballFieldDTO updateField(Long id, FootballFieldDTO fieldDTO);
    void deleteField(Long id);
}
