package com.example.footballbooking.utilis;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.dto.FreeSlotDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.service.ReservationServiceImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FreeSlotsUtilis {

    private final ReservationServiceImpl reservationService;

    public FreeSlotsUtilis(ReservationServiceImpl reservationService) {
        this.reservationService = reservationService;
    }

    public Map<Long, Set<Integer>> loadReservedHoursForFields(List<FootballFieldDTO> fields, LocalDate date) {
        List<Long> fieldIds = fields.stream()
                .map(FootballFieldDTO::getId)
                .collect(Collectors.toList());

        Map<Long, List<ReservationResponseDTO>> reservationsByField =
                reservationService.getAllReservationsByFieldIds(fieldIds, date);

        return reservationsByField.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(reservation -> reservation.getStartTime().getHour())
                                .collect(Collectors.toSet())
                ));
    }

    public List<FreeSlotDTO> createFreeSlotsForField(FootballFieldDTO field, Set<Integer> reservedHours) {
        if (reservedHours == null) {
            reservedHours = Collections.emptySet();
        }

        List<FreeSlotDTO> slots = new ArrayList<>();
        Integer slotStart = null;

        for (int hour = 9; hour < 23; hour++) {
            boolean isHourAvailable = !reservedHours.contains(hour);

            if (isHourAvailable) {
                if (slotStart == null) {
                    slotStart = hour;
                }
            } else {
                if (slotStart != null) {
                    slots.add(new FreeSlotDTO.Builder()
                            .withStartTime(String.format("%02d:00", slotStart))
                            .withEndTime(String.format("%02d:00", hour))
                            .withFieldName(field.getName())
                            .build());
                    slotStart = null;
                }
            }
        }

        if (slotStart != null) {
            slots.add(new FreeSlotDTO.Builder()
                    .withStartTime(String.format("%02d:00", slotStart))
                    .withEndTime(String.format("%02d:00", 23))
                    .withFieldName(field.getName())
                    .build());
        }

        return slots;
    }

    public List<FreeSlotDTO> createFreeSlotsForFieldWithDate(FootballFieldDTO field, Set<Integer> reservedHours, LocalDate date) {
        List<FreeSlotDTO> slots = createFreeSlotsForField(field, reservedHours);

        for (FreeSlotDTO slot : slots) {
            slot.setDate(date.toString());
        }

        return slots;
    }
}
