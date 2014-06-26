package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import java.lang.reflect.Constructor;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

<<<<<<< HEAD
import teammates.common.datatransfer.InstructorAttributes;
=======
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
>>>>>>> origin/master
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.InstructorCourseJoinConfirmationPage;
import teammates.test.pageobjects.LoginPage;

/**
 * Covers the home page for admins.
 * SUT: {@link AdminHomePage}
 */
public class AdminHomePageUiTest extends BaseUiTestCase{
    private static Browser browser;
    private static AdminHomePage homePage;
    private static InstructorCourseJoinConfirmationPage confirmationPage;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();      
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
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
        
        InstructorAttributes instructor = new InstructorAttributes();
        
<<<<<<< HEAD
        String shortName = "Instrúctör";
        instructor.name =  "AHPUiT Instrúctör";
        instructor.email = "AHPUiT.instr1@gmail.com";
        String institute = "National University of Singapore";
        String demoCourseId = "AHPUiT.instr1.gma-demo";
        
       
=======
        account.googleId = "AHPUiT.instr1";
        account.name =  "AHPUiT Instrúctör";
        account.email = "AHPUiT.instr1@gmail.com";
        account.institute = "Institution";
        account.isInstructor = true;
        account.studentProfile = new StudentProfileAttributes();
        account.studentProfile.googleId = account.googleId;
        BackDoor.deleteAccount(account.googleId);
>>>>>>> origin/master
        
        ______TS("action success : create instructor account and the account is created successfully after user's verification");
        
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        
        homePage.createInstructor(shortName,instructor,institute).verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");  
        homePage.logout();
        //verify the instructor and the demo course have been created
        assertNotNull(BackDoor.getCourse(demoCourseId));
        assertNotNull(BackDoor.getInstructorByEmail(instructor.email, demoCourseId));
        
        //get the joinURL which sent to the requester's email
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
      
        String joinLink = Url.addParamToUrl(joinActionUrl, Const.ParamsNames.REGKEY,
                                            StringHelper.encrypt(BackDoor.getKeyForInstructor(demoCourseId, instructor.email)));
       
        joinLink = Url.addParamToUrl(joinLink, Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        
        //simulate the user's verification here because it is added by admin 
        browser.driver.get(joinLink);        
        confirmationPage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsJoiningInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickCancelButton();      
        
        browser.driver.get(joinLink);
        confirmationPage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsJoiningInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);  
        confirmationPage.clickConfirmButton();
        confirmationPage.verifyContains("Instructor Home");
        
<<<<<<< HEAD
        //check a account has been created for the requester successfully
        assertNotNull(BackDoor.getAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT));
        
        BackDoor.deleteAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        
        confirmationPage.logout();
=======
        account.studentProfile = new StudentProfileAttributes();
        account.studentProfile.googleId = account.googleId;
        homePage.createInstructor(account, false)
            .verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");
        verifyAccountCreated(account);
        assertNull(BackDoor.getCourse(demoCourseId));
>>>>>>> origin/master
        
        ______TS("action failure : invalid parameter");
        
        Url homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
        
        instructor.email = "AHPUiT.email.com";
        
        homePage.createInstructor(shortName,instructor,institute).verifyStatus(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, instructor.email, FieldValidator.REASON_INCORRECT_FORMAT));
        
<<<<<<< HEAD
=======
    }

    private void verifyAccountCreated(AccountAttributes expected) {
        AccountAttributes actual = BackDoor.getAccountWithRetry(expected.googleId);
        assertNotNull(actual);
        expected.createdAt = actual.createdAt;
        expected.studentProfile.modifiedDate = actual.studentProfile.modifiedDate;
        assertEquals(expected.toString(), actual.toString());
>>>>>>> origin/master
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    
    private LoginPage createCorrectLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :" + pageSource);
        }
    }
    
    private <T extends AppPage> T createNewPage(Browser browser, Class<T> typeOfPage) {
        Constructor<T> constructor;
        try {
            constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(browser);
            PageFactory.initElements(browser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
