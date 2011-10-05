package uberdust.controllers;

import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.NodeReadingController;
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
    private NodeReadingController nodeReadingManager;

    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityJSONController.class);

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

    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
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

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbedId", command.getTestbedId());
        refData.put("nodeId", command.getNodeId());
        refData.put("capabilityId", command.getCapabilityId());

        // check type of view requested
        return new ModelAndView("capability/chart.html", refData);
    }
}
