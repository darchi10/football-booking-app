package com.example.footballbooking.service;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.model.FootballField;
import com.example.footballbooking.repository.FootballFieldRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballFieldServiceImpl implements FootballFieldService {

    private final FootballFieldRepository footballFieldRepository;


    @Override
    public FootballFieldDTO createField(FootballFieldDTO fieldDTO) {
        FootballField field = mapToEntity(fieldDTO);
        FootballField savedField = footballFieldRepository.save(field);
        return mapToDTO(savedField);
    }

    @Override
    public List<FootballFieldDTO> getAllFields() {
        return footballFieldRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FootballFieldDTO getFieldById(Long id) {
        FootballField field = footballFieldRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FootballField not found with id: " + id));
        return mapToDTO(field);
    }

    @Override
    public FootballFieldDTO updateField(Long id, FootballFieldDTO fieldDTO) {
        FootballField existingField = footballFieldRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FootballField not found with id: " + id));

        existingField.setName(fieldDTO.getName());
        existingField.setAddress(fieldDTO.getAddress());
        existingField.setPrice_per_hour(fieldDTO.getPrice_per_hour());

        FootballField updatedField = footballFieldRepository.save(existingField);
        return mapToDTO(updatedField);
    }

    @Override
    public void deleteField(Long id) {
        if (!footballFieldRepository.existsById(id)) {
            throw new EntityNotFoundException("FootballField not found with id: " + id);
        }
        footballFieldRepository.deleteById(id);
    }

    private FootballFieldDTO mapToDTO(FootballField field) {
        FootballFieldDTO dto = new FootballFieldDTO();
        dto.setId(field.getId());
        dto.setName(field.getName());
        dto.setAddress(field.getAddress());
        dto.setPrice_per_hour(field.getPrice_per_hour());
        return dto;
    }

    private FootballField mapToEntity(FootballFieldDTO dto) {
        FootballField field = new FootballField();

        field.setName(dto.getName());
        field.setAddress(dto.getAddress());
        field.setPrice_per_hour(dto.getPrice_per_hour());
        return field;
    }
}
