package com.example.footballbooking.service;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.dto.FreeSlotDTO;
import com.example.footballbooking.dto.ReservationRequestDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.utilis.DateTimeParser;
import com.example.footballbooking.utilis.DateUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class AiChatToolsService {
    private final ReservationServiceImpl reservationService;
    private final FootballFieldServiceImpl fieldService;

    @Autowired
    public AiChatToolsService(ReservationServiceImpl reservationService, FootballFieldServiceImpl fieldService) {
        this.reservationService = reservationService;
        this.fieldService  = fieldService;
    }

    @Tool(description = "Returns a list of available slots(appointments) for the requested date")
    public List<FreeSlotDTO> getAllAvailableFields(String dateString) {
        LocalDate date = DateTimeParser.parseDate(dateString);

        try {
            List<FootballFieldDTO> fields = fieldService.getAllFields();
            if (fields.isEmpty()) {
                return Collections.emptyList();
            }

            Map<Long, Set<Integer>> reservedHoursByField = loadReservedHoursForFields(fields, date);

            List<FreeSlotDTO> result = fields.parallelStream()
                    .map(field -> createFreeSlotsForField(field, reservedHoursByField.get(field.getId())))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            result.forEach(freeSlotDTO -> System.out.println(freeSlotDTO.getFieldName() + " " +
                    freeSlotDTO.getStartTime() + " " + freeSlotDTO.getEndTime()));

            return result;

        } catch (Exception e) {
            return List.of(new FreeSlotDTO("Error while trying to fetch fields"));
        }

    }

    private Map<Long, Set<Integer>> loadReservedHoursForFields(List<FootballFieldDTO> fields, LocalDate date) {
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

    private List<FreeSlotDTO> createFreeSlotsForField(FootballFieldDTO field, Set<Integer> reservedHours) {
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
                    slots.add(new FreeSlotDTO(
                            String.format("%02d:00", slotStart),
                            String.format("%02d:00", hour),
                            field.getName()
                    ));
                    slotStart = null;
                }
            }
        }

        if (slotStart != null) {
            slots.add(new FreeSlotDTO(
                    String.format("%02d:00", slotStart),
                    String.format("%02d:00", 23),
                    field.getName()
            ));
        }

        return slots;
    }

    @Tool(description = "Returns a list of reservations for the given user")
    public List<ReservationResponseDTO> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return reservationService.getMyIncomingReservations(username);
    }

    @Tool(description = "Creates a reservation for the current user. Requires field name, date and start time.")
    public ReservationResponseDTO createReservation(String fieldName, String dateString, String timeString) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        LocalDateTime parsed = DateTimeParser.parse(dateString + " " + timeString);
        LocalDate date = parsed.toLocalDate();
        LocalTime time = parsed.toLocalTime();

        List<FootballFieldDTO> fields = fieldService.getAllFields();
        FootballFieldDTO field = fields.stream()
                .filter(f -> f.getName().equalsIgnoreCase(fieldName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknow field: " + fieldName));

        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setFieldId(field.getId());
        request.setStartTime(LocalDateTime.of(date, time));

        return reservationService.createReservation(request, currentUsername);
    }

}
