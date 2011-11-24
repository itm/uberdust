package eu.uberdust.rest.controller;

import eu.uberdust.command.CapabilityCommand;
import eu.uberdust.rest.exception.CapabilityNotFoundException;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.LinkController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Link;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class that returns the a web page for a capability.
 */
public final class ShowCapabilityController extends AbstractRestController {

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Capability persistence manager.
     */
    private transient CapabilityController capabilityManager;

    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    /**
     * Link persistence manager.
     */
    private transient LinkController linkManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShowCapabilityController.class);

    /**
     * Constructor.
     */
    public ShowCapabilityController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
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
     * Sets link persistence manager.
     *
     * @param linkManager link persistence manager.
     */
    public void setLinkManager(final LinkController linkManager) {
        this.linkManager = linkManager;
    }

    /**
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException excetion.
     * @return http servlet response
     * @throws InvalidTestbedIdException   InvalidTestbedIdException exception.
     * @throws TestbedNotFoundException    TestbedNotFoundException exception.
     * @throws CapabilityNotFoundException CapabilityNotFoundExcetion.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, CapabilityNotFoundException {
        // set command object
        final CapabilityCommand command = (CapabilityCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // look up capability
        final Capability capability = capabilityManager.getByID(command.getCapabilityName());
        if (capability == null) {
            // if no capability is found throw exception
            throw new CapabilityNotFoundException("Cannot find capability [" + command.getCapabilityName() + "].");
        }

        // get testbed nodes only
        final List<Node> nodes = nodeManager.listCapabilityNodes(capability, testbed);
        final List<Link> links = linkManager.listCapabilityLinks(capability, testbed);

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("testbed", testbed);
        refData.put("capability", capability);
        refData.put("nodes", nodes);
        refData.put("links", links);
        return new ModelAndView("capability/show.html", refData);
    }
}
