package eu.uberdust.lights;


import eu.uberdust.communication.RestClient;
import eu.uberdust.lights.tasks.LastReadingTask;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/7/11
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public final class LightController {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LightController.class);

    /**
     * Rest interface link.
     */
    private static final String REST_LINK =
            "http://gold.cti.gr/uberdust/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x494/payload/1,";

    /**
     * Last Reading link.
     */
    private static final String READINGS_LINK =
            "http://gold.cti.gr/uberdust/rest/testbed/1/node/urn:wisebed:ctitestbed:0x1ccd/capability/urn:wisebed:node:capability:pir/latestreading";

    private boolean zone1;
    private boolean zone2;
    private long lastReading;
    private long lastZone1Reading;
    private long lastZone2Reading;

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
        timer.scheduleAtFixedRate(new LastReadingTask(timer), LastReadingTask.DELAY, LastReadingTask.DELAY);
    }

    public long getLastReading() {
        return lastReading;
    }

    public void setLastReading(final long thatReading) {
        this.lastReading = thatReading;
    }


    public void controlLight(final boolean value, final int zone) {
        final String link = new StringBuilder(REST_LINK).append(zone).append(",").append(value ? 1 : 0).toString();
        LOGGER.info(link);
        RestClient.getInstance().callRestfulWebService(link);
    }

    public boolean isZone1() {
        return zone1;
    }

    public void setZone1(final boolean thatZone) {
        this.zone1 = thatZone;
    }

    public boolean isZone2() {
        return zone2;
    }

    public void setZone2(final boolean thatZone) {
        this.zone2 = thatZone;
    }

    public long getLastZone1Reading() {
        return lastZone1Reading;
    }

    public void setLastZone1Reading(final long thatZoneReading) {
        this.lastZone1Reading = thatZoneReading;
    }

    public long getLastZone2Reading() {
        return lastZone2Reading;
    }

    public void setLastZone2Reading(final long thatZoneReadin) {
        this.lastZone2Reading = thatZoneReadin;
    }

    public long lastPirEvent() {
        final String str = RestClient.getInstance().callRestfulWebService(READINGS_LINK);
        return Long.parseLong(str.split("\t")[0]);
    }

    public static void main(final String[] args) {
        LightController.getInstance();
    }
}
