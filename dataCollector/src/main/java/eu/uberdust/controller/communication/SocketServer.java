package eu.uberdust.controller.communication;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/5/11
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocketServer extends Thread {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(SocketServer.class);

    private static final int SERVER_PORT = 4444;

    private boolean isEnabled;

    private final ServerSocket serverSocket;

    /**
     * Default Constructor.
     */
    public SocketServer() {
        super();
        isEnabled = true;

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            LOGGER.info("Server is up and running");
        } catch (final IOException e) {
            LOGGER.error(e);
            throw new RuntimeException("Could not listen on port: 4444.");
        }
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

        while (isEnabled) {

            try {
                (new MessageHandler(serverSocket.accept())).start();
                LOGGER.info("New Connection established");
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * Enables/Disables the SocketServer.
     *
     * @param enabled boolean
     */
    public void setEnabled(final boolean enabled) {
        isEnabled = enabled;
    }

    public static void main(final String[] args) {
        SocketServer srv = new SocketServer();
        srv.start();
    }
}
