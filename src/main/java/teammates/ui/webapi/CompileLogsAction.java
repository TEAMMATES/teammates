package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.util.EmailWrapper;

/**
 * Cron job: compiles application logs and sends severe logs compilation to the support email.
 */
public class CompileLogsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        Instant endTime = Instant.now();
        // Sets the range to 6 minutes to slightly overlap the 5 minute email timer
        long queryRange = 1000 * 60 * 6;
        Instant startTime = endTime.minusMillis(queryRange);

        QueryLogsParams queryLogsParams = QueryLogsParams.builder(startTime.toEpochMilli(), endTime.toEpochMilli())
                .withMinSeverity(LogSeverity.ERROR)
                .withPageSize(0)
                .build();

        List<ErrorLogEntry> errorLogs = new ArrayList<>();
        for (GeneralLogEntry logEntry : logsProcessor.queryLogs(queryLogsParams).getLogEntries()) {
            errorLogs.add(ErrorLogEntry.fromLogEntry(logEntry));
        }

        // Do not send any emails if there are no severe logs; prevents spamming
        if (!errorLogs.isEmpty()) {
            EmailWrapper message = emailGenerator.generateCompiledLogsEmail(errorLogs);
            emailSender.sendEmail(message);
        }
        return new JsonResult("Successful");
    }

}
