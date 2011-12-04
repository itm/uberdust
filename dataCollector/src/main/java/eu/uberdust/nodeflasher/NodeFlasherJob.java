package eu.uberdust.nodeflasher;

import eu.uberdust.nodeflasher.helper.Helper;
import eu.wisebed.api.rs.PublicReservationData;
import eu.wisebed.api.rs.RSExceptionException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Checks for reservations during the past minutes and flashes the devices with the default application.
 */
public class NodeFlasherJob implements Job {

    /**
     * Interval for executing.
     */
    public static final int INTERVAL = 60;
    /**
     * Time before to check for reservations.
     */
    private static final int MINUTES_BEFORE = 60;
    /**
     * Time after to check for reservations.
     */
    private static final int MINUTES_AFTER = 60;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeFlasherJob.class);
    /**
     * Helper class to connect to TR.
     */
    private final transient Helper helper;
    /**
     * Minutes to Milliseconds converter.
     */
    private static final int MILLIS_2_MINUTES = 60 * 1000;

    /**
     * Default Constructor.
     */
    public NodeFlasherJob() {
        helper = new Helper();
        helper.authenticate();
    }

    /**
     * @param jobExecutionCtx describes the running job.
     * @throws JobExecutionException something went wrong.
     */
    public final void execute(final JobExecutionContext jobExecutionCtx) throws JobExecutionException {
        checkAndFlash();
    }

    /**
     * Checks for reservations about the various node types.
     */
    private void checkAndFlash() {
        LOGGER.info(" |=== Starting a new nodeFlasher");

        checkReservations(helper.getNodes("nodes.isense"), "isense");
        checkReservations(helper.getNodes("nodes.telosb"), "telosb");
    }

    /**
     * Checks and flashes the nodes if needed.
     *
     * @param nodes the nodes to check for reservations
     * @param type  the type of the nodes
     */
    private void checkReservations(final String[] nodes, final String type) {

        LOGGER.info("|==  Checking for " + type + " nodes");

        if (emptyFutureList(nodes)) {
            if (existedPreviousList(nodes)) {
                LOGGER.info("|== Need to reFlash the " + type + " nodes.");
                helper.flash(nodes, type);
            } else {
                LOGGER.info("|=  No reservations found, No need to reFlash the " + type + " nodes.");
            }
        } else {
            LOGGER.info("|=  Pending Reservation, Cannot reFlash the " + type + " nodes now.");
        }
    }

    /**
     * Checks the previous MINUTES_BEFORE for reservations that include the nodes.
     *
     * @param nodes The nodes to check for reservations
     * @return True if there were reservations in the last MINUTES_BEFORE
     */
    public final boolean existedPreviousList(final String[] nodes) {
        final GregorianCalendar cal = new GregorianCalendar();
        long from = (new Date()).getTime() - MINUTES_BEFORE * MILLIS_2_MINUTES;
        cal.setTimeInMillis(from);
        XMLGregorianCalendar timeFrom;

        try {
            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("PreviousTimeFrom :" + e);
            return false;
        }

        from = (new Date()).getTime();
        cal.setTimeInMillis(from);
        XMLGregorianCalendar timeTo;
        try {
            timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("PreviousTimeTo :" + e);
            return false;
        }

        List previousResList;
        try {
            previousResList = helper.getReservations(timeFrom, timeTo);
        } catch (RSExceptionException e) {
            LOGGER.error("previousResList.getReservations " + e);
            return false;
        }

        for (int i = 0; i < previousResList.size(); i++) {
            final PublicReservationData pubData = (PublicReservationData) previousResList.get(i);
            boolean isRelevant = false;
            if (!helper.getUsername().equals(pubData.getUserData())) {
                for (String nodeUrn : nodes) {
                    if (pubData.getNodeURNs().toString().contains(nodeUrn)) {
                        isRelevant = true;
                    }
                }
            }
            if (!isRelevant) {
                previousResList.remove(i);
                //move back to the next reservation which is now current i
                i--;
            }
        }
        return !previousResList.isEmpty();
    }

    /**
     * Checks the next MINUTES_AFTER for reservations that include the nodes.
     *
     * @param nodes The nodes to check for reservations
     * @return True if no reservations in the next MINUTES_AFTER
     */
    public final boolean emptyFutureList(final String[] nodes) {

        final GregorianCalendar cal = new GregorianCalendar();
        long from = (new Date()).getTime();
        cal.setTimeInMillis(from);
        XMLGregorianCalendar timeFrom;

        try {
            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("FutureTimeFrom :" + e);
            return false;
        }

        from = (new Date()).getTime() + MINUTES_AFTER * MILLIS_2_MINUTES;
        cal.setTimeInMillis(from);
        XMLGregorianCalendar timeTo;
        try {
            timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("FutureTimeTo :" + e);
            return false;
        }

        List futureResList;

        try {
            futureResList = helper.getReservations(timeFrom, timeTo);
        } catch (RSExceptionException e) {
            LOGGER.error("futureResList.getReservations " + e);
            return false;
        }


        for (int i = 0; i < futureResList.size(); i++) {

            final PublicReservationData pubData = (PublicReservationData) futureResList.get(i);
            boolean isRelevant = false;
            for (String nodeUrn : nodes) {
                if (pubData.getNodeURNs().toString().contains(nodeUrn)) {
                    isRelevant = true;
                }
            }
            if (!isRelevant) {
                futureResList.remove(i);
                //move back to the next reservation which is now current i
                i--;
            }
        }


        return futureResList.isEmpty();

    }


}
