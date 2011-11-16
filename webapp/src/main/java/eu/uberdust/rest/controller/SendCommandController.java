package eu.uberdust.rest.controller;

import eu.uberdust.command.DestinationPayloadCommand;
import eu.uberdust.controller.protobuf.CommandProtocol;
import eu.uberdust.rest.exception.NodeNotFoundException;
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


public class SendCommandController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(SendCommandController.class);
    private NodeController nodeManager;

    public SendCommandController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest,final  HttpServletResponse httpServletResponse,
                                  final Object commandObj,final  BindException e) throws NodeNotFoundException, IOException {

        // set commandNode object
        final DestinationPayloadCommand command = (DestinationPayloadCommand) commandObj;
        LOGGER.info("command.getDestination() : " + command.getDestination());
        LOGGER.info("command.getPayload() : " + command.getPayload());

        // look for destination node
        final Node destinationNode = nodeManager.getByID(command.getDestination());
        if (destinationNode == null) {
            throw new NodeNotFoundException("Destination Node [" + command.getDestination() + "] is not stored.");
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

        httpServletResponse.setContentType("text/plain");
        final Writer textOutput = (httpServletResponse.getWriter());
        textOutput.write("OK . Destination : " + command.getDestination() + "\nPayload : " + command.getPayload());
        return null;

    }
}
