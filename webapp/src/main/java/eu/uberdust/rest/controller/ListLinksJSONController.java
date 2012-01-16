package eu.uberdust.rest.controller;

import com.google.gson.Gson;
import eu.uberdust.command.LinkCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.uberdust.util.LinkJson;
import eu.wisebed.wisedb.controller.LinkController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Link;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class that returns a list of links for a given testbed in JSON format.
 */
public final class ListLinksJSONController extends AbstractRestController {

/**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Link persistence manager.
     */
    private transient LinkController linkManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ListLinksJSONController.class);

    /**
     * Constructor.
     */
    public ListLinksJSONController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    /**
     * Sets link persistence manager.
     *
     * @param linkManager link persistence manager.
     */
    public void setLinkManager(final LinkController linkManager) {
        this.linkManager = linkManager;
    }

    /**
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return response http servlet response.
     * @throws eu.uberdust.rest.exception.InvalidTestbedIdException an InvalidTestbedIdException exception.
     * @throws eu.uberdust.rest.exception.TestbedNotFoundException  an TestbedNotFoundException exception.
     * @throws java.io.IOException IO Exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, IOException {

        // get command
        final LinkCommand command = (LinkCommand) commandObj;

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

        final List<Link> links = linkManager.list(testbed);
        final List<LinkJson> linkJsons = new ArrayList<LinkJson>();

        // iterate over testbeds
        for (Link link : links) {
            LinkJson linkJson = new LinkJson(link.getSource(),link.getTarget());
            linkJsons.add(linkJson);
        }

        // write on the HTTP response
        response.setContentType("text/json");
        final Writer jsonOutput = (response.getWriter());

        // init GSON
        final Gson gson = new Gson();
        gson.toJson(linkJsons, linkJsons.getClass(), jsonOutput);

        jsonOutput.flush();
        jsonOutput.close();


        return null;
    }
}
