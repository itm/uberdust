package uberdust.controllers;

import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.SimpleModuleImpl;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.TestbedCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ShowTestbedGeoRssController extends AbstractRestController {

    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedGeoRssController.class);


    public ShowTestbedGeoRssController() {
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

        // set up feed and entries
        httpServletResponse.setContentType("application/xml; charset=UTF-8");
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(testbed.getName()  + " GeoRSS");
        feed.setLink(httpServletRequest.getRequestURL().toString());
        feed.setDescription(testbed.getDescription());
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        // make an entry and it
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(testbed.getName()  + " GeoRSS");
        entry.setLink(httpServletRequest.getRequestURL().toString());
        entry.setPublishedDate(new Date());
        SyndContent description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(testbed.getName() + " GeoRSS");
        entry.setDescription(description);
        entries.add(entry);

        // set the GeoRSS module and add it
        GeoRSSModule geoRSSModule = new SimpleModuleImpl();
        geoRSSModule.setPosition(new Position(testbed.getSetup().getOrigin().getX(),
                testbed.getSetup().getOrigin().getY()));
        entry.getModules().add(geoRSSModule);
        feed.setEntries(entries);

        // the feed output goes to response
        SyndFeedOutput output = new SyndFeedOutput();
        try {
            output.output(feed, httpServletResponse.getWriter());
        } catch (FeedException ex) {
            throw new Exception(new Throwable("Error occur while making GeoRSS for testbed [" + testbedId + "]."));
        }

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
