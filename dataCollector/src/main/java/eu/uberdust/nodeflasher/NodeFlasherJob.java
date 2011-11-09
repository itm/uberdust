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
    private transient final Helper helper;

    public NodeFlasherJob() {
        helper = new Helper();
    }

    public void execute(final JobExecutionContext jobExecutionCtx) throws JobExecutionException {
        checkAndFlash();
    }

    public void checkAndFlash() {
        LOGGER.info(" |=== Starting a new nodeFlasher");

        helper.authenticate();

        checkReservations(helper.getNodes("nodes.isense"), "isense");
        checkReservations(helper.getNodes("nodes.telosb"), "telosb");
    }

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

    private boolean existedPreviousList(final String[] nodes) {
        GregorianCalendar cal = new GregorianCalendar();
        long from = (new Date()).getTime() - MINUTES_BEFORE * 60 * 1000;
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

    private boolean emptyFutureList(final String[] nodes) {

        GregorianCalendar cal = new GregorianCalendar();
        long from = (new Date()).getTime();
        cal.setTimeInMillis(from);
        XMLGregorianCalendar timeFrom;

        try {
            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("FutureTimeFrom :" + e);
            return false;
        }

        from = (new Date()).getTime() + MINUTES_AFTER * 60 * 1000;
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
