package uberdust.controllers;


import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.TestbedController;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowNodeKmlController extends AbstractRestController {

    private TestbedController testbedManager;
    private NodeController nodeManager;
    private static final Logger LOGGER = Logger.getLogger(ShowNodeGeoRssController.class);


    public ShowNodeKmlController() {

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setNodeManager(NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }


    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {
        return null;// TODO make this controller
    }
}
