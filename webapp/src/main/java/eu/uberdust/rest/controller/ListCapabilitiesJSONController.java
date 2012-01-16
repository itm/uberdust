package eu.uberdust.rest.controller;

import com.google.gson.Gson;
import eu.uberdust.command.CapabilityCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.uberdust.util.CapabilityJson;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
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

/**
 * Controller class that returns a list of capabilities for a given testbed in JSON format.
 */
public final class ListCapabilitiesJSONController extends AbstractRestController {

     /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Capability persistence manager.
     */
    private transient CapabilityController capabilityManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ListCapabilitiesJSONController.class);

    /**
     * Constructor.
     */
    public ListCapabilitiesJSONController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed peristence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    /**
     * Sets capability peristence manager.
     *
     * @param capabilityManager capability persistence manager.
     */
    public void setCapabilityManager(final CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    /**
     * Handle Request and return the appropriate response.
     * System.out.println(request.getRemoteUser());
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return response http servlet response.
     * @throws eu.uberdust.rest.exception.InvalidTestbedIdException an InvalidTestbedIdException exception.
     * @throws eu.uberdust.rest.exception.TestbedNotFoundException  an TestbedNotFoundException exception.
     * @throws IOException IO exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, IOException {

        // get command
        final CapabilityCommand command = (CapabilityCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // get testbed's capabilities
        final List<Capability> capabilities = capabilityManager.list(testbed);

        // json list
        final List<CapabilityJson> capabilityJsons = new ArrayList<CapabilityJson>();


        // iterate over testbeds
        for (Capability capability : capabilities) {
            CapabilityJson capabilityJson = new CapabilityJson(capability.getName());
            capabilityJsons.add(capabilityJson);
        }

        // write on the HTTP response
        response.setContentType("text/json");
        final Writer jsonOutput = (response.getWriter());

        // init GSON
        final Gson gson = new Gson();
        gson.toJson(capabilityJsons, capabilityJsons.getClass(), jsonOutput);

        jsonOutput.flush();
        jsonOutput.close();


        return null;
    }
}
