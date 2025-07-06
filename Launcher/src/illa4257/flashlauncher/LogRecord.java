package illa4257.flashlauncher;

import illa4257.i4Utils.logger.Level;

public class LogRecord {
    public final Level level;
    public final String prefix, message;

    public LogRecord(final Level level, final String prefix, final String message) {
        this.level = level;
        this.prefix = prefix;
        this.message = message;
    }
}