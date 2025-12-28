package org.ezhik.authTG.nextStep;

import org.apache.commons.lang3.StringUtils;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Arrays;

public class MacroAskHandler implements NextStepHandler{
    private String mccmd;
    private String nsmsg;
    public MacroAskHandler(String mccmd, String nsmsg) {
        this.mccmd = mccmd;
        this.nsmsg = nsmsg;
    }
    @Override
    public void execute(Update update) {
        String[] args = update.getMessage().getText().trim().split("\\s+");
        User user = User.getCurrentUser(update.getMessage().getChatId());

        int count = StringUtils.countMatches(this.mccmd, "[arg");
        boolean greedyLast = count > 0 && this.mccmd.contains("[arg" + count + "]*");

        if (args.length < count || (!greedyLast && args.length != count)) {
            user.sendMessage(AuthTG.getMessage("macronoargs","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }

        String cmd = this.mccmd;

        for (int i = 1; i <= count; i++) {
            String value;
            if (i == count && greedyLast) {
                value = String.join(" ", Arrays.copyOfRange(args, i - 1, args.length));
            } else {
                value = args[i - 1];
            }

            cmd = cmd.replace("[arg" + i + "]*", value);
            cmd = cmd.replace("[arg" + i + "]", value);
        }

        user.sendMessage(AuthTG.getMessage("macrosuccess","TG"));
        Handler.dispatchCommand(user.playername, cmd);
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
