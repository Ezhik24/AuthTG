package org.ezhik.authTG.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.ezhik.authTG.AuthTG;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

public final class MailSmtpService {

    private MailSmtpService() {
    }

    public static boolean sendLinkCode(String playerName, UUID uuid, String ip, String email, String code) {
        FileConfiguration config = AuthTG.getInstance().getConfig();

        String host = trimToNull(config.getString("mail.smtp.host", ""));
        int port = config.getInt("mail.smtp.port", 587);
        boolean auth = config.getBoolean("mail.smtp.auth", true);
        boolean startTls = config.getBoolean("mail.smtp.startTls", true);
        boolean startTlsRequired = config.getBoolean("mail.smtp.startTlsRequired", false);
        boolean ssl = config.getBoolean("mail.smtp.ssl", false);

        String username = trimToNull(config.getString("mail.smtp.username", ""));
        String password = config.getString("mail.smtp.password", "");
        String fromEmail = trimToNull(config.getString("mail.smtp.fromEmail", ""));
        String fromName = config.getString("mail.smtp.fromName", "AuthTG");
        boolean html = config.getBoolean("mail.smtp.html", false);
        boolean debug = config.getBoolean("mail.smtp.debug", false);

        int connectionTimeout = config.getInt("mail.smtp.connectTimeoutMs", 10000);
        int timeout = config.getInt("mail.smtp.timeoutMs", 10000);
        int writeTimeout = config.getInt("mail.smtp.writeTimeoutMs", 10000);

        if (host == null) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] mail.smtp.host is empty");
            return false;
        }

        if (auth && (username == null || password == null || password.isBlank())) {
            AuthTG.logger.log(Level.WARNING,
                    "[AuthTG] SMTP auth is enabled, but username or password is empty");
            return false;
        }

        if (fromEmail == null) {
            fromEmail = username;
        }

        if (fromEmail == null || fromEmail.isBlank()) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] mail.smtp.fromEmail is empty");
            return false;
        }

        String subject = config.getString("mail.smtp.subject", "Mail verification code for {PLAYER}");
        String body = getBody(config, playerName, uuid, ip, email, code);

        subject = replacePlaceholders(subject, playerName, uuid, ip, email, code);
        body = replacePlaceholders(body, playerName, uuid, ip, email, code);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
        props.put("mail.smtp.timeout", String.valueOf(timeout));
        props.put("mail.smtp.writetimeout", String.valueOf(writeTimeout));

        if (startTls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        if (startTlsRequired) {
            props.put("mail.smtp.starttls.required", "true");
        }

        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
        }

        String sslProtocols = config.getString("mail.smtp.sslProtocols", "");
        if (sslProtocols != null && !sslProtocols.isBlank()) {
            props.put("mail.smtp.ssl.protocols", sslProtocols);
        }

        try {
            Session session;
            if (auth) {
                String finalUsername = username;
                String finalPassword = password;

                session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(finalUsername, finalPassword);
                    }
                });
            } else {
                session = Session.getInstance(props);
            }

            session.setDebug(debug);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(buildFromAddress(fromEmail, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            message.setSubject(subject, StandardCharsets.UTF_8.name());

            if (html) {
                message.setContent(body, "text/html; charset=UTF-8");
            } else {
                message.setText(body, StandardCharsets.UTF_8.name());
            }

            Transport.send(message);
            return true;

        } catch (Exception ex) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] Failed to send SMTP mail: " + ex.getMessage(), ex);
            return false;
        }
    }

    private static InternetAddress buildFromAddress(String fromEmail, String fromName) throws Exception {
        if (fromName == null || fromName.isBlank()) {
            return new InternetAddress(fromEmail);
        }

        return new InternetAddress(fromEmail, fromName, StandardCharsets.UTF_8.name());
    }

    private static String getBody(FileConfiguration config,
                                  String playerName,
                                  UUID uuid,
                                  String ip,
                                  String email,
                                  String code) {
        Object rawBody = config.get("mail.smtp.body");

        if (rawBody instanceof List<?>) {
            List<?> lines = (List<?>) rawBody;
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(String.valueOf(lines.get(i)));
            }

            return builder.toString();
        }

        String fallback = config.getString("mail.smtp.body", "Your verification code: {CODE}");
        return fallback == null ? "Your verification code: {CODE}" : fallback;
    }

    private static String replacePlaceholders(String value,
                                              String playerName,
                                              UUID uuid,
                                              String ip,
                                              String email,
                                              String code) {
        if (value == null) {
            return "";
        }

        return value
                .replace("{PLAYER}", playerName)
                .replace("{EMAIL}", email)
                .replace("{CODE}", code)
                .replace("{UUID}", uuid.toString())
                .replace("{IP}", ip == null ? "unknown" : ip);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}