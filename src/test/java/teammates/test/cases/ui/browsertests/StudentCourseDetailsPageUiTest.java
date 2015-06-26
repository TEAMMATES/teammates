package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentCourseDetailsPage;

/**
 * Tests Student Course Details page
 */
public class StudentCourseDetailsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentCourseDetailsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test    
    public void testAll() throws Exception{
        // TODO: Split up both tests, one to use verifyHtml and one to use verifyHtmlMainContent to ensure
        // a full HTML check is done at least

        ______TS("content");
        
        //with teammates"
        verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.alice", "/studentCourseDetailsWithTeammatesHTML.html");

        //without teammates 
        verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.charlie", "/studentCourseDetailsWithoutTeammatesHTML.html");
        
        ______TS("links, inputValidation, actions");
        
        //nothing to test here.

    }

    private void verifyContent(String courseObjectId, String studentObjectId, String filePath) {
        Url detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE)
                                .withUserId(testData.students.get(studentObjectId).googleId)
                                .withCourseId(testData.courses.get(courseObjectId).id);
        
        loginAdminToPage(browser, detailsPageUrl, StudentCourseDetailsPage.class).verifyHtmlMainContent(filePath);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
}