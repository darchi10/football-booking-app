package com.example.footballbooking.repository;

import com.example.footballbooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("SELECT r FROM Reservation r WHERE r.footballField.id = :fieldId AND r.startTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findConflictingReservations(Long fieldId, LocalDateTime startTime, LocalDateTime endTime);

    List<Reservation> findByUserId(Long userId);
}
