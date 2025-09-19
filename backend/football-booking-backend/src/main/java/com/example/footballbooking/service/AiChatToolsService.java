package com.example.footballbooking.service;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.dto.FreeSlotDTO;
import com.example.footballbooking.dto.ReservationRequestDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.utilis.DateTimeParser;
import com.example.footballbooking.utilis.FreeSlotsUtilis;
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

@Component
public class AiChatToolsService {
    private final ReservationServiceImpl reservationService;
    private final FootballFieldServiceImpl fieldService;
    private final FreeSlotsUtilis freeSlotsUtilis;

    @Autowired
    public AiChatToolsService(ReservationServiceImpl reservationService,
                              FootballFieldServiceImpl fieldService,
                              FreeSlotsUtilis freeSlotsUtilis) {
        this.reservationService = reservationService;
        this.fieldService  = fieldService;
        this.freeSlotsUtilis = freeSlotsUtilis;
    }

    @Tool(description = "Returns a list of available slots(appointments) for the requested date")
    public List<FreeSlotDTO> getAllAvailableFields(String dateString) {
        LocalDate date = DateTimeParser.parseDate(dateString);

        try {
            List<FootballFieldDTO> fields = fieldService.getAllFields();
            if (fields.isEmpty()) {
                return Collections.emptyList();
            }

            Map<Long, Set<Integer>> reservedHoursByField = freeSlotsUtilis.loadReservedHoursForFields(fields, date);

            List<FreeSlotDTO> result = fields.parallelStream()
                    .map(field -> freeSlotsUtilis.
                            createFreeSlotsForField(field, reservedHoursByField.get(field.getId())))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            result.forEach(freeSlotDTO -> System.out.println(freeSlotDTO.getFieldName() + " " +
                    freeSlotDTO.getStartTime() + " " + freeSlotDTO.getEndTime()));

            return result;

        } catch (Exception e) {
            return List.of(new FreeSlotDTO.Builder().withMessage("Error while trying to fetch fields").build());
        }

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

    @Tool(description = "Returns available slots for a specific field on a given date")
    public List<FreeSlotDTO> getAvailableSlotsForField(String fieldName, String dateString) {
        LocalDate date = DateTimeParser.parseDate(dateString);

        try {
            List<FootballFieldDTO> fields = fieldService.getAllFields();
            FootballFieldDTO field = fields.stream()
                    .filter(f -> f.getName().equalsIgnoreCase(fieldName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown field: " + fieldName));

            Map<Long, Set<Integer>> reservedHours = freeSlotsUtilis.loadReservedHoursForFields(List.of(field), date);
            return freeSlotsUtilis.createFreeSlotsForField(field, reservedHours.get(field.getId()));
        } catch (Exception e) {
            return List.of(new FreeSlotDTO.Builder().withMessage("Error while trying to fetch field: " + fieldName).build());
        }
    }

    @Tool(description = "Returns available slots for all fields within a date range (e.g., from Monday to Friday)")
    public List<FreeSlotDTO> getAvailableSlotsInDateRange(String startDateString, String endDateString) {
        LocalDate startDate = DateTimeParser.parseDate(startDateString);
        LocalDate endDate = DateTimeParser.parseDate(endDateString);

        if (endDate.isBefore(startDate)) {
            return List.of(new FreeSlotDTO.Builder().withMessage("End date must be after start date").build());
        }

        try {
            List<FootballFieldDTO> fields = fieldService.getAllFields();
            if (fields.isEmpty()) {
                return Collections.emptyList();
            }

            List<FreeSlotDTO> allFreeSlots = new ArrayList<>();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                Map<Long, Set<Integer>> reservedHoursByField = freeSlotsUtilis.loadReservedHoursForFields(fields, date);

                LocalDate finalDate = date;

                List<FreeSlotDTO> dailyFreeSlots = fields.parallelStream()
                        .map(field -> freeSlotsUtilis.
                                createFreeSlotsForFieldWithDate(field, reservedHoursByField.get(field.getId()), finalDate))
                        .flatMap(Collection::stream)
                        .toList();

                allFreeSlots.addAll(dailyFreeSlots);
            }

            return allFreeSlots;

        } catch (Exception e) {
            return List.of(new FreeSlotDTO.Builder().withMessage("Error while trying to fetch fields").build());
        }
    }

    @Tool(description = "Returns all available football fields with their details")
    public List<FootballFieldDTO> getAllFootballFields() {
        try {
            return fieldService.getAllFields();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
