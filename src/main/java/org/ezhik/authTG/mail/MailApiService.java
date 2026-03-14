package org.ezhik.authTG.mail;

import org.bukkit.configuration.ConfigurationSection;
import org.ezhik.authTG.AuthTG;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;

public final class MailApiService {

    private MailApiService() {
    }

    public static boolean sendLinkCode(String playerName, UUID uuid, String ip, String email, String code) {
        String apiUrl = AuthTG.getInstance().getConfig().getString("mail.api.url", "");
        if (apiUrl == null || apiUrl.isBlank()) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] mail.api.url is empty");
            return false;
        }

        String method = AuthTG.getInstance().getConfig().getString("mail.api.method", "POST");
        String contentType = AuthTG.getInstance().getConfig().getString("mail.api.contentType", "application/json");

        ConfigurationSection fields = AuthTG.getInstance().getConfig().getConfigurationSection("mail.api.fields");
        if (fields == null || fields.getKeys(false).isEmpty()) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] mail.api.fields is empty");
            return false;
        }

        ConfigurationSection headers = AuthTG.getInstance().getConfig().getConfigurationSection("mail.api.headers");
        String jsonBody = buildJson(fields, playerName, uuid, ip, email, code);

        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toUpperCase());
            connection.setConnectTimeout(AuthTG.getInstance().getConfig().getInt("mail.api.connectTimeoutMs", 5000));
            connection.setReadTimeout(AuthTG.getInstance().getConfig().getInt("mail.api.readTimeoutMs", 5000));
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", contentType + "; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            if (headers != null) {
                for (String headerName : headers.getKeys(false)) {
                    String headerValue = headers.getString(headerName, "");
                    if (headerValue != null && !headerValue.isBlank()) {
                        connection.setRequestProperty(headerName, headerValue);
                    }
                }
            }

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                readBody(connection.getInputStream());
                return true;
            }

            String errorBody = readBody(connection.getErrorStream());
            AuthTG.logger.log(Level.WARNING,
                    "[AuthTG] Mail API HTTP " + responseCode + ", body=" + errorBody);
            return false;

        } catch (Exception ex) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] Failed to call mail api: " + ex.getMessage(), ex);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String buildJson(ConfigurationSection fields,
                                    String playerName,
                                    UUID uuid,
                                    String ip,
                                    String email,
                                    String code) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (String key : fields.getKeys(false)) {
            Object rawValue = fields.get(key);
            Object value = replacePlaceholders(rawValue, playerName, uuid, ip, email, code);

            if (!first) {
                json.append(",");
            }

            json.append("\"")
                    .append(escapeJson(key))
                    .append("\":")
                    .append(toJsonValue(value));

            first = false;
        }

        json.append("}");
        return json.toString();
    }

    private static Object replacePlaceholders(Object value,
                                              String playerName,
                                              UUID uuid,
                                              String ip,
                                              String email,
                                              String code) {
        if (!(value instanceof String)) {
            return value;
        }

        return ((String) value)
                .replace("{PLAYER}", playerName)
                .replace("{EMAIL}", email)
                .replace("{CODE}", code)
                .replace("{UUID}", uuid.toString())
                .replace("{IP}", ip == null ? "unknown" : ip);
    }

    private static String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }

        return "\"" + escapeJson(String.valueOf(value)) + "\"";
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String readBody(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }

        try (InputStream in = inputStream) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return "";
        }
    }
}