package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.NotFoundPage;
import teammates.test.util.Priority;

/**
 * We do not test all access control at UI level. This class contains a few
 * representative tests only. Access control is tested fully at 'Action' level.
 */
@Priority(6)
public class AllAccessControlUiTests extends BaseUiTestCase {
    
    private static String unregUsername = TestProperties.inst().TEST_UNREG_ACCOUNT;
    private static String unregPassword = TestProperties.inst().TEST_UNREG_PASSWORD;

    private static String studentUsername = TestProperties.inst().TEST_STUDENT1_ACCOUNT;
    private static String studentPassword = TestProperties.inst().TEST_STUDENT1_PASSWORD;
    
    private static String instructorUsername = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
    private static String instructorPassword = TestProperties.inst().TEST_INSTRUCTOR_PASSWORD;

    static String adminUsername = TestProperties.inst().TEST_ADMIN_ACCOUNT;

    private static Browser browser;
    private static DataBundle testData;
    private static AppPage currentPage;
    private static String link;

    private static InstructorAttributes otherInstructor;

    // both TEST_INSTRUCTOR and TEST_STUDENT are from this course
    @SuppressWarnings("unused")
    private static CourseAttributes ownCourse;
    
    @BeforeClass
    public static void classSetup() {

        printTestClassHeader();

        testData = loadDataBundle("/AllAccessControlUiTest.json");
        
        otherInstructor = testData.instructors.get("instructor1OfCourse2");
        ownCourse = testData.courses.get("typicalCourse1");

        browser = BrowserPool.getBrowser();
        
        currentPage = HomePage.getNewInstance(browser);
        
        restoreSpecialTestData();
    }
    
    @Test
    public void testUserNotLoggedIn() throws Exception {
        
        currentPage.logout().verifyHtml("/login.html");

        ______TS("student pages");

        verifyRedirectToLogin(Const.ActionURIs.STUDENT_HOME_PAGE);
        

        ______TS("instructor pages");

        verifyRedirectToLogin(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        

        ______TS("admin pages");

        verifyRedirectToLogin(Const.ActionURIs.ADMIN_HOME_PAGE);
        
        
    }

    @Test
    public void testUserNotRegistered() throws Exception {
        
        ______TS("student pages");

        loginStudent(unregUsername, unregPassword);

        verifyRedirectToWelcomeStrangerPage(Const.ActionURIs.STUDENT_HOME_PAGE, unregUsername);


        ______TS("instructor pages");

        loginInstructorUnsuccessfully(unregUsername, unregPassword);

        Url url = createUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        verifyRedirectToNotAuthorized(url);
        verifyCannotMasquerade(url, otherInstructor.googleId);


        ______TS("admin pages");
        
        //cannot access admin while logged in as student
        verifyCannotAccessAdminPages();
        
        ______TS("incorrect URL");
        
        Url nonExistentActionUrl = new Url(TestProperties.inst().TEAMMATES_URL + "/page/nonExistentAction");
        @SuppressWarnings("unused") //We simply ensures it is the right page type
        NotFoundPage notFoundPage = AppPage.getNewPageInstance(browser, nonExistentActionUrl, NotFoundPage.class);

    }

    @Test
    public void testStudentAccessToAdminPages() throws Exception {
        loginStudent(studentUsername, studentPassword);
        verifyCannotAccessAdminPages();
    }

    @Test
    public void testStudentHome() {
        loginStudent(studentUsername, studentPassword);
        
        ______TS("cannot view other homepage");
        
        link = Const.ActionURIs.STUDENT_HOME_PAGE;
        verifyCannotMasquerade(link, otherInstructor.googleId);
    }
    
    @Test
    public void testInstructorHome() {
    
        loginInstructor(instructorUsername, instructorPassword);
    
        ______TS("cannot view other homepage");
    
        link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        verifyCannotMasquerade(link, otherInstructor.googleId);
    }
    
    @Test
    public void testPubliclyAccessiblePages() {
        
        ______TS("log out page");
        // has been covered in testUserNotLoggedIn method
        
        ______TS("unauthorized page");
        Url url = createUrl(Const.ViewURIs.UNAUTHORIZED);
        currentPage.navigateTo(url);
        verifyRedirectToNotAuthorized();
        
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
        
        ______TS("show message page");
        url = createUrl(Const.ViewURIs.SHOW_MESSAGE);
        currentPage.navigateTo(url);
        currentPage.verifyHtml("/showMessage.html");
    }
    

    private void loginStudent(String userName, String password) {
        currentPage.logout();
        LoginPage loginPage = HomePage.getNewInstance(browser).clickStudentLogin();
        currentPage = loginPage.loginAsStudent(userName, password);
    }
    
    private void loginInstructorUnsuccessfully(String userName, String password) {
        currentPage.logout();
        LoginPage loginPage = HomePage.getNewInstance(browser).clickInstructorLogin();
        currentPage = loginPage.loginAsInstructorUnsuccessfully(userName, password);
    }
    
    private void loginInstructor(String userName, String password) {
        currentPage.logout();
        LoginPage loginPage = HomePage.getNewInstance(browser).clickInstructorLogin();
        currentPage = loginPage.loginAsInstructor(userName, password);
    }
    
    private static void restoreSpecialTestData() {
        
        testData = loadDataBundle("/AllAccessControlUiTest.json");
        
        // This test suite requires some real accounts; Here, we inject them to the test data.
        testData.students.get("student1InCourse1.access").googleId = TestProperties.inst().TEST_STUDENT1_ACCOUNT;
        testData.instructors.get("instructor1OfCourse1").googleId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
        
        removeAndRestoreTestDataOnServer(testData);
    }

    private void verifyCannotAccessAdminPages() {
        //cannot access directly
        Url url = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        verifyRedirectToNotAuthorized(url);
        //cannot access by masquerading either
        url = url.withUserId(adminUsername);
        verifyRedirectToNotAuthorized(url);
    }

    private void verifyCannotMasquerade(String link, String otherInstructorId) {
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, otherInstructorId);
        verifyRedirectToNotAuthorized(link);
    }
    
    private void verifyCannotMasquerade(Url url, String otherInstructorId) {
        verifyRedirectToNotAuthorized(url.withUserId(otherInstructorId));
    }

    private void verifyRedirectToWelcomeStrangerPage(String path, String unregUsername) {
        printUrl(appUrl + path);
        currentPage.navigateTo(createUrl(path));
        // A simple regex check is enough because we do full HTML tests
        // elsewhere
        AssertHelper.assertContainsRegex("{*}" + unregUsername + "{*}Welcome stranger{*}",
                currentPage.getPageSource());
    }

    private void verifyRedirectToNotAuthorized() {
        String pageSource = currentPage.getPageSource();
        //TODO: Distinguish between these two types of access denial
        assertTrue(pageSource.contains("You are not authorized to view this page.")||
                pageSource.contains("Your client does not have permission"));
    }

    private void verifyRedirectToNotAuthorized(String path) {
        printUrl(appUrl + path);
        currentPage.navigateTo(createUrl(path));
        verifyRedirectToNotAuthorized();
    }
    
    private void verifyRedirectToNotAuthorized(Url url) {
        printUrl(url.toString());
        currentPage.navigateTo(url);
        verifyRedirectToNotAuthorized();
    }

    private void verifyRedirectToLogin(String path) {
        printUrl(appUrl + path);
        currentPage.navigateTo(createUrl(path));
        assertTrue(isLoginPage(currentPage));
    }

    private boolean isLoginPage(AppPage currentPage) {
        return GoogleLoginPage.containsExpectedPageContents(currentPage.getPageSource())
                || DevServerLoginPage.containsExpectedPageContents(currentPage.getPageSource());
    }

    private void printUrl(String url) {
        print("   " + url);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        //delete any data related to real accounts used in testing (to prevent state leakage to other tests)
        testData = loadDataBundle("/AllAccessControlUiTest.json");
        
        // This test suite requires some real accounts; Here, we inject them to the test data.
        testData.students.get("student1InCourse1.access").googleId = TestProperties.inst().TEST_STUDENT1_ACCOUNT;
        testData.instructors.get("instructor1OfCourse1").googleId = TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT;
        removeTestDataOnServer(testData);
        BrowserPool.release(browser);
    }
}
