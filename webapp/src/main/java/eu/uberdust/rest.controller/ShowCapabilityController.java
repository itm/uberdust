package eu.uberdust.rest.controller;

import eu.uberdust.command.CapabilityCommand;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Link;
import eu.wisebed.wiseml.model.setup.Node;
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

public class ShowCapabilityController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(ShowCapabilityController.class);
    private TestbedController testbedManager;
    private CapabilityController capabilityManager;
    private NodeReadingController nodeReadingManager;
    private LinkReadingController linkReadingManager;


    public ShowCapabilityController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setCapabilityManager(CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public NodeReadingController getNodeReadingManager() {
        return nodeReadingManager;
    }

    public void setNodeReadingManager(NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    public LinkReadingController getLinkReadingManager() {
        return linkReadingManager;
    }

    public void setLinkReadingManager(LinkReadingController linkReadingManager) {
        this.linkReadingManager = linkReadingManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {
        // set command object
        CapabilityCommand command = (CapabilityCommand) commandObj;
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());
        LOGGER.info("command.getCapabilityName() : " + command.getCapabilityName());

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new Exception(new Throwable("Testbed IDs have number format."));
        }
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new Exception(new Throwable("Cannot find testbed [" + testbedId + "]."));
        }

        // look up capability
        Capability capability = capabilityManager.getByID(command.getCapabilityName());
        if(capability == null){
            // if no capability is found throw exception
            throw new Exception(new Throwable("Cannot find capability [" + command.getCapabilityName() + "]."));
        }

        // how many readings has this capability
        Long nodeReadingsCount = nodeReadingManager.getNodeCapabilityReadingsCount(capability, testbed);
        Long linkReadingsCount = linkReadingManager.getLinkCapabilityReadingsCount(capability, testbed);
        Map<Node, Long> readingCountsPerNode = new HashMap<Node, Long>();
        Map<Link, Long> readingCountsPerLink = new HashMap<Link, Long>();

        // if this capability has no node readings.
        if (nodeReadingsCount == 0) {
            // find the reading count of capabilities per link
            readingCountsPerLink = linkReadingManager.getLinkCapabilityReadingsCountPerLink(capability, testbed);
        }

        // if this capability has no links.
        if (linkReadingsCount == 0) {
            // find the reading count of capabilities per node
            readingCountsPerNode = nodeReadingManager.getNodeCapabilityReadingsCountPerNode(capability, testbed);
        }

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("testbed", testbed);
        refData.put("capability", capability);
        refData.put("nodeReadingsCount", nodeReadingsCount);
        refData.put("linkReadingsCount", linkReadingsCount);
        refData.put("readingCountsPerNode", readingCountsPerNode);
        refData.put("readingCountsPerLink", readingCountsPerLink);

        return new ModelAndView("capability/show.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
