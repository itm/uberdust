package eu.uberdust.nodeflasher;

import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClientListener;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;

import java.util.List;
import java.util.logging.Logger;

class TestbedClient implements ProtobufControllerClientListener {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Helper.class);

    final transient WSNAsyncWrapper wsn;

    /**
     * @param wsn the wsn service wrapper
     */
    public TestbedClient(final WSNAsyncWrapper wsn) {
        this.wsn = wsn;
    }

    /**
     * @param messages List of the messages received
     */
    public void receive(final List messages) {
        LOGGER.debug("receive");
    }

    /**
     * @param requestStatuses list of the status received
     */
    public void receiveStatus(final List requestStatuses) {
        wsn.receive(requestStatuses);
    }

    /**
     * @param messages list of the notifications received
     */
    public void receiveNotification(final List messages) {
        LOGGER.debug("receiveNotification");
    }

    public void experimentEnded() {
        LOGGER.debug("experimentEnded");
    }

    public void onConnectionEstablished() {
        LOGGER.debug("onConnectionEstablished");
    }

    public void onConnectionClosed() {
        LOGGER.debug("onConnectionClosed");
    }
}
