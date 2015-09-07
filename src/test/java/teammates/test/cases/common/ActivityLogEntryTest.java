package teammates.test.cases.common;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.Test;
import com.google.appengine.api.log.AppLogLine;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.ActivityLogEntry;
import teammates.test.cases.BaseTestCase;

public class ActivityLogEntryTest extends BaseTestCase{

    @Test
    public void testActivityLogEntryClass() {
        ______TS("Test constructors and generateLogMessage");
        String logMessage = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor|||UserName|||UserId|||UserEmail|||Message|||URL";
        AccountAttributes acc = new AccountAttributes("UserId", "UserName", true, "UserEmail", "UserInstitute");
        ActivityLogEntry entry = new ActivityLogEntry("instructorHome", "Pageload", acc, "Message", "URL");        
        assertEquals(logMessage, entry.generateLogMessage());
        
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage(logMessage);
        entry = new ActivityLogEntry(appLog);        
        assertEquals(logMessage, entry.generateLogMessage());
        
        logMessage = "TEAMMATESLOG|||instructorHome|||Unknown|||true|||Unknown|||Unknown|||Unknown|||Unknown|||<span class=\"text-danger\">Error. ActivityLogEntry object is not created for this servlet action.</span><br>Message|||URL";
        entry = new ActivityLogEntry("instructorHome", "Message", "URL");        
        assertEquals(logMessage, entry.generateLogMessage());
        
        
        ______TS("Test getters");
        appLog.setTimeUsec(0);
        entry = new ActivityLogEntry(appLog);
        
        assertEquals("<a href=\"URL?user=UserId\" class=\"text-success bold\" target=\"_blank\">instructorHome</a>", entry.getActionInfo());
        assertEquals("01-01-1970 07:30:00", entry.getDateInfo());
        assertEquals("Message", entry.getMessageInfo());
        assertEquals("UserId", entry.getPersonInfo());
        assertEquals("Instructor", entry.getRoleInfo());
    }
    
    @Test
    public void testGetActionName(){
        assertEquals("instructorCourse", ActivityLogEntry.getActionName("/page/instructorCourse"));
        assertEquals("instructorCourse", ActivityLogEntry.getActionName("/page/instructorCourse?user=x"));
        assertEquals("error in getActionName for requestUrl : instructorCourse", ActivityLogEntry.getActionName("instructorCourse"));
    }
}
