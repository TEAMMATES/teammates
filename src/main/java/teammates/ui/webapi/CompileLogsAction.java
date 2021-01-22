package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.LoggingOptions;

import teammates.common.util.Config;
import teammates.common.util.EmailWrapper;

/**
 * Cron job: compiles application logs and sends severe logs compilation to the support email.
 */
class CompileLogsAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        List<LogEntry> errorLogs = getErrorLogs();
        sendEmail(errorLogs);
        return new JsonResult("Successful");
    }

    private List<LogEntry> getErrorLogs() {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return new ArrayList<>();
        }
        LoggingOptions options = LoggingOptions.getDefaultInstance();
        Logging logging = options.getService();

        Instant endTime = Instant.now();
        // Sets the range to 6 minutes to slightly overlap the 5 minute email timer
        long queryRange = 1000 * 60 * 6;
        Instant startTime = endTime.minusMillis(queryRange);

        List<String> logOptions = Arrays.asList(
                "resource.type=\"gae_app\"",
                "resource.labels.module_id=\"default\"",
                "logName=\"projects/" + options.getProjectId() + "/logs/appengine.googleapis.com%2Frequest_log\"",
                "severity>=ERROR",
                "timestamp>\"" + startTime.toString() + "\"",
                "timestamp<=\"" + endTime.toString() + "\""
        );

        List<LogEntry> logs = new ArrayList<>();
        Page<LogEntry> entries = logging.listLogEntries(
                EntryListOption.filter(logOptions.stream().collect(Collectors.joining("\n")))
        );

        for (LogEntry logEntry : entries.iterateAll()) {
            logs.add(logEntry);
        }

        return logs;
    }

    private void sendEmail(List<LogEntry> logs) {
        List<String> logMessages = new ArrayList<>();
        List<String> logLevels = new ArrayList<>();

        for (LogEntry logEntry : logs) {
            // TODO
        }

        // Do not send any emails if there are no severe logs; prevents spamming
        if (!logMessages.isEmpty()) {
            EmailWrapper message = emailGenerator.generateCompiledLogsEmail(logMessages, logLevels);
            emailSender.sendReport(message);
        }
    }

}
