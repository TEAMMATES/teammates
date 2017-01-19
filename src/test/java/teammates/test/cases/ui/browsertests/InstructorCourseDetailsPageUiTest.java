package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
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
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseDetailsPageUiTest.json");
        
        // use both the student accounts injected for this test
        
        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        String student2GoogleId = TestProperties.TEST_STUDENT2_ACCOUNT;
        String student2Email = student2GoogleId + "@gmail.com";
        testData.accounts.get("Alice").googleId = student1GoogleId;
        testData.accounts.get("Charlie").googleId = student2GoogleId;
        testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104").googleId = student1GoogleId;
        testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104").email = student1Email;
        testData.students.get("charlie.tmms@CCDetailsUiT.CS2104").email = student2Email;
        testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2103").googleId = student1GoogleId;
        testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2103").email = student1Email;
        testData.students.get("charlie.tmms@CCDetailsUiT.CS2103").email = student2Email;
        
        removeAndRestoreDataBundle(testData);
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
        courseId = testData.courses.get("CCDetailsUiT.CourseWithoutStudents").getId();
        detailsPage = getCourseDetailsPage();
        detailsPage.verifyIsCorrectPage(courseId);

        // This is the full HTML verification for Instructor Course Details Page, the rest can all be verifyMainHtml
        detailsPage.verifyHtml("/instructorCourseDetailsEmptyCourse.html");
        
        ______TS("content: multiple students with sections");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instr2").googleId;
        courseId = testData.courses.get("CCDetailsUiT.CS2103").getId();

        detailsPage = getCourseDetailsPage();
        detailsPage.verifyHtmlMainContent("/instructorCourseDetailsWithSections.html");
        
        ______TS("content: multiple students with sections with helper view");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instr2Helper").googleId;

        detailsPage = getCourseDetailsPage();
        detailsPage.verifyHtmlMainContent("/instructorCourseDetailsWithSectionsWithHelperView.html");
        
        ______TS("content: multiple students without sections");
        
        instructorId = testData.instructors.get("CCDetailsUiT.instr").googleId;
        courseId = testData.courses.get("CCDetailsUiT.CS2104").getId();
        
        detailsPage = getCourseDetailsPage();
        detailsPage.verifyHtmlMainContent("/instructorCourseDetailsWithoutSections.html");
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
        
        patternString = "Alice Betsy</option></td></div>'\"{*}Benny Charles{*}Charlie Davis{*}Danny Engrid";
        detailsPage.sortByName().verifyTablePattern(1, 3, patternString);
        patternString = "Danny Engrid{*}Charlie Davis{*}Benny Charles{*}Alice Betsy";
        detailsPage.sortByName().verifyTablePattern(1, 3, patternString);
        
        patternString = "Team 1</option><option value=\"dump\"></td><td>'\"{*}"
                        + "Team 1</option><option value=\"dump\"></td><td>'\"{*}"
                        + "Team 2{*}"
                        + "Team 2";
        detailsPage.sortByTeam().verifyTablePattern(1, 2, patternString);
        patternString = "Team 2{*}"
                        + "Team 2{*}"
                        + "Team 1</option><option value=\"dump\"></td><td>'\"{*}"
                        + "Team 1</option><option value=\"dump\"></td><td>'\"";
        detailsPage.sortByTeam().verifyTablePattern(1, 2, patternString);
    }
    
    public void testLinks() {
        StudentAttributes student1 = testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104");
        StudentAttributes student2 = testData.students.get("charlie.tmms@CCDetailsUiT.CS2104");
        
        ______TS("link: view");
        
        InstructorCourseStudentDetailsViewPage studentDetailsPage = detailsPage.clickViewStudent(student1.name);
        studentDetailsPage.verifyIsCorrectPage(student1.email);
        studentDetailsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: edit");
        
        InstructorCourseStudentDetailsEditPage studentEditPage = detailsPage.clickEditStudent(student2.name);
        studentEditPage.verifyIsCorrectPage(student2.email);
        studentEditPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: all records");
        
        InstructorStudentRecordsPage studentAllRecordsPage = detailsPage.clickAllRecordsLink(student2.name);
        studentAllRecordsPage.verifyIsCorrectPage(student2.email);
        studentAllRecordsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        studentAllRecordsPage = detailsPage.clickAllRecordsLink(student1.name);
        studentAllRecordsPage.verifyIsCorrectPage(student1.email);
        studentAllRecordsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: add comment");
        
        InstructorCourseStudentDetailsViewPage studentCommentsPage = detailsPage.clickAddCommentStudent(student1.name);
        studentCommentsPage.verifyIsCorrectPage(student1.email);
        studentCommentsPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: download student list");

        AppUrl studentListDownloadUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD)
                                        .withUserId("CCDetailsUiT.instr")
                                        .withCourseId("CCDetailsUiT.CS2104");

        detailsPage.verifyDownloadLink(studentListDownloadUrl);
    }

    public void testRemindAction() throws Exception {
        String courseId = testData.courses.get("CCDetailsUiT.CS2104").getId();
        String courseName = testData.courses.get("CCDetailsUiT.CS2104").getName();
        StudentAttributes student1 = testData.students.get("CCDetailsUiT.alice.tmms@CCDetailsUiT.CS2104");
        StudentAttributes student2 = testData.students.get("charlie.tmms@CCDetailsUiT.CS2104");

        // student2 is yet to register, student1 is already registered
        String student1Password = TestProperties.TEST_STUDENT1_PASSWORD;
        String student2Password = TestProperties.TEST_STUDENT2_PASSWORD;
        boolean isEmailEnabled = !TestProperties.isDevServer();

        ______TS("action: remind single student");

        detailsPage.clickRemindStudentAndCancel(student2.name);
        if (isEmailEnabled) {
            assertFalse(didStudentReceiveReminder(courseName, courseId, student2.email, student2Password));
        }

        detailsPage.clickRemindStudentAndConfirm(student2.name);
        if (isEmailEnabled) {
            assertTrue(didStudentReceiveReminder(courseName, courseId, student2.email, student2Password));
        }
        
        // Hiding of the 'Send invite' link is already covered by content test.
        //  (i.e., they contain cases of both hidden and visible 'Send invite' links.

        ______TS("action: remind all");

        detailsPage.clickRemindAllAndCancel();
        detailsPage.clickRemindAllAndConfirm();
        
        if (isEmailEnabled) {
            // verify an unregistered student received reminder
            assertTrue(didStudentReceiveReminder(courseName, courseId, student2.email, student2Password));
            // verify a registered student did not receive a reminder
            assertFalse(didStudentReceiveReminder(courseName, courseId, student1.email, student1Password));
        }
    }

    public void testDeleteAction() throws Exception {
        String courseId = testData.courses.get("CCDetailsUiT.CS2104").getId();
        StudentAttributes benny = testData.students.get("benny.tmms@CCDetailsUiT.CS2104");
        StudentAttributes danny = testData.students.get("danny.tmms@CCDetailsUiT.CS2104");
        
        ______TS("action: delete");
                
        detailsPage.clickDeleteAndCancel(benny.name);
        assertNotNull(BackDoor.getStudent(courseId, benny.email));

        //Use ${test.student1} etc.
        detailsPage.clickDeleteAndConfirm(benny.name)
                        .verifyHtmlMainContent("/instructorCourseDetailsStudentDeleteSuccessful.html");
                
        detailsPage.clickDeleteAndCancel(danny.name);
        assertNotNull(BackDoor.getStudent(courseId, danny.email));
    }
    
    private InstructorCourseDetailsPage getCourseDetailsPage() {
        AppUrl detailsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                                .withUserId(instructorId)
                                .withCourseId(courseId);

        return loginAdminToPage(browser, detailsPageUrl, InstructorCourseDetailsPage.class);
    }
    
    private boolean didStudentReceiveReminder(String courseName, String courseId, String studentEmail,
                                              String studentPassword)
            throws Exception {
        String keyToSend = BackDoor.getEncryptedKeyForStudent(courseId, studentEmail);
    
        ThreadHelper.waitFor(5000); //TODO: replace this with a more efficient check
        String keyReceivedInEmail =
                EmailAccount.getRegistrationKeyFromGmail(studentEmail, studentPassword, courseName, courseId);
        return keyToSend.equals(keyReceivedInEmail);
    }

    @AfterClass
    public static void classTearDown() {
        BackDoor.removeDataBundle(testData);
        BrowserPool.release(browser);
    }
}
