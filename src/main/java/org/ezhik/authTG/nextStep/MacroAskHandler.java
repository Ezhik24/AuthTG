package org.ezhik.authTG.nextStep;

import org.apache.commons.lang3.StringUtils;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MacroAskHandler implements NextStepHandler{
    private String mccmd;
    private String nsmsg;
    public MacroAskHandler(String mccmd, String nsmsg) {
        this.mccmd = mccmd;
        this.nsmsg = nsmsg;
    }
    @Override
    public void execute(Update update) {
        String[] args = update.getMessage().getText().split(" ");
        User user = User.getCurrentUser(update.getMessage().getChatId());
        int count = StringUtils.countMatches(this.mccmd, "[arg");
        if (args.length != count) {
            user.sendMessage(AuthTG.getMessage("macronoargs","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        for (int i = 1; i <= count; i++) {
            this.mccmd = this.mccmd.replace("[arg" + i + "]", args[i-1]);
        }
        user.sendMessage(AuthTG.getMessage("macrosuccess","TG"));
        Handler.dispatchCommand(user.playername, this.mccmd);
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
