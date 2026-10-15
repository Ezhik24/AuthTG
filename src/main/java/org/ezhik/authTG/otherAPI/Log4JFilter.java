package org.ezhik.authTG.otherAPI;

<<<<<<< HEAD
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class Log4JFilter implements Filter {
    public Result filter(LogEvent record) {
        try {
            if(record != null && record.getMessage() != null) {
                String npe = record.getMessage().getFormattedMessage().toLowerCase();
                return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/login ") && !npe.contains("/l ") && !npe.contains("/reg ") && !npe.contains("/changepassword ") && !npe.contains("/setpassword ") && !npe.contains("/code ") && !npe.contains("/register ")?Result.NEUTRAL:Result.DENY);
            } else {
                return Result.NEUTRAL;
            }
        } catch (NullPointerException var3) {
            return Result.NEUTRAL;
        }
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        try {
            if(message == null) {
                return Result.NEUTRAL;
            } else {
                String npe = message.toLowerCase();
                return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/login ") && !npe.contains("/l ") && !npe.contains("/reg ") && !npe.contains("/changepassword ") && !npe.contains("/setpassword ") && !npe.contains("/code ") && !npe.contains("/register ")?Result.NEUTRAL:Result.DENY);
            }
        } catch (NullPointerException var7) {
            return Result.NEUTRAL;
        }
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        return null;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
        return null;
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        try {
            if(message == null) {
                return Result.NEUTRAL;
            } else {
                String npe = message.toString().toLowerCase();
                return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/login ") && !npe.contains("/l ") && !npe.contains("/reg ") && !npe.contains("/changepassword ") && !npe.contains("/setpassword ") && !npe.contains("/code ") && !npe.contains("/register ")?Result.NEUTRAL:Result.DENY);
            }
        } catch (NullPointerException var7) {
            return Result.NEUTRAL;
        }
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        try {
            if(message == null) {
                return Result.NEUTRAL;
            } else {
                String npe = message.getFormattedMessage().toLowerCase();
                return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/login ") && !npe.contains("/l ") && !npe.contains("/reg ") && !npe.contains("/changepassword ") && !npe.contains("/setpassword ") && !npe.contains("/code ") && !npe.contains("/register ")?Result.NEUTRAL:Result.DENY);
            }
        } catch (NullPointerException var7) {
            return Result.NEUTRAL;
        }
    }

    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
=======
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.util.Locale;
import java.util.Set;

public final class Log4JFilter extends AbstractFilter {
    private static final Set<String> SECRET_COMMANDS = Set.of(
            "/login", "/l", "/register", "/reg", "/changepassword", "/cp",
            "/setpassword", "/code", "/2fa", "/vk"
    );

    @Override
    public Filter.Result filter(LogEvent event) {
        if (event == null || event.getMessage() == null) return Result.NEUTRAL;
        return containsSecretCommand(event.getMessage().getFormattedMessage()) ? Result.DENY : Result.NEUTRAL;
    }

    static boolean containsSecretCommand(String message) {
        if (message == null) return false;
        String lower = message.toLowerCase(Locale.ROOT);
        for (String command : SECRET_COMMANDS) {
            int index = lower.indexOf(command);
            while (index >= 0) {
                int end = index + command.length();
                boolean left = index == 0 || Character.isWhitespace(lower.charAt(index - 1));
                boolean argument = end < lower.length() && Character.isWhitespace(lower.charAt(end));
                if (left && argument) return true;
                index = lower.indexOf(command, index + 1);
            }
        }
>>>>>>> 1d55b6e (3.2)
        return false;
    }
}
