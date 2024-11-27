package onl.tesseract.lib.logger;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

class LoggerWrapper implements Logger {

    private final Logger base;
    private Level level;

    public LoggerWrapper(Logger base, Level level) {
        this.base = base;
        this.level = level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return level.toInt() <= Level.TRACE.toInt();
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled())
            base.info("[TRACE] {}", msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled())
            base.info("[TRACE] " + format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled())
            base.info("[TRACE] " + format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled())
            base.info("[TRACE] " + format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled())
            base.info("[TRACE] " + msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {

    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled() {
        return level.toInt() <= Level.DEBUG.toInt();
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled())
            base.info("[DEBUG] " + msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled())
            base.info("[DEBUG] " + format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled())
            base.info("[DEBUG] " + format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled())
            base.info("[DEBUG] " + format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled())
            base.info("[DEBUG] " + msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String msg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isInfoEnabled() {
        return level.toInt() <= Level.INFO.toInt();
    }

    @Override
    public void info(String msg) {
        if (isDebugEnabled())
            base.info("[INFO] " + msg);
    }

    @Override
    public void info(String format, Object arg) {
        if (isDebugEnabled())
            base.info("[INFO] " + format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isDebugEnabled())
            base.info("[INFO] " + format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isDebugEnabled())
            base.info("[INFO] " + format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isDebugEnabled())
            base.info("[INFO] " + msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String msg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isWarnEnabled() {
        return level.toInt() <= Level.WARN.toInt();
    }

    @Override
    public void warn(String msg) {
        if (isDebugEnabled())
            base.info("[WARN] " + msg);
    }

    @Override
    public void warn(String format, Object arg) {
        if (isDebugEnabled())
            base.info("[WARN] " + format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isDebugEnabled())
            base.info("[WARN] " + format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isDebugEnabled())
            base.info("[WARN] " + format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isDebugEnabled())
            base.info("[WARN] " + msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String msg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return level.toInt() <= Level.ERROR.toInt();
    }

    @Override
    public void error(String msg) {
        if (isDebugEnabled())
            base.info("[ERROR] " + msg);
    }

    @Override
    public void error(String format, Object arg) {
        if (isDebugEnabled())
            base.info("[ERROR] " + format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isDebugEnabled())
            base.info("[ERROR] " + format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isDebugEnabled())
            base.info("[ERROR] " + format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isDebugEnabled())
            base.info("[ERROR] " + msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String msg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {

    }
}
