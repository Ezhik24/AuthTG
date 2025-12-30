package org.ezhik.authTG.commandTG;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.permissions.PermissionAttachment;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.nextStep.MacroAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Arrays;

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

        String[] args = update.getMessage().getText().trim().split("\\s+");
        if (args.length == 0) return;

        if (user.isadmin || user.commands != null && user.commands.contains(args[0].substring(1))) {
            int count = StringUtils.countMatches(this.mccmd, "[arg");
            boolean greedyLast = count > 0 && this.mccmd.contains("[arg" + count + "]*");

            if (args.length == 1) {
                user.sendMessage(this.nsmsg);
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new MacroAskHandler(this.mccmd, this.nsmsg));
                return;
            }

            int provided = args.length - 1; // без самой команды /bc
            if (provided < count || (!greedyLast && provided != count)) {
                user.sendMessage(AuthTG.getMessage("macronoargs", "TG"));
                return;
            }

            String cmd = this.mccmd;

            for (int i = 1; i <= count; i++) {
                String value;
                if (i == count && greedyLast) {
                    value = String.join(" ", Arrays.copyOfRange(args, i, args.length));
                } else {
                    value = args[i];
                }

                // сначала заменяем вариант со звездочкой, иначе останется лишняя "*"
                cmd = cmd.replace("[arg" + i + "]*", value);
                cmd = cmd.replace("[arg" + i + "]", value);
            }

            user.sendMessage(AuthTG.getMessage("macrosuccess", "TG"));
            Handler.dispatchCommand(user.playername, cmd);
        } else {
            user.sendMessage(AuthTG.getMessage("macronoperm", "TG"));
        }
    }
}
