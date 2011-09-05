package uberdust.controllers;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.LinkCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for displaying link information.
 */
public class LinkController extends AbstractRestController {

    public LinkController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response, Object commandObj, BindException errors)
            throws Exception {

        LinkCommand command = (LinkCommand) commandObj;

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("sourceId", command.getSourceId());
        refData.put("targetId", command.getTargetId());

        return new ModelAndView("link/index", refData);
    }

}
