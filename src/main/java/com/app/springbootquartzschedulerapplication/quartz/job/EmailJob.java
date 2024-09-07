package com.app.springbootquartzschedulerapplication.quartz.job;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class EmailJob extends QuartzJobBean {

    private static final String SUBJECT_KEY = "subject";
    private static final String BODY_KEY = "body";
    private static final String EMAIL_KEY = "email";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String emailSubject = jobDataMap.getString(SUBJECT_KEY);
        String emailBody = jobDataMap.getString(BODY_KEY);
        String recipientEmail = jobDataMap.getString(EMAIL_KEY);
        String senderEmail = mailProperties.getUsername();

        logEmailDetails(senderEmail, recipientEmail, emailSubject, emailBody);

//        try {
//            sendEmail(senderEmail, recipientEmail, emailSubject, emailBody);
//        } catch (MessagingException e) {
//            log.error("Failed to send email: {}", e.getMessage());
//            throw new JobExecutionException(e);
//        }
    }

    private void logEmailDetails(String senderEmail, String recipientEmail, String subject, String body) {
        log.info("Sender: {}, Recipient: {}, Subject: {}, Body: {}", senderEmail, recipientEmail, subject, body);
    }

//    private void sendEmail(String fromEmail, String toEmail, String subject, String body) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
//
//        mimeMessageHelper.setFrom(fromEmail);
//        mimeMessageHelper.setTo(toEmail);
//        mimeMessageHelper.setSubject(subject);
//        mimeMessageHelper.setText(body, true);
//
//        mailSender.send(mimeMessage);
//    }
}