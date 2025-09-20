package com.example.footballbooking.parser;


import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class DateTimeParser {

    private static final Parser parser = new Parser();

    public static LocalDateTime parse(String input) {
        List<DateGroup> groups = parser.parse(input);

        if (groups.isEmpty() || groups.get(0).getDates().isEmpty()) {
            throw new IllegalArgumentException("Ne mogu parsirati datum/vrijeme: " + input);
        }

        Date date = groups.get(0).getDates().get(0);
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDate parseDate(String input) {
        return parse(input).toLocalDate();
    }

    public static LocalTime parseTime(String input) {
        return parse(input).toLocalTime();
    }
}
