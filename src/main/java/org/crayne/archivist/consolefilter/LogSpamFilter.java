package org.crayne.archivist.consolefilter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LogSpamFilter implements Filter {

    @NotNull
    private static final Set<String> MESSAGE_BLACKLIST = Set.of(
            "is not in this chunk, skipping save. This a bug fix to a vanilla bug. Do not report this to PaperMC please.",
            "Could not set level chunk heightmap, array length is 0 instead of 256",
            "Wrong location! (",
            " but it was marked as removed already",
            "Skipping BlockEntity with id ",
            "Ignoring unknown attribute 'forge.swimSpeed'",
            "Unable to find spawn biome",
            "moved too quickly!",
            "Ignoring plugin channel in incoming REGISTER"
    );

    public Result filter(@NotNull final LogEvent event) {
        final String msg = event.getMessage().getFormattedMessage();
        if (MESSAGE_BLACKLIST.stream().anyMatch(msg::contains)) return Result.DENY;

        return Result.NEUTRAL;
    }

    public Result getOnMismatch() {
        return null;
    }
    public Result getOnMatch() {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return null;
    }
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return null;
    }

    public State getState() {
        return null;
    }
    public void initialize() {}
    public void start() {}
    public void stop() {}
    public boolean isStarted() {
        return false;
    }
    public boolean isStopped() {
        return false;
    }
}

