package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Severity;

import teammates.common.exception.LoggingServiceException;
import teammates.common.util.Config;
import teammates.common.util.Logger;

/**
 * Logging service that leverages on Google Cloud Logging to create and search for logs.
 * This service is not supported on the development server.
 *
 * @see <a href="https://cloud.google.com/logging/docs">https://cloud.google.com/logging/docs</a>
 */
public class LoggingService {

    private static final Logger log = Logger.getLogger();

    private LoggingService() {
        // prevent initialization
    }

    /**
     * Creates a log entry.
     */
    public static void createLogEntry(LogEntry entry) throws LoggingServiceException {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return;
        }
        try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {
            logging.write(Collections.singleton(entry));
            logging.close();
        } catch (Exception e) {
            log.severe("Failed to create feedback session log for log entry: " + entry.toString());
            throw new LoggingServiceException(e);
        }
    }

    /**
     * Handles the searching of logs.
     * To create an instance of the LogSearcher, use the builder method.
     */
    public static class LogSearcher {
        private String logName;
        private String resourceType;
        private Severity severity;
        private Instant startTime;
        private Instant endTime;
        private Map<String, String> labels;
        private Map<String, String> resourceLabels;

        private LogSearcher() {
            // prevent initialization
        }

        /**
         * Creates a builder for the LogSearcher.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Gets the log entries as filtered by the given parameters.
         */
        public List<LogEntry> getLogEntries() throws LoggingServiceException {
            List<LogEntry> logEntries = new ArrayList<>();
            if (Config.isDevServer()) {
                // Not supported in dev server
                return logEntries;
            }

            LoggingOptions options = LoggingOptions.getDefaultInstance();

            List<String> logFilters = new ArrayList<>();
            if (this.logName != null) {
                logFilters.add("logName=\"projects/" + options.getProjectId() + "/logs/" + this.logName + "\"");
            }
            if (this.resourceType != null) {
                logFilters.add("resource.type=\"" + this.resourceType + "\"");
            }
            if (this.severity != null) {
                logFilters.add("severity=\"" + this.severity.toString() + "\"");
            }
            if (this.startTime != null) {
                logFilters.add("timestamp>\"" + this.startTime.toString() + "\"");
            }
            if (this.endTime != null) {
                logFilters.add("timestamp<=\"" + this.endTime.toString() + "\"");
            }
            for (Map.Entry<String, String> entry : this.labels.entrySet()) {
                logFilters.add("labels." + entry.getKey() + "=\"" + entry.getValue() + "\"");
            }
            for (Map.Entry<String, String> entry : this.resourceLabels.entrySet()) {
                logFilters.add("resource.labels." + entry.getKey() + "=\"" + entry.getValue() + "\"");
            }
            String logFilter = logFilters.stream().collect(Collectors.joining("\n"));

            try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {
                Page<LogEntry> entries = logging.listLogEntries(EntryListOption.filter(logFilter));
                for (LogEntry entry : entries.iterateAll()) {
                    logEntries.add(entry);
                }
                logging.close();
            } catch (Exception e) {
                throw new LoggingServiceException(e);
            }
            return logEntries;
        }

        /**
         * Builder class for the LogSearcher.
         */
        public static class Builder {
            private String logName;
            private String resourceType;
            private Severity severity;
            private Instant startTime;
            private Instant endTime;
            private Map<String, String> labels = new HashMap<>();
            private Map<String, String> resourceLabels = new HashMap<>();

            public Builder setLogName(String logName) {
                this.logName = logName;
                return this;
            }

            public Builder setResourceType(String resourceType) {
                this.resourceType = resourceType;
                return this;
            }

            public Builder setSeverity(Severity severity) {
                this.severity = severity;
                return this;
            }

            public Builder setStartTime(Instant startTime) {
                this.startTime = startTime;
                return this;
            }

            public Builder setEndTime(Instant endTime) {
                this.endTime = endTime;
                return this;
            }

            public Builder addLabel(String key, String value) {
                this.labels.put(key, value);
                return this;
            }

            public Builder addResourceLabel(String key, String value) {
                this.resourceLabels.put(key, value);
                return this;
            }

            public LogSearcher build() {
                LogSearcher logSearcher = new LogSearcher();
                logSearcher.logName = this.logName;
                logSearcher.resourceType = this.resourceType;
                logSearcher.severity = this.severity;
                logSearcher.startTime = this.startTime;
                logSearcher.endTime = this.endTime;
                logSearcher.labels = this.labels;
                logSearcher.resourceLabels = this.resourceLabels;
                return logSearcher;
            }
        }
    }
}
