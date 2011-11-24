package eu.uberdust.rest.controller;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller class that returns the positoin of a node in KML format.
 */
public final class ShowNodeKmlController extends AbstractRestController {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShowNodeKmlController.class);

    /**
     * Constructor.
     */
    public ShowNodeKmlController() {

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Handle request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return http servlet response.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors) {
        return null; // TODO make this controller
    }
}
