package eu.uberdust.websockets.insert;

import com.caucho.websocket.WebSocketServletRequest;
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
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/25/11
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class InsertReadingWebSocket extends GenericServlet
        implements Controller {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocket.class);


    private static final long serialVersionUID = -7328299291894688509L;


    /**
     * A HashMap<Protocol:NodeID>.
     */
    private InsertReadingWebSocketListener listener;

    private static final String PROTOCOL = "newReading";

    /**
     * Default Constructor.
     */
    public InsertReadingWebSocket() {
        super();
        listener = null;
    }

    /**
     * Services the request.
     *
     * @param servletRequest  the servletRequest
     * @param servletResponse the servletResponse
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    public ModelAndView handleRequest(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws Exception {
        LOGGER.info("handleRequest");

        servletRequest.getSession().setMaxInactiveInterval(Integer.MAX_VALUE);
        /*
        * Process the handshake, selecting the protocol to be used.
        * The protocol is Defined by: NodeID:capabilityID
        */
        final String protocol = servletRequest.getHeader("Sec-WebSocket-Protocol");
        LOGGER.info(protocol);

        if (protocol == null || !protocol.equals(PROTOCOL)) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return null;
        }

        if (listener == null) {
            listener = new InsertReadingWebSocketListener();
        }

        servletResponse.setHeader("Sec-WebSocket-Protocol", protocol);

        final WebSocketServletRequest wsRequest = (WebSocketServletRequest) servletRequest;
        wsRequest.startWebSocket(listener);

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

