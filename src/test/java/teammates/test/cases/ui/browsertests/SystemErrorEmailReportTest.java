package teammates.test.cases.ui.browsertests;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.Emails;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

import com.google.apphosting.api.DeadlineExceededException;

public class SystemErrorEmailReportTest extends BaseTestCase {
	private static BrowserInstance bi;

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(Emails.class, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);

		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);

	}

	@BeforeMethod
	public void caseSetUp() throws ServletException, IOException {
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
	public void testDeadlineExceededException() throws Exception {
			______TS("Deadline Exceeded testing");
			String link = Common.PAGE_ADMIN_EXCEPTION_TEST;
			link = Common.addParamToUrl(link, Common.PARAM_ERROR, DeadlineExceededException.class.getSimpleName());
			bi.goToUrl(link);
			print("DeadlineExceededException triggered, please check your crash report at " + BuildProperties.inst().getAppCrashReportEmail());	
			______TS("DeadlineExceededException error view");
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/deadlineExceededErrorPage.html");
		
	}
	
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		setLogLevelOfClass(Emails.class, Level.WARNING);
		setConsoleLoggingLevel(Level.WARNING);
	}
}
