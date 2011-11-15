package eu.uberdust.rest.controller;

import eu.uberdust.command.TestbedCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.LinkController;
import eu.wisebed.wisedb.controller.NodeController;
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

public class ShowTestbedController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedController.class);
    private CapabilityController capabilityManager;
    private LinkController linkManager;
    private NodeController nodeManager;

    public ShowTestbedController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final eu.wisebed.wisedb.controller.TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setCapabilityManager(final CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    public void setLinkManager(final LinkController linkManager) {
        this.linkManager = linkManager;
    }

    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest,final HttpServletResponse httpServletResponse,
                                  final Object commandObj,final  BindException e) throws TestbedNotFoundException, InvalidTestbedIdException {

        // set command object
        final TestbedCommand command = (TestbedCommand) commandObj;
        LOGGER.info("commandObj.getTestbedId() : " + command.getTestbedId());


        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.");
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // get testbed nodes
        final List<Node> nodes = nodeManager.list(testbed);

        // get testbed links
        final List<Link> links = linkManager.list(testbed);

        // get testbed capabilities
        final List<Capability> capabilities = capabilityManager.list(testbed);

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbed", testbed);
        refData.put("nodes", nodes);
        refData.put("links", links);
        refData.put("capabilities", capabilities);
        return new ModelAndView("testbed/show.html", refData);
    }
}
