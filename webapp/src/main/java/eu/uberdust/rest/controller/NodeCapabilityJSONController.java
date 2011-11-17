package eu.uberdust.rest.controller;

import com.google.gson.Gson;
import eu.uberdust.command.NodeCapabilityCommand;
import eu.uberdust.rest.exception.CapabilityNotFoundException;
import eu.uberdust.rest.exception.InvalidCapabilityNameException;
import eu.uberdust.rest.exception.InvalidNodeIdException;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.NodeNotFoundException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.uberdust.util.NodeReadingJson;
import eu.uberdust.util.ReadingJson;
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
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class NodeCapabilityJSONController extends AbstractRestController {

    private transient NodeController nodeManager;
    private transient CapabilityController capabilityManager;
    private transient NodeReadingController nodeReadingManager;
    private transient TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityJSONController.class);

    public NodeCapabilityJSONController() {
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
    protected ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                                  final Object commandObj, final BindException e)
            throws InvalidNodeIdException, InvalidCapabilityNameException, InvalidTestbedIdException,
            TestbedNotFoundException, NodeNotFoundException, CapabilityNotFoundException, IOException {
        // set commandNode object
        final NodeCapabilityCommand command = (NodeCapabilityCommand) commandObj;
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
            throw new InvalidTestbedIdException("Testbed IDs have number format.",nfe);
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

        // create list of readings and node , capability ids
        final String nodeId = command.getNodeId();
        final String capabilityId = command.getCapabilityId();
        final int LIMIT = 100; // TODO maybe the user should pass it
        final List<NodeReading> nodeReadings = nodeReadingManager.listNodeReadings(node, capability, LIMIT);

        final List<ReadingJson> readingJsons = new ArrayList<ReadingJson>();
        for (NodeReading nodeReading : nodeReadings) {
            readingJsons.add(new ReadingJson(nodeReading.getTimestamp().getTime(), nodeReading.getReading()));
        }
        final NodeReadingJson nodeReadingInJson =
                new NodeReadingJson(nodeId, capabilityId, readingJsons);

        // write on the HTTP response
        httpServletResponse.setContentType("text/json");
        final Writer jsonOutput = (httpServletResponse.getWriter());

        // init GSON
        final Gson gson = new Gson();
        gson.toJson(nodeReadingInJson, nodeReadingInJson.getClass(), jsonOutput);

        jsonOutput.flush();
        jsonOutput.close();

        return null;
    }
}
