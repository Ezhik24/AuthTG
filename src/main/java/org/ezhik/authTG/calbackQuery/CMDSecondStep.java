package org.ezhik.authTG.calbackQuery;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandTG.CommandCMDHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class CMDSecondStep implements CallbackQueryHandler{
    @Override
    public void execute(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        User user = User.getCurrentUser(chatId);
        if (user == null) {
            AuthTG.bot.sendMessage(chatId, AuthTG.getMessage("cmdnull", "TG"));
            return;
        }

        List<String> list = CommandCMDHandler.commands.get(chatId);
        if (list == null || list.size() < 2) {
            user.sendMessage(AuthTG.getMessage("cmdfirst", "TG"));
            return;
        }

        User target = User.getUser(list.get(1));
        if (target == null) {
            user.sendMessage(AuthTG.getMessage("cmdusernotfound", "TG"));
            return;
        }
        String[] args = update.getCallbackQuery().getData().split("_");
        if (args[1].equals("ban")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("ban")) {
                    AuthTG.loader.addCommand(target.uuid, "ban");
                    user.sendMessage(AuthTG.getMessage("cmdsecondadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "TG")));
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondaddplayer", "TG").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "TG")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondaddplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "MC"))));
                } else {
                    user.sendMessage(AuthTG.getMessage("cmdsecondexcadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "TG")));
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("ban")) {
                    AuthTG.loader.removeCommand(target.uuid, "ban");
                    user.sendMessage(AuthTG.getMessage("cmdsecondrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "TG")));
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondremplayer", "TG").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "TG")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondremplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondban", "MC"))));
                } else {
                    user.sendMessage(AuthTG.getMessage("cmdsecondexcrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                }
            }
        }
        if (args[1].equals("kick")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("kick")) {
                    AuthTG.loader.addCommand(target.uuid, "kick");
                    user.sendMessage(AuthTG.getMessage("cmdsecondadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "TG")));;
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondaddplayer", "TG").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "TG")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondaddplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "MC"))));
                } else {
                    user.sendMessage(AuthTG.getMessage("cmdsecondexcadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "TG")));
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("kick")) {
                    AuthTG.loader.removeCommand(target.uuid, "kick");
                    user.sendMessage(AuthTG.getMessage("cmdsecondrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "TG")));
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondremplayer", "TG").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "TG")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondremplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "MC"))));
                } else {
                    user.sendMessage(AuthTG.getMessage("cmdsecondexcrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondkick", "TG")));
                }
            }
        }
        if (args[1].equals("mute")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("mute")) {
                    AuthTG.loader.addCommand(target.uuid, "mute");
                    user.sendMessage(AuthTG.getMessage("cmdsecondadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondaddplayer", "TG").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondaddplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "MC"))));
                } else {
                    user.sendMessage(AuthTG.getMessage("cmdsecondexcadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("mute")) {
                    AuthTG.loader.removeCommand(target.uuid, "mute");
                    user.sendMessage(AuthTG.getMessage("cmdsecondrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondremplayer", "TG").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondremplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "MC"))));
                } else {
                    user.sendMessage(AuthTG.getMessage("cmdsecondexcrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "TG")));
                }
            }
        }
        for (String key : AuthTG.macro.getKeys(false)) {
            if (args[1].equals(key)) {
                if (list.get(0).equals("add")) {
                    if (target.commands != null && !target.commands.contains(key)) {
                        AuthTG.loader.addCommand(target.uuid, key);
                        user.sendMessage(AuthTG.getMessage("cmdsecondadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.macro.getString(key + ".textbutton")));
                        if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondaddplayer", "TG").replace("{PERMISSION}", AuthTG.macro.getString(key + ".textbutton")));
                        if (target.player != null)
                            target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondaddplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "MC"))));
                    } else {
                        user.sendMessage(AuthTG.getMessage("cmdsecondexcadd", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}",AuthTG.macro.getString(key + ".textbutton")));
                    }
                }
                if (list.get(0).equals("rem")) {
                    if (target.commands != null && target.commands.contains(key)) {
                        AuthTG.loader.removeCommand(target.uuid, key);
                        user.sendMessage(AuthTG.getMessage("cmdsecondrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.macro.getString(key + ".textbutton")));
                        if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdsecondremplayer", "TG").replace("{PERMISSION}", AuthTG.macro.getString(key + ".textbutton")));
                        if (target.player != null)
                            target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdsecondremplayer", "MC").replace("{PERMISSION}", AuthTG.getMessage("cmdsecondmute", "MC"))));
                    } else {
                        user.sendMessage(AuthTG.getMessage("cmdsecondexcrem", "TG").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.macro.getString(key + ".textbutton")));
                    }
                }
            }
        }
    }
}
