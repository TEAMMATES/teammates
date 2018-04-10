package teammates.common.util;

import java.util.regex.Pattern;

import com.google.appengine.api.log.AppLogLine;

/**
 * A log entry which contains info about subject, receiver, content
 * and sent date of a sent email.
 */
public class EmailLogEntry {

    private static final Logger log = Logger.getLogger();

    private String receiver;
    private String subject;
    private String content;
    private long time;

    public EmailLogEntry(EmailWrapper msg) {
        receiver = msg.getRecipient();
        subject = msg.getSubject();
        content = msg.getContent();
    }

    public EmailLogEntry(AppLogLine appLog) {
        time = appLog.getTimeUsec() / 1000;
        String[] tokens = appLog.getLogMessage()
                .split(Pattern.quote(Const.EmailLog.FIELD_SEPARATOR), -1);

        try {
            receiver = tokens[1];
            subject = tokens[2];
            content = tokens[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            receiver = "";
            subject = "";
            content = "";

            log.severe(String.format(Const.EmailLog.ERROR_LOG_FORMAT, appLog.getLogMessage()));
        }
    }

    /**
     * Generates a log message that will be logged in the server.
     */
    public String generateLogMessage() {
        // TEAMMATESEMAILSLOG|||RECEIVER|||SUBJECT|||CONTENT
        return String.join(Const.EmailLog.FIELD_SEPARATOR, Const.EmailLog.TEAMMATES_EMAIL_LOG,
                receiver, subject, content);
    }

    public boolean isTestData() {
        return receiver.endsWith(Const.EmailLog.TEST_DATA_POSTFIX);
    }

    // -------- Getter methods --------

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public long getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
