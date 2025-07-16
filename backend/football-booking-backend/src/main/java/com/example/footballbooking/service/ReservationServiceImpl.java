package com.example.footballbooking.service;

import com.example.footballbooking.dto.ReservationRequestDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.model.FootballField;
import com.example.footballbooking.model.Reservation;
import com.example.footballbooking.model.User;
import com.example.footballbooking.repository.FootballFieldRepository;
import com.example.footballbooking.repository.ReservationRepository;
import com.example.footballbooking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FootballFieldRepository fieldRepository;

    @Override
    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FootballField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new EntityNotFoundException("Field not found"));

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getStartTime().plusHours(1);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFootballField(field);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponseDTO(savedReservation);
    }

    //ogromna greska treba biti /api/field/getallreservations
    //jer se inace gledaju sve rezervacije za pojedine terene...
    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponseDTO> getMyReservations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return reservationRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setFieldId(reservation.getFootballField().getId());
        dto.setFieldName(reservation.getFootballField().getName());
        dto.setUsername(reservation.getUser().getUsername());

        return dto;
    }
}
