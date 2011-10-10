package uberdust.util;

import com.hp.hpl.jena.reasoner.rulesys.builtins.Now;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Util {


    public static boolean checkIfDateIsToday(final Date date) {

        final long MILLISECS_PER_MINUTE = 60 * 1000;
        final long MILLISECS_PER_HOUR = 60 * MILLISECS_PER_MINUTE;
        final long MILLISECS_PER_DAY = 24 * MILLISECS_PER_HOUR;

        Calendar now = new GregorianCalendar();
        now.setTime(new Date());

        Calendar then = new GregorianCalendar();
        then.setTime(date);

        long nowL = now.getTimeInMillis() + now.getTimeZone().getOffset(now.getTimeInMillis());
        long thenL = then.getTimeInMillis() + then.getTimeZone().getOffset(then.getTimeInMillis());
        long diff = (nowL - thenL) / MILLISECS_PER_DAY;
        if (diff != 0) {
            return false;
        }
        return true;
    }
}
