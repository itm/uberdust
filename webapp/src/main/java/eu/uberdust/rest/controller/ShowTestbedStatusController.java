package eu.uberdust.rest.controller;

import eu.uberdust.command.TestbedCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.LastLinkReadingController;
import eu.wisebed.wisedb.controller.LastNodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.LastLinkReading;
import eu.wisebed.wisedb.model.LastNodeReading;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTestbedStatusController extends AbstractRestController {

    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedStatusController.class);
    private LastNodeReadingController lastNodeReadingManager;
    private LastLinkReadingController lastLinkReadingManager;


    public ShowTestbedStatusController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setLastNodeReadingManager(final LastNodeReadingController lastNodeReadingManager) {
        this.lastNodeReadingManager = lastNodeReadingManager;
    }

    public void setLastLinkReadingManager(final LastLinkReadingController lastLinkReadingManager) {
        this.lastLinkReadingManager = lastLinkReadingManager;
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                                  final Object commandObj, final BindException e)
            throws InvalidTestbedIdException, TestbedNotFoundException {

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

        // get a list of node last readings from testbed
        long before = System.currentTimeMillis();
        final List<LastNodeReading> lastNodeReadings = lastNodeReadingManager.getByTestbed(testbed);
        long after = System.currentTimeMillis();
        LOGGER.info("lastNodeReadingManager.getByTestbed(testbed) took " + (after - before) + " millis");

        // get a list of link statistics from testbed
        before = System.currentTimeMillis();
        final List<LastLinkReading> lastLinkReadings = lastLinkReadingManager.getByTestbed(testbed);
        after = System.currentTimeMillis();
        LOGGER.info("lastLinkReadingManager.getByTestbed(testbed) took " + (after - before) + " millis");


        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("testbed", testbed);
        refData.put("lastNodeReadings", lastNodeReadings);
        refData.put("lastLinkReadings", lastLinkReadings);

        return new ModelAndView("testbed/status.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(final Throwable exception, final HttpServletResponse response) throws IOException {
        LOGGER.fatal(exception);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
