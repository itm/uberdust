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
import eu.uberdust.command.NodeCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.NodeNotFoundException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.uberdust.util.Coordinate;
import eu.wisebed.wisedb.controller.NodeController;
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

public class ShowNodeGeoRssController extends AbstractRestController {

    private TestbedController testbedManager;
    private NodeController nodeManager;
    private static final Logger LOGGER = Logger.getLogger(ShowNodeGeoRssController.class);
    private String deploymentHost;

    public ShowNodeGeoRssController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    public void setDeploymentHost(String deploymentHost) {
        this.deploymentHost = deploymentHost;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e)
            throws IOException, FeedException, NodeNotFoundException, TestbedNotFoundException,
            InvalidTestbedIdException {

        // set command object
        final NodeCommand command = (NodeCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.");
        }

        // look up testbed
        Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // look up node
        Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            // if no node is found throw exception
            throw new NodeNotFoundException("Cannot find testbed [" + command.getNodeId() + "].");
        }

        // set up feed and entries
        httpServletResponse.setContentType("application/xml; charset=UTF-8");
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(node.getId() + " GeoRSS feed");
        feed.setLink(httpServletRequest.getRequestURL().toString());
        feed.setDescription(testbed.getDescription());
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        // convert testbed origin from long/lat position to xyz
        final Origin origin = testbed.getSetup().getOrigin();
        Coordinate originCoordinate = new Coordinate((double) origin.getX(), (double) origin.getY(),
                (double) origin.getZ(), (double) origin.getPhi(), (double) origin.getTheta());
        final Coordinate cartesian = Coordinate.blh2xyz(originCoordinate);

        // set entry's title,link and publishing date
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(node.getId());
        entry.setLink(new StringBuilder().append("http://").append(deploymentHost).append("/uberdust/rest/testbed/")
                .append(testbed.getId()).append("/node/").append(node.getId()).toString());
        entry.setPublishedDate(new Date());

        // set entry's description (HTML list)
        SyndContent description = new SyndContentImpl();
        StringBuilder descriptionBuffer = new StringBuilder();
        descriptionBuffer.append("<p>").append(node.getDescription()).append("</p>");
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
        GeoRSSModule geoRSSModule = new SimpleModuleImpl();
        geoRSSModule.setPosition(new Position(nodePosition.getX(), nodePosition.getY()));
        entry.getModules().add(geoRSSModule);
        entries.add(entry);

        // add entries to feed
        feed.setEntries(entries);

        // the feed output goes to response
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, httpServletResponse.getWriter());

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage() + "\n" + exception.fillInStackTrace().getMessage();
        LOGGER.error(exception, exception.fillInStackTrace());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}