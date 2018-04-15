package teammates.ui.automated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;

/**
 * Cron job: compiles application logs and sends severe logs compilation to the support email.
 */
public class CompileLogsAction extends AutomatedAction {

    @Override
    protected String getActionDescription() {
        return "send severe log notifications";
    }

    @Override
    protected String getActionMessage() {
        return "Compiling logs for email notification";
    }

    @Override
    public void execute() {
        List<AppLogLine> errorLogs = getErrorLogs();
        sendEmail(errorLogs);
    }

    private List<AppLogLine> getErrorLogs() {
        LogService logService = LogServiceFactory.getLogService();

        long endTime = Instant.now().toEpochMilli();
        // Sets the range to 6 minutes to slightly overlap the 5 minute email timer
        long queryRange = 1000 * 60 * 6;
        long startTime = endTime - queryRange;

        LogQuery q = LogQuery.Builder.withDefaults()
                                     .includeAppLogs(true)
                                     .startTimeMillis(startTime)
                                     .endTimeMillis(endTime)
                                     .minLogLevel(LogLevel.ERROR);

        Iterable<RequestLogs> logs = logService.fetch(q);
        List<AppLogLine> errorLogs = new ArrayList<>();

        for (RequestLogs requestLogs : logs) {
            List<AppLogLine> logList = requestLogs.getAppLogLines();

            for (AppLogLine currentLog : logList) {
                LogLevel logLevel = currentLog.getLogLevel();

                if (LogLevel.FATAL == logLevel || LogLevel.ERROR == logLevel) {
                    errorLogs.add(currentLog);
                }
            }
        }

        return errorLogs;
    }

    private void sendEmail(List<AppLogLine> logs) {
        // Do not send any emails if there are no severe logs; prevents spamming
        if (!logs.isEmpty()) {
            EmailWrapper message = new EmailGenerator().generateCompiledLogsEmail(logs);
            emailSender.sendReport(message);
        }
    }

}
