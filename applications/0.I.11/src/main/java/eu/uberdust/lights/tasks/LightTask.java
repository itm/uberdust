package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/10/11
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class LightTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LightTask.class);

    /**
     * Timer Delay.
     */
    public static final long DELAY = 30000;

    /**
     * The Timer.
     */
    private final Timer timer;

    /**
     * Default Constructor.
     *
     * @param thatTimer the timer
     */
    public LightTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    /**
     * Timer run function.
     */
    @Override
    public final void run() {
        LOGGER.debug("Task to turn off Lights initialized");
        if (System.currentTimeMillis() - LightController.getInstance().getLastReading() > 30000) {
            if (LightController.getInstance().isZone2()) {

                //turn off zone 2
                LOGGER.debug("Turn off zone 2");
                LightController.getInstance().controlLight(false, 2);
                LightController.getInstance().setZone2(false);
                LightController.getInstance().setLastReading(System.currentTimeMillis() - 2000);
                //Re-schedule this timer to run in 15000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY / 6);

            } else if (LightController.getInstance().isZone1()
                    && System.currentTimeMillis() - LightController.getInstance().getLastReading() > 30000) {
                //turn off zone 1
                LightController.getInstance().controlLight(false, 1);
                LOGGER.debug("Turn off zone 1");
                LightController.getInstance().setZone1(false);

            } else if (LightController.getInstance().isZone1()) {
                //Re-schedule this timer to run in 15000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY / 6);
            }
        } else {
            //Re-schedule this timer to run in 7500ms to turn off
            timer.schedule(new LightTask(timer), DELAY / 3);
        }
    }
}
