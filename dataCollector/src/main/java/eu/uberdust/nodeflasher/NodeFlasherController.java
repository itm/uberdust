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
            final Trigger nodeFlasherTrigger = newTrigger()
                    .withIdentity("nodeFlasherTrigger", "group1")
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(60)
                            .repeatForever()).build();

            LOGGER.info("Created nodeFlasherTrigger");


            // Trigger the job to run on the next round minute
            final Trigger telosReFlasherTrigger = newTrigger()
                    .withIdentity("telosReFlasherTrigger", "group2")
                    .startAt(new Date(System.currentTimeMillis() + 20000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever()).build();

            LOGGER.info("Created telosReFlasherTrigger");


            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(nodeFlasherJob, nodeFlasherTrigger);
            LOGGER.info("scheduled nodeFlasherJob by nodeFlasherTrigger");

            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(telosReFlasherJob, telosReFlasherTrigger);
            LOGGER.info("scheduled telosReFlasherJob by telosReFlasherTrigger");

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
