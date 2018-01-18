package teammates.test.cases.util;

import org.testng.annotations.Test;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.util.EmailLogEntry;
import teammates.common.util.EmailWrapper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link EmailLogEntry}.
 */
public class EmailLogEntryTest extends BaseTestCase {

    @Test
    public void emailLog_withEmailWrapper_constructCorrectly() {
        EmailWrapper email = generateTypicalEmail();
        EmailLogEntry logEntry = new EmailLogEntry(email);

        assertEquals("TEAMMATESEMAILLOG|||myRecipient@email.tmt|||mySubject|||myContent", logEntry.generateLogMessage());
        assertEquals("myRecipient@email.tmt", logEntry.getReceiver());
        assertEquals("mySubject", logEntry.getSubject());
        assertEquals("myContent", logEntry.getContent());
        assertEquals(0, logEntry.getTime());
        assertTrue(logEntry.isTestData());
    }

    @Test
    public void emailLog_withAppLogLine_constructCorrectly() {
        String logMessage = "TEAMMATESEMAILLOG|||myRecipient@email.com|||mySubject|||myContent";
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage(logMessage);
        appLog.setTimeUsec(2456L);

        EmailLogEntry logEntry = new EmailLogEntry(appLog);
        assertEquals(logMessage, logEntry.generateLogMessage());
        assertEquals("myRecipient@email.com", logEntry.getReceiver());
        assertEquals("mySubject", logEntry.getSubject());
        assertEquals("myContent", logEntry.getContent());
        assertEquals(2L, logEntry.getTime());
        assertFalse(logEntry.isTestData());
    }

    @Test
    public void emailLog_withAppLogLine_constructionFailAsNotInCorrectFormat() {
        String logMessage = "NotInDesiredFormat";
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage(logMessage);
        appLog.setTimeUsec(410333L);

        EmailLogEntry logEntry = new EmailLogEntry(appLog);
        assertEquals("", logEntry.getReceiver());
        assertEquals("", logEntry.getSubject());
        assertEquals("", logEntry.getContent());
        assertEquals(410L, logEntry.getTime());
        assertFalse(logEntry.isTestData());
    }

    private EmailWrapper generateTypicalEmail() {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient("myRecipient@email.tmt");
        email.setSubject("mySubject");
        email.setContent("myContent");
        return email;
    }
}
