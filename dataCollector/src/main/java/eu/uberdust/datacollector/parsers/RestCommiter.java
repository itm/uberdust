package eu.uberdust.datacollector.parsers;

import eu.uberdust.reading.LinkReading;
import eu.uberdust.reading.NodeReading;
import eu.uberdust.uberlogger.UberLogger;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/4/11
 * Time: 2:15 PM
 */
public class RestCommiter {

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(RestCommiter.class);

    /**
     * The base url of the uberdust server.
     */
    private static final String TESTBED_SERVER = "http://uberdust.cti.gr/rest";

    /**
     * Add a new nodeReading using the rest interfaces.
     *
     * @param nodeReading the node reading to add
     */
    public RestCommiter(final NodeReading nodeReading) {

        if ("urn:wisebed:ctitestbed:0x1ccd".equals(nodeReading.getNodeId())
                && "urn:wisebed:node:capability:pir".equals(nodeReading.getCapabilityName())) {
            UberLogger.getInstance().log(nodeReading.getTimestamp(), "Τ22");
        }
        final StringBuilder urlBuilder = new StringBuilder(TESTBED_SERVER);
        urlBuilder.append(nodeReading.toRestString());
        final String insertReadingUrl = urlBuilder.toString();
        callUrl(insertReadingUrl);
    }

    /**
     * Adds a new link reading using the rest interfaces.
     *
     * @param linkReading the link reading to add
     */
    public RestCommiter(final LinkReading linkReading) {
        final StringBuilder urlBuilder = new StringBuilder(TESTBED_SERVER);
        urlBuilder.append(linkReading.toRestString());
        final String insertReadingUrl = urlBuilder.toString();
        callUrl(insertReadingUrl);

    }

    /**
     * Opens a connection over the Rest Interfaces to the server and adds the event.
     *
     * @param urlString the string url that describes the event
     */
    private void callUrl(final String urlString) {
        HttpURLConnection httpURLConnection = null;

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LOGGER.error(e);
            return;
        }

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                LOGGER.debug("Added " + urlString);
            } else {
                final StringBuilder errorBuilder = new StringBuilder("Problem ");
                errorBuilder.append("with ").append(urlString);
                errorBuilder.append(" Response: ").append(httpURLConnection.getResponseCode());
                LOGGER.error(errorBuilder.toString());
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            LOGGER.error(e);
        }


    }
}
