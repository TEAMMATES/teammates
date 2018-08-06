package teammates.test.cases.browsertests;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.cases.BaseTestCaseWithBackDoorApiAccess;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;

/**
 * Base class for all browser tests.
 *
 * <p>This type of test has no knowledge of the workings of the application,
 * and can only communicate via the UI or via {@link teammates.test.driver.BackDoor} to obtain/transmit data.
 */
public abstract class BaseUiTestCase extends BaseTestCaseWithBackDoorApiAccess {

    protected Browser browser;
    protected DataBundle testData;

    /**
     * Ensure that GodMode is not enabled in CI.
     */
    @BeforeSuite
    public void checkIfGodModeEnabledInCi() {
        if (TestProperties.IS_GODMODE_ENABLED && TestProperties.isCiEnvironment()) {
            fail("GodMode should only be run locally, not in a CI environment. Please revert the change "
                    + "to the test properties template file that enabled GodMode in CI.");
        }
    }

    @BeforeClass
    public void baseClassSetup() throws Exception {
        prepareTestData();
        browser = BrowserPool.getBrowser();
    }

    protected abstract void prepareTestData() throws Exception;

    @AfterClass
    public void baseClassTearDown() {
        releaseBrowser();
    }

    protected void releaseBrowser() {
        BrowserPool.release(browser);
    }

    /**
     * Reminder to disable GodMode and re-run the test(s).
     */
    @AfterSuite
    public static void remindUserToDisableGodModeIfRequired() {
        if (TestProperties.IS_GODMODE_ENABLED) {
            print("=============================================================");
            print("IMPORTANT: Remember to disable GodMode and rerun the test(s)!");
            print("=============================================================");
        }
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the value of test.app.url in test.properties.
     * {@code relativeUrl} must start with a "/".
     */
    protected static AppUrl createUrl(String relativeUrl) {
        return new AppUrl(TestProperties.TEAMMATES_URL + relativeUrl);
    }

    /**
     * Creates a {@link Url} to navigate to the file named {@code testFileName}
     * inside {@link TestProperties#TEST_PAGES_FOLDER}.
     * {@code testFileName} must start with a "/".
     */
    protected static Url createLocalUrl(String testFileName) throws IOException {
        return new Url("file:///" + new File(".").getCanonicalPath() + "/"
                                  + TestProperties.TEST_PAGES_FOLDER + testFileName);
    }

    /**
     * Logs in a page using admin credentials (i.e. in masquerade mode).
     */
    protected <T extends AppPage> T loginAdminToPage(AppUrl url, Class<T> typeOfPage) {

        if (browser.isAdminLoggedIn) {
            browser.driver.get(url.toAbsoluteString());
            try {
                return AppPage.getNewPageInstance(browser, typeOfPage);
            } catch (Exception e) {
                //ignore and try to logout and login again if fail.
                ignorePossibleException();
            }
        }

        //logout and attempt to load the requested URL. This will be
        //  redirected to a dev-server/google login page
        logout();
        browser.driver.get(url.toAbsoluteString());

        String adminUsername = TestProperties.TEST_ADMIN_ACCOUNT;
        String adminPassword = TestProperties.TEST_ADMIN_PASSWORD;

        String instructorId = url.get(Const.ParamsNames.USER_ID);

        if (instructorId == null) { //admin using system as admin
            instructorId = adminUsername;
        }

        //login based on the login page type
        LoginPage loginPage = AppPage.createCorrectLoginPageType(browser);
        loginPage.loginAdminAsInstructor(adminUsername, adminPassword, instructorId);

        //After login, the browser should be redirected to the page requested originally.
        //  No need to reload. In fact, reloading might results in duplicate request to the server.
        return AppPage.getNewPageInstance(browser, typeOfPage);
    }

    /**
     * Logs in a page as an instructor.
     */
    protected <T extends AppPage> T loginInstructorToPage(String instructorGoogleId, String password,
            AppUrl url, Class<T> typeOfPage) {
        //logout
        logout();

        //load the page to be checked
        browser.driver.get(url.toAbsoluteString());

        //login based on the login page type
        LoginPage loginPage = AppPage.createCorrectLoginPageType(browser);
        loginPage.loginAsInstructor(instructorGoogleId, password, typeOfPage);

        //After login, the browser will redirect to the original page requested
        return AppPage.getNewPageInstance(browser, typeOfPage);

    }

    /**
     * Navigates to the application's home page (as defined in test.properties)
     * and gives the {@link HomePage} instance based on it.
     */
    protected HomePage getHomePage() {
        return AppPage.getNewPageInstance(browser, createUrl(""), HomePage.class);
    }

    /**
     * Equivalent to clicking the 'logout' link in the top menu of the page.
     */
    protected void logout() {
        browser.driver.get(createUrl(Const.ActionURIs.LOGOUT).toAbsoluteString());
        AppPage.getNewPageInstance(browser).waitForPageToLoad();
        browser.isAdminLoggedIn = false;
    }

    protected AdminHomePage loginAdmin() {
        return loginAdminToPage(createUrl(Const.ActionURIs.ADMIN_HOME_PAGE), AdminHomePage.class);
    }

}
