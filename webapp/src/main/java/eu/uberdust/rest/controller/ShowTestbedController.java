package eu.uberdust.rest.controller;

import com.googlecode.ehcache.annotations.Cacheable;
import eu.uberdust.command.TestbedCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wisedb.controller.LinkController;
import eu.wisebed.wisedb.controller.NodeController;
import eu.wisebed.wisedb.controller.SlseController;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Slse;
import eu.wisebed.wisedb.model.Testbed;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Link;
import eu.wisebed.wiseml.model.setup.Node;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class that returns the a web page for a testbed.
 */
public final class ShowTestbedController extends AbstractRestController {

    /**
     * Testbed persistence manager.
     */
    private transient TestbedController testbedManager;

    /**
     * Capability persistence manager.
     */
    private transient CapabilityController capabilityManager;

    /**
     * Link persistence manager.
     */
    private transient LinkController linkManager;

    /**
     * Node persistence manager.
     */
    private transient NodeController nodeManager;

    private transient SlseController slseManager;

    public void setSlseManager(SlseController slseManager) {
        this.slseManager = slseManager;
    }

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ShowTestbedController.class);

    /**
     * Constructor.
     */
    public ShowTestbedController() {
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
     * Sets capability persistence manager.
     *
     * @param capabilityManager capability persistence manager.
     */
    public void setCapabilityManager(final CapabilityController capabilityManager) {
        this.capabilityManager = capabilityManager;
    }

    /**
     * Sets link persistence manager.
     *
     * @param linkManager link persistence manager.
     */
    public void setLinkManager(final LinkController linkManager) {
        this.linkManager = linkManager;
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
     * Handle request and return the appropriate response.
     *
     * @param request    http servlet request.
     * @param response   http servlet response.
     * @param commandObj command object.
     * @param errors     a BindException exception.
     * @return http servlet response
     * @throws TestbedNotFoundException  a TestbedNotFoundException exception.
     * @throws InvalidTestbedIdException a InvalidTestbedException exception.
     */
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors)
            throws TestbedNotFoundException, InvalidTestbedIdException {


        LOGGER.info("nodeslist cache exists : " + CacheManager.getInstance().cacheExists("nodeslist"));
        if (CacheManager.getInstance().cacheExists("nodeslist")) {
            LOGGER.info("nodeslist cache size : " + CacheManager.getInstance().getCache("nodeslist").getKeys().size());
        }
        String[] caches = CacheManager.getInstance().getCacheNames();

        LOGGER.info("TotalCaches : " + caches.length);
        for (String cach : caches) {
            LOGGER.info("Cache: " + cach + " contains : " + CacheManager.getInstance().getCache(cach).getKeys().size());
        }

        LOGGER.info("Remote address: " + request.getRemoteAddr());
        LOGGER.info("Remote host: " + request.getRemoteHost());

        // set command object
        final TestbedCommand command = (TestbedCommand) commandObj;

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

        // get testbed nodes
        final List<Node> nodes = getNodes(testbed.getId());

        // get testbed links
        final List<Link> links = linkManager.list(testbed);

        // get testbed capabilities
        final List<Capability> capabilities = capabilityManager.list(testbed);

        final List<Slse> slses = slseManager.list(testbed);

        // Prepare data to pass to jsp
        final Map<String, Object> refData = new HashMap<String, Object>();

        // else put thisNode instance in refData and return index view
        refData.put("testbed", testbed);
        refData.put("nodes", nodes);
        refData.put("links", links);
        refData.put("capabilities", capabilities);
        refData.put("slses", slses);
        return new ModelAndView("testbed/show.html", refData);
    }

    @Cacheable(cacheName = "nodeListsCache")
    List<Node> getNodes(int testbedId) {


        return nodeManager.list(testbedManager.getByID(testbedId));
    }
}
