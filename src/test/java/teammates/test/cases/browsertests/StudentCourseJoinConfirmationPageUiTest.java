package teammates.test.cases.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.StudentCourseJoinConfirmationPage;
import teammates.test.pageobjects.StudentHomePage;

/**
 * SUT: {@link Const.ActionURIs#STUDENT_COURSE_JOIN},
 *      {@link Const.ActionURIs#STUDENT_COURSE_JOIN_AUTHENTICATED},
 *      {@link Const.ActionURIs#STUDENT_COURSE_JOIN_NEW}.
 */
public class StudentCourseJoinConfirmationPageUiTest extends BaseUiTestCase {
    private StudentCourseJoinConfirmationPage confirmationPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseJoinConfirmationPageUiTest.json");

        // use the 1st student account injected for this test

        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        testData.accounts.get("alice.tmms").googleId = student1GoogleId;
        testData.accounts.get("alice.tmms").email = student1Email;
        testData.students.get("alice.tmms@SCJConfirmationUiT.CS2104").email = student1Email;
        testData.students.get("alice.tmms@SCJConfirmationUiT.CS2103").email = student1Email;
        testData.students.get("alice.tmms@SCJConfirmationUiT.CS1101").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SCJConfirmationUiT.CS1101").email = student1Email;

        removeAndRestoreDataBundle(testData);
    }

    @BeforeClass
    public void classSetup() {
        browser.driver.manage().deleteAllCookies();
        logout();
    }

    @Test
    public void testAll() throws Exception {

        testContent();
        testJoinNewConfirmation();
        // TODO: remove this test by 21/09/2014
        testJoinConfirmation();
    }

    private void testJoinNewConfirmation() throws Exception {
        String expectedMsg;
        String homePageActionUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE).toAbsoluteString();
        String joinLink;
        StudentHomePage studentHomePage;

        ______TS("click join link, skips confirmation and asks for login");

        String courseId = testData.courses.get("SCJConfirmationUiT.CS2104").getId();
        String courseName = testData.courses.get("SCJConfirmationUiT.CS2104").getName();
        String studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2104").email;
        joinLink = createUrl(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                        .withRegistrationKey(getKeyFromBackDoor(courseId, studentEmail))
                        .withCourseId(courseId)
                        .withStudentEmail(studentEmail)
                        .toAbsoluteString();

        browser.driver.get(joinLink);
        studentHomePage = AppPage.createCorrectLoginPageType(browser)
                           .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                                  TestProperties.TEST_STUDENT1_PASSWORD);

        studentHomePage.waitForTextsForAllStatusMessagesToUserEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[" + courseId + "] " + courseName),
                String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[" + courseId + "] " + courseName),
                "Meanwhile, you can update your profile here.");

        ______TS("test student confirmation page content");

        courseId = testData.courses.get("SCJConfirmationUiT.CS2103").getId();
        courseName = testData.courses.get("SCJConfirmationUiT.CS2103").getName();
        studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2103").email;
        joinLink = createUrl(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                        .withRegistrationKey(getKeyFromBackDoor(courseId, studentEmail))
                                        .toAbsoluteString();

        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, StudentCourseJoinConfirmationPage.class);
        // this test uses accounts from test.properties.
        // NOTE: the logout link at the bottom of the page has to be changed to {*}
        //       since the link is different in dev and staging servers

        // This is the full HTML verification for Student Course Join Confirmation Page, the rest can all be verifyMainHtml
        confirmationPage.verifyHtml("/studentCourseJoinConfirmationHTML.html");

        ______TS("Cancelling goes to login page");
        confirmationPage.clickCancelButton();

        ______TS("Confirming goes to home page");
        browser.driver.get(homePageActionUrl);
        studentHomePage = AppPage.createCorrectLoginPageType(browser)
                            .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                       TestProperties.TEST_STUDENT1_PASSWORD);
        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();
        studentHomePage = AppPage.getNewPageInstance(browser, StudentHomePage.class);
        studentHomePage.waitForTextsForAllStatusMessagesToUserEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[" + courseId + "] " + courseName),
                String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[" + courseId + "] " + courseName),
                "Meanwhile, you can update your profile here.");

        ______TS("already joined, no confirmation page");

        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();

        studentHomePage = AppPage.getNewPageInstance(browser, StudentHomePage.class);
        expectedMsg = "You (" + TestProperties.TEST_STUDENT1_ACCOUNT + ") have already joined this course";
        studentHomePage.waitForTextsForAllStatusMessagesToUserEquals(expectedMsg);

        assertTrue(browser.driver.getCurrentUrl().contains(Const.ParamsNames.ERROR + "=true"));
        studentHomePage.logout();
    }

    private void testContent() {

        /*covered in testJoinConfirmation()
         *case: click join link then confirm: success: valid key
         */
    }

    private void testJoinConfirmation() throws Exception {
        logout();
        removeAndRestoreDataBundle(testData);
        String expectedMsg;
        String homePageActionUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE).toAbsoluteString();
        String joinLink;
        StudentHomePage studentHomePage;

        ______TS("click join link, skips confirmation and asks for login");

        String courseId = testData.courses.get("SCJConfirmationUiT.CS2104").getId();
        String courseName = testData.courses.get("SCJConfirmationUiT.CS2104").getName();
        String studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2104").email;
        joinLink = createUrl(Const.ActionURIs.STUDENT_COURSE_JOIN)
                                        .withRegistrationKey(getKeyFromBackDoor(courseId, studentEmail))
                                        .toAbsoluteString();

        browser.driver.get(joinLink);
        studentHomePage = AppPage.createCorrectLoginPageType(browser)
                           .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                                  TestProperties.TEST_STUDENT1_PASSWORD);
        studentHomePage.waitForTextsForAllStatusMessagesToUserEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[" + courseId + "] " + courseName),
                String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[" + courseId + "] " + courseName),
                "Meanwhile, you can update your profile here.");

        ______TS("test student confirmation page content");

        courseId = testData.courses.get("SCJConfirmationUiT.CS2103").getId();
        courseName = testData.courses.get("SCJConfirmationUiT.CS2103").getName();
        studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2103").email;
        joinLink = createUrl(Const.ActionURIs.STUDENT_COURSE_JOIN)
                                        .withRegistrationKey(getKeyFromBackDoor(courseId, studentEmail))
                                        .toAbsoluteString();

        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, StudentCourseJoinConfirmationPage.class);
        // this test uses accounts from test.properties

        // This is also a HTML verification for Student Course Join Confirmation Page because they use the
        // same html file for verification
        confirmationPage.verifyHtml("/studentCourseJoinConfirmationHTML.html");

        ______TS("Cancelling goes to login page");
        confirmationPage.clickCancelButton();

        ______TS("Confirming goes to home page");
        browser.driver.get(homePageActionUrl);
        studentHomePage = AppPage.createCorrectLoginPageType(browser)
                            .loginAsStudent(TestProperties.TEST_STUDENT1_ACCOUNT,
                                       TestProperties.TEST_STUDENT1_PASSWORD);
        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();
        studentHomePage = AppPage.getNewPageInstance(browser, StudentHomePage.class);
        studentHomePage.waitForTextsForAllStatusMessagesToUserEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[" + courseId + "] " + courseName),
                String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[" + courseId + "] " + courseName),
                "Meanwhile, you can update your profile here.");

        ______TS("already joined, no confirmation page");

        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();

        studentHomePage = AppPage.getNewPageInstance(browser, StudentHomePage.class);
        expectedMsg = "You (" + TestProperties.TEST_STUDENT1_ACCOUNT + ") have already joined this course";
        studentHomePage.waitForTextsForAllStatusMessagesToUserEquals(expectedMsg);

        assertTrue(browser.driver.getCurrentUrl().contains(Const.ParamsNames.ERROR + "=true"));
    }

    @AfterClass
    public void classTearDown() {
        BackDoor.removeDataBundle(testData);
    }

    // continuously ask BackDoor to get the key until a legit key is returned
    private String getKeyFromBackDoor(String courseId, String studentEmail) {
        int numberOfRemainingRetries = 10;
        String key = "[BACKDOOR_STATUS_FAILURE]";
        while (key.startsWith("[BACKDOOR_STATUS_FAILURE]") && numberOfRemainingRetries > 0) {
            key = BackDoor.getEncryptedKeyForStudent(courseId, studentEmail);
            numberOfRemainingRetries--;
            ThreadHelper.waitFor(100);
        }
        return key;
    }

}
