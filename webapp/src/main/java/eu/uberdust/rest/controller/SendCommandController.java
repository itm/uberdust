package eu.uberdust.rest.controller;

import eu.uberdust.command.DestinationPayloadCommand;
import eu.uberdust.controller.protobuf.CommandProtocol;
import eu.uberdust.rest.exception.NodeNotFoundException;
import eu.uberdust.uberlogger.UberLogger;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * Controller class for sending a message with certain payload to a destination.
 */
public final class SendCommandController extends AbstractRestController {

    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SendCommandController.class);

    /**
     * Constructor.
     */
    public SendCommandController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets node persistence manager.
     *
     * @param nodeManager node persistence manager.
     */
    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    /**
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return http servlet response.
     * @throws NodeNotFoundException NodeNotFoundException exception.
     * @throws IOException           IOException exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws NodeNotFoundException, IOException {

        LOGGER.info("Remote address: " + request.getRemoteAddr());
        LOGGER.info("Remote host: " + request.getRemoteHost());

        // set commandNode object
        final DestinationPayloadCommand command = (DestinationPayloadCommand) commandObj;
        final String payload = command.getPayload().replaceAll(",", "");
        final String nodeId = payload.substring(3);

        if (command.getDestination().contains("494")) {
            UberLogger.getInstance().LOG(nodeId, "T81");
        }


        // look for destination node
        final Node destinationNode = nodeManager.getByID(command.getDestination());
        if (destinationNode == null) {
            throw new NodeNotFoundException("Destination Node [" + command.getDestination() + "] is not stored.");
        }

        if (command.getDestination().contains("494")) {
            UberLogger.getInstance().LOG(nodeId, "T82");
        }
        // prepare socket for connection and writer
        final Socket kkSocket = new Socket("gold.cti.gr", 4444);
        final PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);

        // build command and send it through the socket stream
        final CommandProtocol.Command cmd = CommandProtocol.Command.newBuilder()
                .setDestination(command.getDestination())
                .setPayload(command.getPayload())
                .build();
        cmd.writeTo(kkSocket.getOutputStream());

        // close stream after command execution
        out.close();
        kkSocket.close();
        if (command.getDestination().contains("494")) {
            UberLogger.getInstance().LOG(nodeId, "T83");
        }
        response.setContentType("text/plain");
        final Writer textOutput = (response.getWriter());
        textOutput.write("OK . Destination : " + command.getDestination() + "\nPayload : " + command.getPayload());
        if (command.getDestination().contains("494")) {
            UberLogger.getInstance().LOG(nodeId, "T84");
        }
        return null;

    }
}
