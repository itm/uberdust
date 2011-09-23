package uberdust.controllers;

import eu.wisebed.wiseml.model.setup.Link;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.LinkCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for displaying link information.
 */
public class LinkController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.LinkController linkManager;
        private static final Logger LOGGER = Logger.getLogger(LinkController.class);

    public LinkController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setLinkManager(eu.wisebed.wisedb.controller.LinkController linkManager) {
        this.linkManager = linkManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws Exception {

        // set command object
        LinkCommand command = (LinkCommand) commandObj;
        LOGGER.info("command.getNodeId() " + command.getSourceId());
        LOGGER.info("command.getTargetId() " + command.getTargetId());

        // a link instance  and link list
        Link link = null;
        Link linkInv = null;
        List<Link> links = new ArrayList<Link>();


        // Retrieve the link and it's inverse
        if (command.getSourceId() != null && command.getTargetId() != null) {
            link = linkManager.getByID(command.getSourceId(),command.getTargetId());
            linkInv = linkManager.getByID(command.getTargetId(),command.getSourceId());
        }

        // if no link or inverse link found return error view
        if(link == null && linkInv == null){
            throw new Exception(new Throwable("Cannot find link [" + command.getSourceId() +"," + command.getTargetId()+
                    "] or the inverse link [" + command.getTargetId() +"," + command.getSourceId()+"]"));
        }

        // if at least link or linkInv was found
        if(link != null) links.add(link);
        if(linkInv != null) links.add(linkInv);

        // return index view with refData
        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        refData.put("links", links);
        return new ModelAndView("link/index", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,formattedErrorForFrontEnd);
    }
}
