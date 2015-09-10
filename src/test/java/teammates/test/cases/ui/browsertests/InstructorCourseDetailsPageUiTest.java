package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.EmailAccount;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Tests 'Course Details' view for Instructors.
 * SUT {@link InstructorCourseDetailsPage}. <br>
 * This class uses real user accounts for students.
 */
public class InstructorCourseDetailsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorCourseDetailsPage detailsPage;
    private static DataBundle testData;
    
    private static String instructorId;
    private static String courseId;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseDetailsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser(true);
    }
    
    @Test 
    public void allTests() throws Exception {
        testContent();
        testCommentToWholeCourse();
        testTableSort();
        //No input validation required
        testLinks();
        testRemindAction();
        testDeleteAction();
    }

    public void testContent() throws Exception {
        
        ______TS("content: no students");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instrForEmptyCourse").googleId;
        courseId = testData.courses.get("CCDetailsUiT.CourseWithoutStudents").id;
        detailsPage = getCourseDetailsPage();
        detailsPage.verifyIsCorrectPage(courseId);

        // This is the full HTML verification for Instructor Course Details Page, the rest can all be verifyMainHtml
        detailsPage.verifyHtml("/InstructorCourseDetailsEmptyCourse.html");
        
        ______TS("content: multiple students with sections");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instr2").googleId;
        courseId = testData.courses.get("CCDetailsUiT.CS2103").id;

        detailsPage = getCourseDetailsPage();
        detailsPage.verifyHtmlMainContent("/InstructorCourseDetailsWithSections.html");
        
        ______TS("content: multiple students with sections with helper view");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instr2Helper").googleId;

        detailsPage = getCourseDetailsPage();
        detailsPage.verifyHtmlMainContent("/InstructorCourseDetailsWithSectionsWithHelperView.html");
        
        ______TS("content: multiple students without sections");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instr").googleId;
        courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
        
        detailsPage = getCourseDetailsPage();
        detailsPage.verifyHtmlMainContent("/InstructorCourseDetailsWithoutSections.html");
    }
    
    private void testCommentToWholeCourse() {
        ______TS("comment to whole course: submit empty comment");
        detailsPage.submitCommentToCourse("");
        detailsPage.verifyStatus("Please enter a valid comment. The comment can't be empty.");
        detailsPage.clickAddCommentToCourseButton();
        
        ______TS("comment to whole course: any comment");
        detailsPage.submitCommentToCourse("this is a comment");
        detailsPage.verifyStatus(Const.StatusMessages.COMMENT_ADDED);
    }

    private void testTableSort() {
        ______TS("content: sorting");
        
        //the first table is the hidden table used for comments' visibility options
        String patternString = "Joined{*}Joined{*}Yet to join{*}Yet to join";
        detailsPage.sortByStatus().verifyTablePattern(1, 4, patternString);
        patternString = "Yet to join{*}Yet to join{*}Joined{*}Joined";
        detailsPage.sortByStatus().verifyTablePattern(1, 4, patternString);
        
        patternString = "Alice Betsy{*}Benny Charles{*}Charlie Davis{*}Danny Engrid";
        detailsPage.sortByName().verifyTablePattern(1, 3, patternString);
        patternString = "Danny Engrid{*}Charlie Davis{*}Benny Charles{*}Alice Betsy";
        detailsPage.sortByName().verifyTablePattern(1, 3, patternString);
        
        patternString = "Team 1{*}Team 1{*}Team 2{*}Team 2";
        detailsPage.sortByTeam().verifyTablePattern(1, 2, patternString);
        patternString = "Team 2{*}Team 2{*}Team 1{*}Team 1";
        detailsPage.sortByTeam().verifyTablePattern(1, 2, patternString);
    }
    
    public void testLinks() {
        
        ______TS("link: view");
        
        StudentAttributes alice = testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104");
        InstructorCourseStudentDetailsViewPage studentDetailsPage = detailsPage.clickViewStudent(alice.name);
        studentDetailsPage.verifyIsCorrectPage(alice.email);
        studentDetailsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: edit");
        
        StudentAttributes charlie = testData.students.get("charlie.tmms@CCDetailsUiT.CS2104");
        InstructorCourseStudentDetailsEditPage studentEditPage = detailsPage.clickEditStudent(charlie.name);
        studentEditPage.verifyIsCorrectPage(charlie.email);
        studentEditPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: all records");
        
        InstructorStudentRecordsPage studentAllRecordsPage = detailsPage.clickAllRecordsLink(charlie.name);
        studentAllRecordsPage.verifyIsCorrectPage(charlie.email);
        studentAllRecordsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        studentAllRecordsPage = detailsPage.clickAllRecordsLink(alice.name);
        studentAllRecordsPage.verifyIsCorrectPage(alice.email);
        studentAllRecordsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: add comment");
        
        StudentAttributes aliceBetsy = testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104");
        InstructorCourseStudentDetailsViewPage studentCommentsPage = detailsPage.clickAddCommentStudent(aliceBetsy.name);
        studentCommentsPage.verifyIsCorrectPage(aliceBetsy.email);
        studentCommentsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: download student list");
        
        Url studentListDownloadUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD)
                                        .withUserId("CCDetailsUiT.instr")
                                        .withCourseId("CCDetailsUiT.CS2104");
        
        detailsPage.verifyDownloadLink(studentListDownloadUrl);
    }

    public void testRemindAction() {

        //Charlie is yet to register
        StudentAttributes charlie = testData.students.get("charlie.tmms@CCDetailsUiT.CS2104");
        String charlieEmail = charlie.email;
        String charliePassword = TestProperties.inst().TEST_STUDENT2_PASSWORD;
        
        //Alice is already registered
        StudentAttributes alice = testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104");
        String alicePassword = TestProperties.inst().TEST_STUDENT2_PASSWORD;
        
        String courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
        boolean isEmailEnabled = !TestProperties.inst().isDevServer();

        ______TS("action: remind single student");

        detailsPage.clickRemindStudentAndCancel(charlie.name);
        if (isEmailEnabled) {
            assertFalse(didStudentReceiveReminder(courseId, charlieEmail, charliePassword));
        }
        

        detailsPage.clickRemindStudentAndConfirm(charlie.name);
        if (isEmailEnabled) {
            assertTrue(didStudentReceiveReminder(courseId, charlie.email, charliePassword));
        }
        
        // Hiding of the 'Send invite' link is already covered by content test.
        //  (i.e., they contain cases of both hidden and visible 'Send invite' links.

        ______TS("action: remind all");

        detailsPage.clickRemindAllAndCancel();
        detailsPage.clickRemindAllAndConfirm();
        
        if (isEmailEnabled) {
            // verify an unregistered student received reminder
            assertTrue(didStudentReceiveReminder(courseId, charlie.email, charliePassword));
            // verify a registered student did not receive a reminder
            assertFalse(didStudentReceiveReminder(courseId, alice.email, alicePassword));
        }
    }

    public void testDeleteAction() throws Exception {
        
        ______TS("action: delete");
        
        String studentName = testData.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
        String studentEmail = testData.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
        String courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
        
        detailsPage.clickDeleteAndCancel(studentName);
        assertNotNull(BackDoor.getStudent(courseId, studentEmail));

        //Use ${test.student1} etc. 
        detailsPage.clickDeleteAndConfirm(studentName)
                        .verifyHtmlMainContent("/instructorCourseDetailsStudentDeleteSuccessful.html");
        
        studentName = testData.students.get("danny.tmms@CCDetailsUiT.CS2104").name;
        studentEmail = testData.students.get("danny.tmms@CCDetailsUiT.CS2104").email;
        courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
        
        detailsPage.clickDeleteAndCancel(studentName);
        assertNotNull(BackDoor.getStudent(courseId, studentEmail));
        
    }
    
    private InstructorCourseDetailsPage getCourseDetailsPage() {
        Url detailsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                                .withUserId(instructorId)
                                .withCourseId(courseId);

        return loginAdminToPage(browser, detailsPageUrl, InstructorCourseDetailsPage.class);
    }
    
    private boolean didStudentReceiveReminder(String courseId, String studentEmail, String studentPassword) {
        String keyToSend = StringHelper.encrypt(BackDoor.getKeyForStudent(courseId, studentEmail));
    
        ThreadHelper.waitFor(5000); //TODO: replace this with a more efficient check
        String keyReceivedInEmail = EmailAccount.getRegistrationKeyFromGmail(studentEmail, studentPassword, courseId);
        return (keyToSend.equals(keyReceivedInEmail));
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BackDoor.removeDataBundleFromDb(testData);
        BrowserPool.release(browser);
    }
}