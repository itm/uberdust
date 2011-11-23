package eu.uberdust.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility abstract class.
 */
public abstract class Util {

    /**
     * Milliseconds per minute.
     */
    private static final long MILLISECS_PER_MINUTE = 60 * 1000;

    /**
     * Milliseconds per hour.
     */
    private static final long MILLISECS_PER_HOUR = 60 * MILLISECS_PER_MINUTE;

    /**
     * Milliseconds per day.
     */
    private static final long MILLISECS_PER_DAY = 24 * MILLISECS_PER_HOUR;


    /**
     * Returns check if the date provided references today.
     *
     * @param date date
     * @return true or false.
     */
    public static boolean checkIfDateIsToday(final Date date) {


        final Calendar now = new GregorianCalendar();
        now.setTime(new Date());

        final Calendar then = new GregorianCalendar();
        then.setTime(date);

        final long nowL = now.getTimeInMillis() + now.getTimeZone().getOffset(now.getTimeInMillis());
        final long thenL = then.getTimeInMillis() + then.getTimeZone().getOffset(then.getTimeInMillis());
        final long diff = (nowL - thenL) / MILLISECS_PER_DAY;
        return (diff == 0);
    }
}
