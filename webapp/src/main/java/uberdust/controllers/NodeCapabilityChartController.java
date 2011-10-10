package uberdust.controllers;

import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCapabilityCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class NodeCapabilityChartController extends AbstractRestController {

    private NodeController nodeManager;
    private CapabilityController capabilityManager;
    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityJSONController.class);
    private NodeReadingController nodeReadingManager;

    public NodeCapabilityChartController() {
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

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setNodeReadingManager(NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set command object
        NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;
        LOGGER.info("command.getNodeId() : " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() : " + command.getCapabilityId());
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());

        // check input
        if (command.getNodeId() == null || command.getNodeId().isEmpty() || command.getCapabilityId() == null ||
                command.getCapabilityId().isEmpty()) {
            throw new Exception(new Throwable("Must provide node/link id and capability id"));
        }

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new Exception(new Throwable("Testbed IDs have number format."));
        }

        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new Exception(new Throwable("Cannot find testbed [" + testbedId + "]."));
        }

        // retrieve node
        Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            throw new Exception(new Throwable("Cannot find node [" + command.getNodeId() + "]"));
        }

        // retrieve capability
        Capability capability = capabilityManager.getByID(command.getCapabilityId());
        if (capability == null) {
            throw new Exception(new Throwable("Cannot find capability [" + command.getCapabilityId() + "]"));
        }

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbed", testbed);
        refData.put("node", node);
        refData.put("capability", capability);

        // check type of view requested
        return new ModelAndView("nodecapability/chart.html", refData);
    }
}
