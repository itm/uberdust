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

            final JobDetail nFlasherJob = newJob(NodeFlasherJob.class)
                    .withIdentity("NodeFlasherJob", "group1")
                    .build();

            LOGGER.debug("Created NodeFlasherJob in group1");


            final JobDetail tReFlasherJob = newJob(PeriodicFlasherJob.class)
                    .withIdentity("tReFlasherJob", "group2")
                    .build();

            LOGGER.debug("Created tReFlasherJob in group2");


            // Trigger the job to run on the next round minute
            final Trigger pFlasherTrigger = newTrigger()
                    .withIdentity("pFlasherTrigger", "group1")
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(60)
                            .repeatForever()).build();

            LOGGER.debug("Created pFlasherTrigger");


            // Trigger the job to run on the next round minute
            final Trigger tReFlasherTrigger = newTrigger()
                    .withIdentity("tReFlasherTrigger", "group2")
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever()).build();

            LOGGER.debug("Created tReFlasherTrigger");


            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(nFlasherJob, pFlasherTrigger);
            LOGGER.debug("scheduled nFlasherJob by pFlasherTrigger");

            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(tReFlasherJob, tReFlasherTrigger);
            LOGGER.debug("scheduled tReFlasherJob by tReFlasherTrigger");

            // Start up the scheduler (nothing can actually run until the
            // scheduler has been started)
            sched.start();
            LOGGER.debug("stated scheduler");

        } catch (SchedulerException e) {
            LOGGER.error(e);  //To change body of catch statement use File | Settings | File Templates.
        }

        LOGGER.info("Started NodeFlasherController");
    }


}
