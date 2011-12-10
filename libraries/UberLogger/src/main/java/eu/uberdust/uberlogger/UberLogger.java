package eu.uberdust.uberlogger;

import eu.wisebed.wisedb.model.NodeReading;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 12/5/11
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class UberLogger {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UberLogger.class);


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static UberLogger ourInstance = null;


    /**
     * UberLogger is loaded on the first execution of UberLogger.getInstance()
     * or the first access to UberLogger.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static UberLogger getInstance() {
        synchronized (UberLogger.class) {
            if (ourInstance == null) {
                ourInstance = new UberLogger();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private UberLogger() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));
    }

    public final void log(final NodeReading nodeReading, final String level) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(level).append(" -- ").append("ID: ").append(nodeReading.getTimestamp().getTime()).append(" , ");
        stringBuilder.append(System.currentTimeMillis());
        LOGGER.info(stringBuilder.toString());
    }

    public final void log(final String nodeReadingID, final String level) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(level).append(" -- ").append("ID: ").append(nodeReadingID).append(" , ");
        stringBuilder.append(System.currentTimeMillis());
        LOGGER.info(stringBuilder.toString());
    }

    public final void log(final long nodeReadingID, final String level) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(level).append(" -- ").append("ID: ").append(nodeReadingID).append(" , ");
        stringBuilder.append(System.currentTimeMillis());
        LOGGER.info(stringBuilder.toString());
    }

    public static void main(final String[] args) {
        UberLogger.getInstance();
    }
}

