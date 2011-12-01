package eu.uberdust.websockets.insert;

import com.caucho.websocket.WebSocketServletRequest;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Insert Reading Web Socket controller class.
 */
public final class InsertReadingWebSocket extends GenericServlet
        implements Controller {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocket.class);

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = -279704326229266519L;


    /**
     * NodeReading persistence manager.
     */
    private transient NodeReadingController nodeReadingManager;

    /**
     * LinkReading persistence manager.
     */
    private transient LinkReadingController linkReadingManager;

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Insert Reading Web Socket Listener.
     */
    private transient InsertReadingWebSocketListener listener;


    /**
     * Default Constructor.
     */
    public InsertReadingWebSocket() {
        // empty constructor.
    }

    /**
     * Node Reading persistence manager.
     *
     * @param nodeReadingManager node reading persistence manager.
     */
    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
        LOGGER.info(this.nodeReadingManager.toString());
    }

    /**
     * Link Reading persistence manager.
     *
     * @param linkReadingManager persistence manager.
     */
    public void setLinkReadingManager(final LinkReadingController linkReadingManager) {
        this.linkReadingManager = linkReadingManager;
        LOGGER.info(this.linkReadingManager.toString());
    }

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
        LOGGER.info(this.testbedManager.list().size());
    }

    /**
     * Sets web socket listener.
     *
     * @param listener web socket listener.
     */
    public void setListener(final InsertReadingWebSocketListener listener) {
        this.listener = listener;
        LOGGER.info(this.listener.toString());
    }


    /**
     * Handles the request.
     *
     * @param servletRequest  the servletRequest.
     * @param servletResponse the servletResponse.
     * @return servlet response.
     * @throws javax.servlet.ServletException ServletException exception.
     * @throws java.io.IOException            IOException exception.
     */
    public ModelAndView handleRequest(final HttpServletRequest servletRequest,
                                      final HttpServletResponse servletResponse) throws ServletException, IOException {
        LOGGER.info("handleRequest()");
        /**
         * Protocol definition.
         */
        final String PROTOCOL = "INSERTREADING";

        servletRequest.getSession().setMaxInactiveInterval(Integer.MAX_VALUE);

        //Process the handshake, selecting the protocol to be used.
        final String protocol = servletRequest.getHeader("Sec-WebSocket-Protocol");
        LOGGER.info(protocol);

        if (protocol == null || !protocol.equals(PROTOCOL)) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return null;
        }

        servletResponse.setHeader("Sec-WebSocket-Protocol", protocol);

        LOGGER.info("handleRequest() -- 1");
        final WebSocketServletRequest wsRequest = (WebSocketServletRequest) servletRequest;
        wsRequest.startWebSocket(listener);

        LOGGER.info("handleRequest() -- 2");
        return null;
    }

    /**
     * Service Generic method implementation.
     *
     * @param servletRequest  servlet request.
     * @param servletResponse servlet response
     * @throws ServletException ServletException exception.
     * @throws IOException      IOException exception.
     */
    public void service(final ServletRequest servletRequest, final ServletResponse servletResponse)
            throws ServletException, IOException {
        try {
            LOGGER.info("service()");
            handleRequest((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } catch (Exception ex) {
            LOGGER.fatal(ex);
            ex.printStackTrace();
        }
    }
}

