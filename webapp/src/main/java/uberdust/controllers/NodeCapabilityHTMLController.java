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
import java.io.IOException;
import java.util.*;

public class NodeCapabilityHTMLController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.NodeController nodeManager;
    private eu.wisebed.wisedb.controller.CapabilityController capabilityManager;
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityHTMLController.class);

    public NodeCapabilityHTMLController() {
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
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object commandObj, BindException e) throws Exception {

        // set commandNode object
        NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;
        LOGGER.info("command.getNodeId() " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() " + command.getCapabilityId());

        List<NodeReading> readingsOnCapability = new ArrayList<NodeReading>();

        // check for null or empty parameters
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

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("readings", readingsOnCapability);

        // check type of view requested
        return new ModelAndView("capability/readings.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
