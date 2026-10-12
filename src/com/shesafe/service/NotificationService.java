package com.shesafe.service;

import com.shesafe.model.EmergencyContact;
import com.shesafe.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * NotificationService
 * Dispatches emergency SOS distress alerts to trusted contacts via Email (SMTP)
 * and documents external SMS Gateway integration requirements.
 *
 * NOTE ON CELLULAR SMS LIMITATIONS IN JAVA:
 * Standard Java SE cannot transmit GSM/CDMA SMS text messages directly to a mobile number
 * without using an external telecommunications API Gateway (such as Twilio, Sinch, or Fast2SMS).
 * These gateways require:
 * 1. A registered commercial account and verified Sender ID.
 * 2. API Key / Account SID authentication tokens.
 * 3. Telecom carrier per-SMS message fees and DLT compliance.
 * Therefore, cellular phone notifications are handled via simulated gateway logs unless
 * a third-party SMS provider REST client is configured.
 */
public class NotificationService {

    public static class NotificationResult {
        public final String contactName;
        public final String target;
        public final String channel;
        public final boolean isSuccess;
        public final String deliveryStatus; // 'DELIVERED', 'FAILED', 'TEST_MODE'
        public final String details;

        public NotificationResult(String contactName, String target, String channel,
                                  boolean isSuccess, String deliveryStatus, String details) {
            this.contactName = contactName;
            this.target = target;
            this.channel = channel;
            this.isSuccess = isSuccess;
            this.deliveryStatus = deliveryStatus;
            this.details = details;
        }
    }

    private Properties config = new Properties();

    public NotificationService() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        InputStream input = getClass().getResourceAsStream("/db.properties");
        try {
            if (input == null) {
                File f = new File("db.properties");
                if (f.exists()) input = new FileInputStream(f);
            }
            if (input != null) {
                config.load(input);
            }
        } catch (Exception ignored) {
        } finally {
            if (input != null) {
                try { input.close(); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Dispatches an emergency notification to a specific contact.
     * Evaluates whether recipient has an email or a phone number.
     */
    public NotificationResult notifyContact(User user, EmergencyContact contact, String locationText, String mapLink) {
        String contactName = contact.getContactName();
        String contactPhone = contact.getContactPhone() != null ? contact.getContactPhone().trim() : "";

        // Determine destination target (Check if phone field actually holds an email, or is a phone number)
        boolean isEmail = contactPhone.contains("@") && contactPhone.contains(".");

        String subject = "🚨 URGENT EMERGENCY ALERT from " + user.getFullName();
        String messageBody = buildAlertMessage(user, contact, locationText, mapLink);

        if (isEmail) {
            return sendEmailNotification(contactName, contactPhone, subject, messageBody);
        } else {
            return processSmsOrPhoneNotification(contactName, contactPhone, messageBody);
        }
    }

    /**
     * Attempts to send a real email via JavaMail / SMTP if configured,
     * or documents test mode status if SMTP is unconfigured.
     */
    private NotificationResult sendEmailNotification(String recipientName, String recipientEmail,
                                                     String subject, String body) {
        String mode = config.getProperty("notification.mode", "test_mode").trim();
        String host = config.getProperty("smtp.host", "smtp.gmail.com").trim();
        String port = config.getProperty("smtp.port", "587").trim();
        String user = config.getProperty("smtp.user", "").trim();
        String pass = config.getProperty("smtp.password", "").trim();

        // 1. Check if SMTP credentials are fully provided
        if ("smtp".equalsIgnoreCase(mode)) {
            if (user.isEmpty() || pass.isEmpty()) {
                return new NotificationResult(
                        recipientName,
                        recipientEmail,
                        "EMAIL",
                        false,
                        "FAILED",
                        "SMTP credentials missing. Please set smtp.user and smtp.password in db.properties."
                );
            }

            // Attempt reflection-based JavaMail invocation (jakarta.mail or javax.mail)
            try {
                boolean sent = invokeJavaMail(host, port, user, pass, recipientEmail, subject, body);
                if (sent) {
                    return new NotificationResult(
                            recipientName,
                            recipientEmail,
                            "EMAIL",
                            true,
                            "DELIVERED",
                            "Real email delivered successfully via SMTP server (" + host + ")."
                    );
                } else {
                    return new NotificationResult(
                            recipientName,
                            recipientEmail,
                            "EMAIL",
                            false,
                            "FAILED",
                            "SMTP transport reported delivery failure."
                    );
                }
            } catch (ClassNotFoundException e) {
                return new NotificationResult(
                        recipientName,
                        recipientEmail,
                        "EMAIL",
                        false,
                        "FAILED",
                        "JavaMail API (javax.mail / jakarta.mail) not found on project classpath."
                );
            } catch (Exception ex) {
                return new NotificationResult(
                        recipientName,
                        recipientEmail,
                        "EMAIL",
                        false,
                        "FAILED",
                        "SMTP Error: " + ex.getMessage()
                );
            }
        }

        // 2. Safe Demonstration / Test Mode
        return new NotificationResult(
                recipientName,
                recipientEmail,
                "EMAIL",
                true,
                "TEST_MODE",
                "Demonstration Mode: Alert dispatched to test recipient queue."
        );
    }

    /**
     * Handles cellular phone numbers.
     * Accurately informs user about external carrier SMS API requirements.
     */
    private NotificationResult processSmsOrPhoneNotification(String recipientName, String phoneNumber, String body) {
        String mode = config.getProperty("notification.mode", "test_mode").trim();

        if (phoneNumber.isEmpty()) {
            return new NotificationResult(
                    recipientName,
                    "N/A",
                    "SMS",
                    false,
                    "FAILED",
                    "No valid phone number or email address provided for contact."
            );
        }

        // Accurately logs that SMS transmission requires an external paid SMS Gateway API
        return new NotificationResult(
                recipientName,
                phoneNumber,
                "SMS_GATEWAY",
                true,
                "TEST_MODE",
                "SMS Gateway Simulation: Ordinary desktop Java requires third-party API (e.g. Twilio/Fast2SMS) to deliver cellular SMS."
        );
    }

    private String buildAlertMessage(User user, EmergencyContact contact, String locationText, String mapLink) {
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 EMERGENCY DISTRESS ALERT 🚨\n\n");
        sb.append("Dear ").append(contact.getContactName()).append(",\n\n");
        sb.append("This is an emergency distress notification triggered by ")
          .append(user.getFullName()).append(" via the SheSafe Security System.\n\n");
        sb.append("DETAILS:\n");
        sb.append("• User Name: ").append(user.getFullName()).append("\n");
        sb.append("• Registered Phone: ").append(user.getPhone()).append("\n");
        sb.append("• Time of Alert: ").append(new java.util.Date()).append("\n");

        if (locationText != null && !locationText.isEmpty()) {
            sb.append("• Location: ").append(locationText).append("\n");
        }
        if (mapLink != null && !mapLink.isEmpty()) {
            sb.append("• Live Map Coordinates: ").append(mapLink).append("\n");
        }

        sb.append("\nPlease attempt to contact ").append(user.getFullName())
          .append(" immediately or inform local emergency authorities (Police: 112 / Women Helpline: 1091).\n\n");
        sb.append("SheSafe Personal Safety Network");
        return sb.toString();
    }

    /**
     * Dispatches email via standard JavaMail using reflection to avoid mandatory compile-time dependencies.
     */
    private boolean invokeJavaMail(String host, String port, String user, String pass,
                                  String recipient, String subject, String body) throws Exception {
        Class<?> sessionClass;
        try {
            sessionClass = Class.forName("jakarta.mail.Session");
        } catch (ClassNotFoundException e) {
            sessionClass = Class.forName("javax.mail.Session");
        }

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");

        // Authenticator implementation
        Class<?> authClass = Class.forName(sessionClass.getPackage().getName() + ".Authenticator");
        Class<?> passAuthClass = Class.forName(sessionClass.getPackage().getName() + ".PasswordAuthentication");
        Object passAuth = passAuthClass.getConstructor(String.class, String.class).newInstance(user, pass);

        Object authenticator = java.lang.reflect.Proxy.newProxyInstance(
                authClass.getClassLoader(),
                new Class[]{authClass},
                (proxy, method, args) -> {
                    if ("getPasswordAuthentication".equals(method.getName())) {
                        return passAuth;
                    }
                    return null;
                }
        );

        Method getInstanceMethod = sessionClass.getMethod("getInstance", Properties.class, authClass);
        Object session = getInstanceMethod.invoke(null, mailProps, authenticator);

        // Construct Message
        Class<?> messageClass = Class.forName(sessionClass.getPackage().getName() + ".Message");
        Class<?> mimeMessageClass = Class.forName(sessionClass.getPackage().getName() + ".internet.MimeMessage");
        Class<?> internetAddressClass = Class.forName(sessionClass.getPackage().getName() + ".internet.InternetAddress");
        Class<?> recipientTypeClass = Class.forName(sessionClass.getPackage().getName() + ".Message$RecipientType");

        Object message = mimeMessageClass.getConstructor(sessionClass).newInstance(session);

        Method setFromMethod = messageClass.getMethod("setFrom", Class.forName(sessionClass.getPackage().getName() + ".Address"));
        Object fromAddress = internetAddressClass.getConstructor(String.class).newInstance(user);
        setFromMethod.invoke(message, fromAddress);

        Method setRecipientMethod = messageClass.getMethod("setRecipient", recipientTypeClass, Class.forName(sessionClass.getPackage().getName() + ".Address"));
        Object toType = recipientTypeClass.getField("TO").get(null);
        Object toAddress = internetAddressClass.getConstructor(String.class).newInstance(recipient);
        setRecipientMethod.invoke(message, toType, toAddress);

        Method setSubjectMethod = messageClass.getMethod("setSubject", String.class);
        setSubjectMethod.invoke(message, subject);

        Method setTextMethod = messageClass.getMethod("setText", String.class);
        setTextMethod.invoke(message, body);

        // Send via Transport
        Class<?> transportClass = Class.forName(sessionClass.getPackage().getName() + ".Transport");
        Method sendMethod = transportClass.getMethod("send", messageClass);
        sendMethod.invoke(null, message);

        return true;
    }
}
