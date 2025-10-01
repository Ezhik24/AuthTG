package org.ezhik.authTG.otherAPI;

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
        return false;
    }
}
