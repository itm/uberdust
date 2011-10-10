package uberdust.controllers;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.TestbedController;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowCapabilityController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(ShowCapabilityController.class);
    private CapabilityController capabilityManager;
    private TestbedController testbedManager;

    public ShowCapabilityController(){
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {
        return null;// TODO make this controller
    }

    public void setCapabilityManager(CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }
}
