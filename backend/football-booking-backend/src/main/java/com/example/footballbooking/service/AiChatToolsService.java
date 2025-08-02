package com.example.footballbooking.service;

import com.example.footballbooking.dto.FootballFieldDTO;
import com.example.footballbooking.dto.FreeSlotDTO;
import com.example.footballbooking.dto.ReservationResponseDTO;
import com.example.footballbooking.utilis.DateUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Optional<LocalDate> optionalDate = DateUtil.parse(datumString);

        if (optionalDate.isEmpty()) {
            return List.of(new FreeSlotDTO("Greska, datum je potrebno napisati u drugom formatu"));
        }
        LocalDate date = optionalDate.get();

        if (date.isBefore(LocalDate.now())) {
            return List.of(new FreeSlotDTO("Greska, trazeni datum je u proslosti"));
        }

        List<FootballFieldDTO> fields = fieldService.getAllFields();
        List<FreeSlotDTO> result = new ArrayList<>();

        for (FootballFieldDTO field : fields) {
            List<ReservationResponseDTO> reservations = reservationService.getAllReservationsByFieldId(field.getId());

            List<Integer> availableHours = IntStream.range(9, 23)
                    .boxed()
                    .collect(Collectors.toList());

            for (ReservationResponseDTO reservation : reservations) {
                if (reservation.getStartTime().toLocalDate().equals(date)) {
                    availableHours.remove(Integer.valueOf(reservation.getStartTime().getHour()));
                }
            }

            if (!availableHours.isEmpty()) {
                int start = availableHours.get(0);
                int end = start;

                for (int i = 1; i < availableHours.size(); i++) {
                    if (availableHours.get(i) == end + 1) {
                        end++;
                    } else {
                        result.add(new FreeSlotDTO(start + ":00", end + 1 + ":00", field.getName()));
                        start = availableHours.get(i);
                        end = start;
                    }
                }

                result.add(new FreeSlotDTO(start + ":00", end + 1 + ":00", field.getName()));
            }
        }
        result.forEach(freeSlotDTO -> System.out.println(freeSlotDTO.getFieldName() + " " +
                freeSlotDTO.getStartTime() + " " + freeSlotDTO.getEndTime()));
        return result;
    }


}
