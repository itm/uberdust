package eu.uberdust.communication.websocket.task;

import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;

import java.util.TimerTask;

/**
 * Ping task timer class.
 */
public class PingTask extends TimerTask {

    /**
     * Delay.
     */
    public static final long DELAY = 30000;

    /**
     * Constructor.
     */

    public PingTask() {
        super();
    }

    @Override
    public final void run() {
        InsertReadingWebSocketClient.getInstance().ping();
    }
}
