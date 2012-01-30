package eu.uberdust.util;

import eu.uberdust.util.jobs.DescUpdaterJob;
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
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/25/12
 * Time: 6:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobSchedulerController {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(JobSchedulerController.class);
    private static final long TIME_OFFSET = 1000;

    public JobSchedulerController() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        LOGGER.info(" new StdSchedulerFactory()");

        final Scheduler sched;
        try {
            sched = schedulerFactory.getScheduler();

            final JobDetail descUpdaterJob = newJob(DescUpdaterJob.class)
                    .withIdentity("DescUpdaterJob", "group1")
                    .build();

            LOGGER.info("Created DescUpdaterJob in group1");


            // Trigger the job to run on the next round minute
            final Trigger descUpdaterTrigger = newTrigger()
                    .withIdentity("DescUpdaterTrigger", "group1")
                    .startAt(new Date(System.currentTimeMillis() + TIME_OFFSET))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever()).build();

            LOGGER.info("Created DescUpdaterTrigger");

            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(descUpdaterJob, descUpdaterTrigger);
            LOGGER.info("scheduled DescUpdaterJob by DescUpdaterTrigger");

            // Start up the scheduler (nothing can actually run until the
            // scheduler has been started)
            sched.start();
            LOGGER.info("stated scheduler");
        } catch (SchedulerException e) {
            LOGGER.error(e);
        }
    }
}