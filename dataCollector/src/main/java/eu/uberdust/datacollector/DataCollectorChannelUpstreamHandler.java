package eu.uberdust.datacollector;

import com.google.protobuf.InvalidProtocolBufferException;
import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import eu.uberdust.datacollector.parsers.MessageParser;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/1/11
 * Time: 5:04 PM
 */
public class DataCollectorChannelUpstreamHandler extends SimpleChannelUpstreamHandler {
    private static final Logger LOGGER = Logger.getLogger(DataCollectorChannelUpstreamHandler.class);
    /**
     * counts the messages received - stats.
     */
    private transient int messageCounter;
    /**
     *
     */
    private static final int REPORT_LIMIT = 1000;
    /**
     * saves the last time 1000 messages were received - stats.
     */
    private transient long lastTime;
    /**
     * executors for handling incoming messages.
     */
    private final transient ExecutorService executorService;
    private transient Map<String, String> sensors;
    private final transient DataCollector dataCollector;

    public DataCollectorChannelUpstreamHandler(final DataCollector dataCollector) {
        this.dataCollector = dataCollector;
        messageCounter = 0;
        lastTime = System.currentTimeMillis();

        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent messageEvent)
            throws InvalidProtocolBufferException {
        final Messages.Msg message = (Messages.Msg) messageEvent.getMessage();
        if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {
            final WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());
            parse(wsnAppMessage.toString());
            messageCounter++;
            if (messageCounter == REPORT_LIMIT) {
                final long milliseconds = System.currentTimeMillis() - lastTime;
                final double stat = messageCounter / (milliseconds / (double) REPORT_LIMIT);
                LOGGER.info("MessageRate : " + stat + " messages/sec");
                final ThreadPoolExecutor pool = (ThreadPoolExecutor) executorService;
                LOGGER.info("PoolSize : " + pool.getPoolSize() + " Active :" + pool.getActiveCount());
                LOGGER.info("Peak : " + pool.getLargestPoolSize());

                lastTime = System.currentTimeMillis();
                messageCounter = 0;
            }

        } else {
            LOGGER.error("got a message of type " + message.getMsgType());
        }
    }

    public void setSensors(final Map sensors) {
        this.sensors = sensors;

    }


    /**
     * @param ctx
     * @param channelStateEvent
     * @throws Exception
     */
    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent channelStateEvent)
            throws Exception {     //NOPMD
        super.channelDisconnected(ctx, channelStateEvent);
        shutdown();

    }

    /**
     * Shuts down the executorService
     */
    private void shutdown() {
        LOGGER.error("Shutting down!!!");
        executorService.shutdown();
        dataCollector.restart();
    }

    /**
     * @param toString
     */
    private void parse(final String toString) {
        executorService.submit(new MessageParser(toString, sensors));
    }

}
