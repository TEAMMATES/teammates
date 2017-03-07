package teammates.common.util;

import java.util.regex.Pattern;

import com.google.appengine.api.log.AppLogLine;

/**
 * A log entry which contains info about subject, receiver, content
 * and sent date of a sent email.
 */
public class EmailLogEntry {

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
        String[] tokens = appLog.getLogMessage().split(Pattern.quote("|||"), -1);

        try {
            receiver = tokens[1];
            subject = tokens[2];
            content = tokens[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            receiver = "";
            subject = "";
            content = "";
        }
    }

    /**
     * Generates a log message that will be logged in the server
     */
    public String generateLogMessage() {
        //TEAMMATESEMAILSLOG|||RECEIVER|||SUBJECT|||CONTENT
        return StringHelper.join("|||", "TEAMMATESEMAILLOG", receiver, subject, content);
    }

    public boolean isTestData() {
        return receiver.endsWith(".tmt");
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
