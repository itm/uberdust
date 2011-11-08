package eu.uberdust.websockets;

import com.caucho.websocket.WebSocketServletRequest;
import eu.wisebed.wisedb.listeners.LastNodeReadingObservable;
import org.apache.log4j.Logger;

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
public class LastReadingWebSocket extends GenericServlet {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LastReadingWebSocket.class);
    private static final long serialVersionUID = -7328799291894688509L;


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
    public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
        final HttpServletRequest req = (HttpServletRequest) servletRequest;
        final HttpServletResponse res = (HttpServletResponse) servletResponse;

        /*
        * Process the handshake, selecting the protocol to be used.
        * The protocol is Defined by: capabilityID:NodeID
        */
        final String protocol = req.getHeader("Sec-WebSocket-Protocol");

        /**
         * TODO: FIX this check.
         */
        if (protocol == null) {
            res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        final CustomWebSocketListener thisListener;
        if (listeners.containsKey(protocol)) {
            res.setHeader("Sec-WebSocket-Protocol", protocol);
            thisListener = listeners.get(protocol);
        } else {
            thisListener = new CustomWebSocketListener(protocol);
            LOGGER.info(LastNodeReadingObservable.getInstance().countObservers());
            LastNodeReadingObservable.getInstance().addObserver(thisListener);
            LOGGER.info(LastNodeReadingObservable.getInstance().countObservers());
            listeners.put(protocol, thisListener);
            res.setHeader("Sec-WebSocket-Protocol", protocol);
        }

        final WebSocketServletRequest wsRequest = (WebSocketServletRequest) servletRequest;
        wsRequest.startWebSocket(thisListener);
    }

}

