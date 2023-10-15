package ir.co.sadad.departuretaxapi.utilities;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(profiles = {"qa"})
public class UtilityTest {

    @Test
    void paymentDate() {
        String inputDate = "10/24/22, 4:00 PM";
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yy, h:mm a");

        LocalDateTime localDateTime = LocalDateTime.parse(inputDate, inputFormatter);

        // Convert the localDateTime to ZonedDateTime in the Iran timezone
        ZonedDateTime iranZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Tehran"));

        // Convert the Iran time to UTC
        ZonedDateTime utcZonedDateTime = iranZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        // Format the result as a string
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        assertEquals("2022-10-24T12:30:00.000Z", utcZonedDateTime.format(outputFormatter));
    }

    @Test
    void createReferenceNumberByRandom(){
        Random random = new Random();
        char[] digits = new char[12];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < 12; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        assertEquals("391295685555", new String(digits));
    }

    @Test
    void currentDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        assertEquals("2023-09-12T09:44:52.123Z", formatter.format(Instant.now().atZone(ZoneId.of("UTC"))));
    }

}
