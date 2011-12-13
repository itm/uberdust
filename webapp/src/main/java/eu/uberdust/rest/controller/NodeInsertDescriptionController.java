package eu.uberdust.rest.controller;

import eu.uberdust.command.NodeCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Controller class for inserting description of a node.
 */
public final class NodeInsertDescriptionController extends AbstractRestController {

    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeInsertDescriptionController.class);

    /**
     * Sets node persistence manager.
     *
     * @param nodeManager node persistence manager.
     */
    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    /**
     * Sets testbed persistence manager.
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    /**
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return response http servlet response.
     * @throws InvalidTestbedIdException an invalid testbed id exception.
     * @throws TestbedNotFoundException testbed not found exception.
     * @throws IOException IO exception.
     */
    @Override
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors) throws InvalidTestbedIdException,
            TestbedNotFoundException, IOException {

        // set commandNode object
        final NodeCommand command = (NodeCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());
        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(testbedId);
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // look up node
        final String nodeId = command.getNodeId();
        Node node = nodeManager.getByID(nodeId);
        if (node == null) {
            // if no node is found create it and store it.
            node = new Node();
            node.setId(nodeId);
        }

        // update description
        final String description = command.getDescription();
        node.setDescription(description);
        nodeManager.update(node);

        // make response
        response.setContentType("text/plain");
        final Writer textOutput = (response.getWriter());
        textOutput.write("Desciption \"" + description + "\" inserted for Node(" + node.getId() + ")"
                + ") Testbed(" + testbed.getId() + "). OK");
        textOutput.flush();
        textOutput.close();

        return null;
    }
}
