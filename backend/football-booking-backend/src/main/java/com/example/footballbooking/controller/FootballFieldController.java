package com.example.footballbooking.controller;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.service.FootballFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FootballFieldController {

    private final FootballFieldService footballFieldService;

    @PostMapping
    public ResponseEntity<FootballFieldDTO> createField(@RequestBody FootballFieldDTO fieldDTO) {
        FootballFieldDTO createdField = footballFieldService.createField(fieldDTO);
        return new ResponseEntity<>(createdField, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FootballFieldDTO>> getAllFields() {
        List<FootballFieldDTO> fields =  footballFieldService.getAllFields();
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FootballFieldDTO> getFieldById(@PathVariable Long id) {
        FootballFieldDTO field = footballFieldService.getFieldById(id);
        return ResponseEntity.ok(field);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FootballFieldDTO> updateField(@PathVariable Long id,
                                                        @RequestBody FootballFieldDTO fieldDTO) {
        FootballFieldDTO updatedField = footballFieldService.updateField(id, fieldDTO);
        return ResponseEntity.ok(updatedField);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable Long id) {
        footballFieldService.deleteField(id);
        return ResponseEntity.noContent().build();
    }
}
