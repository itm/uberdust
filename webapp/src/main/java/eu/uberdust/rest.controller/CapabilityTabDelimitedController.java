package eu.uberdust.rest.controller;

import eu.uberdust.command.CapabilityCommand;
import eu.uberdust.rest.exception.CapabilityNotFoundException;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.LastLinkReadingController;
import eu.wisebed.wisedb.controller.LastNodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.LastLinkReading;
import eu.wisebed.wisedb.model.LastNodeReading;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CapabilityTabDelimitedController extends AbstractRestController {

    private TestbedController testbedManager;
    private CapabilityController capabilityManager;
    private LastNodeReadingController lastNodeReadingManager;
    private LastLinkReadingController lastLinkReadingManager;
    private static final Logger LOGGER = Logger.getLogger(CapabilityTabDelimitedController.class);

    public CapabilityTabDelimitedController(){
                super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setCapabilityManager(CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    public void setLastNodeReadingManager(LastNodeReadingController lastNodeReading) {
        this.lastNodeReadingManager = lastNodeReading;
    }

    public void setLastLinkReadingManager(LastLinkReadingController lastLinkReading) {
        this.lastLinkReadingManager = lastLinkReading;
    }


    @Override
    protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object commandObj,
                                  BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, IOException, CapabilityNotFoundException {
        // set command object
        CapabilityCommand command = (CapabilityCommand) commandObj;
        LOGGER.info("commandObj.getTestbedId() : " + command.getTestbedId());
        LOGGER.info("commandObj.getCapabilityId() : " + command.getCapabilityName());

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException(new Throwable("Invalid Testbed ID."));
        }

        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException(new Throwable("Cannot find testbed [" + testbedId + "]."));
        }

        // look up capability
        Capability capability = capabilityManager.getByID(command.getCapabilityName());
        if (capability == null) {
            // if no capability is found throw exception
            throw new CapabilityNotFoundException(new Throwable("Cannot find capability [" +
                    command.getCapabilityName() + "]."));
        }

        // write on the HTTP response
        response.setContentType("text/plain");
        final Writer textOutput = (response.getWriter());

        // get latest node readings
        List<LastNodeReading> lastNodeReadings = lastNodeReadingManager.getByCapability(testbed, capability);
        if (lastNodeReadings == null || lastNodeReadings.isEmpty()) {
            // if not last node readings are found for this capability and testbed check for last link readings
            List<LastLinkReading> lastLinkReadings = lastLinkReadingManager.getByCapability(testbed, capability);
            if (lastLinkReadings == null || lastLinkReadings.isEmpty()) {
                // if not found return nothing
                textOutput.flush();
                textOutput.close();
                return null;
            } else {
                // get lastest link readings
                for (LastLinkReading llr : lastLinkReadings) {
                    textOutput.write("[" + llr.getLink().getSource() + " -> " + llr.getLink().getTarget() + "]\t"
                            + llr.getTimestamp().getTime() + "\t" + llr.getReading() + "\n");
                }
            }
        } else {
            // get lastest node readings
            for (LastNodeReading lnr : lastNodeReadings) {
                textOutput.write(lnr.getNode().getId() + "\t" +
                        lnr.getTimestamp().getTime() + "\t" + lnr.getReading() + "\n");
            }
        }

        // flush close output
        textOutput.flush();
        textOutput.close();

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage() +"\n"+ exception.fillInStackTrace().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
