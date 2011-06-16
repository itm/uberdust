package uberdust.controllers;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for displaying user information.
 */
public class NodeController extends AbstractRestController {

    public NodeController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws Exception {

        NodeCommand command = (NodeCommand) commandObj;

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("nodeId", command.getNodeId());

        return new ModelAndView("node/index", refData);
    }

}