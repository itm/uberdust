package eu.uberdust.rest.controller;

import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.SimpleModuleImpl;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import eu.uberdust.command.TestbedCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.uberdust.util.Coordinate;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import eu.wisebed.wiseml.model.setup.Origin;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowTestbedGeoRssController extends AbstractRestController {

    private TestbedController testbedManager;
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedGeoRssController.class);
    private String deploymentHost;

    public ShowTestbedGeoRssController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setDeploymentHost(final String deploymentHost) {
        this.deploymentHost = deploymentHost;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected ModelAndView handle(final HttpServletRequest httpServletRequest,final HttpServletResponse httpServletResponse,
                                  final Object commandObj,final BindException e)
            throws TestbedNotFoundException, InvalidTestbedIdException, IOException, FeedException {

        // set command object
        final TestbedCommand command = (TestbedCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.");
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // set up feed and entries
        httpServletResponse.setContentType("application/xml; charset=UTF-8");
        final SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(testbed.getName() + " GeoRSS");
        feed.setLink(httpServletRequest.getRequestURL().toString());
        feed.setDescription(testbed.getDescription());
        final List<SyndEntry> entries = new ArrayList<SyndEntry>();


        // convert testbed origin from long/lat position to xyz
        final Origin origin = testbed.getSetup().getOrigin();
        final Coordinate originCoordinate = new Coordinate((double) origin.getX(), (double) origin.getY(),
                (double) origin.getZ(), (double) origin.getPhi(), (double) origin.getTheta());
        final Coordinate cartesian = Coordinate.blh2xyz(originCoordinate);

        // make an entry and it
        for (Node node : testbed.getSetup().getNodes()) {
            SyndEntry entry = new SyndEntryImpl();

            // set entry's title,link and publishing date
            entry.setTitle(node.getId());
            entry.setLink(new StringBuilder().append("http://").append(deploymentHost).append("/uberdust/rest/testbed/")
                    .append(testbed.getId()).append("/node/").append(node.getId()).toString());
            entry.setPublishedDate(new Date());

            // set entry's description (HTML list)
            final SyndContent description = new SyndContentImpl();
            StringBuilder descriptionBuffer = new StringBuilder();
            descriptionBuffer.append("<p>").append(node.getDescription()).append("</p>");
            descriptionBuffer.append("<p><a href=\"http://").append(deploymentHost).append("/uberdust/rest/testbed/")
                    .append(testbed.getId()).append("/node/").append(node.getId()).append("/georss").append("\">")
                    .append("GeoRSS feed").append("</a></p>");
            descriptionBuffer.append("<ul>");
            for (Capability capability : node.getCapabilities()) {
                descriptionBuffer.append("<li><a href=\"http://").append(deploymentHost).append("/uberdust/rest/testbed/")
                        .append(testbed.getId()).append("/node/").append(node.getId()).append("/capability/")
                        .append(capability.getName()).append("\">").append(capability.getName()).append("</a></li>");
            }
            descriptionBuffer.append("</ul>");
            description.setType("text/html");
            description.setValue(descriptionBuffer.toString());
            entry.setDescription(description);

            // convert node position from xyz to long/lat
            final eu.wisebed.wiseml.model.setup.Position position = node.getPosition();
            final Coordinate nodeCoordinate = new Coordinate((double) position.getX(), (double) position.getY(),
                    (double) position.getZ(), (double) position.getPhi(), (double) position.getTheta());
            final Coordinate rotated = Coordinate.rotate(nodeCoordinate, originCoordinate.getPhi());
            final Coordinate absolute = Coordinate.absolute(cartesian, rotated);
            final Coordinate nodePosition = Coordinate.xyz2blh(absolute);

            // set the GeoRSS module and add it
            final GeoRSSModule geoRSSModule = new SimpleModuleImpl();
            geoRSSModule.setPosition(new Position(nodePosition.getX(), nodePosition.getY()));
            entry.getModules().add(geoRSSModule);
            entries.add(entry);
        }

        // add entries to feed
        feed.setEntries(entries);

        // the feed output goes to response
        final SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, httpServletResponse.getWriter());

        return null;
    }
}
