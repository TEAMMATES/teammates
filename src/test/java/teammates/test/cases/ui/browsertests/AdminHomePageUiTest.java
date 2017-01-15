package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseJoinConfirmationPage;
import teammates.test.pageobjects.InstructorCoursesPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.pageobjects.InstructorHomePage;
import teammates.test.pageobjects.StudentCommentsPage;
import teammates.test.pageobjects.StudentCourseDetailsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

/**
 * Covers the home page for admins.
 * SUT: {@link AdminHomePage}
 */
@Priority(6)
public class AdminHomePageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static AdminHomePage homePage;
    private static InstructorCourseJoinConfirmationPage confirmationPage;

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
    }
    
    @Test
    public void testAll() throws InvalidParametersException, EntityDoesNotExistException, Exception {
        testContent();
        //no links to check
        testCreateInstructorAction();
    }

    private void testContent() throws Exception {
        
        ______TS("content: typical page");
        
        AppUrl homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE).withUserId(TestProperties.TEST_ADMIN_ACCOUNT);
        homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
        
        homePage.verifyHtml("/adminHomePage.html");
    }

    @SuppressWarnings("deprecation")
    private void testCreateInstructorAction() throws Exception {
        
        InstructorAttributes instructor = new InstructorAttributes();
        
        String shortName = "Instrúctör";
        instructor.name = "AHPUiT Instrúctör";
        instructor.email = "AHPUiT.instr1@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";
        String demoCourseId = "AHPUiT.instr1.gma-demo";
        
        String instructorDetails = instructor.name + " | " + instructor.email + "\n"
                                 + instructor.name + " | " + instructor.email + " | " + institute;
        
        ______TS("action fail & success: add multiple instructors");
        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        homePage.createInstructorByInstructorDetailsSingleLineForm(instructorDetails);
        assertEquals(String.format(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID,
                                   Const.LENGTH_FOR_NAME_EMAIL_INSTITUTION),
                     homePage.getMessageFromResultTable(1));
        
        String encryptedKey = BackDoor.getEncryptedKeyForInstructor(demoCourseId, instructor.email);
        // use AppUrl from Config because the join link takes its base URL from build.properties
        String expectedjoinUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(encryptedKey)
                                        .withInstructorInstitution(institute)
                                        .toAbsoluteString();
        assertEquals("Instructor AHPUiT Instrúctör has been successfully created with join link:\n" + expectedjoinUrl,
                     homePage.getMessageFromResultTable(2));
        homePage.clearInstructorDetailsSingleLineForm();
        
        ______TS("action success : create instructor account and the account is created successfully "
                 + "after user's verification");
        
        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        
        homePage.createInstructor(shortName, instructor, institute);
        
        encryptedKey = BackDoor.getEncryptedKeyForInstructor(demoCourseId, instructor.email);
        // use AppUrl from Config because the join link takes its base URL from build.properties
        expectedjoinUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(encryptedKey)
                                        .withInstructorInstitution(institute)
                                        .toAbsoluteString();
       
        assertEquals("Instructor AHPUiT Instrúctör has been successfully created with join link:\n" + expectedjoinUrl,
                     homePage.getMessageFromResultTable(1));
        
        homePage.logout();
        //verify the instructor and the demo course have been created
        assertNotNull(BackDoor.getCourse(demoCourseId));
        assertNotNull(BackDoor.getInstructorByEmail(instructor.email, demoCourseId));
        
        //get the joinURL which sent to the requester's email
        String regkey = BackDoor.getEncryptedKeyForInstructor(demoCourseId, instructor.email);
        String joinLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(regkey)
                                        .withInstructorInstitution(institute)
                                        .toAbsoluteString();
      
        //simulate the user's verification here because it is added by admin
        browser.driver.get(joinLink);
        confirmationPage = AppPage.createCorrectLoginPageType(browser)
                           .loginAsJoiningInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickCancelButton();
        
        browser.driver.get(joinLink);
        confirmationPage = AppPage.createCorrectLoginPageType(browser)
                           .loginAsJoiningInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickConfirmButton();
        
        //check a account has been created for the requester successfully
        assertNotNull(BackDoor.getAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT));

        //verify sample course is accessible for newly joined instructor as an instructor
        
        ______TS("new instructor can see sample course in homepage");
        InstructorHomePage instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorHomePage.html");
        
        ______TS("new instructor can access sample coure enroll page");
        InstructorCourseEnrollPage enrollPage = instructorHomePage.clickCourseErollLink(demoCourseId);
        enrollPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseEnrollPage.html");
        
        ______TS("new instructor can access sample coure details page");
        instructorHomePage = enrollPage.goToPreviousPage(InstructorHomePage.class);
        InstructorCourseDetailsPage detailsPage = instructorHomePage.clickCourseViewLink(demoCourseId);
        detailsPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseDetailsPage.html");
        
        ______TS("new instructor can access sample coure edit page");
        instructorHomePage = detailsPage.goToPreviousPage(InstructorHomePage.class);
        InstructorCourseEditPage editPage = instructorHomePage.clickCourseEditLink(demoCourseId);
        editPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseEditPage.html");
        
        ______TS("new instructor can access sample coure feedback session adding page");
        instructorHomePage = editPage.goToPreviousPage(InstructorHomePage.class);
        InstructorFeedbacksPage feedbacksPage = instructorHomePage.clickCourseAddEvaluationLink(demoCourseId);
        feedbacksPage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbacksPage.html");
        
        ______TS("new instructor can archive sample course");
        instructorHomePage = feedbacksPage.goToPreviousPage(InstructorHomePage.class);
        instructorHomePage.clickArchiveCourseLinkAndConfirm(demoCourseId);
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorHomePageSampleCourseArchived.html");
        
        ______TS("new instructor can unarchive sample course");
        AppUrl url = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
                                        .withUserId(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        InstructorCoursesPage coursesPage = AppPage.getNewPageInstance(browser, url, InstructorCoursesPage.class);
        coursesPage.waitForAjaxLoadCoursesSuccess();
        coursesPage.unarchiveCourse(demoCourseId);
        coursesPage.verifyHtmlMainContent("/newlyJoinedInstructorCoursesPageSampleCourseUnarhived.html");
        
        ______TS("new instructor can access sample course students page");
        coursesPage.loadStudentsTab().verifyHtmlMainContent("/newlyJoinedInstructorStudentListPage.html");
        ______TS("new instructor can access sample course comments page");
        coursesPage.loadInstructorCommentsTab().verifyHtmlMainContent("/newlyJoinedInstructorCommentsPage.html");
        
        ______TS("new instructor can view feedbackSession result of sample course");
        coursesPage.loadInstructorHomeTab();
        instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        instructorHomePage.clickFeedbackSessionViewResultsLink("AHPUiT.instr1.gma-demo", "Second team feedback session")
                          .waitForPageToLoad();
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackResultsPage.html");
        
        ______TS("new instructor can edit feedbackSession of sample course");
        instructorHomePage.loadInstructorHomeTab();
        InstructorFeedbackEditPage feedbackEditPage =
                instructorHomePage.clickFeedbackSessionEditLink("AHPUiT.instr1.gma-demo", "Second team feedback session");
        
        feedbackEditPage.clickEditSessionButton();
        
        FeedbackSessionAttributes feedbackSession = BackDoor.getFeedbackSession("AHPUiT.instr1.gma-demo",
                                                                                "Second team feedback session");
        feedbackEditPage.editFeedbackSession(feedbackSession.getStartTime(),
                                             feedbackSession.getEndTime(),
                                             new Text("updated instructions"),
                                             feedbackSession.getGracePeriod());
        feedbackEditPage.reloadPage();
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionSuccessEdited.html");

        ______TS("new instructor can click submit button of sample feedbackSession");
        instructorHomePage.loadInstructorHomeTab();
        FeedbackSubmitPage fbsp = instructorHomePage.clickFeedbackSessionSubmitLink("AHPUiT.instr1.gma-demo",
                                                                                    "Second team feedback session");
        fbsp.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSubmissionEditPage.html");
        
        ______TS("new instructor can send reminder of sample course");
        instructorHomePage.loadInstructorHomeTab();
        instructorHomePage.clickFeedbackSessionRemindLink("AHPUiT.instr1.gma-demo",
                                                          "Second team feedback session");
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionRemind.html");
        
        ______TS("new instructor can unpublish feedbackSession of sample course");
        instructorHomePage.loadInstructorHomeTab();
        instructorHomePage.clickFeedbackSessionUnpublishLink("AHPUiT.instr1.gma-demo", "Second team feedback session");
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionUnpublished.html");
        
        ______TS("new instructor can publish feedbackSession of sample course");
        instructorHomePage.loadInstructorHomeTab();
        instructorHomePage.clickFeedbackSessionPublishLink("AHPUiT.instr1.gma-demo", "Second team feedback session");
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionPublished.html");
        
        feedbacksPage.logout();
        
        ______TS("action failure : invalid parameter");
        
        AppUrl homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
        
        instructor.email = "AHPUiT.email.tmt";
        homePage.createInstructor(shortName, instructor, institute);
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.EMAIL_ERROR_MESSAGE, instructor.email,
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.EMAIL_MAX_LENGTH),
                     homePage.getMessageFromResultTable(1));

        ______TS("action success: course is accessible for newly joined instructor as student");
        //in staging server, the student account uses the hardcoded email above, so this can only be test on dev server
        if (!TestProperties.isDevServer()) {
            
            BackDoor.deleteCourse(demoCourseId);
            BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
            BackDoor.deleteInstructor(demoCourseId, instructor.email);
            return;
        }
        
        //verify sample course is accessible for newly joined instructor as an student
        
        StudentHomePage studentHomePage = getHomePage(browser).clickStudentLogin()
                                                              .loginAsStudent(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                                              TestProperties.TEST_INSTRUCTOR_PASSWORD);
        
        studentHomePage.verifyContains(demoCourseId);
        studentHomePage.clickViewTeam();
        
        StudentCourseDetailsPage courseDetailsPage = AppPage.getNewPageInstance(browser, StudentCourseDetailsPage.class);
        courseDetailsPage.verifyHtmlMainContent("/newlyJoinedInstructorStudentCourseDetailsPage.html");
        
        studentHomePage = courseDetailsPage.goToPreviousPage(StudentHomePage.class);
        studentHomePage.clickViewFeedbackButton("First team feedback session");
        StudentFeedbackResultsPage sfrp = AppPage.getNewPageInstance(browser, StudentFeedbackResultsPage.class);
        sfrp.verifyHtmlMainContent("/newlyJoinedInstructorStudentFeedbackResultsPage.html");
        
        studentHomePage = sfrp.goToPreviousPage(StudentHomePage.class);
        studentHomePage.clickEditFeedbackButton("First team feedback session");
        FeedbackSubmitPage fsp = AppPage.getNewPageInstance(browser, FeedbackSubmitPage.class);
        fsp.verifyHtmlMainContent("/newlyJoinedInstructorStudentFeedbackSubmissionEdit.html");
        
        studentHomePage = fsp.loadStudentHomeTab();
        StudentCommentsPage scp = studentHomePage.loadStudentCommentsTab();
        scp.verifyHtmlMainContent("/newlyJoinedInstructorStudentCommentsPage.html");
        
        studentHomePage = scp.loadStudentHomeTab();
        
        StudentProfilePage spp = studentHomePage.loadProfileTab();
        spp.verifyContains("Student Profile");
        spp.verifyContains("AHPUiT Instrúctör");
        
        studentHomePage.logout();
        
        //login in as instructor again to test sample course deletion
        instructorHomePage = getHomePage(browser).clickInstructorLogin()
                                                 .loginAsInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                                    TestProperties.TEST_INSTRUCTOR_PASSWORD);

        instructorHomePage.clickAndConfirm(instructorHomePage.getDeleteCourseLink(demoCourseId));
        assertTrue(instructorHomePage.getStatus().contains("The course " + demoCourseId + " has been deleted."));
     
        instructorHomePage.logout();
        
        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);

    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
}
