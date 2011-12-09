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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller class that returns the position of a node in GeoRSS format.
 */
public final class ShowNodeGeoRssController extends AbstractRestController {

    /**
     * Tested persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    /**
     * Deployment host.
     */
    private transient String deploymentHost;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShowNodeGeoRssController.class);

    /**
     * Constructor.
     */
    public ShowNodeGeoRssController() {
        super();

        // Make sure to set which method this controller will support.
        this.setSupportedMethods(new String[]{METHOD_GET});
    }

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    /**
     * Sets node persistence manager.
     *
     * @param nodeManager node persistence manager.
     */
    public void setNodeManager(final NodeController nodeManager) {
        this.nodeManager = nodeManager;
    }

    /**
     * Sets deployment host.
     *
     * @param deploymentHost deployment host.
     */
    public void setDeploymentHost(final String deploymentHost) {
        this.deploymentHost = deploymentHost;
    }

    /**
     * Handle request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     BindException exception.
     * @return http servlet response.
     * @throws IOException               an IOException exception.
     * @throws FeedException             a FeedException exception.
     * @throws NodeNotFoundException     NodeNotFoundException exception.
     * @throws TestbedNotFoundException  TestbedNotFoundException exception.
     * @throws InvalidTestbedIdException InvalidTestbedIdException exception.
     */
    @SuppressWarnings("unchecked")
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws IOException, FeedException, NodeNotFoundException, TestbedNotFoundException,
            InvalidTestbedIdException {

        LOGGER.info("Remote address: " + request.getRemoteAddr());
        LOGGER.info("Remote host: " + request.getRemoteHost());

        // set command object
        final NodeCommand command = (NodeCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());

        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(Integer.parseInt(command.getTestbedId()));
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }

        // look up node
        final Node node = nodeManager.getByID(command.getNodeId());
        if (node == null) {
            // if no node is found throw exception
            throw new NodeNotFoundException("Cannot find testbed [" + command.getNodeId() + "].");
        }

        // set up feed and entries
        response.setContentType("application/xml; charset=UTF-8");
        final SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(node.getId() + " GeoRSS feed");
        feed.setLink(request.getRequestURL().toString());
        feed.setDescription(testbed.getDescription());
        final List<SyndEntry> entries = new ArrayList<SyndEntry>();

        // set entry's title,link and publishing date
        final SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(node.getId());
        entry.setLink(new StringBuilder().append("http://").append(deploymentHost).append("/rest/testbed/")
                .append(testbed.getId()).append("/node/").append(node.getId()).toString());
        entry.setPublishedDate(new Date());

        // set entry's description (HTML list)
        final SyndContent description = new SyndContentImpl();
        final StringBuilder descriptionBuffer = new StringBuilder();
        descriptionBuffer.append("<p>").append(node.getDescription()).append("</p>");
        descriptionBuffer.append("<ul>");
        for (Capability capability : node.getCapabilities()) {
            descriptionBuffer.append("<li><a href=\"http://").append(deploymentHost).append("/rest/testbed/")
                    .append(testbed.getId()).append("/node/").append(node.getId()).append("/capability/")
                    .append(capability.getName()).append("\">").append(capability.getName()).append("</a></li>");
        }
        descriptionBuffer.append("</ul>");
        description.setType("text/html");
        description.setValue(descriptionBuffer.toString());
        entry.setDescription(description);

        // set the GeoRSS module and add it
        final GeoRSSModule geoRSSModule = new SimpleModuleImpl();
        if ((testbed.getSetup().getCoordinateType().equals("Cartesian"))) {

            // convert testbed origin from long/lat position to xyz if needed
            final Origin origin = testbed.getSetup().getOrigin();
            final Coordinate originCoordinate = new Coordinate((double) origin.getX(), (double) origin.getY(),
                    (double) origin.getZ(), (double) origin.getPhi(), (double) origin.getTheta());
            final Coordinate properOrigin = Coordinate.blh2xyz(originCoordinate);

            // convert node position from xyz to long/lat
            final eu.wisebed.wiseml.model.setup.Position position = node.getPosition();
            final Coordinate nodeCoordinate = new Coordinate((double) position.getX(), (double) position.getY(),
                    (double) position.getZ(), (double) position.getPhi(), (double) position.getTheta());
            final Coordinate rotated = Coordinate.rotate(nodeCoordinate, properOrigin.getPhi());
            final Coordinate absolute = Coordinate.absolute(properOrigin, rotated);
            final Coordinate nodePosition = Coordinate.xyz2blh(absolute);
            geoRSSModule.setPosition(new Position(nodePosition.getX(), nodePosition.getY()));
        } else {
            geoRSSModule.setPosition(new Position(node.getPosition().getX(), node.getPosition().getY()));
        }
        entry.getModules().add(geoRSSModule);
        entries.add(entry);

        // add entries to feed
        feed.setEntries(entries);

        // the feed output goes to response
        final SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, response.getWriter());

        return null;
    }
}
