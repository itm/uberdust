package eu.uberdust.websockets.insert;

import com.caucho.websocket.WebSocketServletRequest;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
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
     * Insert Reading Web Socket Listener.
     */
    private InsertReadingWebSocketListener listener = null;


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
    }

    /**
     * Link Reading persistence manager.
     *
     * @param linkReadingManager persistence manager.
     */
    public void setLinkReadingManager(final LinkReadingController linkReadingManager) {
        this.linkReadingManager = linkReadingManager;
    }

    /**
     * Returns Web Socket listener.
     *
     * @return listener web socket listener.
     */
    public InsertReadingWebSocketListener getListener() {
        return listener;
    }

    /**
     * Sets web socket listener.
     * @param listener web socket listener.
     */
    public void setListener(final InsertReadingWebSocketListener listener) {
        this.listener = listener;
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

        // if listener is not defined initialize it
        if (listener == null) {
            listener = new InsertReadingWebSocketListener();

            // also adding peristence managers for inserting readings
            listener.setNodeReadingManager(nodeReadingManager);
            listener.setLinkReadingManager(linkReadingManager);
        }

        servletResponse.setHeader("Sec-WebSocket-Protocol", protocol);

        final WebSocketServletRequest wsRequest = (WebSocketServletRequest) servletRequest;
        wsRequest.startWebSocket(listener);

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
            handleRequest((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } catch (Exception ex) {
            LOGGER.fatal(ex);
        }
    }
}

