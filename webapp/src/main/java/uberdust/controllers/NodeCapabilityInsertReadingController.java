package uberdust.controllers;

import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCapabilityInsertReadingCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set commandNode object
        NodeCapabilityInsertReadingCommand command = (NodeCapabilityInsertReadingCommand) commandObj;
        LOGGER.info("command.getNodeId() : " + command.getNodeId());
        LOGGER.info("command.getCapabilityId() : " + command.getCapabilityId());
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());
        LOGGER.info("command.getReading() : " + command.getReading());
        LOGGER.info("command.getTimestamp() : " + command.getTimestamp());


        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new Exception(new Throwable("Cannot find testbed [" + command.getTestbedId() + "]."));
        }

        // parse reading and timestamp
        final double reading;
        final Date timestamp;
        try {
            reading = Double.parseDouble(command.getReading());
            timestamp = new Date(Long.parseLong(command.getTimestamp()));
        } catch (Exception ex) {
            throw ex;
        }

        // insert reading
        nodeReadingManager.insertReading(command.getNodeId(), command.getCapabilityId(), testbed.getUrnPrefix(),
                reading, timestamp);

        httpServletResponse.setContentType("text/plain");
        final Writer textOutput = (httpServletResponse.getWriter());
        textOutput.write("Inserted for Node(" + command.getNodeId() + ") Capability(" + command.getCapabilityId()
                + ") : " + testbed + " : " + reading + ". OK");
        textOutput.flush();
        textOutput.close();
        return null;
    }
}
