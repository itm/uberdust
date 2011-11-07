package eu.uberdust.nodeflasher;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PeriodicFlasherJob implements Job {

    private static final Logger LOGGER = Logger.getLogger(PeriodicFlasherJob.class);
    private Helper helper;

    public PeriodicFlasherJob() {
    }

    public void execute(final JobExecutionContext jobExecutionCtx) throws JobExecutionException {
        LOGGER.info(" |=== Starting a new PeriodicFlasherJob");

        helper = new Helper();
        helper.authenticate();
        try {
            helper.flash(helper.getNodes("nodes.telosb"), "telosb");
        } catch (Exception e) {
            LOGGER.info(e);
        }
    }
}
