package teammates.test.cases.util;

import org.testng.annotations.Test;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.ActivityLogEntry;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link ActivityLogEntry}.
 */
public class ActivityLogEntryTest extends BaseTestCase {

    @Test
    public void testActivityLogEntryClass() {
        ______TS("Test constructors and generateLogMessage");
        String logMessage = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor|||UserName|||UserId"
                            + "|||UserEmail|||Message|||URL";
        AccountAttributes acc = new AccountAttributes("UserId", "UserName", true, "UserEmail", "UserInstitute");
        UserType userType = new UserType("googleId");
        ActivityLogEntry entry = new ActivityLogEntry("instructorHome", "Pageload", acc, "Message", "URL", userType);
        AssertHelper.assertLogMessageEquals(logMessage, entry.generateLogMessage());

        logMessage = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor|||UserName|||UserId"
                     + "|||UserEmail|||Message|||URL|||UserId20151019143729608";
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage(logMessage);
        entry = new ActivityLogEntry(appLog);
        assertEquals(logMessage, entry.generateLogMessage());

        logMessage = "TEAMMATESLOG|||instructorHome|||Unknown|||true|||Unknown|||Unknown|||Unknown|||Unknown"
                     + "|||<span class=\"text-danger\">Error. ActivityLogEntry object is not created "
                     + "for this servlet action.</span><br>Message|||URL";
        entry = new ActivityLogEntry("instructorHome", "Message", "URL");
        AssertHelper.assertLogMessageEquals(logMessage, entry.generateLogMessage());

        ______TS("Test getters");
        appLog.setTimeUsec(0);
        entry = new ActivityLogEntry(appLog);

        assertEquals("instructorHome", entry.getServletName());
        assertEquals(0, entry.getTime());
        assertEquals("Message", entry.getMessage());
        assertEquals("UserId", entry.getGoogleId());
        assertEquals("Instructor", entry.getRole());
    }

    @Test
    public void testGetActionName() {
        assertEquals("instructorCourse", ActivityLogEntry.getActionName("/page/instructorCourse"));
        assertEquals("instructorCourse", ActivityLogEntry.getActionName("/page/instructorCourse?user=x"));
        try {
            ActivityLogEntry.getActionName("instructorCourse");
            signalFailureToDetectException("getActionName should throw an exception if an action cannot be retrieved");
        } catch (ArrayIndexOutOfBoundsException e) {
            assertEquals("java.lang.ArrayIndexOutOfBoundsException: 2", e.toString());
        }
    }
}
