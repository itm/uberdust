package uberdust.controllers;

import eu.wisebed.wiseml.model.setup.Node;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for displaying node information.
 */
public class NodeInfoController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.NodeController nodeManager;

    public NodeInfoController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setNodeManager(eu.wisebed.wisedb.controller.NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws Exception {

        // set command object
        NodeCommand command = (NodeCommand) commandObj;

        // Node instance
        Node thisNode = null;

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // Retrieve the node
        if (command.getNodeId() != null) {
            thisNode = nodeManager.getByID(command.getNodeId());
        }

        // if no link found return error view
        if(thisNode == null){
            refData.put("nodeId", command.getNodeId());
            return new ModelAndView("node/error",refData);
        }

        // else put thisNode instance in refData and return index view
        refData.put("thisNode", thisNode);
        return new ModelAndView("node/index", refData);
    }
}