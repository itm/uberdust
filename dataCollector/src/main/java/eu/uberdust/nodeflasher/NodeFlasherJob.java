package eu.uberdust.nodeflasher;

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

public class NodeFlasherJob implements Job {

    public static final int INTERVAL = 60;
    private static final int MINUTES_BEFORE = 40;
    private static final int MINUTES_AFTER = 60;

    private static final Logger LOGGER = Logger.getLogger(NodeFlasherJob.class);
    private final transient Helper helper;
    private static final int MILLIS_2_MINUTES = 60 * 1000;

    public NodeFlasherJob() {
        helper = new Helper();
    }

    /**
     * @param jobExecutionCtx
     * @throws JobExecutionException
     */
    public final void execute(final JobExecutionContext jobExecutionCtx) throws JobExecutionException {
        checkAndFlash();
    }

    private void checkAndFlash() {
        LOGGER.info(" |=== Starting a new nodeFlasher");

        helper.authenticate();

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
    private boolean existedPreviousList(final String[] nodes) {
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
            for (String nodeUrn : nodes) {
                if (pubData.getNodeURNs().toString().contains(nodeUrn)) {
                    isRelevant = true;
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
    private boolean emptyFutureList(final String[] nodes) {

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
