package com.digiCart.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.digiCart.notification_service.dto.OrderPlacedNotificationEvent;
import com.digiCart.notification_service.dto.PaymentSuccessNotificationEvent;
import com.digiCart.notification_service.dto.UserRegistrationNotificationEvent;

import jakarta.mail.internet.MimeMessage;

@Service
public class NotificationEmailService {

    private static final Logger log = LoggerFactory.getLogger(NotificationEmailService.class);

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final OrderServiceClient orderServiceClient;

    @Value("${notification.mail.from}")
    private String fromEmail;

    public NotificationEmailService(TemplateEngine templateEngine,
                                    JavaMailSender mailSender,
                                    OrderServiceClient orderServiceClient) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.orderServiceClient = orderServiceClient;
    }

    public void sendOrderPlacedEmail(OrderPlacedNotificationEvent event) {
        Context context = new Context();
        context.setVariable("orderId", event.getOrderId());
        context.setVariable("paymentLink", event.getPaymentLink());

        String subject = "Order placed successfully - complete your payment";
        String htmlMessage = templateEngine.process("order-placed-payment-link", context);

        boolean sent = sendEmail(event.getUserEmail(), subject, htmlMessage);
        if (sent) {
            orderServiceClient.markOrderPlacedEmailSent(event.getOrderId());
        }
    }

    public void sendPaymentSuccessEmail(PaymentSuccessNotificationEvent event) {
        Context context = new Context();
        context.setVariable("orderId", event.getOrderId());
        context.setVariable("paymentId", event.getPaymentId());

        String subject = "Payment successful - your order is being processed";
        String htmlMessage = templateEngine.process("payment-success-order-processing", context);

        boolean sent = sendEmail(event.getUserEmail(), subject, htmlMessage);
        if (sent) {
            orderServiceClient.markPaymentSuccessEmailSent(event.getOrderId());
        }
    }

    public void sendUserRegistrationEmail(UserRegistrationNotificationEvent event) {
        Context context = new Context();
        context.setVariable("name", event.getName());
        context.setVariable("verificationCode", event.getVerificationCode());

        String subject = "Welcome to DigiCart - activate your account";
        String htmlMessage = templateEngine.process("user-registration-verify", context);

        sendEmail(event.getEmail(), subject, htmlMessage);
    }

    private boolean sendEmail(String toEmail, String subject, String htmlMessage) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Skipping email send because toEmail is not set");
            return false;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);
            mailSender.send(mimeMessage);
            log.info("Email sent to {} with subject '{}'", toEmail, subject);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email to {} with subject '{}'", toEmail, subject, ex);
            return false;
        }
    }
}
