package uberdust.controllers;

import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.NodeCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for displaying node information.
 */
public class NodeController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.NodeController nodeManager;
    private static final Logger LOGGER = Logger.getLogger(NodeController.class);

    public NodeController() {
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
        LOGGER.info("command.getNodeId() " + command.getNodeId());

        // List of nodes
        List<Node> nodes = new ArrayList<Node>();
        if(command.getNodeId() == null){
            // no node id is given. Select em all
            nodes = nodeManager.list();
        }else{
            // a specific node is requested by node Id
            // look up node
            Node node = nodeManager.getByID(command.getNodeId());
            if(node == null){
                // if node not found throw exception
                throw new Exception(new Throwable("Cannot find node [" + command.getNodeId() + "]"));
            }
            // else add it to the returning list
            nodes.add(node);
        }

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("nodes", nodes);
        return new ModelAndView("node/view.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,formattedErrorForFrontEnd);
    }
}