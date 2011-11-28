package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/24/11
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class TurnOffTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TurnOffTask.class);

    public static final long DELAY = 30000;

    public TurnOffTask() {
        super();
    }

    @Override
    public final void run() {
        LOGGER.debug("Task to turn off Lights initialized");
        if (LightController.getInstance().isScreenLocked()) {
            LightController.getInstance().controlLight(false, 2);
        }
    }
}

