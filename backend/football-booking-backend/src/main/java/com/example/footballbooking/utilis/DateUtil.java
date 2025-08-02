package com.example.footballbooking.utilis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class DateUtil {
    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d.M.yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    );

    public static Optional<LocalDate> parse(String input) {
        for (DateTimeFormatter formatter :  SUPPORTED_FORMATS) {
            try {
                return Optional.of(LocalDate.parse(input, formatter));
            } catch (DateTimeParseException ignore) {}
        }
        return Optional.empty();
    }
}
