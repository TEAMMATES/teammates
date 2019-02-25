package teammates.e2e.cases.e2e;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.HomePageNew;
import teammates.e2e.pageobjects.StudentHelpPageNew;
import teammates.e2e.pageobjects.StudentHomePageNew;
import teammates.e2e.util.NewBackDoor;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.ActionURIs#STUDENT_HOME_PAGE}.
 */
public class StudentHomePageE2ETest extends BaseE2ETestCase {
    private StudentHomePageNew studentHome;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentHomePageE2ETest.json");

        // use the 1st student account injected for this test
        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        testData.accounts.get("alice.tmms").googleId = student1GoogleId;
        testData.accounts.get("alice.tmms").email = student1Email;

        // This student's name is Amy Betsy and has a registered account but yet to join course
        testData.students.get("alice.tmms@SHomeUiT.CS2104").email = student1Email;

        testData.students.get("alice.tmms@SHomeUiT.CS1101").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS1101").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").email = student1Email;

        // TODO: Do I handle this using backdoor operations?
        removeAndRestoreDataBundle(testData);

        // TODO: what should the return type be for this case?
        String gracedFeedbackSession =
                NewBackDoor.getFeedbackSession("SHomeUiT.CS2104", "Graced Feedback Session");

        // gracedFeedbackSession.setEndTime(Instant.now());
        // NewBackDoor.editFeedbackSession(gracedFeedbackSession);
    }

    @Test
    public void allTests() throws Exception {
        testContentAndLogin();
        testLinks();
        testLinkAndContentAfterDelete();
    }

    private void testContentAndLogin() throws Exception {

        ______TS("content: no courses, 'welcome stranger' message");

        String unregUserId = TestProperties.TEST_UNREG_ACCOUNT;
        String unregPassword = TestProperties.TEST_UNREG_PASSWORD;
        NewBackDoor.deleteAccount(unregUserId);

        logout();
        studentHome = getHomePageNew().clickStudentLogin().loginAsStudent(unregUserId, unregPassword);

        // this test uses the accounts from test.properties
        // do not do full HTML verification here as the unregistered username is not predictable
        studentHome.verifyHtmlMainContent("/studentHomeHTMLEmpty.html");

        ______TS("persistence check");

        loginWithPersistenceProblem();

        // This is the full HTML verification for Student Home Page, the rest can all be verifyMainHtml
        studentHome.verifyHtml("/studentHomeHTMLPersistenceCheck.html");


        ______TS("login");

        studentHome = getHomePageNew().clickStudentLogin()
                                   .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                                   TestProperties.TEST_STUDENT1_PASSWORD);

        ______TS("content: multiple courses");

        // this test uses the accounts from test.properties
        studentHome.verifyHtmlMainContent("/studentHomeHTML.html");

        AppUrl detailsPageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                             .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePageNew studentHomePage = loginAdminToPageNew(detailsPageUrl, StudentHomePageNew.class);

        studentHomePage.verifyHtmlMainContent("/studentHomeTypicalHTML.html");

        ______TS("content: requires sanitization");

        detailsPageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                            .withUserId(testData.students.get("SHomeUiT.student1InTestingSanitizationCourse").googleId);

        studentHomePage = loginAdminToPageNew(detailsPageUrl, StudentHomePageNew.class);

        studentHomePage.verifyHtmlMainContent("/studentHomeTypicalTestingSanitization.html");

    }

    private void testLinks() {

        AppUrl homePageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePageNew studentHomePageNew = loginAdminToPageNew(homePageUrl, StudentHomePageNew.class);

        ______TS("link: help page");

        StudentHelpPageNew helpPage = studentHomePageNew.loadStudentHelpTab();
        helpPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view team link");

        studentHomePageNew.clickViewTeam();

        AppUrl detailsPageUrl = createUrl(Const.WebPageURIs.STUDENT_COURSE_DETAILS_PAGE)
                .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS1101").googleId)
                .withCourseId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS1101").course);
        assertEquals(detailsPageUrl.toAbsoluteString(), browser.driver.getCurrentUrl());
        studentHomePageNew.loadStudentHomeTab();

        ______TS("link: link of published feedback");

        studentHomePageNew.clickViewFeedbackButton("Closed Feedback Session");
        studentHomePageNew.reloadPage();
        String pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Feedback Results"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Closed Feedback Session"));
        studentHomePageNew.loadStudentHomeTab();

        studentHomePageNew.clickSubmitFeedbackButton("Closed Feedback Session");
        studentHomePageNew.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Submit Feedback"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Closed Feedback Session"));
        assertTrue(pageSource.contains(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN));
        studentHomePageNew.loadStudentHomeTab();

        ______TS("link: link of Grace period feedback");

        assertTrue(studentHomePageNew.getViewFeedbackButton("Graced Feedback Session")
                .getAttribute("class").contains("disabled"));

        studentHomePageNew.clickSubmitFeedbackButton("Graced Feedback Session");
        studentHomePageNew.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Submit Feedback"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Graced Feedback Session"));
        assertTrue(pageSource.contains(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN));
        studentHomePageNew.loadStudentHomeTab();

        ______TS("link: link of pending feedback");

        assertTrue(studentHomePageNew.getViewFeedbackButton("First Feedback Session")
                .getAttribute("class").contains("disabled"));

        studentHomePageNew.clickSubmitFeedbackButton("First Feedback Session");
        studentHomePageNew.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Submit Feedback"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("First Feedback Session"));
        studentHomePageNew.loadStudentHomeTab();
    }

    private void testLinkAndContentAfterDelete() throws Exception {

        AppUrl detailsPageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                             .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePageNew studentHomePageNew = loginAdminToPageNew(detailsPageUrl, StudentHomePageNew.class);

        ______TS("access the feedback session exactly after it is deleted");

        NewBackDoor.deleteFeedbackSession("First Feedback Session", "SHomeUiT.CS2104");
        studentHomePageNew.clickSubmitFeedbackButton("First Feedback Session");
        studentHomePageNew.waitForPageToLoad();
        studentHomePageNew.verifyHtmlMainContent("/studentHomeFeedbackDeletedHTML.html");

    }

    private void loginWithPersistenceProblem() {
        AppUrl homeUrl = ((AppUrl) createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                    .withParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "SHomeUiT.CS2104"))
                    .withUserId("unreg_user");
        // http://localhost:4200/web/admin/home?user=abc@example.com // does not work despite entering the details
        studentHome = loginAdminToPageNew(homeUrl, StudentHomePageNew.class);

    }

    @AfterClass
    public void classTearDown() {
        // The courses of test student1 account is not the same as `StudentProfilePageUiTest`
        // so it has to be cleared before running that test. This is the reason why `StudentProfilePageUiTest`
        // would fail if we don't remove the data bundle in this test.

        // Since the account of user being logged in is shared in this test and `StudentProfilePageUiTest`,
        // we need to explicitly remove the data bundle of tests.
        // The test data needs to be removed for both `StudentHomePageUiTest` and `StudentProfilePageUiTest`
        // as the tests can run in any order.

        // See `BackDoor#removeAndRestoreDataBundle(DataBundle))` for more details.
        // NewBackDoor.removeDataBundle(testData);
    }
}