package eu.uberdust.lights;

import eu.uberdust.communication.rest.RestClient;
import eu.uberdust.communication.websocket.WSocketClient;
import eu.uberdust.lights.tasks.LightTask;
import eu.uberdust.uberlogger.UberLogger;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/14/11
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public final class LightController {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LightController.class);

    private final String REST_LINK =
            "http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x494/payload/1,";

    private boolean zone1;

    private boolean zone2;

    private long lastReading;

    private long zone1TurnedOnTimestamp;

    private long zone2TurnedOnTimestamp;

    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static LightController ourInstance = null;


    /**
     * LightController is loaded on the first execution of LightController.getInstance()
     * or the first access to LightController.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static LightController getInstance() {
        synchronized (LightController.class) {
            if (ourInstance == null) {
                ourInstance = new LightController();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private LightController() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("Light Controller initialized");
        zone1 = false;
        zone2 = false;
        timer = new Timer();
        WSocketClient.getInstance();
    }

    public long getLastReading() {
        return lastReading;
    }

    public void setLastReading(final long thatReading) {
        this.lastReading = thatReading;
        if (!zone1) {
            controlLight(true, 1);
            zone1TurnedOnTimestamp = thatReading;
            timer.schedule(new LightTask(timer), LightTask.DELAY);
        } else if (!zone2) {
            controlLight(true, 1);
            if (thatReading - zone1TurnedOnTimestamp > 15000) {
                controlLight(true, 2);
                zone2TurnedOnTimestamp = thatReading;
            }
        } else {
            controlLight(true, 2);
        }
    }


    public void controlLight(final boolean value, final int zone) {
        if (zone == 1) {
            zone1 = value;
        } else {
            zone2 = value;
        }
        final String reading = String.valueOf(lastReading);
        final StringBuilder linkBuilder = new StringBuilder(REST_LINK).append(zone).append(",").append(value ? 1 : 0);
        for (int i = 0; i < reading.length(); i++) {
            linkBuilder.append(",").append(reading.charAt(i));
        }
        UberLogger.getInstance().log(reading, "T7a");
        LOGGER.info(linkBuilder.toString());
        RestClient.getInstance().callRestfulWebService(linkBuilder.toString());
        UberLogger.getInstance().log(reading, "T7b");
    }

    public boolean isZone1() {
        return zone1;
    }

    public boolean isZone2() {
        return zone2;
    }

    public static void main(final String[] args) {
        LightController.getInstance();
    }
}

