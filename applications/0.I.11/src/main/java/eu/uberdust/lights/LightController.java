package eu.uberdust.lights;


import eu.uberdust.communication.RestClient;
import eu.uberdust.lights.timers.LastReadingTask;
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
public class LightController {

    private final static Logger LOGGER = Logger.getLogger(LightController.class);

    private final String REST_LINK =
            new String("http://gold.cti.gr:8080/eu.uberdust/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x494/payload/1,");

    private final String READINGS_LINK =
            new String("http://gold.cti.gr:8080/eu.uberdust/rest/testbed/1/node/urn:wisebed:ctitestbed:0x1ccd/capability/urn:wisebed:node:capability:pir/latestreading");

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

    public void setLastReading(final long lastReading) {
        this.lastReading = lastReading;
    }


    public void controlLight(final boolean value, final int zone) {
        LOGGER.info(new StringBuilder(REST_LINK).append(zone).append(",").append(value ? 1 : 0).toString());
        RestClient.getInstance().callRestfulWebService(new StringBuilder(REST_LINK).append(zone).append(",").append(value ? 1 : 0).toString());

    }

    public boolean isZone1() {
        return zone1;
    }

    public void setZone1(boolean zone1) {
        this.zone1 = zone1;
    }

    public boolean isZone2() {
        return zone2;
    }

    public void setZone2(boolean zone2) {
        this.zone2 = zone2;
    }

    public long getLastZone1Reading() {
        return lastZone1Reading;
    }

    public void setLastZone1Reading(final long lastZone1Reading) {
        this.lastZone1Reading = lastZone1Reading;
    }

    public long getLastZone2Reading() {
        return lastZone2Reading;
    }

    public void setLastZone2Reading(final long lastZone2Reading) {
        this.lastZone2Reading = lastZone2Reading;
    }

    public long lastPirEvent() {
        final String str = RestClient.getInstance().callRestfulWebService(READINGS_LINK);
        return Long.parseLong(str.split("\t")[0]);
    }

    public static void main(String[] args) {
        LightController.getInstance();
        //LightController.getInstance();

    }
}
