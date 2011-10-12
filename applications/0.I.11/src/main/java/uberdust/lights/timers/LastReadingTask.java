package uberdust.lights.timers;

import org.apache.log4j.Logger;
import uberdust.lights.LightController;

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

    private final static Logger LOGGER = Logger.getLogger(LastReadingTask.class);

    public static final int DELAY = 500;

    final Timer timer;

    public LastReadingTask(final Timer timer) {
        super();
        this.timer = timer;
    }

    @Override
    public void run() {

        final long previousPirEvent = LightController.getInstance().getLastReading();

        final long lastPirEvent = LightController.getInstance().lastPirEvent();
        LOGGER.info("Last pir event: " + new Date(lastPirEvent));
        if (System.currentTimeMillis() - lastPirEvent > 5000) {
            LOGGER.info("Too old Pir Event");
            return;
        }

        if (lastPirEvent - previousPirEvent > 0) {
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
    }
}
