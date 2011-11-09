package eu.uberdust.nodeflasher;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/6/11
 * Time: 3:18 PM
 */
public class NodeFlasherController {

    private static final Logger LOGGER = Logger.getLogger(NodeFlasherController.class);

    public NodeFlasherController() {

        final SchedulerFactory schedulerFactory = new StdSchedulerFactory();

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
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(60)
                            .repeatForever()).build();

            LOGGER.info("Created nodeFlasherTg");


            // Trigger the job to run on the next round minute
            final Trigger telosReFlasherTg = newTrigger()
                    .withIdentity("telosReFlasherTg", "group2")
                    .startAt(new Date(System.currentTimeMillis() + 20000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(24)
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
