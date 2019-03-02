package teammates.e2e.cases.e2e;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_HOME_PAGE}
 */
public class StudentHomePageE2ETest extends BaseE2ETestCase {
    private StudentHomePage studentHome;

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

        removeAndRestoreDataBundle(testData);
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
        BackDoor.deleteStudent(unregUserId);

        logout();
        studentHome = getHomePage().clickStudentLogin().loginAsStudent(unregUserId, unregPassword);

        browser.waitForPageLoad();
        assertTrue(studentHome.verifyStrangerMessage());

        ______TS("login successfully");

        logout();
        studentHome = getHomePage().clickStudentLogin()
                                   .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                                   TestProperties.TEST_STUDENT1_PASSWORD);

        browser.waitForPageLoad();
        assertTrue(studentHome.verifyFirstPanelFeedbackSessionName("[SHomeUiT.CS1101]: Programming Methodology"));

        ______TS("login successfully as admin (masquerade mode)");

        AppUrl detailsPageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(detailsPageUrl, StudentHomePage.class);
        browser.waitForPageLoad();

        assertTrue(studentHomePage.verifyAdminPagePresent());
    }

    private void testLinks() {

        AppUrl homePageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(homePageUrl, StudentHomePage.class);

        ______TS("link: view team link");

        studentHomePage.clickViewTeam();

        ______TS("link: help page");

        studentHomePage.loadStudentHelpTab();

        ______TS("link: student profile page");

        studentHomePage.loadStudentProfileTab();

        ______TS("link: student home page");

        studentHomePage.loadStudentHomeTab();

        ______TS("link: log out to home page");

        studentHomePage.clickLogout();
    }

    private void testLinkAndContentAfterDelete() throws Exception {

        AppUrl detailsPageUrl = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                             .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(detailsPageUrl, StudentHomePage.class);

        ______TS("access the feedback session exactly after it is deleted");

        BackDoor.deleteFeedbackSession("First Feedback Session", "SHomeUiT.CS2104");
        studentHomePage.clickSubmitFeedbackButton("First Feedback Session");
        studentHomePage.waitForPageToLoad();

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
        BackDoor.removeDataBundle(testData);
    }

}
