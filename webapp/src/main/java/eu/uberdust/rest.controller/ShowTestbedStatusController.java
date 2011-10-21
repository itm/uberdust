package eu.uberdust.rest.controller;

import eu.uberdust.command.TestbedCommand;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.LinkReadingStat;
import eu.wisebed.wisedb.model.NodeReadingStat;
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
    private NodeReadingController nodeReadingManager;
    private LinkReadingController linkReadingManager;
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedStatusController.class);


    public ShowTestbedStatusController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    public void setLinkReadingManager(final LinkReadingController linkReadingManager) {
        this.linkReadingManager = linkReadingManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set command object
        TestbedCommand command = (TestbedCommand) commandObj;
        LOGGER.info("commandObj.getTestbedId() : " + command.getTestbedId());

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

        // get a list of node statistics from testbed
        long before = System.currentTimeMillis();
        List<NodeReadingStat> nodeStats = nodeReadingManager.getLatestNodeReadingUpdates(testbed);
        long after = System.currentTimeMillis();
        LOGGER.info("nodeReadingManager.getLatestNodeReadingUpdates(testbed) took " + (after-before) + " millis");

        // get a list of link statistics from testbed
        before = System.currentTimeMillis();
        List<LinkReadingStat> linkStats = linkReadingManager.getLatestLinkReadingUpdates(testbed);
        after = System.currentTimeMillis();
        LOGGER.info("linkReadingManager.getLatestLinkReadingUpdates(testbed) took " + (after-before) + " millis");


        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("testbed", testbed);
        refData.put("nodestats", nodeStats);
        refData.put("linkstats", linkStats);

        return new ModelAndView("testbed/status.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
