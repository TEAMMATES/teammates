package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.ui.controller.ActivityLogEntry;

public class ActivityLogTest extends BaseTestCase{

	@Test
	public void TestActivityLogEntryClass() {
		______TS("Test constructors and generateLogMessage");
		String logMessage = "TEAMMATESLOG|||instructorHome|||Page Load|||true|||Instructor|||UserName|||UserId|||UserEmail|||Message|||URL";
		AccountData acc = new AccountData("UserId", "UserName", true, "UserEmail", "UserInstitute");
		ActivityLogEntry entry = new ActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD, true, acc, "Message", "URL");		
		assertEquals(logMessage, entry.generateLogMessage());
		
		AppLogLine appLog = new AppLogLine();
		appLog.setLogMessage(logMessage);
		entry = new ActivityLogEntry(appLog);		
		assertEquals(logMessage, entry.generateLogMessage());
		
		logMessage = "TEAMMATESLOG|||instructorHome|||Unknown|||true|||Unknown|||Unknown|||Unknown|||Unknown|||<span class=\"color_red\">Error. ActivityLogEntry object is not created for this servlet action.</span><br>Message|||URL";
		entry = new ActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, "Message", "URL");		
		assertEquals(logMessage, entry.generateLogMessage());
		
		
		______TS("Test getters");
		appLog.setTimeUsec(0);
		entry = new ActivityLogEntry(appLog);
		
		assertEquals("<span class=\"color_green bold\">instructorHome</span><br>Page Load", entry.getActionInfo());
		assertEquals("01-01-1970 15:30:00", entry.getDateInfo());
		assertEquals("Message<br><br><a href=\"URL\" target=\"blank\" title=\"URL\">URL</a>", entry.getMessageInfo());
		assertEquals("<span class=\"bold\">Id: </span>UserId<br><span class=\"bold\">Name: </span>UserName<br>UserEmail<br>", entry.getPersonInfo());
		assertEquals("<span class=\"bold\">Instructor</span>", entry.getRoleInfo());
	}
}
