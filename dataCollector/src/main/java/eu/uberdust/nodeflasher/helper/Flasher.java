package eu.uberdust.nodeflasher.helper;

import com.google.common.collect.Lists;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import de.uniluebeck.itm.wisebed.cmdlineclient.jobs.JobResult;
import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClient;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;
import eu.uberdust.nodeflasher.TestbedClient;
import eu.wisebed.api.sm.ExperimentNotRunningException_Exception;
import eu.wisebed.api.sm.SecretReservationKey;
import eu.wisebed.api.sm.UnknownReservationIdException_Exception;
import eu.wisebed.api.wsn.WSN;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Used for flashing nodes.
 */
public class Flasher extends Helper {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Flasher.class);

    /**
     * Flashing success percentage.
     */
    private static final int SUCCESS = 100;

    /**
     * reference to helper class that contains properties.
     */
    private final Helper helper;

    /**
     * Constructor.
     *
     * @param helper the helper class
     */
    public Flasher(Helper helper) {
        this.helper = helper;
    }

    /**
     * Flashes the requested nodes if possible.
     *
     * @param nodes The nodes to flash
     * @param type  The type of the nodes, used to select the correct image
     */
    public void flash(final String[] nodes, final String type) {

        LOGGER.info("|+   flashing nodes of type: " + type);
        final String imagePath = helper.getProperties().getProperty("image." + type);
        LOGGER.info("|+   set image path to " + imagePath);

        final String reservationKey = reserveNodes(nodes);
        if ("".equals(reservationKey)) {
            return;
        }
        LOGGER.info("|+   reservationKey=" + reservationKey);

        final String wsnEndpointURL;
        try {
            final List<SecretReservationKey> keys = BeanShellHelper.parseSecretReservationKeys(reservationKey);
            wsnEndpointURL = helper.getSessionManagement().getInstance(keys, "NONE");
        } catch (ExperimentNotRunningException_Exception e) {
            LOGGER.error(e.toString());
            return;
        } catch (UnknownReservationIdException_Exception e) {
            LOGGER.error(e.toString());
            return;
        }

        LOGGER.info("|+   Got a WSN instance URL, endpoint is: " + wsnEndpointURL);
        final WSN wsnService = WSNServiceHelper.getWSNService(wsnEndpointURL);
        final WSNAsyncWrapper wsn = WSNAsyncWrapper.of(wsnService);

        final ProtobufControllerClient pcc = ProtobufControllerClient.create(helper.getPccHost(), helper.getPccPort(),
                BeanShellHelper.parseSecretReservationKeys(reservationKey));
        pcc.addListener(new TestbedClient(wsn));
        pcc.connect();

        // retrieve reserved node URNs from testbed
        final List nodeURNsToFlash = Lists.newArrayList(nodes);

        LOGGER.info("|+   Flashing nodes...");

        // flash iSense nodes
        final List programIndices = new ArrayList();
        final List programs = new ArrayList();
        for (Object aNodeURNsToFlash : nodeURNsToFlash) {
            programIndices.add(0);
        }

        try {
            programs.add(BeanShellHelper.readProgram(imagePath, "", "", "iSense", "1.0"));
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return;
        }

        final Future flashFuture = wsn.flashPrograms(nodeURNsToFlash, programIndices, programs, 3, TimeUnit.MINUTES);
        JobResult flashJobResult;

        try {
            flashJobResult = (JobResult) flashFuture.get();
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
            return;
        } catch (ExecutionException e) {
            LOGGER.error(e);
            return;
        }

        LOGGER.info(flashJobResult);
        if (flashJobResult.getSuccessPercent() < SUCCESS) {
            LOGGER.info("|*   Not all nodes could be flashed. Exiting");
        }

        LOGGER.info("|+   Closing connection...");
        pcc.disconnect();

    }
}
