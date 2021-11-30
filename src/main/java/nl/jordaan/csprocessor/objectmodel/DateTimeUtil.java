package nl.jordaan.csprocessor.objectmodel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeUtil {

    public static ZonedDateTime convertToZonedDateTime(Date date) {
        return date != null ? ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()) : null;
    }

}
