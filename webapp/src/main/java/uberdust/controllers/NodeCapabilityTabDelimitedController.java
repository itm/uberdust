package uberdust.controllers;

import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.model.NodeReading;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCapabilityCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class NodeCapabilityTabDelimitedController extends AbstractRestController {

    private NodeController nodeManager;
    private CapabilityController capabilityManager;
    private NodeReadingController nodeReadingManager;

    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityTabDelimitedController.class);

    public NodeCapabilityTabDelimitedController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setCapabilityManager(final CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    public void setNodeReadingManager(final NodeReadingController nodeReadingManager){
        this.nodeReadingManager = nodeReadingManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set commandNode object
        NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;
        LOGGER.info("command.getNodeId() " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() " + command.getCapabilityId());

        // retrieve node
        if (command.getNodeId() == null || command.getNodeId().isEmpty() || command.getCapabilityId() == null ||
                command.getCapabilityId().isEmpty()) {
            throw new Exception(new Throwable("Must provide node/link id and capability id"));
        }

        Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            throw new Exception(new Throwable("Cannot find node [" + command.getNodeId() + "]"));
        }

        // retrieve capability
        Capability capability = capabilityManager.getByID(command.getCapabilityId());
        if (capability == null) {
            throw new Exception(new Throwable("Cannot find capability [" + command.getCapabilityId() + "]"));
        }

        // retrieve readings based on node/capability
        List<NodeReading> nodeReadings = nodeReadingManager.listReadings(node,capability);

        // write on the HTTP response
        httpServletResponse.setContentType("text/plain");
        final Writer textOutput = (httpServletResponse.getWriter());
        for (NodeReading reading : nodeReadings) {
            textOutput.write(reading.getTimestamp().getTime() + "\t" + reading.getReading() +"\n");
        }
        textOutput.flush();
        textOutput.close();

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,formattedErrorForFrontEnd);
    }
}
