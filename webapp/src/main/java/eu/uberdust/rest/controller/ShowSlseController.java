package eu.uberdust.rest.controller;

import eu.uberdust.command.SlseCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.NodeNotFoundException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.SlseController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Slse;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/20/12
 * Time: 12:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShowSlseController extends AbstractRestController {
    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    private transient SlseController slseManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShowSlseController.class);

    /**
     * Constructor.
     */
    public ShowSlseController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets node persistence manager.
     *
     * @param nodeManager node persistence manager.
     */
    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setSlseManager(SlseController slseManager) {
        this.slseManager = slseManager;
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
     * Handle request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException errors.
     * @return http servlet response.
     * @throws eu.uberdust.rest.exception.InvalidTestbedIdException
     *          InvalidTestbedIdException exception.
     * @throws eu.uberdust.rest.exception.TestbedNotFoundException
     *          TestbedNotFoundException exception.
     * @throws eu.uberdust.rest.exception.NodeNotFoundException
     *          NodeNotFoundException exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws InvalidTestbedIdException, TestbedNotFoundException, NodeNotFoundException {

        LOGGER.info("Remote address: " + request.getRemoteAddr());
        LOGGER.info("Remote host: " + request.getRemoteHost());

        // set command object
        final SlseCommand command = (SlseCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // look up node
        final Slse slse = slseManager.getByID(command.getSlseId());
        if (slse == null) {
            // if no testbed is found throw exception
            throw new NodeNotFoundException("Cannot find testbed [" + command.getSlseId() + "].");
        }

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbed", testbed);
        refData.put("slse", slse);
        refData.put("nodes", slse.getNodes());
        return new ModelAndView("slse/show.html", refData);
    }
}
