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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        Optional<Reservation> sameReservation = Optional.ofNullable(reservationRepository
                .findReservationByFootballFieldAndStartTime(field, startTime));
        if (sameReservation.isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Field '%s' at %s is already booked. Please choose another time.",
                            field.getName(),
                            startTime.toLocalTime().toString()
                    )
            );
        }
        else {
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setFootballField(field);
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);

            Reservation savedReservation = reservationRepository.save(reservation);

            return mapToResponseDTO(savedReservation);
        }

    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public List<ReservationResponseDTO> getAllReservationsByFieldId(Long id) {

        return reservationRepository.findReservationsByFootballFieldId(id).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponseDTO> getMyReservations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return reservationRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<ReservationResponseDTO>> getAllReservationsByFieldIds(
            List<Long> fieldIds, LocalDate date) {

        try {
            List<Reservation> reservations = reservationRepository.findByFieldIdsAndDate(fieldIds, date);

            return reservations.stream()
                    .map(this::mapToResponseDTO) // konvertiraj Reservation -> ReservationResponseDTO
                    .collect(Collectors.groupingBy(
                            ReservationResponseDTO::getFieldId
                    ));
        } catch (Exception e) {
            return Collections.emptyMap(); // Vrati prazan Map u slučaju greške
        }
    }

    public List<ReservationResponseDTO> getMyIncomingReservations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        return reservationRepository.findByUserIdAndStartTimeAfter(user.getId(), now).stream()
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

    private Boolean checkIfFieldAlreadyBooked() {
        return false;
    }
}
