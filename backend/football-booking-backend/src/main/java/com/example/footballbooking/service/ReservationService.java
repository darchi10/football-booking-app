package com.example.footballbooking.service;

import com.example.footballbooking.dto.ReservationRequestDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {
    ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO, String username);
    void deleteReservation(Long id);
    List<ReservationResponseDTO> getAllReservationsByFieldId(Long id);
    List<ReservationResponseDTO> getMyReservations(String username);
    List<ReservationResponseDTO> getMyIncomingReservations(String username);
    Map<Long, List<ReservationResponseDTO>> getAllReservationsByFieldIds(List<Long> fieldIds, LocalDate date);

}
