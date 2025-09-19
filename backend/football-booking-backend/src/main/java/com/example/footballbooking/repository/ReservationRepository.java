package com.example.footballbooking.repository;

import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.model.FootballField;
import com.example.footballbooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByUserId(Long userId);

    List<Reservation> findReservationsByFootballFieldId(Long id);

    Reservation findReservationByFootballFieldAndStartTime(FootballField footballField, LocalDateTime startTime);

    @Query("SELECT r FROM Reservation r WHERE r.footballField.id IN :fieldIds " +
            "AND DATE(r.startTime) = :date")
    List<Reservation> findByFieldIdsAndDate(@Param("fieldIds") List<Long> fieldIds,
                                            @Param("date") LocalDate date);

    List<Reservation> findByUserIdAndStartTimeAfter(Long userId, LocalDateTime now);
}
