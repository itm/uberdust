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
public class nodeFlasherController {

    private static final Logger log = Logger.getLogger(nodeFlasherController.class);

    public nodeFlasherController() {


        final SchedulerFactory sf = new StdSchedulerFactory();

        final Scheduler sched;
        try {
            sched = sf.getScheduler();

            final JobDetail nodeflasherjob = newJob(nodeFlasherJob.class)
                    .withIdentity("nodeFlasherJob", "group1")
                    .build();


            // Trigger the job to run on the next round minute
            final Trigger nodeFlasherTrigger = newTrigger()
                    .withIdentity("nodeFlasherTrigger", "group1")
                    .startAt(new Date(System.currentTimeMillis() + 10000))
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(nodeFlasherJob.INTERVAL)
                            .repeatForever()).build();


            // Tell quartz to schedule the job using our trigger
            sched.scheduleJob(nodeflasherjob, nodeFlasherTrigger);

            // Start up the scheduler (nothing can actually run until the
            // scheduler has been started)
            sched.start();

        } catch (SchedulerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        log.info("Started nodeFlasherController");
    }
}
