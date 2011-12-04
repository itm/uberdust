package eu.uberdust.websockets.insert;

import com.caucho.websocket.WebSocketServletRequest;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Insert Reading Web Socket controller class.
 */
public final class InsertReadingWebSocket implements Controller {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocket.class);

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = -279704326229266519L;

    /**
     * Insert Reading Web Socket Listener.
     */
    private InsertReadingWebSocketListener insertReadingWebSocketListener;

    /**
     * Default Constructor.
     */
    public InsertReadingWebSocket() {
        // empty constructor.
    }

    /**
     * Sets the web socket listener.
     * @param insertReadingWebSocketListener insert reading web socket listener.
     */
    public void setInsertReadingWebSocketListener(final InsertReadingWebSocketListener insertReadingWebSocketListener) {
        this.insertReadingWebSocketListener = insertReadingWebSocketListener;
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

        if (protocol == null || !protocol.equals(PROTOCOL)) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return null;
        }

        servletResponse.setHeader("Sec-WebSocket-Protocol", protocol);

        final WebSocketServletRequest wsRequest = (WebSocketServletRequest) servletRequest;
        wsRequest.startWebSocket(insertReadingWebSocketListener);

        return null;
    }
}

