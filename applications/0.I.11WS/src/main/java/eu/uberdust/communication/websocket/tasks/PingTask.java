package eu.uberdust.communication.websocket.tasks;

import eu.uberdust.communication.websocket.WSocketClient;
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

    private final static Logger LOGGER = Logger.getLogger(PingTask.class);

    public static final long DELAY = 30000;

    final Timer timer;

    public PingTask(final Timer timer) {
        super();
        this.timer = timer;
    }

    @Override
    public void run() {
        WSocketClient.getInstance().ping();
    }
}
