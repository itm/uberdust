package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/10/11
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class LastReadingTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LastReadingTask.class);

    /**
     * Timer Delay.
     */
    public static final int DELAY = 500;

    /**
     * The timer.
     */
    private final Timer timer;

    /**
     * Default Constructor.
     *
     * @param thatTimer the timer
     */
    public LastReadingTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    /**
     * Timer Run.
     */
    @Override
    public final void run() {
        final long start = System.currentTimeMillis();
        final long previousPirEvent = LightController.getInstance().getLastReading();

        final long lastPirEvent = LightController.getInstance().lastPirEvent();

        if (System.currentTimeMillis() - lastPirEvent > 5000) {
            //LOGGER.info("Too old Pir Event");
            return;
        }

        if (lastPirEvent - previousPirEvent > 0) {
            LOGGER.info("New pir event: " + new Date(lastPirEvent));
            if (!LightController.getInstance().isZone1()) {
                //turn on Zone 1;
                LightController.getInstance().controlLight(true, 1);
                LOGGER.info("Turn On Zone 1");
                LightController.getInstance().setZone1(true);
                LightController.getInstance().setLastZone1Reading(lastPirEvent);
                //Start Timer to Turn off the Lights.
                timer.schedule(new LightTask(timer), LightTask.DELAY);

            } else if (LightController.getInstance().isZone1()) {
                if (lastPirEvent - LightController.getInstance().getLastZone1Reading() > 15000) {
                    if (!LightController.getInstance().isZone2()) {
                        //turn on Zone 2;
                        //RestClient.getInstance().callRestfulWebService();
                        LightController.getInstance().controlLight(true, 2);
                        LOGGER.info("Turn On Zone 2");
                        LightController.getInstance().setZone2(true);
                        LightController.getInstance().setLastZone2Reading(lastPirEvent);
                    }
                }
            }
            LightController.getInstance().setLastReading(lastPirEvent);
        }

        final long end = System.currentTimeMillis();
        LOGGER.info(end - start);
    }
}
