package pt.psoft.g1.psoftg1.shared.util;

import java.time.format.DateTimeFormatter;

public class DateUtils {
    // ISO-8601 format: "2024-11-22"
    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    // Custom format example: "22-Nov-2024"
    public static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private DateUtils() {
    }
}
