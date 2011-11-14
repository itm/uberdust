package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/14/11
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class LightTask extends TimerTask {

    private final static Logger LOGGER = Logger.getLogger(LightTask.class);

    public static final long DELAY = 30000;

    final Timer timer;

    public LightTask(final Timer timer) {
        super();
        this.timer = timer;
    }

    @Override
    public void run() {
        LOGGER.debug("Task to turn off Lights initialized");
        if (LightController.getInstance().isZone2()) {
            if (System.currentTimeMillis() - LightController.getInstance().getLastReading() > DELAY) {
                //turn off zone 2
                LOGGER.debug("Turn off zone 2");
                LightController.getInstance().controlLight(false, 2);

                //Re-schedule this timer to run in 30000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY);
            } else {
                //Re-schedule this timer to run in 5000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY / 6);
            }
        } else if (LightController.getInstance().isZone1()) {
            if (System.currentTimeMillis() - LightController.getInstance().getLastReading() > 30000) {
                //turn off zone 1
                LightController.getInstance().controlLight(false, 1);
                LOGGER.debug("Turn off zone 1");
            } else {
                //Re-schedule this timer to run in 5000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY / 6);
            }
        }
    }
}
