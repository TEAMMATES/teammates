package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminAccountDetailsPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.util.Priority;

/**
 * Covers the 'accounts management' view for admins.
 * SUT: {@link AdminAccountDetailsPage}
 */
@Priority(1)
public class AdminAccountDetailsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AdminAccountDetailsPage detailsPage;
    private static DataBundle testData;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/AdminAccountDetailsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() throws Exception {
        testContent();
        //no links or input validation to check
        testRemoveFromCourseAction();
    }
    
    public void testContent() throws Exception {
        
        ______TS("content: typical page");
        
        AppUrl detailsPageUrl = createUrl(Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE)
                .withInstructorId("AAMgtUiT.instr2")
                .withUserId(TestProperties.TEST_ADMIN_ACCOUNT);
        detailsPage = loginAdminToPage(browser, detailsPageUrl, AdminAccountDetailsPage.class);
        
        detailsPage.verifyHtml("/adminAccountDetails.html");
    }

    public void testRemoveFromCourseAction() throws Exception {
        
        ______TS("action: remove instructor from course");
        
        String googleId = "AAMgtUiT.instr2";
        String courseId = "AAMgtUiT.CS2104";
        
        detailsPage.clickRemoveInstructorFromCourse(courseId)
            .verifyStatus(Const.StatusMessages.INSTRUCTOR_REMOVED_FROM_COURSE);
        assertNull(BackDoor.getInstructorByGoogleId(googleId, courseId));
    
        ______TS("action: remove student from course");
        
        courseId = "AAMgtUiT.CS1101";
        detailsPage.clickRemoveStudentFromCourse(courseId)
            .verifyStatus(Const.StatusMessages.STUDENT_DELETED);
        assertNull(BackDoor.getStudent(courseId, "AAMgtUiT.instr2@gmail.com"));
        detailsPage.verifyHtmlMainContent("/adminAccountDetailsRemoveStudent.html");
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
}
