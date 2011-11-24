package eu.uberdust.rest.controller;

import eu.uberdust.command.NodeCapabilityCommand;
import eu.uberdust.rest.exception.CapabilityNotFoundException;
import eu.uberdust.rest.exception.InvalidCapabilityNameException;
import eu.uberdust.rest.exception.InvalidLimitException;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Controller class that returns the readings of a node capability pair in WiseML format.
 */
public final class NodeCapabilityWiseMlController extends AbstractRestController {

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    /**
     * Capability persistence manager.
     */
    private transient CapabilityController capabilityManager;

    /**
     * NodeReading persistence manager.
     */
    private transient NodeReadingController nodeReadingManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityWiseMlController.class);

    /**
     * Constructor.
     */
    public NodeCapabilityWiseMlController() {

        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
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
     * Sets capability persistence manager.
     *
     * @param capabilityManager capability persistence manager.
     */
    public void setCapabilityManager(final CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    /**
     * Sets NodeReading persistence manager.
     *
     * @param nodeReadingManager NodeReading persistence manager.
     */
    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }


    /**
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object
     * @param errors     a BindException exception.
     * @return http servlet response.
     * @throws InvalidNodeIdException         a InvalidNodeIdException exception.
     * @throws InvalidCapabilityNameException a InvalidCapabilityNameException exception.
     * @throws InvalidTestbedIdException      a InvalidTestbedIdException exception.
     * @throws TestbedNotFoundException       a TestbedNotFoundException exception.
     * @throws NodeNotFoundException          a NodeNotFoundExeption exception.
     * @throws CapabilityNotFoundException    a CapabilityNotFoundException exception.
     * @throws InvalidLimitException          a InvalidLimitException exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws InvalidNodeIdException, InvalidCapabilityNameException, InvalidTestbedIdException,
            TestbedNotFoundException, NodeNotFoundException, CapabilityNotFoundException, InvalidLimitException {

        // set commandNode object
        final NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;

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
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // retrieve node
        final Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            throw new NodeNotFoundException("Cannot find node [" + command.getNodeId() + "]");
        }

        // retrieve capability
        final Capability capability = capabilityManager.getByID(command.getCapabilityId());
        if (capability == null) {
            throw new CapabilityNotFoundException("Cannot find capability [" + command.getCapabilityId() + "]");
        }

        // retrieve readings based on node/capability
        final List<NodeReading> nodeReadings;
        if (command.getReadingsLimit() == null) {
            // no limit is provided
            nodeReadings = nodeReadingManager.listNodeReadings(node, capability);
        } else {
            int limit;
            try {
                limit = Integer.parseInt(command.getReadingsLimit());
            } catch (NumberFormatException nfe) {
                throw new InvalidLimitException("Limit must have have number format.", nfe);
            }
            nodeReadings = nodeReadingManager.listNodeReadings(node, capability, limit);
        }

        // Results are here . TODO make this controller.
        nodeReadings.size();

        return null;
    }
}

