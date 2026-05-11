package org.ezhik.authTG.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.BotVK;
import org.ezhik.authTG.callbackQueryVK.CallbackQueryVK;
import org.ezhik.authTG.commandTG.CommandHandler;
import org.ezhik.authTG.commandVK.VKCommandHandler;
import org.ezhik.authTG.nextStep.NextStepHandler;
import org.ezhik.authTG.nextStepVK.NextStepVK;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.ezhik.authTG.BotVK.*;

public class VKCheckHandler extends BukkitRunnable {
    private final BotVK vk;
    private final String server;
    private final String key;
    private volatile String ts;

    private final AtomicBoolean inFlight = new AtomicBoolean(false);

    public VKCheckHandler(BotVK vk, String server, String ts, String key) {
        this.vk = vk;
        this.server = server;
        this.ts = ts;
        this.key = key;
    }

    @Override
    public void run() {
        if (!AuthTG.isVKEnabled()) {
            cancel();
            return;
        }

        if (!inFlight.compareAndSet(false, true)) {
            return;
        }

        vk.checkEvent(server, key, ts).whenComplete((json, throwable) -> {
            try {
                if (throwable != null) {
                    AuthTG.logger.warning("[AuthTG] VK long poll request failed: " + throwable.getMessage());
                    return;
                }

                if (json == null) {
                    return;
                }

                if (json.has("failed")) {
                    int failed = json.optInt("failed", 0);

                    if (failed == 1) {
                        String newTs = json.optString("ts", null);
                        if (newTs != null && !newTs.isBlank()) {
                            ts = newTs;
                        }
                        return;
                    }

                    AuthTG.logger.warning("[AuthTG] VK long poll returned failed=" + failed + ". Re-init VK bot is recommended.");
                    return;
                }

                String newTs = json.optString("ts", null);
                if (newTs != null && !newTs.isBlank()) {
                    ts = newTs;
                }

                JSONArray updates = json.optJSONArray("updates");
                if (updates == null || updates.isEmpty()) {
                    return;
                }

                for (int i = 0; i < updates.length(); i++) {
                    JSONObject event = updates.optJSONObject(i);
                    if (event == null) {
                        continue;
                    }

                    String type = event.optString("type", "");
                    if (!"message_new".equals(type)) {
                        continue;
                    }

                    JSONObject object = event.optJSONObject("object");
                    if (object == null) {
                        continue;
                    }

                    JSONObject message = object.optJSONObject("message");
                    if (message == null) {
                        continue;
                    }

                    int peerId = message.optInt("peer_id", 0);
                    String text = message.optString("text", "");

                    if (peerId <= 0) {
                        continue;
                    }
                    String payloadStr = "{}";

                    if (nextStepHandler.containsKey(peerId) && !text.startsWith("/")) {
                        NextStepVK h = nextStepHandler.get(peerId);
                        if (h != null) h.execute(peerId,text);
                        return;
                    } else if (text.startsWith("/")) {
                        if (nextStepHandler.containsKey(peerId)) nextStepHandler.remove(peerId);

                        String[] str = text.split(" ");
                        VKCommandHandler h = commandHandler.get(str[0]);
                        if (h != null) h.execute(peerId,text);
                        return;
                    } else if (message.has("payload")) {
                        payloadStr = message.getString("payload");
                        JSONObject payload = new JSONObject(payloadStr);
                        if (payload.has("answer")) {
                            String answer = payload.getString("answer");
                            UUID uuid = UUID.fromString(payload.getString("uuid"));
                            CallbackQueryVK h = callbackQueryHandler.get(answer);
                            if (h != null) h.execute(peerId, uuid);
                        }
                    }

                }
            } catch (Exception e) {
                AuthTG.logger.severe("[AuthTG] VK long poll handler exception: " + e.getMessage());
            } finally {
                inFlight.set(false);
            }
        });
    }
}
