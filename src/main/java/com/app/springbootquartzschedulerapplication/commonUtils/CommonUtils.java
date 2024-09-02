package com.app.springbootquartzschedulerapplication.commonUtils;

import com.app.springbootquartzschedulerapplication.model.TriggerInfo;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
public class CommonUtils {

    public JobDetail getJobDetail(Class<? extends Job> jobClass, TriggerInfo triggerInfo) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobClass.getSimpleName(), triggerInfo);

        return JobBuilder.newJob(jobClass)
                .withIdentity(jobClass.getSimpleName(), "grp1")
                .setJobData(jobDataMap)
                .withDescription("Job details")
                .build();
    }

    public Trigger getTriggerOfJob(Class<? extends Job> jobClass, TriggerInfo triggerInfo) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(triggerInfo.getTimeInterval());

        if (triggerInfo.isRunForever()) {
            scheduleBuilder = scheduleBuilder.repeatForever();
        } else {
            scheduleBuilder = scheduleBuilder.withRepeatCount(triggerInfo.getTriggerCount());
        }

        return TriggerBuilder.newTrigger()
                .withIdentity(jobClass.getSimpleName() + "Trigger", "grp1")
                .startAt(new Date(System.currentTimeMillis() + triggerInfo.getInitialOffset()))
                .withSchedule(scheduleBuilder)
                .withDescription("Trigger for job " + jobClass.getSimpleName())
                .build();
    }

    public TriggerInfo getTriggerInfo(int triggerCount, boolean runForever, Long repeatValue, Long initialOffset, String info) {
        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setRunForever(runForever);
        triggerInfo.setTriggerCount(triggerCount);
        triggerInfo.setInitialOffset(initialOffset);
        triggerInfo.setTimeInterval(repeatValue);
        triggerInfo.setInfo(info);
        return triggerInfo;
    }
}
