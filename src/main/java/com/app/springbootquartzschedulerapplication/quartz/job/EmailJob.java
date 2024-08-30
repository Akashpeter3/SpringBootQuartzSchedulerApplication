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

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Get the job data map containing the email details
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String receiverEmail = jobDataMap.getString("email");
        String senderEmail = mailProperties.getUsername(); // Get the sender email from MailProperties

        System.out.println("body "+body);
        System.out.println("receiverEmail "+receiverEmail);
        System.out.println("senderEmail "+senderEmail);

        log.info("body : "+body,"subject : "+subject,"body : "+body,"receiverEmail : "+receiverEmail);

        // Send the email
        sendEmail(senderEmail, receiverEmail, subject, body);
    }

    private void sendEmail(String fromEmail, String toEmail, String subject, String body) throws JobExecutionException {
        try {
            // Create a MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            // Set email details
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true); // true indicates that the body contains HTML content

            // Send the email
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Log the error message
            System.out.println("Failed to send email: " + e.getMessage());
            throw new JobExecutionException(e); // Re-throw as JobExecutionException to signal failure
        }
    }
}
