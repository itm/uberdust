package uberdust.controllers;

import com.hp.hpl.jena.sparql.expr.E_UnaryMinus;
import eu.wisebed.wisedb.model.LinkReading;
import eu.wisebed.wisedb.model.NodeReading;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.CapabilityCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class CapabilityController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.NodeController nodeManager;
    private eu.wisebed.wisedb.controller.LinkController linkManager;
    private eu.wisebed.wisedb.controller.CapabilityController capabilityManager;
    private static final Logger LOGGER = Logger.getLogger(CapabilityController.class);

    public CapabilityController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setLinkManager(eu.wisebed.wisedb.controller.LinkController linkManager) {
        this.linkManager = linkManager;
    }

    public void setNodeManager(eu.wisebed.wisedb.controller.NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setCapabilityManager(eu.wisebed.wisedb.controller.CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object commandObj, BindException e) throws Exception {

        // set command object
        CapabilityCommand command = (CapabilityCommand) commandObj;
        LOGGER.info("command.getNodeId() " + command.getNodeId());
        LOGGER.info("command.getLinkId() " + command.getLinkId());
        LOGGER.info("command.getCapabilityId() " + command.getCapabilityId());

        List<NodeReading> readingsOnCapability = new ArrayList<NodeReading>();
//        List<LinkReading> linkReadingsOnCapability = new ArrayList<LinkReading>();

        // retrieve node
        if(command.getNodeId() == null || command.getNodeId().isEmpty() || command.getCapabilityId()== null ||
                command.getCapabilityId().isEmpty()){
            throw new Exception(new Throwable("Must provide node/link id and capability id"));
        }

        Node node = nodeManager.getByID(command.getNodeId());
        if(node == null){
            throw new Exception(new Throwable("Cannot find node [" + command.getNodeId() + "]"));
        }

        // retrieve capability
        Capability capability = capabilityManager.getByID(command.getCapabilityId());
        if(capability == null){
            throw new Exception(new Throwable("Cannot find capability [" + command.getCapabilityId() + "]"));
        }

        // retrieve node readings
        Set<NodeReading> readings = node.getReadings();
        if(readings == null){
            throw new Exception(new Throwable("Cannot find readings of node [" + command.getNodeId() + "]"));
        }
        for(NodeReading reading : readings){
            if(reading.getCapability().equals(capability)) readingsOnCapability.add(reading);
        }


                // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("readings", readingsOnCapability);
        return new ModelAndView("capability/index", refData);
    }
}
