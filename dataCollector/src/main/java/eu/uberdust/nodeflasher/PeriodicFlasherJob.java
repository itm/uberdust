package eu.uberdust.nodeflasher;

import org.apache.log4j.*;
import org.quartz.*;

public class PeriodicFlasherJob implements Job {

    private static final Logger LOGGER = Logger.getLogger(PeriodicFlasherJob.class);
    private Helper helper;

    public PeriodicFlasherJob() {
        helper = new Helper();
    }

    public void execute(final JobExecutionContext jobExecutionCtx) throws JobExecutionException {
        LOGGER.info(" |=== Starting a new PeriodicFlasherJob");

        helper.authenticate();

        helper.flash(helper.getNodes("telosb"), "telosb");
    }
}
