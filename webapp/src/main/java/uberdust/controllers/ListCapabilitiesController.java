package uberdust.controllers;

import eu.wisebed.ns.wiseml._1.Capability;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.CapabilityCommand;
import uberdust.commands.LinkCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListCapabilitiesController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(ListCapabilitiesController.class);
    private TestbedController testbedManager;

    public ListCapabilitiesController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // get command
        CapabilityCommand command = (CapabilityCommand) commandObj;
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());

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

        // TODO Fetch Testbed's capabilities
        List<Capability> capabilities = null;

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        refData.put("testbed", testbed);
        refData.put("capabilities",capabilities);
        return new ModelAndView("capability/list.html", refData);
    }
}
