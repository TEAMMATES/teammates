package teammates.test.cases.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.util.Priority;
import teammates.e2e.util.TestProperties;
import teammates.test.AssertHelper;
import teammates.test.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;

/**
 * We do not test all access control at UI level. This class contains a few
 * representative tests only. Access control is tested fully at 'Action' level.
 */
@Priority(6)
public class AllAccessControlUiTests extends BaseLegacyUiTestCase {

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
        currentPage = null; // getHomePage();
    }

    @Test
    public void testUserNotLoggedIn() {

        logout();
        AppPage.getNewPageInstance(browser, HomePage.class);

        ______TS("student pages");

        verifyRedirectToLogin(createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE));

        ______TS("instructor pages");

        verifyRedirectToLogin(createUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE));

        ______TS("admin pages");

        verifyRedirectToLogin(createUrl(Const.WebPageURIs.ADMIN_HOME_PAGE));

    }

    @Test
    public void testUserNotRegistered() {

        ______TS("student pages");

        loginStudent(TestProperties.TEST_UNREG_ACCOUNT, TestProperties.TEST_UNREG_PASSWORD);

        verifyRedirectToWelcomeStrangerPage(createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE),
                                            TestProperties.TEST_UNREG_ACCOUNT);

        ______TS("instructor pages");

        loginInstructorUnsuccessfully(TestProperties.TEST_UNREG_ACCOUNT, TestProperties.TEST_UNREG_PASSWORD);

        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE);
        verifyRedirectToNotAuthorized(url);
        verifyCannotMasquerade(url, otherInstructor.googleId);

        ______TS("admin pages");

        //cannot access admin while logged in as student
        verifyCannotAccessAdminPages();

        ______TS("incorrect URL");

        // AppUrl nonExistentActionUrl = createUrl("/page/nonExistentAction");
        // AppPage.getNewPageInstance(browser, nonExistentActionUrl, NotFoundPage.class);

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

        verifyCannotMasquerade(createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE), otherInstructor.googleId);
    }

    @Test
    public void testInstructorHome() {

        loginInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT, TestProperties.TEST_INSTRUCTOR_PASSWORD);

        ______TS("cannot view other homepage");

        verifyCannotMasquerade(createUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE), otherInstructor.googleId);
    }

    private void loginStudent(String userName, String password) {
        logout();
        LoginPage loginPage = null; // getHomePage().clickStudentLogin();
        currentPage = loginPage.loginAsStudent(userName, password);
    }

    private void loginInstructorUnsuccessfully(String userName, String password) {
        logout();
        LoginPage loginPage = null; // getHomePage().clickInstructorLogin();
        currentPage = loginPage.loginAsInstructorUnsuccessfully(userName, password);
    }

    private void loginInstructor(String userName, String password) {
        logout();
        LoginPage loginPage = null; // getHomePage().clickInstructorLogin();
        currentPage = loginPage.loginAsInstructor(userName, password);
    }

    private void verifyCannotAccessAdminPages() {
        //cannot access directly
        AppUrl url = createUrl(Const.WebPageURIs.ADMIN_HOME_PAGE);
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
        // currentPage.changePageType(NotAuthorizedPage.class);
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
