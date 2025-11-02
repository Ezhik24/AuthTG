package org.ezhik.authTG.commandTG;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.permissions.PermissionAttachment;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.nextStep.MacroAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MacroCMDHandler implements CommandHandler {
    String mccmd, nsmsg;
    public MacroCMDHandler(String mccmd, String nsmsg) {
        this.mccmd = mccmd;
        this.nsmsg = nsmsg;
    }
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getFrom().getId());
        if (user == null) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("macronotfound", "TG"));
            return;
        }
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("macronotg", "TG"));
            return;
        }
        String[] args = update.getMessage().getText().split(" ");
        if (user.isadmin || user.commands != null && user.commands.contains(args[0].substring(1))) {
            int count = StringUtils.countMatches(this.mccmd, "[arg");
            if (args.length == 1) {
                user.sendMessage(this.nsmsg);
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new MacroAskHandler(this.mccmd, this.nsmsg));
                return;
            }
            if (args.length != count + 1) {
                user.sendMessage(AuthTG.getMessage("macronoargs", "TG"));
                return;
            }
            for (int i = 0; i <= count; i++) {
                this.mccmd = this.mccmd.replace("[arg" + i + "]", args[i]);
            }
            user.sendMessage(AuthTG.getMessage("macrosuccess", "TG"));
            Handler.dispatchCommand(user.playername, this.mccmd);
        } else {
            user.sendMessage(AuthTG.getMessage("macronoperm", "TG"));
        }
    }
}
