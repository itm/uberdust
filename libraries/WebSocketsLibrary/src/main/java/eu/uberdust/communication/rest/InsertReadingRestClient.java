package eu.uberdust.communication.rest;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Implementation of a InsertReading client using Uberdust's REST interface.
 */
public final class InsertReadingRestClient {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static InsertReadingRestClient ourInstance = null;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingRestClient.class);

    /**
     * RestClient is loaded on the first execution of RestClient.getInstance()
     * or the first access to RestClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static InsertReadingRestClient getInstance() {
        synchronized (InsertReadingRestClient.class) {
            if (ourInstance == null) {
                ourInstance = new InsertReadingRestClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private InsertReadingRestClient() {
        // empty constructor
    }

    /**
     * Call RESTful Web Service.
     *
     * @param address , the address URL of service.
     * @return this call result.
     */
    public String callRestfulWebService(final String address) {
        try {
            final URL url = new URL(address);
            final URLConnection yc;

            yc = url.openConnection();

            final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            final StringBuilder inputLine = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                inputLine.append(str);
            }
            in.close();

            return inputLine.toString();
        } catch (final Exception e) {
            LOGGER.error(e);
            if (e.getMessage().contains("406")) {
                return "0\t0";
            }
            try {
                LOGGER.info("Retrying...");
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                LOGGER.error(e);
            }
            callRestfulWebService(address);
        }
        return "0\t0";
    }
}
