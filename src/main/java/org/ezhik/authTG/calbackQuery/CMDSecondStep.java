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
        User user = User.getCurrentUser((update.getCallbackQuery().getFrom().getId()));
        List<String> list = CommandCMDHandler.commands.get(user.chatid);
        User target = User.getUser(list.get(1));
        String[] args = update.getCallbackQuery().getData().split("_");
        if (args[1].equals("ban")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("ban")) {
                    AuthTG.loader.addCommand(target.uuid, "ban");
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondadd").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondban")));
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondaddplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondban")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cmdsecondaddplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.minecraft.cmdsecondban"))));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondexcadd").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("ban")) {
                    AuthTG.loader.removeCommand(target.uuid, "ban");
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondrem").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondban")));
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondremplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondban")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cmdsecondremplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.minecraft.cmdsecondban"))));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondexcrem").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                }
            }
        }
        if (args[1].equals("kick")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("kick")) {
                    AuthTG.loader.addCommand(target.uuid, "kick");
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondadd").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondkick")));;
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondaddplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondkick")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cmdsecondaddplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.minecraft.cmdsecondkick"))));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondexcadd").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("kick")) {
                    AuthTG.loader.removeCommand(target.uuid, "kick");
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondrem").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondkick")));
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondremplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondkick")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cmdsecondremplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.minecraft.cmdsecondkick"))));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondexcrem").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                }
            }
        }
        if (args[1].equals("mute")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("mute")) {
                    AuthTG.loader.addCommand(target.uuid, "mute");
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondadd").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondaddplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cmdsecondaddplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.minecraft.cmdsecondmute"))));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondexcadd").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("mute")) {
                    AuthTG.loader.removeCommand(target.uuid, "mute");
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondrem").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondremplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cmdsecondremplayer").replace("{PERMISSION}", AuthTG.config.getString("messages.minecraft.cmdsecondmute"))));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdsecondexcrem").replace("{PLAYER}", target.playername).replace("{PERMISSION}", AuthTG.config.getString("messages.telegram.cmdsecondmute")));
                }
            }
        }
    }
}
