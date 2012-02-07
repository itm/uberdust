package eu.uberdust.communication.websocket.listener.tasks;


import eu.uberdust.communication.websocket.listener.WSocketClient;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/14/11
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PingTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PingTask.class);

    public static final long DELAY = 30000;

    private final Timer timer;

    public PingTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {
        WSocketClient.getInstance().ping();
    }
}
