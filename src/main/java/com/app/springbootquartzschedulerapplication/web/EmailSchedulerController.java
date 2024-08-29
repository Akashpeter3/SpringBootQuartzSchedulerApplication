package com.app.springbootquartzschedulerapplication.web;

import com.app.springbootquartzschedulerapplication.payload.EmailRequest;
import com.app.springbootquartzschedulerapplication.payload.EmailResponse;
import com.app.springbootquartzschedulerapplication.quartz.job.EmailJob;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;


@Slf4j
@RestController
public class EmailSchedulerController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/schedule/email")
     ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getLocalDateTime(), emailRequest.getTimeZone());
            if (dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse emailResponse = new EmailResponse(false, "date time must be after current time");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
            }
            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully");
            return  ResponseEntity.ok(emailResponse);
        } catch (SchedulerException e) {
            log.error("error while scheduling email", e);
            EmailResponse emailResponse = new EmailResponse(false, "Error while scheduling email,Please try again");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }


    }
}

private JobDetail buildJobDetail(EmailRequest emailRequest) {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("email", emailRequest.getEmail());
    jobDataMap.put("subject", emailRequest.getSubject());
    jobDataMap.put("body", emailRequest.getBody());


    return JobBuilder.newJob(EmailJob.class)
            .withIdentity(UUID.randomUUID().toString(), "email-jobs")
            .withDescription("Send Email Job")
            .usingJobData(jobDataMap)
            .storeDurably() //store data in db without trigger
            .build();

}

private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
    return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.getKey().getName(), "email-triggers")
            .withDescription("Send Email Trigger")
            .startAt(Date.from(startAt.toInstant()))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build();
}
