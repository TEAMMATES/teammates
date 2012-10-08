package teammates.test.cases;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.logic.Emails;
import teammates.test.driver.BackDoor;

import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class SystemErrorEmailReportTest extends BaseTestCase {
	private LocalServiceTestHelper helper;
	private LocalMailServiceTestConfig localMailService;
	private static DataBundle scn;

	private String from;
	private String replyTo;
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(Emails.class, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);

		
		
	}

	@Before
	public void caseSetUp() throws ServletException, IOException {
		localMailService = new LocalMailServiceTestConfig();
		helper = new LocalServiceTestHelper(localMailService);
		helper.setUp();
		
		from 		= "noreply@"+Common.APP_ID+".appspotmail.com";
		replyTo 	= "teammates@comp.nus.edu.sg";
	}

	@Test
	public void testSystemCrashReportEmainSending() throws MessagingException {
			______TS("generic crash report email");

			BackDoor.generateException();
			print("Exception triggered, please check your crash report at " + BuildProperties.inst().getAppCrashReportEmail());
	}

	@Test
	public void testSystemCrashReportEmailContent() throws IOException,
			MessagingException {

		
		AssertionError error = new AssertionError("invalid parameter");
		StackTraceElement s1 = new StackTraceElement(
				SystemErrorEmailReportTest.class.getName(), 
				"testSystemCrashReportEmailContent", 
				"SystemErrorEmailReportTest.java", 
				89);
		error.setStackTrace(new StackTraceElement[] {s1});
		String stackTrace = Common.stackTraceToString(error);
		String requestPath = "/page/studentHome";
		String requestParam = "{}";
		String version = "4.27";

		______TS("generic crash report email");

		MimeMessage email = new Emails().sendSystemErrorEmail(
				error.getMessage(), 
				stackTrace, requestPath, requestParam, version);

		// check receiver
		String recipient = BuildProperties.inst().getAppCrashReportEmail();
		assertEquals(recipient, email.getAllRecipients()[0].toString());

		// check sender
		assertEquals(from, email.getFrom()[0].toString());
		
			
		// check email body
		String emailBody = email.getContent().toString();
		assertContainsRegex(
				"<b>Error Message</b><br/><pre><code>" + error.getMessage()
				+ "</code></pre><br/><b>Request Path</b>" + requestPath 
				+ "<br/><b>Request Parameters</b>" + requestParam
				+ "<br/><b>Stack Trace</b><pre><code>" + stackTrace + "</code></pre>",
				emailBody);

		printEmail(email);

	}



	private void printEmail(MimeMessage email) throws MessagingException,
			IOException {
		print("Here's the generated email (for your eyeballing pleasure):");
		print(".............[Start of email]..............");
		print("Subject: " + email.getSubject());
		print("Body:");
		print(email.getContent().toString());
		print(".............[End of email]................");
	}

	@After
	public void caseTearDown() {
		helper.tearDown();
	}

	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		setLogLevelOfClass(Emails.class, Level.WARNING);
		setConsoleLoggingLevel(Level.WARNING);
	}
}
