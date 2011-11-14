package eu.uberdust.rest.controller;

import eu.uberdust.command.NodeCapabilityInsertReadingCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.exception.UnknownTestbedException;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;


public class NodeCapabilityInsertReadingController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityInsertReadingController.class);
    private NodeReadingController nodeReadingManager;
    private TestbedController testbedManager;


    public NodeCapabilityInsertReadingController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest,final  HttpServletResponse httpServletResponse,
                                  final Object commandObj,final  BindException e)
            throws InvalidTestbedIdException, TestbedNotFoundException, UnknownTestbedException, IOException {

        // set commandNode object
        final NodeCapabilityInsertReadingCommand command = (NodeCapabilityInsertReadingCommand) commandObj;
        LOGGER.info("command.getNodeId() : " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() : " + command.getCapabilityId());
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());
        LOGGER.info("command.getReading() : " + command.getReading());
        LOGGER.info("command.getTimestamp() : " + command.getTimestamp());

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

        // parse reading and timestamp
        final double reading;
        final Date timestamp;
        reading = Double.parseDouble(command.getReading());
        timestamp = new Date(Long.parseLong(command.getTimestamp()));

        // insert reading
        nodeReadingManager.insertReading(command.getNodeId(), command.getCapabilityId(), testbed.getUrnPrefix(),
                reading, timestamp);

        httpServletResponse.setContentType("text/plain");
        final Writer textOutput = (httpServletResponse.getWriter());
        textOutput.write("Inserted for Node(" + command.getNodeId() + ") Capability(" + command.getCapabilityId()
                + ") Testbed(" + testbed.getName() + ") : " + reading + ". OK");
        textOutput.flush();
        textOutput.close();
        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(final Throwable exception, final  HttpServletResponse response) throws IOException {
        final String formattedErrorForFrontEnd = exception.getCause().getMessage() + "\n" + exception.fillInStackTrace().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
