package uberdust.controllers;

import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.SimpleModuleImpl;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
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
        feed.setTitle(testbed.getName() + " GeoRSS");
        feed.setLink(httpServletRequest.getRequestURL().toString());
        feed.setDescription(testbed.getDescription());
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        // make an entry and it
        for (Node node : testbed.getSetup().getNodes()) {
            SyndEntry entry = new SyndEntryImpl();

            // set entry's title,link and publishing date
            entry.setTitle(node.getId());
            entry.setLink("http://150.140.5.11:8080" +     // TODO those constants should get out.They suck
                    "/uberdust/rest/testbed/" + testbed.getId() + "/node/" + node.getId());
            entry.setPublishedDate(new Date());

            // set entry's description (HTML list)
            SyndContent description = new SyndContentImpl();
            StringBuffer descriptionBuffer = new StringBuffer();
            descriptionBuffer.append("<p>" + node.getDescription() + "</p>");
            descriptionBuffer.append("<ul>");
            for(Capability capability : node.getCapabilities()) {
                descriptionBuffer.append("<li><a href=\"http://150.140.5.11:8080" +
                        "/uberdust/rest/testbed/" + testbed.getId() +
                        "/node/" + node.getId() + "/capability/" + capability.getName() +"\">"+ capability.getName()
                        + "</a></li>"
                );
            }
            descriptionBuffer.append("</ul>");
            description.setType("text/html");
            description.setValue(descriptionBuffer.toString());
            entry.setDescription(description);

            // set the GeoRSS module and add it
            GeoRSSModule geoRSSModule = new SimpleModuleImpl();
            geoRSSModule.setPosition(new Position(
                    testbed.getSetup().getOrigin().getX() - node.getPosition().getX(),
                    testbed.getSetup().getOrigin().getY() - node.getPosition().getY()));
            entry.getModules().add(geoRSSModule);
            entries.add(entry);
        }

        // add entries to feed
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
