package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.testcases.BaseTestCase;

public class AccessControlUiTest extends BaseTestCase {
	
	private static BrowserInstance bi;
	private static String appUrl = Config.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup(){
		printTestClassHeader();
		bi = BrowserInstancePool.getBrowserInstance();
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testUserNotLoggedIn() throws Exception{
		
		bi.logout();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/login.html");
		
		______TS("student");
		
		verifyRedirectToLogin(appUrl + Common.PAGE_STUDENT_HOME);
		verifyRedirectToLogin(appUrl + Common.PAGE_STUDENT_JOIN_COURSE);
		verifyRedirectToLogin(appUrl + Common.PAGE_STUDENT_COURSE_DETAILS);
		verifyRedirectToLogin(appUrl + Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT);
		verifyRedirectToLogin(appUrl + Common.PAGE_STUDENT_EVAL_RESULTS);
		
		______TS("coord");
		
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_HOME);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_DELETE);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_DETAILS);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_ENROLL);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_REMIND);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_STUDENT_DELETE);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_STUDENT_DETAILS);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_COURSE_STUDENT_EDIT);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_EDIT);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_DELETE);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_REMIND);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_RESULTS);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_PUBLISH);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_UNPUBLISH);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_SUBMISSION_VIEW);
		verifyRedirectToLogin(appUrl + Common.PAGE_COORD_EVAL_SUBMISSION_EDIT);
		
	}
	
	@Test
	public void testUserNotRegistered() throws Exception{
		
		______TS("student");
		
		String unregUsername = Config.inst().TEAMMATES_UNREG_ACCOUNT;
		String unregPassword = Config.inst().TEAMMATES_UNREG_PASSWORD;
		bi.logout();
		bi.loginStudent(unregUsername, unregPassword);
		
		verifyRedirectToWelcomeStrangerPage(appUrl + Common.PAGE_STUDENT_HOME, unregUsername);
		verifyRedirectToWelcomeStrangerPage(appUrl + Common.PAGE_STUDENT_JOIN_COURSE, unregUsername);
		
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_STUDENT_COURSE_DETAILS);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_STUDENT_EVAL_RESULTS);
		
		______TS("coord");
		
		bi.logout();
		bi.loginCoord(unregUsername, unregPassword);
		
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_HOME);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_DELETE);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_DETAILS);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_ENROLL);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_REMIND);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_STUDENT_DELETE);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_STUDENT_DETAILS);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_COURSE_STUDENT_EDIT);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_EDIT);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_DELETE);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_REMIND);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_RESULTS);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_PUBLISH);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_UNPUBLISH);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_SUBMISSION_VIEW);
		verifyRedirectToNotAuthorized(appUrl + Common.PAGE_COORD_EVAL_SUBMISSION_EDIT);
		
	}
	
	@Test
	public void testStudentAccessControl() throws Exception{
		
	}

	private void verifyRedirectToWelcomeStrangerPage(String link, String unregUsername) {
		printUrl(link);
		bi.goToUrl(link);
		//A simple regex check is enough because we do full HTML tests elsewhere
		assertContainsRegex("{*}"+unregUsername+"{*}Welcome stranger{*}", bi.getCurrentPageSource());
	}

	private void verifyRedirectToNotAuthorized(String link) {
		printUrl(link);
		bi.goToUrl(link);
		assertContains("You are not authorized to view this page.", bi.getCurrentPageSource());
	}

	private void verifyRedirectToLogin(String link) {
		printUrl(link);
		bi.goToUrl(link);
		assertTrue(bi.isLocalLoginPage()||bi.isGoogleLoginPage());
	}

	private void printUrl(String link) {
		print("   "+link);
	}

}
