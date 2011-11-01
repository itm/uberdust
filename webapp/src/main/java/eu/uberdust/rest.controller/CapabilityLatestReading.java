package eu.uberdust.rest.controller;

import eu.uberdust.command.CapabilityCommand;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.LastLinkReadingController;
import eu.wisebed.wisedb.controller.LastNodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.LastLinkReading;
import eu.wisebed.wisedb.model.LastNodeReading;
import eu.wisebed.wisedb.model.NodeReading;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CapabilityLatestReading extends AbstractRestController {

    private TestbedController testbedManager;
    private CapabilityController capabilityManager;
    private LastNodeReadingController lastNodeReadingManager;
    private LastLinkReadingController lastLinkReadingManager;
    private static final Logger LOGGER = Logger.getLogger(CapabilityLatestReading.class);


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
    protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response,
                                  Object commandObj, BindException errors) throws Exception {
        // set command object
        CapabilityCommand command = (CapabilityCommand) commandObj;
        LOGGER.info("commandObj.getTestbedId() : " + command.getTestbedId());
        LOGGER.info("commandObj.getCapabilityId() : " + command.getCapabilityName());

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

        // look up capability
        Capability capability = capabilityManager.getByID(command.getCapabilityName());
        if (capability == null) {
            // if no capability is found throw exception
            throw new Exception(new Throwable("Cannot find capability [" + command.getCapabilityName() + "]."));
        }

        // write on the HTTP response
        response.setContentType("text/plain");
        final Writer textOutput = (response.getWriter());
        // get latest node readings
        List<LastNodeReading> lastNodeReadings = lastNodeReadingManager.getByCapability(capability);
        if (lastNodeReadings != null || !lastNodeReadings.isEmpty()) {
            for (LastNodeReading lnr : lastNodeReadings) {
                textOutput.write(lnr.getNode().getId() + "\t" + lnr.getTimestamp().getTime() + "\t" + lnr.getReading() + "\n");
            }
        } else {
            // get lastest link readings
            List<LastLinkReading> lastLinkReadings = lastLinkReadingManager.getByCapability(capability);
            for (LastLinkReading llr : lastLinkReadings) {
                textOutput.write("[" + llr.getLink().getSource() + " -> " + llr.getLink().getTarget() + "]\t"
                        + llr.getTimestamp().getTime() + "\t" + llr.getReading() + "\n");
            }
        }
        // flush close output
        textOutput.flush();
        textOutput.close();

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
