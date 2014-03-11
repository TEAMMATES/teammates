package teammates.test.cases.ui;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.Test;
import com.google.appengine.api.log.AppLogLine;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.LogEntryAttributes;
import teammates.test.cases.BaseTestCase;

public class ActivityLogTest extends BaseTestCase{

	@Test
	public void testLogEntryAttributesClass() {
		______TS("Test constructors and generateLogMessage");
		String logMessage = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor|||UserName|||UserId|||UserEmail|||Message|||URL";
		AccountAttributes acc = new AccountAttributes("UserId", "UserName", true, "UserEmail", "UserInstitute");
		LogEntryAttributes entry = new LogEntryAttributes("instructorHome", "Pageload", acc, "Message", "URL");		
		assertEquals(logMessage, entry.generateLogMessage());
		
		AppLogLine appLog = new AppLogLine();
		appLog.setLogMessage(logMessage);
		entry = new LogEntryAttributes(appLog);		
		assertEquals(logMessage, entry.generateLogMessage());
		
		logMessage = "TEAMMATESLOG|||instructorHome|||Unknown|||true|||Unknown|||Unknown|||Unknown|||Unknown|||<span class=\"color_red\">Error. LogEntryAttributes object is not created for this servlet action.</span><br>Message|||URL";
		entry = new LogEntryAttributes("instructorHome", "Message", "URL");		
		assertEquals(logMessage, entry.generateLogMessage());
		
		
		______TS("Test getters");
		appLog.setTimeUsec(0);
		entry = new LogEntryAttributes(appLog);
		
		assertEquals("<a href=\"URL?user=UserId\" class=\"color_green bold\" target=\"_blank\">instructorHome</a>", entry.getActionInfo());
		assertEquals("01-01-1970 07:30:00", entry.getDateInfo());
		assertEquals("Message", entry.getMessageInfo());
		assertEquals("[UserName <a href=\"/page/instructorHomePage?user=UserId\" target=\"_blank\">UserId</a> <a href=\"mailto:UserEmail\" target=\"_blank\">UserEmail</a>]", entry.getPersonInfo());
		assertEquals("Instructor", entry.getRoleInfo());
	}
	
	@Test
	public void testGetActionName(){
		assertEquals("instructorCourse", LogEntryAttributes.getActionName("/page/instructorCourse"));
		assertEquals("instructorCourse", LogEntryAttributes.getActionName("/page/instructorCourse?user=x"));
		assertEquals("error in getActionName for requestUrl : instructorCourse", LogEntryAttributes.getActionName("instructorCourse"));
	}
}
