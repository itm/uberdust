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

    private static final Logger log = Logger.getLogger(NodeFlasherController.class);

    public NodeFlasherController() {

        final SchedulerFactory sf = new StdSchedulerFactory();

        final Scheduler sched;
        try {
            sched = sf.getScheduler();

            final JobDetail nodeFlasherJob = newJob(NodeFlasherJob.class)
                    .withIdentity("NodeFlasherJob", "group1")
                    .build();

            log.debug("Created NodeFlasherJob in group1");


            final JobDetail telosReflasherJob = newJob(PeriodicFlasherJob.class)
                    .withIdentity("telosReflasherJob", "group2")
                    .build();

            log.debug("Created telosReflasherJob in group2");


            // Trigger the job to run on the next round minute
            final Trigger nodeFlasherTrigger = newTrigger()
                    .withIdentity("nodeFlasherTrigger", "group1")
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(60)
                            .repeatForever()).build();

            log.debug("Created nodeFlasherTrigger");


            // Trigger the job to run on the next round minute
            final Trigger telosReFlasherTrigger = newTrigger()
                    .withIdentity("telosReFlasherTrigger", "group2")
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever()).build();

            log.debug("Created telosReFlasherTrigger");


            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(nodeFlasherJob, nodeFlasherTrigger);
            log.debug("scheduled nodeFlasherJob by nodeFlasherTrigger");

            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(telosReflasherJob, telosReFlasherTrigger);
            log.debug("scheduled telosReflasherJob by telosReFlasherTrigger");

            // Start up the scheduler (nothing can actually run until the
            // scheduler has been started)
            sched.start();
            log.debug("stated scheduler");

        } catch (SchedulerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        log.info("Started NodeFlasherController");
    }


}
