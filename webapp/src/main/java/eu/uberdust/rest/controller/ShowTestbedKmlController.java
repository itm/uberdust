package eu.uberdust.rest.controller;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowTestbedKmlController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(ShowTestbedKmlController.class);

    public ShowTestbedKmlController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                                  final Object commandObj, final BindException e) {
        return null;// TODO make this controller
    }
}
