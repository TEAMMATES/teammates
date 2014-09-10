package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorSearchPage;

public class InstructorSearchPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorSearchPage searchPage;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        putDocuments(testData);
        browser = BrowserPool.getBrowser();
        
        // upload a profile picture for one of the students
        StudentAttributes student = testData.students.get("student2InCourse1");
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = Utils.getTeammatesGson().toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
        assertEquals("Unable to upload profile picture", "[BACKDOOR_STATUS_SUCCESS]", 
                BackDoor.uploadAndUpdateStudentProfilePicture(student.googleId, pictureData));
    }
    
    @Test 
    public void allTests() throws Exception{
        
        testContent();
        testSearch();
        
    }
    
    private void testContent() {
        
        ______TS("content: default search page");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorId);
        searchPage.verifyHtml("/InstructorSearchPageDefault.html");
        
    }
    
    private void testSearch() {
        
        ______TS("search for nothing");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        String searchContent = "comment";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchNone.html");
        
        ______TS("search for student comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchStudentComments.html");
        
        ______TS("search for feedback response comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchFeedbackResponseComments.html");
        
        ______TS("search for all comments");
        
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchComments.html");
        
        ______TS("search for all comments as helper");
        
        String instructorHelperId = testData.accounts.get("helperOfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorHelperId);
        searchPage.clickStudentCommentCheckBox();
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchCommentsAsHelper.html");
        
        
        searchPage = getInstructorSearchPage(instructorId);
        
        ______TS("search for students");
        
        searchPage.clearSearchBox();
        searchContent = "student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchStudentsForStudent1.html");
        searchPage.clearSearchBox();
        searchContent = "student2";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickAndHoverPicture("studentphoto-c0.1");
        searchPage.verifyHtmlMainContent("/InstructorSearchPageSearchStudentsForStudent2.html");        
    }

    private InstructorSearchPage getInstructorSearchPage(String instructorId) {
        Url commentsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE)
                .withUserId(instructorId);

        return loginAdminToPage(browser, commentsPageUrl, InstructorSearchPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
