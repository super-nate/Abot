package online.abot.alertbot.test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TimeZone {
    public static void main(String[] args) {

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

       /* String timestamp = "2016-02-16 11:00:02";
        TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(timestamp);
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        Instant result = Instant.from(zonedDateTime);*/



// works with ZonedDateTime
        ZonedDateTime zdt = ZonedDateTime.now();
        System.out.println(zdt.format(DateTimeFormatter.ISO_INSTANT));

        long now = Instant.now().toEpochMilli();
        System.out.println(now);
    }
}
