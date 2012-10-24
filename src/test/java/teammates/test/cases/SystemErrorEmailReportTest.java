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
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.Emails;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class SystemErrorEmailReportTest extends BaseTestCase {
	private static BrowserInstance bi;

	private LocalServiceTestHelper helper;
	private LocalMailServiceTestConfig localMailService;

	private String from;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(Emails.class, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);

		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
	}

	@Before
	public void caseSetUp() throws ServletException, IOException {
		localMailService = new LocalMailServiceTestConfig();
		helper = new LocalServiceTestHelper(localMailService);
		helper.setUp();
		
		from 		= "noreply@"+Common.APP_ID+".appspotmail.com";
	}

	@Test
	public void testAssertionError() {
			______TS("AssertionError testing");
			String link = Common.PAGE_ADMIN_EXCEPTION_TEST;
			link = Common.addParamToUrl(link, Common.PARAM_ERROR, AssertionError.class.getSimpleName());
			bi.goToUrl(link);
			print("AssertionError triggered, please check your crash report at " + BuildProperties.inst().getAppCrashReportEmail());	
	}
	
	@Test
	public void testEntityDoesNotExistException() {
			______TS("EntityDoesNotExistException testing");
			String link = Common.PAGE_ADMIN_EXCEPTION_TEST;
			link = Common.addParamToUrl(link, Common.PARAM_ERROR, EntityDoesNotExistException.class.getSimpleName());
			bi.goToUrl(link);
			print("This exception is handled by system, make sure you don't receive any emails. ");
	}
	
	@Test
	public void testUnauthorizedAccessException() {
			______TS("UnauthorizedAccessException testing");
			String link = Common.PAGE_ADMIN_EXCEPTION_TEST;
			link = Common.addParamToUrl(link, Common.PARAM_ERROR, UnauthorizedAccessException.class.getSimpleName());
			bi.goToUrl(link);
			print("This exception is handled by system, make sure you don't receive any emails. ");
	}
	
	@Test
	public void testNullPointerException() {
			______TS("NullPointerException testing");
			String link = Common.PAGE_ADMIN_EXCEPTION_TEST;
			link = Common.addParamToUrl(link, Common.PARAM_ERROR, NullPointerException.class.getSimpleName());
			bi.goToUrl(link);
			print("NullPointerException triggered, please check your crash report at " + BuildProperties.inst().getAppCrashReportEmail());	
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

		______TS("generic crash report email");

		MimeMessage email = new Emails().generateSystemErrorEmail(
				error, 
				requestPath, requestParam,
				TestProperties.inst().TEAMMATES_VERSION);

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
