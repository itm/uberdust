package eu.uberdust.rest.controller;

import eu.uberdust.command.NodeCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.NodeNotFoundException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
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
import java.util.Map;

public class ShowNodeController extends AbstractRestController {

    private NodeController nodeManager;
    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ShowNodeController.class);


    public ShowNodeController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e)
            throws InvalidTestbedIdException, TestbedNotFoundException, NodeNotFoundException {

        // set command object
        NodeCommand command = (NodeCommand) commandObj;
        LOGGER.info("command.getNodeId() : " + command.getNodeId());
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException(new Throwable("Testbed IDs have number format."));
        }
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException(new Throwable("Cannot find testbed [" + testbedId + "]."));
        }

        // look up node
        Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            // if no testbed is found throw exception
            throw new NodeNotFoundException(new Throwable("Cannot find testbed [" + command.getNodeId() + "]."));
        }

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbed", testbed);
        refData.put("node", node);
        refData.put("capabilities", node.getCapabilities());
        return new ModelAndView("node/show.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        final String formattedErrorForFrontEnd = exception.getCause().getMessage() + "\n" + exception.fillInStackTrace().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
