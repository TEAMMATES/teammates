package teammates.e2e.cases.e2e;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.ErrorReportingModal;
import teammates.e2e.pageobjects.StudentHomePage;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class StudentCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
    
	private StudentAttributes newStudent;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle(Const.TestCase.STUDENT_COURSE_JOIN_CONFIRMATION_PAGE_E2E_TEST_JSON);
        removeAndRestoreDataBundle(testData);

        newStudent = testData.students.get(Const.TestCase.ALICE_TMMS_SCJ_CONFIRMATION_E2E_T_CS2104);
        newStudent.googleId = testData.accounts.get(Const.TestCase.ALICE_TMMS).googleId;
    }

    @BeforeClass
    public void classSetup() {
        browser.driver.manage().deleteAllCookies();
    }

    @Test
    public void testAll() {
        ______TS(Const.TestCase.CLICK_JOIN_LINK_INVALID_KEY);
        String courseId = testData.courses.get(Const.TestCase.SCJ_CONFIRMATION_E2E_T_CS2104).getId();
        String invalidEncryptedKey = Const.TestCase.INVALID_KEY;
        AppUrl joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(invalidEncryptedKey)
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT)
                .withUserId(newStudent.googleId);
        ErrorReportingModal errorPage = loginAdminToPage(joinLink, ErrorReportingModal.class);

        errorPage.verifyErrorMessage(Const.TestCase.NO_STUDENT_WITH_GIVEN_REGISTRATION_KEY + invalidEncryptedKey);

        ______TS(Const.TestCase.CLICK_JOIN_LINK_VALID_KEY);
        joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForStudent(newStudent))
                .withCourseId(courseId)
                .withEntityType(Const.EntityType.STUDENT)
                .withUserId(newStudent.googleId);
        CourseJoinConfirmationPage confirmationPage = loginAdminToPage(joinLink, CourseJoinConfirmationPage.class);

        confirmationPage.verifyJoiningUser(newStudent.googleId);
        confirmationPage.confirmJoinCourse(StudentHomePage.class);

        ______TS(Const.TestCase.ALREADY_JOINED_NO_CONFIRMATION_PAGE);
        browser.driver.get(joinLink.toAbsoluteString());
        AppPage.getNewPageInstance(browser, StudentHomePage.class);
    }
}
