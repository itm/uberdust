package uberdust.controllers;

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
import javax.xml.ws.Response;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class NodeCapabilityTabDelimitedController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.NodeController nodeManager;
    private eu.wisebed.wisedb.controller.CapabilityController capabilityManager;
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityTabDelimitedController.class);

    public NodeCapabilityTabDelimitedController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(eu.wisebed.wisedb.controller.NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setCapabilityManager(eu.wisebed.wisedb.controller.CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set commandNode object
        NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;
        LOGGER.info("command.getNodeId() " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() " + command.getCapabilityId());

        List<NodeReading> readingsOnCapability = new ArrayList<NodeReading>();

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

        // retrieve node readings
        Set<NodeReading> readings = node.getReadings();
        if (readings == null) {
            throw new Exception(new Throwable("Cannot find readings of node [" + command.getNodeId() + "]"));
        }
        for (NodeReading reading : readings) {
            if (reading.getCapability().equals(capability)) readingsOnCapability.add(reading);
        }

        // write on the HTTP response
        httpServletResponse.setContentType("text/plain");
        final Writer textOutput = (httpServletResponse.getWriter());
        for (NodeReading reading : readings) {
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
