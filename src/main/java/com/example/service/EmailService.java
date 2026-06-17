package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:no-reply@vehicle-rent.local}") String fromAddress) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.fromAddress = fromAddress;
    }

    public void sendAccountCreatedEmail(String to) {
        sendEmail(to, "Account created",
                "Congratulations account successfuly created"
                        + "\n\n\n\n\nBest regards,\nVehicle Rent Team");
    }

    public void sendPasswordResetCode(String to, String code) {
        sendEmail(
                to,
                "Password reset code",
                "Your password reset code is: " + code
                        + "\nReset code will expire in 15 minutes."
                        + "\n\nPlease don't share this code with anyone."
                        + "\nIf you didn't request a password reset, please ignore this email."
                        + "\n\n\n\n\nBest regards,\nVehicle Rent Team");
    }

    private void sendEmail(String to, String subject, String text) {
        if (mailSender == null) {
            logEmail(to, subject, text);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception exception) {
            LOGGER.warn("Email could not be sent to {}. Subject: {}. Text: {}", to, subject, text, exception);
        }
    }

    private void logEmail(String to, String subject, String text) {
        LOGGER.info("Email fallback. To: {}. Subject: {}. Text: {}", to, subject, text);
    }
}
