package eu.uberdust.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/17/12
 * Time: 11:08 AM
 */
public class PropertyReader {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PropertyReader.class);

    private static PropertyReader instance = null;
    final Properties properties;
    private static final String PROPERTY_FILE = "testbedlistener.properties";

    private PropertyReader() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));

        properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTY_FILE));
        } catch (IOException e) {
            LOGGER.error("No properties file found! " + PROPERTY_FILE + " not found!");
            return;
        }
        LOGGER.info("Loaded properties from file: " + PROPERTY_FILE);
    }


    public static PropertyReader getInstance() {
        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }


    public Properties getProperties() {
        LOGGER.debug("getProperties()");
        return properties;
    }
}
