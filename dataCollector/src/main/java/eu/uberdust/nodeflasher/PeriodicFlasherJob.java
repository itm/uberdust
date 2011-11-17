package eu.uberdust.nodeflasher;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Flashes every Time executed the devices.
 */
public class PeriodicFlasherJob implements Job {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PeriodicFlasherJob.class);

    /**
     * execute flashing.
     *
     * @param jobExecutionCtx the job executing in.
     * @throws JobExecutionException something went wrong.
     */
    public final void execute(final JobExecutionContext jobExecutionCtx) throws JobExecutionException {
        LOGGER.info(" |=== Starting a new PeriodicFlasherJob");

        final Helper helper = new Helper();
        helper.authenticate();
        try {
            helper.flash(helper.getNodes("nodes.telosb"), "telosb");
        } catch (Exception e) {
            LOGGER.info(e);
        }
    }
}
