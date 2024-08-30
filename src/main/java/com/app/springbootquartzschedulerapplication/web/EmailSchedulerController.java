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
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class EmailSchedulerController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping(value = "/schedule/email", consumes = "application/json", produces = "application/json")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            // Convert LocalDateTime and ZoneId to ZonedDateTime
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getLocalDateTime(), emailRequest.getTimeZone());

            // Validate that the scheduled time is in the future
            if (dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse emailResponse = new EmailResponse(false, "Date time must be after the current time");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailResponse);
            }

            // Build the job detail
            JobDetail jobDetail = buildJobDetail(emailRequest);

            // Build the trigger with the job detail and start time
            Trigger trigger = buildTrigger(jobDetail, dateTime);

            // Schedule the job
            scheduler.scheduleJob(jobDetail, trigger);

            // Return a successful response
            EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully");
            return ResponseEntity.ok(emailResponse);
        } catch (SchedulerException e) {
            log.error("Error while scheduling email", e);

            // Return an error response
            EmailResponse emailResponse = new EmailResponse(false, "Error while scheduling email. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }
    }

    @GetMapping("/hi")
    public ResponseEntity<String> getTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello World");
    }

    private JobDetail buildJobDetail(EmailRequest emailRequest) {
        // Populate JobDataMap with email request data
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());

        // Build and return the JobDetail
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        // Build and return the Trigger with the specified start time
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
