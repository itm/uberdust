package uberdust.controllers;

import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Link;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.TestbedCommand;
import uberdust.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShowTestbedStatusController extends AbstractRestController {

    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedController.class);


    public ShowTestbedStatusController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set command object
        TestbedCommand command = (TestbedCommand) commandObj;
        LOGGER.info("commandObj.getTestbedId() : " + command.getTestbedId());

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new Exception(new Throwable("Testbed IDs have number format."));
        }

        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new Exception(new Throwable("Cannot find testbed [" + testbedId + "]."));
        }

        // get nodes latest reading and total readings
        final Map<String,Date> nodeLastReadingDateMap = new HashMap<String,Date>();
        final Map<String,String> nodeTotalReadingsMap = new HashMap<String, String>();
        for(Node node : testbed.getSetup().getNodes()){
            final String nodeId = node.getId();
            final Date lastReadingDate = Util.getLastNodeReadingRecordedDate(node.getReadings());
            final String totalReadings = "" + node.getReadings().size();
            nodeLastReadingDateMap.put(nodeId,lastReadingDate);
            nodeTotalReadingsMap.put(nodeId,totalReadings);
        }

        // get links latest reading and total readings
        final Map<String,HashMap<String,Date>> linkLastReadingDateMap = new HashMap<String,HashMap<String,Date>>();
        final Map<String,HashMap<String,String>> linkTotalReadingsMap = new HashMap<String,HashMap<String,String>>();
        for(Link link : testbed.getSetup().getLink()){
            final String source = link.getSource();
            final String target = link.getTarget();
            final Date lastReadingDate = Util.getLastLinkReadingRecordedDate(link.getReadings());
            final String totalReadings = "" + link.getReadings().size();

            // set map1 for lastReadingDate
            HashMap<String,Date> map1 = new HashMap<String, Date>();
            map1.put(target, lastReadingDate);
            linkLastReadingDateMap.put(source, map1);

            // set map2 for total readings
            HashMap<String,String> map2 = new HashMap<String, String>();
            map2.put(target, totalReadings);
            linkTotalReadingsMap.put(source,map2);
        }


        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();
        refData.put("testbed", testbed);
        refData.put("nodeLastReadingDateMap",nodeLastReadingDateMap);
        refData.put("nodeTotalReadingsMap",nodeTotalReadingsMap);
        refData.put("linkLastReadingDateMap",linkLastReadingDateMap);
        refData.put("linkTotalReadingsMap",linkTotalReadingsMap);

        return new ModelAndView("testbed/status.html", refData);
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
