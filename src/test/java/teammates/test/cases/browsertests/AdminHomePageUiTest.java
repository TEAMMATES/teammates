package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseJoinConfirmationPage;
import teammates.test.pageobjects.InstructorCoursesPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.InstructorHomePage;
import teammates.test.pageobjects.StudentCourseDetailsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;

/**
 * SUT: {@link Const.ActionURIs#ADMIN_HOME_PAGE}.
 */
@Priority(6)
public class AdminHomePageUiTest extends BaseUiTestCase {
    private AdminHomePage homePage;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
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
        homePage = loginAdminToPage(homeUrl, AdminHomePage.class);

        homePage.verifyHtml("/adminHomePage.html");
    }

    @SuppressWarnings("deprecation")
    private void testCreateInstructorAction() throws Exception {

        InstructorAttributes instructor = new InstructorAttributes();

        String shortName = "Instrúctör";
        instructor.name = "AHPUiT Instrúctör WithPlusInEmail";
        instructor.email = "AHPUiT+++_.instr1!@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";
        String demoCourseId = "AHPUiT____.instr1_.gma-demo";

        String instructorDetails = instructor.name + " | " + instructor.email + "\n"
                                 + instructor.name + " | " + instructor.email + " | " + institute;

        ______TS("action fail & success: add multiple instructors");
        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        homePage.createInstructorByInstructorDetailsSingleLineForm(instructorDetails);
        InstructorAttributes instructorInBackend = getInstructorWithRetry(demoCourseId, instructor.email);
        assertEquals(String.format(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID,
                                   Const.LENGTH_FOR_NAME_EMAIL_INSTITUTION),
                     homePage.getMessageFromResultTable(1));

        String encryptedKey = getKeyForInstructorWithRetry(demoCourseId, instructor.email);
        // use AppUrl from Config because the join link takes its base URL from build.properties
        String expectedjoinUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(encryptedKey)
                                        .withInstructorInstitution(institute)
                                        .toAbsoluteString();
        assertEquals("Instructor AHPUiT Instrúctör WithPlusInEmail has been successfully created with join link:\n"
                     + expectedjoinUrl, homePage.getMessageFromResultTable(2));
        assertEquals(instructor.getName(), instructorInBackend.getName());
        assertEquals(instructor.getEmail(), instructorInBackend.getEmail());
        homePage.clearInstructorDetailsSingleLineForm();

        ______TS("action success: displayed instructor details are properly HTML-encoded");

        InstructorAttributes dangerousInstructor = new InstructorAttributes();

        String shortNameDangerous = "<b>MaliciousInstrúctör</b>";
        dangerousInstructor.name = "Malicious <script>alert('dangerous');</script>Instrúctör";
        dangerousInstructor.email = "malicious.instr1<>!@gmail.tmt";
        String dangerousInstitute = "TEAMMATES Malicious Institute <!@!@!>";
        String dangerousDemoCourseId = "malicious.instr1___.gma-demo";

        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(dangerousDemoCourseId);
        BackDoor.deleteInstructor(dangerousDemoCourseId, dangerousInstructor.email);

        homePage.createInstructor(shortNameDangerous, dangerousInstructor, dangerousInstitute);

        assertEquals(shortNameDangerous, homePage.getShortNameFromResultTable(1));
        assertEquals(dangerousInstructor.name, homePage.getNameFromResultTable(1));
        assertEquals(dangerousInstructor.email, homePage.getEmailFromResultTable(1));
        assertEquals(dangerousInstitute, homePage.getInstitutionFromResultTable(1));

        ______TS("action success : create instructor account and the account is created successfully "
                 + "after user's verification");

        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);

        homePage.createInstructor(shortName, instructor, institute);

        encryptedKey = getKeyForInstructorWithRetry(demoCourseId, instructor.email);
        // use AppUrl from Config because the join link takes its base URL from build.properties
        expectedjoinUrl = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(encryptedKey)
                                        .withInstructorInstitution(institute)
                                        .toAbsoluteString();

        assertEquals("Instructor AHPUiT Instrúctör WithPlusInEmail has been successfully created with join link:\n"
                     + expectedjoinUrl, homePage.getMessageFromResultTable(1));

        homePage.logout();
        //verify the instructor and the demo course have been created
        assertNotNull(getCourseWithRetry(demoCourseId));
        assertNotNull(getInstructorWithRetry(demoCourseId, instructor.email));

        //get the joinURL which sent to the requester's email
        String regkey = getKeyForInstructorWithRetry(demoCourseId, instructor.email);
        String joinLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(regkey)
                                        .withInstructorInstitution(institute)
                                        .toAbsoluteString();

        //simulate the user's verification here because it is added by admin
        browser.driver.get(joinLink);
        InstructorCourseJoinConfirmationPage confirmationPage =
                AppPage.createCorrectLoginPageType(browser)
                       .loginAsJoiningInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                 TestProperties.TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickCancelButton();

        browser.driver.get(joinLink);
        confirmationPage = AppPage.createCorrectLoginPageType(browser)
                           .loginAsJoiningInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickConfirmButtonWithRetry();

        //check a account has been created for the requester successfully
        assertNotNull(getAccountWithRetry(TestProperties.TEST_INSTRUCTOR_ACCOUNT));

        //verify sample course is accessible for newly joined instructor as an instructor

        ______TS("new instructor can see sample course in homepage");
        InstructorHomePage instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorHomePage.html");

        ______TS("new instructor can access sample coure details page");
        InstructorCourseEnrollPage enrollPage = instructorHomePage.clickCourseEnrollLink(demoCourseId);
        instructorHomePage = enrollPage.goToPreviousPage(InstructorHomePage.class);
        InstructorCourseDetailsPage detailsPage = instructorHomePage.clickCourseViewLink(demoCourseId);
        detailsPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseDetailsPage.html");

        ______TS("new instructor can access sample coure edit page");
        instructorHomePage = detailsPage.goToPreviousPage(InstructorHomePage.class);
        InstructorCourseEditPage editPage = instructorHomePage.clickCourseEditLink(demoCourseId);
        editPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseEditPage.html");

        ______TS("new instructor can view result of First team feedback session of sample course");
        AppUrl url = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
                .withUserId(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        InstructorCoursesPage coursesPage = AppPage.getNewPageInstance(browser, url, InstructorCoursesPage.class);
        coursesPage.waitForAjaxLoadCoursesSuccess();
        coursesPage.loadInstructorHomeTab();
        instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        InstructorFeedbackResultsPage resultsPage =
                instructorHomePage.clickFeedbackSessionViewResultsLink(demoCourseId, "First team feedback session");
        if (resultsPage.isElementPresent("collapse-panels-button")) {
            resultsPage.clickCollapseExpandButton();
            resultsPage.waitForPanelsToExpand();
        }
        resultsPage.verifyHtmlMainContent("/newlyJoinedInstructorFirstFeedbackSessionResultsPage.html");

        ______TS("new instructor can view result of Second team feedback session of sample course");
        coursesPage.loadInstructorHomeTab();
        instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        resultsPage =
                instructorHomePage.clickFeedbackSessionViewResultsLink(demoCourseId, "Second team feedback session");
        if (resultsPage.isElementPresent("collapse-panels-button")) {
            resultsPage.clickCollapseExpandButton();
            resultsPage.waitForPanelsToExpand();
        }
        resultsPage.verifyHtmlMainContent("/newlyJoinedInstructorSecondFeedbackSessionResultsPage.html");

        ______TS("new instructor can view result of Third team feedback session of sample course");
        coursesPage.loadInstructorHomeTab();
        instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        resultsPage = instructorHomePage.clickFeedbackSessionViewResultsLink(
                demoCourseId, "Session with different question types");
        if (resultsPage.isElementPresent("collapse-panels-button")) {
            resultsPage.clickCollapseExpandButton();
            resultsPage.waitForPanelsToExpand();
        }
        resultsPage.verifyHtmlMainContent("/newlyJoinedInstructorThirdFeedbackSessionResultsPage.html");

        //verify the given email is valid or not
        ______TS("action failure : invalid parameter");

        AppUrl homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        homePage = loginAdminToPage(homeUrl, AdminHomePage.class);

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

        StudentHomePage studentHomePage =
                getHomePage().clickStudentLogin().loginAsStudent(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
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
        StudentProfilePage spp = studentHomePage.loadProfileTab();
        spp.verifyContains("Student Profile");
        spp.verifyContains("AHPUiT Instrúctör WithPlusInEmail");

        studentHomePage.logout();

        //login in as instructor again to test sample course deletion
        instructorHomePage =
                getHomePage().clickInstructorLogin().loginAsInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                                       TestProperties.TEST_INSTRUCTOR_PASSWORD);

        instructorHomePage.clickAndConfirm(instructorHomePage.getDeleteCourseLink(demoCourseId));
        assertTrue(instructorHomePage.getStatus().contains("The course " + demoCourseId + " has been deleted."));

        instructorHomePage.logout();

        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);

        BackDoor.deleteAccount(TestProperties.TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(dangerousDemoCourseId);
        BackDoor.deleteInstructor(dangerousDemoCourseId, dangerousInstructor.email);

    }

}
