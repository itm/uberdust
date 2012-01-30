package eu.uberdust.util.jobs;

import eu.uberdust.restfullapiclient.RestClient;
import eu.wisebed.wisedb.controller.CapabilityController;
import eu.wisebed.wiseml.model.setup.Capability;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/25/12
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DescUpdaterJob implements Job {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(DescUpdaterJob.class);

    /**
     * .
     *
     * @param jobExecutionContext .
     * @throws JobExecutionException .
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        checkCapabilities();


    }

    /**
     * Check and update the capabilities ddscriptions.
     */
    private static void checkCapabilities() {
        LOGGER.info("started");

        final List<Capability> capabilities = CapabilityController.getInstance().list();
        try {
            for (Capability capability : capabilities) {
                RestClient.getInstance().convert("observedPropery", capability.getName());
                System.out.println(capability.getName());
            }
        } catch (NullPointerException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
