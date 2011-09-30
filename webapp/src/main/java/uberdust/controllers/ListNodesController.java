package uberdust.controllers;

import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for displaying node information.
 */
public class ListNodesController extends AbstractRestController {

    private NodeController nodeManager;
    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ListNodesController.class);

    public ListNodesController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws Exception {

        // get command object
        NodeCommand command = (NodeCommand) commandObj;
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());

        // fetch testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));

        // List of nodes
        List<Node> nodes = nodeManager.listTestbedNodes(testbed);

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbedId", command.getTestbedId());
        refData.put("nodes", nodes);
        return new ModelAndView("node/list.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}