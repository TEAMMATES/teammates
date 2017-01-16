package teammates.test.cases.ui.browsertests;

import java.io.File;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorSearchPage;
import teammates.test.util.FileHelper;

public class InstructorSearchPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorSearchPage searchPage;
    private static DataBundle testData;

    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
        browser = BrowserPool.getBrowser();
        
        // upload a profile picture for one of the students
        StudentAttributes student = testData.students.get("student2InCourse1");
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = JsonUtils.toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
        assertEquals("Unable to upload profile picture", "[BACKDOOR_STATUS_SUCCESS]",
                BackDoor.uploadAndUpdateStudentProfilePicture(student.googleId, pictureData));
    }
    
    @Test
    public void allTests() throws Exception {
        
        testContent();
        testSearch();
        
    }
    
    private void testContent() throws Exception {
        
        ______TS("content: default search page");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorId);

        // This is the full HTML verification for Instructor Search Page, the rest can all be verifyMainHtml
        searchPage.verifyHtml("/instructorSearchPageDefault.html");
        
    }
    
    private void testSearch() throws Exception {
        
        ______TS("search for nothing");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        String searchContent = "comment";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchNone.html");
        
        ______TS("search for student comments");
        
        searchContent = "student comment";
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentComments.html");
        
        ______TS("search for feedback response comments");
        
        searchContent = "response comment";
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchFeedbackResponseComments.html");
        
        ______TS("search for all comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchComments.html");
        
        ______TS("search for all comments as helper");
        
        String instructorHelperId = testData.accounts.get("helperOfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorHelperId);
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchCommentsAsHelper.html");
        
        searchPage = getInstructorSearchPage(instructorId);

        ______TS("search exact string for students");
        
        searchPage.clearSearchBox();
        searchContent = "\"student2 2 In Course1\"";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentsForStudent2WithExactString.html");
        
        ______TS("search for students");
        
        searchPage.clearSearchBox();
        searchContent = "Course1 In student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentsForStudent1.html");
        searchPage.clearSearchBox();
        searchContent = "In student2";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickAndHoverPicture("studentphoto-c0.1");
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentsForStudent2.html");
    }

    private InstructorSearchPage getInstructorSearchPage(String instructorId) {
        AppUrl commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE)
                .withUserId(instructorId);

        return loginAdminToPage(browser, commentsPageUrl, InstructorSearchPage.class);
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

}
