package teammates.test.cases.util;

import org.testng.annotations.Test;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.ActivityLogEntry.Builder;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link ActivityLogEntry}.
 */
public class ActivityLogEntryTest extends BaseTestCase {

    @Test
    public void builder_defaultValues() {
        Builder builder = new Builder("instructorHome", "URL", 10);
        String logMessage = "TEAMMATESLOG|||instructorHome|||instructorHome|||true|||Unknown|||Unknown|||Unknown"
                            + "|||Unknown|||Unknown|||URL";
        AssertHelper.assertLogMessageEquals(logMessage, builder.build().generateLogMessage());
    }

    @Test
    public void builder_withNullValues_ignoreNullValues() {
        Builder builder = new Builder(null, null, 10);
        builder.withActionResponse(null)
               .withLogId(null)
               .withLogMessage(null)
               .withUserEmail(null)
               .withUserGoogleId(null)
               .withUserName(null)
               .withUserRole(null);
        String logMessage = "TEAMMATESLOG|||Unknown|||Unknown|||true|||Unknown|||Unknown|||Unknown"
                            + "|||Unknown|||Unknown|||Unknown";
        ActivityLogEntry entry = builder.build();
        AssertHelper.assertLogMessageEquals(logMessage, entry.generateLogMessage());
        assertEquals(Const.ActivityLog.UNKNOWN, entry.getLogId());
    }

    @Test
    public void builder_validInputs() {
        ______TS("Test generateLogMessage");

        String statusToAdmin = "<span class=\"text-danger\">Error. ActivityLogEntry object is not created "
                               + "for this servlet action.</span><br>Message";
        String logMessage = "TEAMMATESLOG|||instructorHome|||Servlet Action Failure|||true"
                            + "|||Instructor(M)|||Joe|||GoogleIdA|||instructor@email.tmt"
                            + "|||" + statusToAdmin + "|||url.com";
        Builder builder = new Builder("instructorHome", "url.com", 10);
        builder.withActionResponse(Const.ACTION_RESULT_FAILURE)
               .withUserRole(Const.ActivityLog.ROLE_INSTRUCTOR)
               .withUserName("Joe")
               .withUserGoogleId("GoogleIdA")
               .withUserEmail("instructor@email.tmt")
               .withLogMessage(statusToAdmin)
               .withLogId("GoogleIdA@10")
               .withActionTimeTaken(20)
               .withMasqueradeUserRole(true);

        ActivityLogEntry entry = builder.build();
        AssertHelper.assertLogMessageEquals(logMessage, entry.generateLogMessage());

        ______TS("Test getters");

        assertEquals("instructorHome", entry.getActionName());
        assertEquals(Const.ACTION_RESULT_FAILURE, entry.getActionResponse());
        assertEquals(10, entry.getLogTime());
        assertEquals("url.com", entry.getActionUrl());
        assertEquals("Instructor", entry.getUserRole());
        assertTrue(entry.isMasqueradeUserRole());
        assertEquals("GoogleIdA", entry.getUserGoogleId());
        assertEquals("instructor@email.tmt", entry.getUserEmail());
        assertEquals("Joe", entry.getUserName());
        assertEquals("GoogleIdA@10", entry.getLogId());
        assertEquals(20, entry.getActionTimeTaken());
        assertEquals(statusToAdmin, entry.getLogMessage());
        assertTrue(entry.isTestingData());
        assertTrue(entry.getShouldShowLog());
    }

    @Test
    public void logEntry_withAppLogLine_constructSuccessfully() {
        ______TS("Success: Generate activityLog from appLogLine (with TimeTaken)");
        String logMessageWithoutTimeTaken = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor"
                                            + "|||UserName|||UserId|||UserEmail|||Message|||URL|||UserId20151019143729608";
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage(logMessageWithoutTimeTaken + Const.ActivityLog.FIELD_SEPARATOR + "20");
        ActivityLogEntry entry = ActivityLogEntry.buildFromAppLog(appLog);
        assertEquals(logMessageWithoutTimeTaken, entry.generateLogMessage());
        assertEquals(20, entry.getActionTimeTaken());

        ______TS("Success: Generate activityLog from appLogLine (without TimeTaken)");
        appLog.setLogMessage(logMessageWithoutTimeTaken);
        entry = ActivityLogEntry.buildFromAppLog(appLog);
        assertEquals(logMessageWithoutTimeTaken, entry.generateLogMessage());
        assertEquals(0, entry.getActionTimeTaken());

        ______TS("Success with severe log: timeTaken not in correct format");
        appLog.setLogMessage(logMessageWithoutTimeTaken + Const.ActivityLog.FIELD_SEPARATOR + "random");
        entry = ActivityLogEntry.buildFromAppLog(appLog);
        assertEquals(logMessageWithoutTimeTaken, entry.generateLogMessage());
        assertEquals(0, entry.getActionTimeTaken());
    }

    @Test
    public void logEntry_withMalformationAppLogLine_constructionFail() {
        ______TS("Fail: log message not in correct format");
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage("TEAMMATESLOG||RANDOM");
        ActivityLogEntry entry = ActivityLogEntry.buildFromAppLog(appLog);
        assertTrue(entry.generateLogMessage().contains(Const.ActivityLog.MESSAGE_ERROR_LOG_MESSAGE_FORMAT));

        String logMessageMalformation = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor"
                                        + "|||UserName|||UserId|||UserEmail|||Message|||URL";
        appLog.setLogMessage(logMessageMalformation);
        entry = ActivityLogEntry.buildFromAppLog(appLog);
        assertTrue(entry.generateLogMessage().contains(Const.ActivityLog.MESSAGE_ERROR_LOG_MESSAGE_FORMAT));
    }

}
