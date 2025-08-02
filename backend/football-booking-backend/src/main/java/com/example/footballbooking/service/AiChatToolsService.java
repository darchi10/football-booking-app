package com.example.footballbooking.service;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.dto.FreeSlotDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiChatToolsService {
    private final ReservationServiceImpl reservationService;
    private final FootballFieldService fieldService;

    @Autowired
    public AiChatToolsService(ReservationServiceImpl reservationService, FootballFieldService fieldService) {
        this.reservationService = reservationService;
        this.fieldService  = fieldService;
    }

    @Tool(description = "Returns a list of available slots(appointments) for the requested date")
    public List<FreeSlotDTO> getAllAvailableFields(String datumString) {
        LocalDate date;
        try {
            date = LocalDate.parse(datumString);
        } catch (DateTimeParseException e) {
            return List.of(new FreeSlotDTO("Problem","Datum nije ispravan. Molimo koristi format YYYY-MM-DD.", "", ""));
        }

        if (date.isBefore(LocalDate.now())) {
            return List.of(new FreeSlotDTO("Greska", "Trazeni datum je u proslosti", "", ""));
        }

        List<FootballFieldDTO> fields = fieldService.getAllFields();
        List<FreeSlotDTO> result = new ArrayList<>();

        for (FootballFieldDTO field : fields) {
            List<ReservationResponseDTO> reservations = reservationService.getAllReservationsByFieldId(field.getId());

            List<Integer> workDayHours = new ArrayList<>();
            for (int i = 9; i < 23; i++) {
                workDayHours.add(i);
            }

            for (ReservationResponseDTO reservation : reservations) {
                if (reservation.getStartTime().toLocalDate().equals(date)) {
                    workDayHours.remove(Integer.valueOf(reservation.getStartTime().getHour()));
                }
            }

            if (!workDayHours.isEmpty()) {
                int start = workDayHours.get(0);
                int end = start;

                for (int i = 1; i < workDayHours.size(); i++) {
                    if (workDayHours.get(i) == end + 1) {
                        end++;
                    } else {
                        result.add(new FreeSlotDTO("Slobodan termin", field.getName(),
                                start + ":00", end + 1 + ":00"));
                        start = workDayHours.get(i);
                        end = start;
                    }
                }

                result.add(new FreeSlotDTO("slobodan termin", field.getName(),
                        start + ":00", end + 1 + ":00"));
            }
        }
        result.forEach(freeSlotDTO -> System.out.println(freeSlotDTO.getFieldName() + " " +
                freeSlotDTO.getStartTime() + " " + freeSlotDTO.getEndTime()));
        return result;
    }


}
