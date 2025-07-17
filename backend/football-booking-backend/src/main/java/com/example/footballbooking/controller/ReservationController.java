package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ReservationRequestDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.service.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        try {
            ReservationResponseDTO response = reservationService.createReservation(request, currentUsername);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating reservation.");
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        List<ReservationResponseDTO> reservations = reservationService.getMyReservations(currentUsername);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservationsByFieldId(@RequestParam("fieldId") Long id) {
        List<ReservationResponseDTO> reservations = reservationService.getAllReservationsByFieldId(id);
        return ResponseEntity.ok(reservations);
    }

}
