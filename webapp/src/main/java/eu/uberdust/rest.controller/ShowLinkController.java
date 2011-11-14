package eu.uberdust.rest.controller;

import eu.uberdust.command.LinkCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.LinkNotFoundException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.LinkController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Link;
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

public class ShowLinkController extends AbstractRestController {

    private LinkController linkManager;
    private TestbedController testbedManager;

    private static final Logger LOGGER = Logger.getLogger(ShowLinkController.class);


    public void setLinkManager(LinkController linkManager) {
        this.linkManager = linkManager;
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, LinkNotFoundException {

        // set command object
        LinkCommand command = (LinkCommand) commandObj;
        LOGGER.info("command.getNodeId() : " + command.getSourceId());
        LOGGER.info("command.getTargetId() : " + command.getTargetId());
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException(new Throwable("Testbed IDs have number format."));
        }
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException(new Throwable("Cannot find testbed [" + testbedId + "]."));
        }

        // a link instance  and link list
        Link link = null;
        Link linkInv = null;
        Map<Link,List<Capability>> linkCapabilityMap = new HashMap<Link,List<Capability>>();

        // Retrieve the link and it's inverse
        if (command.getSourceId() != null && command.getTargetId() != null) {
            link = linkManager.getByID(command.getSourceId(), command.getTargetId());
            linkInv = linkManager.getByID(command.getTargetId(), command.getSourceId());
        }

        // if no link or inverse link found return error view
        if (link == null && linkInv == null) {
            throw new LinkNotFoundException(new Throwable("Cannot find link [" + command.getSourceId() + "," + command.getTargetId() +
                    "] or the inverse link [" + command.getTargetId() + "," + command.getSourceId() + "]"));
        }

        // if at least link or linkInv was found
        if (link != null) {
            linkCapabilityMap.put(link,link.getCapabilities());
        }
        if (linkInv != null) {
            linkCapabilityMap.put(linkInv,linkInv.getCapabilities());
        }

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        refData.put("testbed", testbed);
        refData.put("linkCapabilityMap",linkCapabilityMap);
        return new ModelAndView("link/show.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        final String formattedErrorForFrontEnd = exception.getCause().getMessage() + "\n" + exception.fillInStackTrace().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
