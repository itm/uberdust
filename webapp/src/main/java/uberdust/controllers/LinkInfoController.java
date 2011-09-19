package uberdust.controllers;

import eu.wisebed.wiseml.model.setup.Link;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.LinkCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for displaying link information.
 */
public class LinkInfoController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.LinkController linkManager;

    public LinkInfoController() {
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

        // a link instance
        Link thisLink = null;

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // Retrieve the link
        if (command.getSourceId() != null && command.getTargetId() != null) {
            thisLink = linkManager.getByID(
                    command.getSourceId(),command.getTargetId());
        }

        // if no link found return error view
        if(thisLink == null){
            throw new Exception(
                    new Throwable("Cannot find link [" + command.getSourceId() +"," + command.getTargetId())+"]");
        }

        // return index view with refData
        refData.put("thisLink", thisLink);
        return new ModelAndView("link/index", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,formattedErrorForFrontEnd);
    }
}
