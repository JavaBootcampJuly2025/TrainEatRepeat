package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String username, String verificationCode) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject("Email Verification");
        helper.setText(
                "<h1>Welcome, " + username + "!</h1>" +
                        "<p>Please verify your email by entering the following code on the verification page:</p>" +
                        "<h2>" + verificationCode + "</h2>" +
                        "<p>This code will expire in 24 hours.</p>", true);

        mailSender.send(mimeMessage);
    }

    public void sendPasswordResetEmail(String to, String username, String resetToken) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(
                "<h1>Hello, " + username + "!</h1>" +
                        "<p>You have requested to reset your password. Please click the link below to reset your password:</p>" +
                        "<p><a href='http://localhost:8080/reset-password?token=" + resetToken + "&email=" + to + "'>Reset Password</a></p>" +
                        "<p>This link will expire in 24 hours.</p>", true);

        mailSender.send(mimeMessage);
    }
}