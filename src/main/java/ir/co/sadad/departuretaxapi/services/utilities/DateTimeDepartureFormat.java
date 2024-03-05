package ir.co.sadad.departuretaxapi.services.utilities;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeDepartureFormat {

    public static String currentUTCDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return formatter.format(Instant.now().atZone(ZoneId.of("UTC")));
    }

    public static String paymentDate(String inputDate) {
//        String initiationDate = "10/24/22, 4:19 PM";
        if (inputDate == null)
            return null;
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yy, h:mm a");

            LocalDateTime localDateTime = LocalDateTime.parse(inputDate, inputFormatter);

            // Convert the localDateTime to ZonedDateTime in the Iran timezone
            ZonedDateTime iranZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Tehran"));

            // Convert the Iran time to UTC
            ZonedDateTime utcZonedDateTime = iranZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

            // Format the result as a string
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return utcZonedDateTime.format(outputFormatter);

        } catch (Exception e) {
            return inputDate;
        }
    }

    public static String pushOrderDate(String inputDate) {
        if (inputDate == null) // current date
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);
            DateTimeFormatter outPutFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
            return dateTime.format(outPutFormatter);

        } catch (Exception e) {
            return inputDate;
        }
    }

    public static ZonedDateTime daysBeforeCurrentUTCDate(Integer days) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return ZonedDateTime.parse(currentDateTime.minusDays(days).format(formatter));
    }
}
