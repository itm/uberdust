package eu.uberdust.nodeflasher.helper;

import com.google.common.collect.Lists;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import eu.wisebed.api.rs.AuthorizationExceptionException;
import eu.wisebed.api.rs.ConfidentialReservationData;
import eu.wisebed.api.rs.RSExceptionException;
import eu.wisebed.api.rs.ReservervationConflictExceptionException;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Used for reserving nodes an managing reservations.
 */
public class Reserver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Reserver.class);
    /**
     * Helper class.
     */
    private final transient Helper helper;

    /**
     * Reserves the requested nodes if possible.
     *
     * @param nodes the nodes to reserve
     * @return the reservation key
     */
    public final String reserve(final String[] nodes) throws ReservervationConflictExceptionException {
        helper.authenticate();

        LOGGER.info("|+   Trying to reserve " + nodes.length + " nodes");

        final List nodeURNsToReserve = Lists.newArrayList(nodes);
        // create reservation request data to reserve all iSense nodes for 10 minutes
        final ConfidentialReservationData reservationData = BeanShellHelper.generateConfidentialReservationData(
                nodeURNsToReserve, new Date(System.currentTimeMillis()), 4, TimeUnit.MINUTES,
                helper.getUrnPrefixes(), helper.getUsernames());

        List secretResKeys;
        try {
            secretResKeys = helper.getReservationSystem().makeReservation(
                    BeanShellHelper.copySnaaToRs(helper.getSecretAuthKeys()),
                    reservationData
            );
        } catch (AuthorizationExceptionException e) {
            LOGGER.error(e);
            return "";
        } catch (RSExceptionException e) {
            LOGGER.error(e);
            return "";
        }
        LOGGER.info("|+   Successfully reserved " + nodeURNsToReserve.size() + " nodes");
        LOGGER.info("|+   Reservation Key(s): " + BeanShellHelper.toString(secretResKeys));

        return BeanShellHelper.toString(secretResKeys);

    }

    /**
     * Constructor.
     *
     * @param helper the helper class
     */
    public Reserver(final Helper helper) {
        this.helper = helper;
    }
}
