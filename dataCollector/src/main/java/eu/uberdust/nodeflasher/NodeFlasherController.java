package eu.uberdust.nodeflasher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Controls the Quartz scheduling of Node flashing.
 */
public class NodeFlasherController {

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeFlasherController.class);
    /**
     * Timer offset to start the first flashing check.
     */
    private static final int TIME_OFFSET = 10000;
    /**
     * Timer offset to start the second flashing check.
     */
    private static final int TIME_OFFSET2 = 20000;
    /**
     * Hours in a day.
     */
    private static final int HOURS_OF_DAY = 24;

    /**
     * Default Constructor.
     */
    public NodeFlasherController() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("NodeFlasherController");
        final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        LOGGER.info(" new StdSchedulerFactory()");




        final Scheduler sched;
        try {
            sched = schedulerFactory.getScheduler();

            final JobDetail nodeFlasherJob = newJob(NodeFlasherJob.class)
                    .withIdentity("nodeFlasherJob", "group1")
                    .build();

            LOGGER.info("Created nodeFlasherJob in group1");


            final JobDetail telosReFlasherJob = newJob(PeriodicFlasherJob.class)
                    .withIdentity("telosReFlasherJob", "group2")
                    .build();

            LOGGER.info("Created telosReFlasherJob in group2");


            // Trigger the job to run on the next round minute
            final Trigger nodeFlasherTg = newTrigger()
                    .withIdentity("nodeFlasherTg", "group1")
                    .startAt(new Date(System.currentTimeMillis() + TIME_OFFSET))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(1)
                            .repeatForever()).build();

            LOGGER.info("Created nodeFlasherTg");


            // Trigger the job to run on the next round minute
            final Trigger telosReFlasherTg = newTrigger()
                    .withIdentity("telosReFlasherTg", "group2")
                    .startAt(new Date(System.currentTimeMillis() + TIME_OFFSET2))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(HOURS_OF_DAY)
                            .repeatForever()).build();

            LOGGER.info("Created telosReFlasherTg");


            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(nodeFlasherJob, nodeFlasherTg);
            LOGGER.info("scheduled nodeFlasherJob by nodeFlasherTg");

            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(telosReFlasherJob, telosReFlasherTg);
            LOGGER.info("scheduled telosReFlasherJob by telosReFlasherTg");

            // Start up the scheduler (nothing can actually run until the
            // scheduler has been started)
            sched.start();
            LOGGER.info("stated scheduler");

        } catch (SchedulerException e) {
            LOGGER.error(e);  //To change body of catch statement use File | Settings | File Templates.
        }
        LOGGER.info("Started NodeFlasherController");
    }
}
