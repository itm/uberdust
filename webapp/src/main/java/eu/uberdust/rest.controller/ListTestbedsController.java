package eu.uberdust.rest.controller;

import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTestbedsController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.TestbedController testbedManager;

    public ListTestbedsController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                                  final Object commandObj, final  BindException e) {

        // testbed list
        final List<Testbed> testbeds = testbedManager.list();

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        refData.put("testbeds", testbeds);
        return new ModelAndView("testbed/list.html", refData);
    }
}
