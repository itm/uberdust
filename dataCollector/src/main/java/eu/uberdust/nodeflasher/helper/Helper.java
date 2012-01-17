package eu.uberdust.nodeflasher.helper;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import eu.uberdust.util.PropertyReader;
import eu.wisebed.api.rs.RS;
import eu.wisebed.api.rs.RSExceptionException;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.api.snaa.AuthenticationExceptionException;
import eu.wisebed.api.snaa.AuthenticationTriple;
import eu.wisebed.api.snaa.SNAA;
import eu.wisebed.api.snaa.SNAAExceptionException;
import eu.wisebed.testbed.api.rs.RSServiceHelper;
import eu.wisebed.testbed.api.snaa.helpers.SNAAServiceHelper;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Helper class for authenticating, reserving and flashing testbedruntime.
 */
public class Helper {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Helper.class);

    /**
     * TR res system.
     */
    private transient RS reservationSystem;
    /**
     * TR sm system.
     */
    private transient SessionManagement sessionManagement;
    /**
     * TR usernames.
     */
    private transient List usernames;
    /**
     * TR prefixes.
     */
    private transient List urnPrefixes;
    /**
     * TR authentication keys.
     */
    private transient List secretAuthKeys;
    /**
     * TR server protobuf host.
     */
    private transient String pccHost;
    /**
     * TR server protobuf port.
     */
    private transient int pccPort;
    /**
     * Error message when wrong user arguments were given.
     */
    private static final String WRONG_SIZE_ERROR_MESSAGE =
            "The list of URN prefixes must have the same length as the list of usernames and the list of passwords";

    /**
     * returns the reservation system used.
     *
     * @return the RS object
     */
    public final RS getReservationSystem() {
        return reservationSystem;
    }

    /**
     * returns the sm system used.
     *
     * @return the SM object
     */
    public final SessionManagement getSessionManagement() {

        return sessionManagement;
    }

    /**
     * returns the usernames used for the testbeds.
     *
     * @return a list of all declared usernames
     */
    public final List getUsernames() {
        return usernames;
    }

    /**
     * returns the prefixes used for the testbeds.
     *
     * @return a list of all declared urnPrefixes
     */
    public final List getUrnPrefixes() {
        return urnPrefixes;
    }

    /**
     * @return the authentication keys
     */
    public final List getSecretAuthKeys() {
        return secretAuthKeys;
    }

    /**
     * @return the port to connect to
     */
    public final int getPccPort() {
        return pccPort;
    }

    /**
     * @return the hostname to connect to
     */
    public final String getPccHost() {
        return pccHost;
    }

    /**
     * Default Constructor.
     */
    public Helper() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));
    }

    /**
     * Authenticates to the testbed authentication system.
     */
    public final void authenticate() {

        // Authentication credentials and other relevant information used again and again as method parameters

        final Splitter csvSplitter = Splitter.on(",").trimResults().omitEmptyStrings();

        final String urnPrefixesString = PropertyReader.getInstance().getProperties().getProperty("testbed.prefix");
        urnPrefixes = Lists.newArrayList(csvSplitter.split(urnPrefixesString));

        final String passFileName = PropertyReader.getInstance().getProperties().getProperty("testbed.passwords");
        FileInputStream fileInputStream = null;

        ArrayList<String> passwords = null;
        try {
            final Properties passProperties = new Properties();
            fileInputStream = new FileInputStream(passFileName);
            passProperties.load(fileInputStream);
            usernames = Lists.newArrayList(csvSplitter.split(passProperties.getProperty("testbed.usernames")));
            passwords = Lists.newArrayList(csvSplitter.split(passProperties.getProperty("testbed.passwords")));

        } catch (final IOException e) {
            LOGGER.error(e);

        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (final IOException e) {
                    LOGGER.error(e);
                }
            }
        }

        pccHost = PropertyReader.getInstance().getProperties().getProperty("testbed.hostname");
        pccPort = Integer.parseInt(PropertyReader.getInstance().getProperties().getProperty("testbed.port"));

        try {
            Preconditions.checkArgument(
                    urnPrefixes.size() == usernames.size() && usernames.size() == passwords.size(),
                    WRONG_SIZE_ERROR_MESSAGE
            );

        } catch (Exception e) {

            LOGGER.fatal(e);

        }

        // Endpoint URLs of Authentication (SNAA), Reservation (RS) and Experimentation (iWSN) services
        final String snaaEndpointURL = PropertyReader.getInstance().getProperties().getProperty("testbed.snaa.endpointurl");
        final String rsEndpointURL = PropertyReader.getInstance().getProperties().getProperty("testbed.rs.endpointurl");
        final String smEndpointURL = PropertyReader.getInstance().getProperties().getProperty("testbed.sm.endpointurl");


        // Retrieve Java proxies of the endpoint URLs above
        final SNAA authSystem = SNAAServiceHelper.getSNAAService(snaaEndpointURL);
        reservationSystem = RSServiceHelper.getRSService(rsEndpointURL);
        sessionManagement = WSNServiceHelper.getSessionManagementService(smEndpointURL);


        // build argument types
        final List credentialsList = new ArrayList();
        final AuthenticationTriple credentials = new AuthenticationTriple();
        for (int i = 0; i < urnPrefixes.size(); i++) {

            credentials.setUrnPrefix((String) urnPrefixes.get(i));
            credentials.setUsername((String) usernames.get(i));
            credentials.setPassword(passwords.get(i));

            credentialsList.add(credentials);
        }

        // do the authentication
        LOGGER.info("|   Authenticating to SNAA...");
        try {
            secretAuthKeys = authSystem.authenticate(credentialsList);
        } catch (AuthenticationExceptionException e) {
            LOGGER.error(e);
        } catch (SNAAExceptionException e) {
            LOGGER.error(e);
        }
        LOGGER.info("|   Successfully authenticated!");
    }

    /**
     * Retrieves the list of testbed reservations from the TestbedRuntime RS.
     *
     * @param timeFrom Starting time
     * @param timeTo   Ending time
     * @return list of the Reservations
     * @throws RSExceptionException an exception when unable to get reservations
     */
    public final List getReservations(final XMLGregorianCalendar timeFrom, final XMLGregorianCalendar timeTo)
            throws RSExceptionException {
        return reservationSystem.getReservations(timeFrom, timeTo);
    }

    /**
     * flashes the nodes with the default image defined by type.
     *
     * @param nodes The nodes to flash
     * @param type  The type of the nodes, used to select the correct image
     */
    public void flash(final String[] nodes, final String type) {
        (new Flasher(this)).flash(nodes, type);
    }

    /**
     * @param nodeType type of the devices
     * @return an array of the devices
     */
    public final String[] getNodes(final String nodeType) {
        LOGGER.info("| gotNodes " + nodeType);
        return PropertyReader.getInstance().getProperties().getProperty(nodeType).split(",");
    }

    /**
     * @return the first username registered
     */
    public final String getUsername() {
        return (String) usernames.get(0);
    }
}

