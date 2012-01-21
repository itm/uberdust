package eu.uberdust.rest.controller;

import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class that returns a list of testbed in HTML format.
 */
public final class ListTestbedsHTMLController extends AbstractRestController {

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ListTestbedsHTMLController.class);

    /**
     * Constructor.
     */
    public ListTestbedsHTMLController() {
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
     * Handle Request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return response http servlet response.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors) {


        // testbed list
        final List<Testbed> testbeds = testbedManager.list();
        final Map<String, Long> nodesCount = testbedManager.countNodes();
        final Map<String, Long> linksCount = testbedManager.countLinks();
        final Map<String, Long> slsesCount = testbedManager.countSlses();

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        refData.put("testbeds", testbeds);
        refData.put("nodesCount", nodesCount);
        refData.put("linksCount", linksCount);
        refData.put("slsesCount", slsesCount);
        return new ModelAndView("testbed/list.html", refData);
    }
}
