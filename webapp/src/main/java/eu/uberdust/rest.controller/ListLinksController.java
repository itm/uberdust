package eu.uberdust.rest.controller;

import eu.uberdust.command.LinkCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.LinkController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
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

/**
 * REST Controller for displaying link information.
 */
public class ListLinksController extends AbstractRestController {

    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ListLinksController.class);
    private LinkController linkManager;

    public ListLinksController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setLinkManager(LinkController linkManager) {
        this.linkManager = linkManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws TestbedNotFoundException, InvalidTestbedIdException {

        // get command
        LinkCommand command = (LinkCommand) commandObj;
        LOGGER.info("command.getTestbedId() : " + command.getTestbedId());

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.");
        }

        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }
        List<Link> links = linkManager.list(testbed);

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        refData.put("testbed", testbed);
        refData.put("links", links);
        return new ModelAndView("link/list.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage() +"\n"+ exception.fillInStackTrace().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
