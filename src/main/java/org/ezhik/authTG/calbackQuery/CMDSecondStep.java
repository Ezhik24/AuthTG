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
                    user.sendMessage("Вы успешно выдали право банить пользователю " + target.playername);
                    if (target.activetg) target.sendMessage("Вам было выдано право банить");
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВам было выдано право банить"));
                } else {
                    user.sendMessage("У пользователя " + target.playername + " уже есть право банить");
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("ban")) {
                    AuthTG.loader.removeCommand(target.uuid, "ban");
                    user.sendMessage("Вы успешно забрали право банить пользователю " + target.playername);
                    if (target.activetg) target.sendMessage("У вас было забрали право банить");
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cУ вас забрали право банить"));
                } else {
                    user.sendMessage("У пользователя " + target.playername + " нет право банить");
                }
            }
        }
        if (args[1].equals("kick")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("kick")) {
                    AuthTG.loader.addCommand(target.uuid, "kick");
                    user.sendMessage("Вы успешно выдали право кика пользователю " + target.playername);
                    if (target.activetg) target.sendMessage("Вам было выдано право кика");
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВам было выдано право кика"));
                } else {
                    user.sendMessage("У пользователя " + target.playername + " уже есть право кика");
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("kick")) {
                    AuthTG.loader.removeCommand(target.uuid, "kick");
                    user.sendMessage("Вы успешно забрали право кика пользователю " + target.playername);
                    if (target.activetg) target.sendMessage("У вас было забрали право кика");
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cУ вас забрали право кика"));
                } else {
                    user.sendMessage("У пользователя " + target.playername + " нет право кика");
                }
            }
        }
        if (args[1].equals("mute")) {
            if (list.get(0).equals("add")) {
                if (target.commands != null && !target.commands.contains("mute")) {
                    AuthTG.loader.addCommand(target.uuid, "mute");
                    user.sendMessage("Вы успешно выдали право мута пользователю " + target.playername);
                    if (target.activetg) target.sendMessage("Вам было выдано право мута");
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВам было выдано право мута"));
                } else {
                    user.sendMessage("У пользователя " + target.playername + " уже есть право мута");
                }
            }
            if (list.get(0).equals("rem")) {
                if (target.commands != null && target.commands.contains("mute")) {
                    AuthTG.loader.removeCommand(target.uuid, "mute");
                    user.sendMessage("Вы успешно забрали право мута пользователю " + target.playername);
                    if (target.activetg) target.sendMessage("У вас было забрали право мута");
                    if (target.player != null)
                        target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cУ вас забрали право мута"));
                } else {
                    user.sendMessage("У пользователя " + target.playername + " нет право мута");
                }
            }
        }
    }
}
