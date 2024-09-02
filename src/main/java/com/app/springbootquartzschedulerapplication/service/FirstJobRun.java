package com.app.springbootquartzschedulerapplication.service;

import com.app.springbootquartzschedulerapplication.commonUtils.CommonUtils;
import com.app.springbootquartzschedulerapplication.jobs.FirstJob;
import com.app.springbootquartzschedulerapplication.jobs.SecondJob;
import com.app.springbootquartzschedulerapplication.model.TriggerInfo;
import com.app.springbootquartzschedulerapplication.scheduler.MainScheduler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@AllArgsConstructor
public class FirstJobRun {

    private final MainScheduler scheduler;

    private final CommonUtils commonUtils;

    @PostConstruct
    public void init() {
        try {
            TriggerInfo triggerInfo = commonUtils.getTriggerInfo(2, false, 1000L, 1000L, "info");
            scheduler.scheduleJob(FirstJob.class, triggerInfo);
            Thread.sleep(1000);
            scheduler.scheduleJob(SecondJob.class, triggerInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
