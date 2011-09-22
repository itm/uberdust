package uberdust.controllers;

import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Setup;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.TestbedSetupCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestbedSetupInfoController extends AbstractRestController {

    private eu.wisebed.wisedb.controller.SetupController setupManager;

    private eu.wisebed.wisedb.controller.TestbedController testbedManager;

    public TestbedSetupInfoController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setSetupManager(final eu.wisebed.wisedb.controller.SetupController setupManager) {
        this.setupManager = setupManager;
    }

    public void setTestbedManager(final eu.wisebed.wisedb.controller.TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set command object
        TestbedSetupCommand command = (TestbedSetupCommand) commandObj;

        // setting up testbed
        // final int oneTestbed = testbedManager.list().size(); //expecting only one testbed so far ! ! ! TODO manager more Testbeds
        final int oneTestbed = 1;
        Testbed thisTestbed = testbedManager.getByID(oneTestbed);
        command.setTestbedId(thisTestbed.getId());
        command.setName(thisTestbed.getName());

        // Setup instance
        // final int oneSetup = setupManager.list().size(); //expecting only one setup so far ! ! !    TODO manage more setups
        final int oneSetup = 1;
        Setup thisSetup = setupManager.getByID(oneSetup);
        command.setSetupId(thisSetup.getId());

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("thisTestbed", thisTestbed);
        refData.put("thisSetup", thisSetup);
        return new ModelAndView("testbedsetup/index", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
