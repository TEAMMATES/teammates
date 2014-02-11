package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Covers the home page for admins.
 * SUT: {@link AdminHomePage}
 */
public class AdminHomePageUiTest extends BaseUiTestCase{
	private static Browser browser;
	private static AdminHomePage homePage;
	private static AccountAttributes account;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		browser = BrowserPool.getBrowser();
	}
	
	@Test
	public void testAll(){
		testContent();
		//no links to check
		testCreateInstructorAction();
	}

	private void testContent() {
		
		______TS("content: typical page");
		
		Url homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
		homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
		//Full page content check is omitted because this is an internal page. 
	}

	private void testCreateInstructorAction() {
		
		account = new AccountAttributes();
		
		account.googleId = "AHPUiT.instr1";
		account.name =  "AHPUiT Instrúctör";
		account.email = "AHPUiT.instr1@gmail.com";
		account.institute = "Institution";
		account.isInstructor = true;
	    
		BackDoor.deleteAccount(account.googleId);
	    
		______TS("action success : create instructor account with demo course");
		
		String demoCourseId = "AHPUiT.instr1.gma-demo";
		BackDoor.deleteCourse(demoCourseId);
		
		//with sample course
		homePage.createInstructor(account, true)
			.verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");

		verifyAccountCreated(account);
		assertNotNull(BackDoor.getCourse(demoCourseId));

		______TS("action failure : trying to create duplicate instructor account");
		
		homePage.navigateTo(createUrl(Const.ActionURIs.ADMIN_HOME_PAGE));
		homePage.createInstructor(account, false)
			.verifyStatus("The Google ID AHPUiT.instr1 is already registered as an instructor");
		
		______TS("action success : create instructor account without demo course");
		
		account.googleId = "AHPUiT.instr2";
		demoCourseId = account.googleId + "-demo";
		BackDoor.deleteAccount(account.googleId);
		
		homePage.createInstructor(account, false)
			.verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");
		verifyAccountCreated(account);
		assertNull(BackDoor.getCourse(demoCourseId));
		
		______TS("action failure : invalid parameter");

		account.email = "AHPUiT.email.com";
		BackDoor.deleteAccount(account.googleId);
		
		homePage.createInstructor(account, false)
			.verifyStatus(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, account.email, FieldValidator.REASON_INCORRECT_FORMAT));
		
	}

	private void verifyAccountCreated(AccountAttributes expected) {
		AccountAttributes actual = BackDoor.getAccountWithRetry(expected.googleId);
		assertNotNull(actual);
		expected.createdAt = actual.createdAt;
		assertEquals(expected.toString(), actual.toString());
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}

}
