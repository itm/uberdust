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
    private final Helper helper;

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
        try {

            GregorianCalendar cal = new GregorianCalendar();
            long from = (new Date()).getTime();
            cal.setTimeInMillis(from);
            XMLGregorianCalendar timeFrom;

            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            from = (new Date()).getTime() + MINUTES_AFTER * 60 * 1000;
            cal.setTimeInMillis(from);
            XMLGregorianCalendar timeTo;
            timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            List futureResList;

            futureResList = helper.getReservations(timeFrom, timeTo);


            for (int i = 0; i < futureResList.size(); i++) {
                final PublicReservationData pubData = (PublicReservationData) futureResList.get(i);

                boolean isRelevant = false;
                for (String nodeurn : nodes) {
                    if (pubData.getNodeURNs().toString().contains(nodeurn)) {
                        isRelevant = true;
                    }
                }
                if (!isRelevant) {
                    futureResList.remove(i);
                    //move back to the next reservation which is now current i
                    i--;
                }
            }


            cal = new GregorianCalendar();
            from = (new Date()).getTime() - MINUTES_BEFORE * 60 * 1000;
            cal.setTimeInMillis(from);


            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            from = (new Date()).getTime();
            cal.setTimeInMillis(from);

            timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            List previousResList;
            previousResList = helper.getReservations(timeFrom, timeTo);

            for (int i = 0; i < previousResList.size(); i++) {
                final PublicReservationData pubData = (PublicReservationData) previousResList.get(i);
                boolean isRelevant = false;
                for (String nodeurn : nodes) {
                    if (pubData.getNodeURNs().toString().contains(nodeurn)) {
                        isRelevant = true;
                    }
                }
                if (!isRelevant) {
                    previousResList.remove(i);
                    //move back to the next reservation which is now current i
                    i--;
                }
            }


            if (futureResList.isEmpty()) {
                if (previousResList.isEmpty()) {
                    LOGGER.info("|=  No reservations found, No need to reFlash the " + type + " nodes.");
                } else {
                    LOGGER.info("|== Need to reFlash the " + type + " nodes.");
                    helper.flash(nodes, type);
                }
            } else {
                LOGGER.info("|=  Pending Reservation, Cannot reFlash the " + type + " nodes now.");
            }


        } catch (DatatypeConfigurationException e) {
            LOGGER.error(e);
        } catch (RSExceptionException e) {
            LOGGER.error(e);
        }

    }


}
