package eu.uberdust.rest.controller;

import eu.uberdust.command.NodeCapabilityCommand;
import eu.uberdust.rest.exception.CapabilityNotFoundException;
import eu.uberdust.rest.exception.InvalidCapabilityNameException;
import eu.uberdust.rest.exception.InvalidNodeIdException;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.NodeNotFoundException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.NodeReading;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeCapabilityHTMLController extends AbstractRestController {

    private NodeController nodeManager;
    private CapabilityController capabilityManager;
    private NodeReadingController nodeReadingManager;
    private TestbedController testbedManager;

    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityHTMLController.class);

    public NodeCapabilityHTMLController() {
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

    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws CapabilityNotFoundException, NodeNotFoundException, TestbedNotFoundException, InvalidTestbedIdException, InvalidCapabilityNameException, InvalidNodeIdException {

        // set commandNode object
        NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;
        LOGGER.info("command.getNodeId() : " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() : " + command.getCapabilityId());
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());


        // check node id
        if (command.getNodeId() == null || command.getNodeId().isEmpty()) {
            throw new InvalidNodeIdException("Must provide node id");
        }

        // check capability name
        if (command.getCapabilityId() == null || command.getCapabilityId().isEmpty()) {
            throw new InvalidCapabilityNameException("Must provide capability name");
        }

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.");
        }

        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // retrieve node
        Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            throw new NodeNotFoundException("Cannot find node [" + command.getNodeId() + "]");
        }

        // retrieve capability
        Capability capability = capabilityManager.getByID(command.getCapabilityId());
        if (capability == null) {
            throw new CapabilityNotFoundException("Cannot find capability [" + command.getCapabilityId() + "]");
        }

        // retrieve readings based on node/capability
        List<NodeReading> nodeReadings = nodeReadingManager.listNodeReadings(node, capability);

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbedId", command.getTestbedId());
        refData.put("readings", nodeReadings);

        // check type of view requested
        return new ModelAndView("nodecapability/readings.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        final String formattedErrorForFrontEnd = exception.getCause().getMessage() + "\n" + exception.fillInStackTrace().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
