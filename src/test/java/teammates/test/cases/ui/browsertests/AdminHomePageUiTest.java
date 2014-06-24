package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
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
        
        InstructorAttributes instructor = new InstructorAttributes();
        
        String shortName = "Instrúctör";
        instructor.name =  "AHPUiT Instrúctör";
        instructor.email = "AHPUiT.instr1@gmail.com";
        String institute = "Institution";
        String demoCourseId = "AHPUiT.instr1.gma-demo";
        
       
        
        ______TS("action success : create instructor account with demo course");
        
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        
        //with sample course
        homePage.createInstructor(shortName,instructor,institute).verifyStatus("Instructor AHPUiT Instrúctör has been successfully created");      
        assertNotNull(BackDoor.getCourse(demoCourseId));

        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        ______TS("action failure : invalid parameter");

        instructor.email = "AHPUiT.email.com";
        
        homePage.createInstructor(shortName,instructor,institute).verifyStatus(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, instructor.email, FieldValidator.REASON_INCORRECT_FORMAT));
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
