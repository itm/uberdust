package eu.uberdust.controller.communication;

import eu.uberdust.controller.Controller;
import eu.uberdust.controller.TestbedController;
import eu.uberdust.controller.protobuf.CommandProtocol;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/5/11
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageHandler extends Thread {

    private final static Logger LOGGER  = Logger.getLogger(MessageHandler.class);

    private final Socket thisSocket;


    /**
     * Default Constructor.
     *
     * @param socket the Socket.
     */
    public MessageHandler(final Socket socket) {
        super();
        this.thisSocket = socket;
    }


    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p/>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        super.run();
         try {
            CommandProtocol.Command
                    cmd = CommandProtocol.Command.parseFrom(thisSocket.getInputStream());
            LOGGER.info("New Command Received:\n" + cmd.toString());
             TestbedController.getInstance().sendCommand(cmd.toBuilder());
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }
}
