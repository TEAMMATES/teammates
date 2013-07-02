package teammates.test.cases.ui;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.Test;
import com.google.appengine.api.log.AppLogLine;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Config;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.ActivityLogEntry;

public class ActivityLogTest extends BaseTestCase{

	@Test
	public void testActivityLogEntryClass() {
		______TS("Test constructors and generateLogMessage");
		String logMessage = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor|||UserName|||UserId|||UserEmail|||Message|||URL";
		AccountAttributes acc = new AccountAttributes("UserId", "UserName", true, "UserEmail", "UserInstitute");
		ActivityLogEntry entry = new ActivityLogEntry(Config.INSTRUCTOR_HOME_SERVLET, Config.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD, acc, "Message", "URL");		
		assertEquals(logMessage, entry.generateLogMessage());
		
		AppLogLine appLog = new AppLogLine();
		appLog.setLogMessage(logMessage);
		entry = new ActivityLogEntry(appLog);		
		assertEquals(logMessage, entry.generateLogMessage());
		
		logMessage = "TEAMMATESLOG|||instructorHome|||Unknown|||true|||Unknown|||Unknown|||Unknown|||Unknown|||<span class=\"color_red\">Error. ActivityLogEntry object is not created for this servlet action.</span><br>Message|||URL";
		entry = new ActivityLogEntry(Config.INSTRUCTOR_HOME_SERVLET, "Message", "URL");		
		assertEquals(logMessage, entry.generateLogMessage());
		
		
		______TS("Test getters");
		appLog.setTimeUsec(0);
		entry = new ActivityLogEntry(appLog);
		
		assertEquals("<span class=\"color_green bold\">instructorHome</span><br>Pageload", entry.getActionInfo());
		assertEquals("01-01-1970 00:00:00", entry.getDateInfo());
		assertEquals("Message<br><br><a href=\"URL?user=UserId\" target=\"blank\" title=\"URL?user=UserId\">URL</a>", entry.getMessageInfo());
		assertEquals("<span class=\"bold\">Id: </span>UserId<br><span class=\"bold\">Name: </span>UserName<br>UserEmail<br>", entry.getPersonInfo());
		assertEquals("<span class=\"bold\">Instructor</span>", entry.getRoleInfo());
	}
	
	@Test
	public void testGetActionName(){
		assertEquals("instructorCourse", ActivityLogEntry.getActionName("/page/instructorCourse"));
		assertEquals("instructorCourse", ActivityLogEntry.getActionName("/page/instructorCourse?user=x"));
		assertEquals("error in getActionName for requestUrl : instructorCourse", ActivityLogEntry.getActionName("instructorCourse"));
	}
}
