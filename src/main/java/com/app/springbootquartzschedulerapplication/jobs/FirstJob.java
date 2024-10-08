package com.app.springbootquartzschedulerapplication.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FirstJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        System.out.println(Arrays.toString(jobDataMap.getKeys()));
        System.out.println("First Job1 in quartz scheduling..");
    }
}
