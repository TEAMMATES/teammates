package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
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
		testInputValidation();
		testCreateInstructorAction();
	}

	private void testContent() {
		
		______TS("content: typical page");
		
		Url homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
		homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
		//Full page content check is omitted because this is an internal page. 
	}

	private void testInputValidation() {
		
		______TS("input validation: empty fields");
		
		account = new AccountAttributes();
		
		homePage.createInstructor(account, false)
			.verifyStatus(Const.StatusMessages.FIELDS_EMPTY);
		
		account.googleId = "AHPUiT.instr1";
		account.name =  "!@#$%^&";
		account.email = "AHPUiT.instr1";
	    account.institute = "Institution";
	    account.isInstructor = true;
		
	    ______TS("input validation: invalid email");
	    
		BackDoor.deleteAccount(account.googleId);
		
		homePage.createInstructor(account, false)
			.verifyStatus("The e-mail address is invalid.");
	
		______TS("input validation: invalid name");
		
		account.email = "AHPUiT.instr1@gmail.com";
		homePage.createInstructor(account, false)
			.verifyStatus("Name should only consist of alphanumerics or hyphens, " +
					"apostrophes, fullstops, commas, slashes, round brackets" +
					"\nand not more than 40 characters.");
	}

	private void testCreateInstructorAction() {
		
		______TS("action success : create instructor account with demo course");
		
		String demoCourseId = "AHPUiT.instr1.gma-demo";
		BackDoor.deleteCourse(demoCourseId);
		
		//with sample course
		
		account.name =  "New Instructor";
		homePage.createInstructor(account, true)
			.verifyStatus("Instructor New Instructor has been successfully created");

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
			.verifyStatus("Instructor New Instructor has been successfully created");
		verifyAccountCreated(account);
		assertNull(BackDoor.getCourse(demoCourseId));
		

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
