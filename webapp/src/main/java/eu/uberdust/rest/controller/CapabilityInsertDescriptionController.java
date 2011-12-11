package eu.uberdust.rest.controller;

import eu.uberdust.command.CapabilityCommand;
import eu.uberdust.rest.exception.CapabilityNotFoundException;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
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
public final class CapabilityInsertDescriptionController extends AbstractRestController {

    /**
     * Testbed persistence manager.
     */
    private TestbedController testbedManager;

    /**
     * Capability persistence manager.
     */
    private CapabilityController capabilityManager;

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
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
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return response http servlet response.
     * @throws InvalidTestbedIdException an invalid testbed id exception.
     * @throws TestbedNotFoundException testbed not found exception.
     * @throws CapabilityNotFoundException capability not found exception.
     * @throws java.io.IOException IO exception.
     */
    @Override
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors) throws InvalidTestbedIdException,
            TestbedNotFoundException, CapabilityNotFoundException, IOException {

        // set commandNode object
        final CapabilityCommand command = (CapabilityCommand) commandObj;

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
        final String capabilityName = command.getCapabilityName();
        final Capability capability = capabilityManager.getByID(capabilityName);
        if (capability == null) {
            // if no node is found throw exception
            throw new CapabilityNotFoundException("Cannot find capability [" + command.getCapabilityName() + "].");
        }

        // update description
        final String description = command.getDescription();
        capability.setDescription(description);
        capabilityManager.update(capability);

        // make response
        response.setContentType("text/plain");
        final Writer textOutput = (response.getWriter());
        textOutput.write("Desciption \"" + description + "\" inserted for Capability(" + command.getCapabilityName()
                + ")" + ") Testbed(" + testbed.getId() + "). OK");
        textOutput.flush();
        textOutput.close();

        return null;
    }
}
