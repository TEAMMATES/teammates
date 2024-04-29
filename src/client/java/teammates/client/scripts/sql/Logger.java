package teammates.client.scripts.sql;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import teammates.common.util.Const;

public class Logger {
    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";
    String logPrefix;

    protected Logger(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    /**
     * Returns the prefix for the log line.
     */
    private String getLogPrefix() {
        return String.format("%s", logPrefix);
    }

    /**
     * Logs a line and persists it to the disk.
     */
    public void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));

        Path logPath = Paths.get(BASE_LOG_URI + this.getClass().getSimpleName() + ".log");
        try (OutputStream logFile = Files.newOutputStream(logPath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            logFile.write((logLine + System.lineSeparator()).getBytes(Const.ENCODING));
        } catch (Exception e) {
            System.err.println("Error writing log line: " + logLine);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Logs an error and persists it to the disk.
     */
    public void logError(String logLine) {
        System.err.println(logLine);

        log("[ERROR]" + logLine);
    }
}
