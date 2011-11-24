package eu.uberdust.websockets;

import com.caucho.websocket.WebSocketServletRequest;
import eu.wisebed.wisedb.listeners.LastNodeReadingConsumer;
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
import java.util.HashMap;

/**
 * Validates the initial HTTP request and  dispatches a new WebSocket connection.
 */
public class LastReadingWebSocket
        extends GenericServlet
        implements Controller {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LastReadingWebSocket.class);
    private static final long serialVersionUID = -7328799291894688509L;
    public static final String DELIMITER = "@";

    /**
     * A HashMap<CapabilityID:NodeID>.
     */
    private final HashMap<String, CustomWebSocketListener> listeners;

    /**
     * Default Constructor.
     */
    public LastReadingWebSocket() {
        super();
        listeners = new HashMap<String, CustomWebSocketListener>();
    }

    /**
     * Services the request.
     *
     * @param servletRequest  the servletRequest
     * @param servletResponse the servletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public ModelAndView handleRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        LOGGER.info("handleRequest");

        servletRequest.getSession().setMaxInactiveInterval(Integer.MAX_VALUE);
        /*
        * Process the handshake, selecting the protocol to be used.
        * The protocol is Defined by: NodeID:capabilityID
        */
        final String protocol = servletRequest.getHeader("Sec-WebSocket-Protocol");
        LOGGER.info(protocol);

        /**
         * TODO: FIX this check.
         */
        if (protocol == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return null;
        }

        final CustomWebSocketListener thisListener;
        if (listeners.containsKey(protocol)) {
            servletResponse.setHeader("Sec-WebSocket-Protocol", protocol);
            thisListener = listeners.get(protocol);
            LOGGER.info("registered listener");
        } else {
            thisListener = new CustomWebSocketListener(protocol.split(DELIMITER)[0], protocol.split(DELIMITER)[1]);
            LastNodeReadingConsumer.getInstance().registerListener(protocol.split(DELIMITER)[0], protocol.split(DELIMITER)[1],
                    thisListener);
            LOGGER.info("new listener");
            listeners.put(protocol, thisListener);
            servletResponse.setHeader("Sec-WebSocket-Protocol", protocol);
        }

        final WebSocketServletRequest wsRequest = (WebSocketServletRequest) servletRequest;
        wsRequest.startWebSocket(thisListener);

        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        LOGGER.info("service");
        try {
            handleRequest((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } catch (Exception ex) {
            LOGGER.fatal(ex);
        }
    }
}

