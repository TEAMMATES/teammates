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
import teammates.test.driver.TestProperties;
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
        
        account.name =  "AHPUiT Instrúctör";
        account.email = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT + "@gmail.com";
        account.institute = "Institution";
        account.isInstructor = true;
        String shorName = "shorName";
        
        ______TS("action success : create instructor with demo course");
        
        String demoCourseId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT+".gma-demo";
        BackDoor.deleteCourse(demoCourseId);
        
        //with sample course
        homePage.createInstructor(account, shorName,true)
                .verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");

        assertNotNull(BackDoor.getCourse(demoCourseId));

        
        ______TS("action success : create instructor account without demo course");
       
        demoCourseId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT+".gma-demo";
        BackDoor.deleteCourse(demoCourseId);
        
        homePage.createInstructor(account,shorName,false)
                .verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");
        
        //we actually first create the demo course then after the Account is verified, the course will be deleted 
        assertNotNull(BackDoor.getCourse(demoCourseId));
        
        ______TS("action failure : invalid parameter");

        account.email = "AHPUiT.email.com";
        
        homePage.createInstructor(account, shorName, false)
                .verifyStatus(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, account.email, FieldValidator.REASON_INCORRECT_FORMAT));
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
