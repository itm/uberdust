package eu.uberdust.rest.controller;

import com.google.gson.Gson;
import eu.uberdust.util.TestbedJson;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
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
 * Controller class that returns a list of testbed in JSON format.
 */
public final class ListTestbedJSONController extends AbstractRestController {

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
    public ListTestbedJSONController() {
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
                                  final Object commandObj, final BindException errors) throws IOException {
        // testbed list
        final List<Testbed> testbeds = testbedManager.list();

        // json list
        final List<TestbedJson> testbedJsons = new ArrayList<TestbedJson>();


        // iterate over testbeds
        for (Testbed testbed : testbeds) {
            TestbedJson testbedJson = new TestbedJson(testbed.getId(),testbed.getName());
            testbedJsons.add(testbedJson);
        }

        // write on the HTTP response
        response.setContentType("text/json");
        final Writer jsonOutput = (response.getWriter());

        // init GSON
        final Gson gson = new Gson();
        gson.toJson(testbedJsons, testbedJsons.getClass(), jsonOutput);

        jsonOutput.flush();
        jsonOutput.close();

        return null;
    }
}
