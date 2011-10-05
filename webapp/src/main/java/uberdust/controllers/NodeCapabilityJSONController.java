package uberdust.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.model.NodeReading;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCapabilityCommand;
import uberdust.util.NodeReadingJson;
import uberdust.util.ReadingJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NodeCapabilityJSONController extends AbstractRestController {

    private NodeController nodeManager;
    private CapabilityController capabilityManager;
    private NodeReadingController nodeReadingManager;

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

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {
        // set commandNode object
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

        // create list of readings and node , capability ids
        final String nodeId = command.getNodeId();
        final String capabilityId = command.getCapabilityId();
        NodeReadingJson nodeReadingInJson = new NodeReadingJson(nodeId, capabilityId, new ArrayList<ReadingJson>());
        for (NodeReading nodeReading : nodeReadingManager.listReadings(node, capability)) {
            nodeReadingInJson.getReadings().add(new ReadingJson(nodeReading.getTimestamp().getTime(), nodeReading.getReading()));
        }

        // write on the HTTP response
        httpServletResponse.setContentType("text/json");
        final Writer jsonOutput = (httpServletResponse.getWriter());

        // init GSON
        Gson gson = new Gson();
        gson.toJson(nodeReadingInJson, nodeReadingInJson.getClass(), jsonOutput);


        jsonOutput.flush();
        jsonOutput.close();

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
