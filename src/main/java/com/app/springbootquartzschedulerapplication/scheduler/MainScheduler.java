package com.app.springbootquartzschedulerapplication.scheduler;

import com.app.springbootquartzschedulerapplication.commonUtils.CommonUtils;
import com.app.springbootquartzschedulerapplication.model.TriggerInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MainScheduler {
    private final Scheduler scheduler;
    private final CommonUtils commonUtils;

    @PostConstruct
    public void startScheduler() {
        initializeScheduler();
    }

    @PreDestroy
    public void stopScheduler() {
        shutdownScheduler();
    }

    private void initializeScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to start scheduler", e);
        }
    }

    private void shutdownScheduler() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to stop scheduler", e);
        }
    }

    public void scheduleJob(Class<? extends org.quartz.Job> jobClass, TriggerInfo triggerInfo) {
        try {
            JobDetail jobDetail = commonUtils.getJobDetail(jobClass, triggerInfo);
            Trigger trigger = commonUtils.getTriggerOfJob(jobClass, triggerInfo);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule job: " + jobClass.getSimpleName(), e);
        }
    }
}