package eu.uberdust.nodeflasher;

import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClientListener;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;
import eu.uberdust.nodeflasher.helper.Helper;

import java.util.List;

/**
 * Client to connect to TR.
 */
public class TestbedClient implements ProtobufControllerClientListener {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Helper.class);
    /**
     * TR wsn connection.
     */
    private final transient WSNAsyncWrapper wsn;

    /**
     * @param wsnAsyncWrapper the wsn service wrapper
     */
    public TestbedClient(final WSNAsyncWrapper wsnAsyncWrapper) {
        this.wsn = wsnAsyncWrapper;
    }

    /**
     * @param messages List of the messages received
     */
    public final void receive(final List messages) {
        LOGGER.debug("receive");
    }

    /**
     * @param requestStatuses list of the status received
     */
    public final void receiveStatus(final List requestStatuses) {
        wsn.receive(requestStatuses);
    }

    /**
     * @param messages list of the notifications received
     */
    public final void receiveNotification(final List messages) {
        LOGGER.debug("receiveNotification");
    }

    /**
     * Experiment ended.
     */
    public final void experimentEnded() {
        LOGGER.debug("experimentEnded");
    }

    /**
     * Connection established.
     */
    public final void onConnectionEstablished() {
        LOGGER.debug("onConnectionEstablished");
    }

    /**
     * Connection closed.
     */
    public final void onConnectionClosed() {
        LOGGER.debug("onConnectionClosed");
    }
}
