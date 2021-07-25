package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.util.EmailWrapper;

/**
 * Cron job: compiles application logs and sends severe logs compilation to the support email.
 */
class CompileLogsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        List<ErrorLogEntry> errorLogs = logsProcessor.getRecentErrorLogs();
        sendEmail(errorLogs);
        return new JsonResult("Successful");
    }

    private void sendEmail(List<ErrorLogEntry> logs) {
        // Do not send any emails if there are no severe logs; prevents spamming
        if (!logs.isEmpty()) {
            EmailWrapper message = emailGenerator.generateCompiledLogsEmail(logs);
            emailSender.sendReport(message);
        }
    }

}
