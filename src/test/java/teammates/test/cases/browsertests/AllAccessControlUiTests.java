package teammates.test.cases.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.NotAuthorizedPage;
import teammates.test.pageobjects.NotFoundPage;
import teammates.test.pageobjects.UserErrorReportPage;

/**
 * We do not test all access control at UI level. This class contains a few
 * representative tests only. Access control is tested fully at 'Action' level.
 */
@Priority(6)
public class AllAccessControlUiTests extends BaseUiTestCase {

    private AppPage currentPage;

    private InstructorAttributes otherInstructor;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AllAccessControlUiTest.json");

        // This test suite requires some real accounts; Here, we inject them to the test data.
        testData.students.get("student1InCourse1.access").googleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        testData.instructors.get("instructor1OfCourse1").googleId = TestProperties.TEST_INSTRUCTOR_ACCOUNT;

        removeAndRestoreDataBundle(testData);

        otherInstructor = testData.instructors.get("instructor1OfCourse2");
    }

    @BeforeClass
    public void classSetup() {
        currentPage = getHomePage();
    }

    @Test
    public void testUserNotLoggedIn() {

        logout();
        AppPage.getNewPageInstance(browser, HomePage.class);

        ______TS("student pages");

        verifyRedirectToLogin(createUrl(Const.ActionURIs.STUDENT_HOME_PAGE));

        ______TS("instructor pages");

        verifyRedirectToLogin(createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));

        ______TS("admin pages");

        verifyRedirectToLogin(createUrl(Const.ActionURIs.ADMIN_HOME_PAGE));

    }

    @Test
    public void testUserNotRegistered() {

        ______TS("student pages");

        loginStudent(TestProperties.TEST_UNREG_ACCOUNT, TestProperties.TEST_UNREG_PASSWORD);

        verifyRedirectToWelcomeStrangerPage(createUrl(Const.ActionURIs.STUDENT_HOME_PAGE),
                                            TestProperties.TEST_UNREG_ACCOUNT);

        ______TS("instructor pages");

        loginInstructorUnsuccessfully(TestProperties.TEST_UNREG_ACCOUNT, TestProperties.TEST_UNREG_PASSWORD);

        AppUrl url = createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        verifyRedirectToNotAuthorized(url);
        verifyCannotMasquerade(url, otherInstructor.googleId);

        ______TS("admin pages");

        //cannot access admin while logged in as student
        verifyCannotAccessAdminPages();

        ______TS("incorrect URL");

        AppUrl nonExistentActionUrl = createUrl("/page/nonExistentAction");
        AppPage.getNewPageInstance(browser, nonExistentActionUrl, NotFoundPage.class);

    }

    @Test
    public void testStudentAccessToAdminPages() {
        loginStudent(TestProperties.TEST_STUDENT1_ACCOUNT, TestProperties.TEST_STUDENT1_PASSWORD);
        verifyCannotAccessAdminPages();
    }

    @Test
    public void testStudentHome() {
        loginStudent(TestProperties.TEST_STUDENT1_ACCOUNT, TestProperties.TEST_STUDENT1_PASSWORD);

        ______TS("cannot view other homepage");

        verifyCannotMasquerade(createUrl(Const.ActionURIs.STUDENT_HOME_PAGE), otherInstructor.googleId);
    }

    @Test
    public void testInstructorHome() {

        loginInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT, TestProperties.TEST_INSTRUCTOR_PASSWORD);

        ______TS("cannot view other homepage");

        verifyCannotMasquerade(createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE), otherInstructor.googleId);
    }

    @Test
    public void testPubliclyAccessiblePages() throws Exception {

        ______TS("log out page");
        // has been covered in testUserNotLoggedIn method

        ______TS("unauthorized page");
        AppUrl url = createUrl(Const.ViewURIs.UNAUTHORIZED);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/unauthorized.html");

        ______TS("error page");
        url = createUrl(Const.ViewURIs.ERROR_PAGE);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/errorPage.html");

        ______TS("deadline exceeded error page");
        url = createUrl(Const.ViewURIs.DEADLINE_EXCEEDED_ERROR_PAGE);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/deadlineExceededErrorPage.html");

        ______TS("entity not found page");
        url = createUrl(Const.ViewURIs.ENTITY_NOT_FOUND_PAGE);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/entityNotFoundPage.html");

        ______TS("action not found page");
        url = createUrl(Const.ViewURIs.ACTION_NOT_FOUND_PAGE);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/pageNotFound.html");

        ______TS("enable javascript page");
        url = createUrl(Const.ViewURIs.ENABLE_JS);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/enableJs.html");

        ______TS("user error report form - submit successfully");
        url = createUrl(Const.ViewURIs.ERROR_PAGE);
        currentPage.navigateTo(url);
        UserErrorReportPage errorReportPage = currentPage.changePageType(UserErrorReportPage.class);
        errorReportPage.verifyErrorReportFormContents();
        errorReportPage.fillFormAndClickSubmit("This is an error report.");
        errorReportPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.ERROR_FEEDBACK_SUBMIT_SUCCESS);

        ______TS("user error report form - submit failed");
        logout();
        url = createUrl(Const.ViewURIs.ERROR_PAGE);
        errorReportPage.navigateTo(url);
        errorReportPage.verifyErrorReportFormContents();
        errorReportPage.fillFormAndClickSubmit("This is an error report.");
        final String failedStatusMessage = "Failed to record the error message. Please email our support team at "
                + Config.SUPPORT_EMAIL + ".";
        errorReportPage.waitForTextsForAllStatusMessagesToUserEquals(failedStatusMessage);
    }

    private void loginStudent(String userName, String password) {
        logout();
        LoginPage loginPage = getHomePage().clickStudentLogin();
        currentPage = loginPage.loginAsStudent(userName, password);
    }

    private void loginInstructorUnsuccessfully(String userName, String password) {
        logout();
        LoginPage loginPage = getHomePage().clickInstructorLogin();
        currentPage = loginPage.loginAsInstructorUnsuccessfully(userName, password);
    }

    private void loginInstructor(String userName, String password) {
        logout();
        LoginPage loginPage = getHomePage().clickInstructorLogin();
        currentPage = loginPage.loginAsInstructor(userName, password);
    }

    private void verifyCannotAccessAdminPages() {
        //cannot access directly
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        verifyRedirectToForbidden(url);
        //cannot access by masquerading either
        url = url.withUserId(TestProperties.TEST_ADMIN_ACCOUNT);
        verifyRedirectToForbidden(url);
    }

    private void verifyCannotMasquerade(AppUrl url, String otherInstructorId) {
        AppUrl masqueradeUrl = url.withUserId(otherInstructorId);
        verifyRedirectToNotAuthorized(masqueradeUrl);
    }

    private void verifyRedirectToWelcomeStrangerPage(AppUrl url, String unregUsername) {
        printUrl(url.toAbsoluteString());
        currentPage.navigateTo(url);
        // A simple regex check is enough because we do full HTML tests
        // elsewhere
        AssertHelper.assertContainsRegex(
                unregUsername + "{*}Ooops! Your Google account is not known to TEAMMATES",
                currentPage.getPageSource());
    }

    private void verifyRedirectToForbidden(AppUrl url) {
        if (TestProperties.isDevServer()) {
            verifyRedirectToNotAuthorized(url);
        } else {
            printUrl(url.toAbsoluteString());
            currentPage.navigateTo(url);
            assertTrue(currentPage.getPageSource().contains("Your client does not have permission"));
        }
    }

    private void verifyRedirectToNotAuthorized(AppUrl url) {
        printUrl(url.toAbsoluteString());
        currentPage.navigateTo(url);
        currentPage.changePageType(NotAuthorizedPage.class);
    }

    private void verifyRedirectToLogin(AppUrl url) {
        printUrl(url.toAbsoluteString());
        currentPage.navigateTo(url);
        AppPage.createCorrectLoginPageType(browser);
    }

    private void printUrl(String url) {
        print("   " + url);
    }

    @AfterClass
    public void classTearDown() {
        BackDoor.removeDataBundle(testData);
    }
}
