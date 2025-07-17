package com.example.footballbooking.service;

import com.example.footballbooking.dto.ReservationRequestDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO, String username);
    void deleteReservation(Long id);
    List<ReservationResponseDTO> getAllReservationsByFieldId(Long id);
}
