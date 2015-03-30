package teammates.logic.automated;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

import teammates.common.util.Utils;
import teammates.logic.core.Emails;

import java.util.logging.*;

public class CompileLogs {
    private static Logger log = Utils.getLogger();
    
    public String doLogExam() {
        LogService logService = LogServiceFactory.getLogService();

        long endTime = (new java.util.Date()).getTime();
        // Sets the range to 6 minutes to slightly overlap the 5 minute email timer
        long queryRange = 1000 * 60 * 6;
        long startTime = endTime - queryRange;

        LogQuery q = LogQuery.Builder.withDefaults()
                                     .includeAppLogs(true)
                                     .startTimeMillis(startTime)
                                     .endTimeMillis(endTime)
                                     .minLogLevel(LogLevel.ERROR);
        
        Iterator<RequestLogs> logIterator = logService.fetch(q).iterator();
        String message = "";

        int numberOfErrors = 0;

        while (logIterator.hasNext()) {
            RequestLogs requestLogs = logIterator.next();
            List<AppLogLine> logList = requestLogs.getAppLogLines();

            for (int i = 0; i < logList.size(); i++) {
                AppLogLine currentLog = logList.get(i);
                LogLevel logLevel = currentLog.getLogLevel();
                
                if (LogService.LogLevel.FATAL.equals(logLevel) || LogService.LogLevel.ERROR.equals(logLevel)) {
                    numberOfErrors++;
                    message += numberOfErrors + ". " +
                                "Error Type: " + currentLog.getLogLevel().toString() + "<br/>" +
                                "Error Message: " + currentLog.getLogMessage() + "<br/><br/>";
                }
            }
        }

        return message;
    }

    public void sendEmail(String logs) {
        if (!logs.isEmpty()) {
            Emails emails = new Emails();
            MimeMessage message;
            try {
                message = emails.generateCompiledLogsEmail(logs);
                emails.sendLogReport(message);
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.severe(e.getMessage());
            }
        } else {
            // Do not send any emails if there are no severe logs; prevents spamming
        }
    }
}
