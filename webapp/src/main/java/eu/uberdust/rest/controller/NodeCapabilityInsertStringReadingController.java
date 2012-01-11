package eu.uberdust.rest.controller;

import eu.uberdust.command.NodeCapabilityInsertReadingCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.uberdust.uberlogger.UberLogger;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.exception.UnknownTestbedException;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

public final class NodeCapabilityInsertStringReadingController extends AbstractRestController {

    /**
     * NodeReading persistence manager.
     */
    private transient NodeReadingController nodeReadingManager;

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Looger.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityInsertStringReadingController.class);

    /**
     * Constructor.
     */
    public NodeCapabilityInsertStringReadingController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets NodeReading persistence manager.
     *
     * @param nodeReadingManager NodeReading persistence manager.
     */
    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    /**
     * Sets Testbed persistence manager.
     *
     * @param testbedManager Testbed persistence manager.
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
     * @throws eu.uberdust.rest.exception.InvalidTestbedIdException invalid testbed id exception.
     * @throws eu.uberdust.rest.exception.TestbedNotFoundException  testbed not found exception.
     * @throws java.io.IOException               IO exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, IOException {

        LOGGER.info("Remote address: " + request.getRemoteAddr());
        LOGGER.info("Remote host: " + request.getRemoteHost());

        // set commandNode object
        final NodeCapabilityInsertReadingCommand command = (NodeCapabilityInsertReadingCommand) commandObj;

        if (command.getNodeId().contains("1ccd")) {
            UberLogger.getInstance().log(Long.parseLong(command.getTimestamp()), "T23");
        }
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

        // parse reading and timestamp
        final Date timestamp = new Date(Long.parseLong(command.getTimestamp()));
        final String reading = command.getStringReading();
        final String nodeId = command.getNodeId();
        final String capabilityId = command.getCapabilityId();
        if (nodeId.contains("1ccd")) {
            UberLogger.getInstance().log(timestamp.getTime(), "T24");
        }

        // insert reading
        try {
            nodeReadingManager.insertReading(nodeId, capabilityId, testbedId, null, reading, timestamp);
        } catch (UnknownTestbedException e) {
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].",e);
        }

        // make response
        response.setContentType("text/plain");
        final Writer textOutput = (response.getWriter());
        textOutput.write("Inserted for Node(" + command.getNodeId() + ") Capability(" + command.getCapabilityId()
                + ") Testbed(" + testbed.getName() + ") : " + reading + ". OK");
        textOutput.flush();
        textOutput.close();
        if (command.getNodeId().contains("1ccd")) {
            UberLogger.getInstance().log(timestamp.getTime(), "T25");
        }

        LOGGER.info("MEMSTAT_3: " + Runtime.getRuntime().totalMemory() + ":" + Runtime.getRuntime().freeMemory() + " -- " + Runtime.getRuntime().freeMemory() * 100 / Runtime.getRuntime().totalMemory() + "% free mem");

        return null;
    }
}
