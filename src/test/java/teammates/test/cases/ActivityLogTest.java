package teammates.test.cases;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import com.google.appengine.api.log.AppLogLine;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.ui.controller.ActivityLogEntry;

public class ActivityLogTest extends BaseTestCase{

	@Test
	public void TestActivityLogEntryClass() {
		______TS("Test constructors and generateLogMessage");
		String logMessage = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor|||UserName|||UserId|||UserEmail|||Message|||URL";
		AccountAttributes acc = new AccountAttributes("UserId", "UserName", true, "UserEmail", "UserInstitute");
		ActivityLogEntry entry = new ActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD, true, acc, "Message", "URL");		
		AssertJUnit.assertEquals(logMessage, entry.generateLogMessage());
		
		AppLogLine appLog = new AppLogLine();
		appLog.setLogMessage(logMessage);
		entry = new ActivityLogEntry(appLog);		
		AssertJUnit.assertEquals(logMessage, entry.generateLogMessage());
		
		logMessage = "TEAMMATESLOG|||instructorHome|||Unknown|||true|||Unknown|||Unknown|||Unknown|||Unknown|||<span class=\"color_red\">Error. ActivityLogEntry object is not created for this servlet action.</span><br>Message|||URL";
		entry = new ActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, "Message", "URL");		
		AssertJUnit.assertEquals(logMessage, entry.generateLogMessage());
		
		
		______TS("Test getters");
		appLog.setTimeUsec(0);
		entry = new ActivityLogEntry(appLog);
		
		AssertJUnit.assertEquals("<span class=\"color_green bold\">instructorHome</span><br>Pageload", entry.getActionInfo());
		AssertJUnit.assertEquals("01-01-1970 07:30:00", entry.getDateInfo());
		AssertJUnit.assertEquals("Message<br><br><a href=\"URL?user=UserId\" target=\"blank\" title=\"URL?user=UserId\">URL</a>", entry.getMessageInfo());
		AssertJUnit.assertEquals("<span class=\"bold\">Id: </span>UserId<br><span class=\"bold\">Name: </span>UserName<br>UserEmail<br>", entry.getPersonInfo());
		AssertJUnit.assertEquals("<span class=\"bold\">Instructor</span>", entry.getRoleInfo());
	}
}
