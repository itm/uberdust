package eu.uberdust.nodeflasher;

import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClientListener;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;

import java.util.List;

class TestbedClient implements ProtobufControllerClientListener {
    final WSNAsyncWrapper wsn;

    /**
     * @param wsn the wsn service wrapper
     */
    public TestbedClient(WSNAsyncWrapper wsn) {
        this.wsn = wsn;
    }

    /**
     * @param messages List of the messages received
     */
    public void receive(final List messages) {
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
    }

    public void experimentEnded() {
    }

    public void onConnectionEstablished() {
    }

    public void onConnectionClosed() {
    }
}
