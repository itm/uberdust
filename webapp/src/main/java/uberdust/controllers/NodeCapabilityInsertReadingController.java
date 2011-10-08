package uberdust.controllers;

import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.NodeController;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class NodeCapabilityInsertReadingController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityInsertReadingController.class);
    private NodeController nodeManager;
    private CapabilityController capabilityManager;

    public NodeCapabilityInsertReadingController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setCapabilityManager(CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {
        return null;
    }
}
