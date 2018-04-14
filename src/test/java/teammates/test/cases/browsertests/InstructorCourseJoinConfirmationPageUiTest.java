package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.InstructorCourseJoinConfirmationPage;
import teammates.test.pageobjects.InstructorHomePage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_JOIN}.
 */
public class InstructorCourseJoinConfirmationPageUiTest extends BaseUiTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseJoinConfirmationPageUiTest.json");

        // use the instructor account injected for this test

        testData.instructors.get("ICJConfirmationUiT.instr.CS2104").googleId =
                                        TestProperties.TEST_INSTRUCTOR_ACCOUNT;
        testData.instructors.get("ICJConfirmationUiT.instr.CS2104").email =
                                        TestProperties.TEST_INSTRUCTOR_ACCOUNT + "@gmail.com";
        testData.instructors.get("ICJConfirmationUiT.instr.CS1101").email =
                                        TestProperties.TEST_INSTRUCTOR_ACCOUNT + "@gmail.com";

        removeAndRestoreDataBundle(testData);
    }

    @BeforeClass
    public void classSetup() {
        browser.driver.manage().deleteAllCookies();
    }

    @Test
    public void testAll() throws Exception {

        testContent();
        testJoinConfirmation();
    }

    private void testContent() {

        /*covered in testJoinConfirmation()
         *case: Click join link then confirm: success: valid key
         */
    }

    private void testJoinConfirmation() throws Exception {

        String invalidEncryptedKey = StringHelper.encrypt("invalidKey");

        ______TS("Click join link then cancel");

        String joinLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(invalidEncryptedKey)
                                        .toAbsoluteString();
        logout();
        browser.driver.get(joinLink);

        InstructorCourseJoinConfirmationPage confirmationPage =
                AppPage.createCorrectLoginPageType(browser)
                           .loginAsJoiningInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.TEST_INSTRUCTOR_PASSWORD);

        confirmationPage.clickCancelButton();

        ______TS("Click join link then confirm: fail: invalid key");

        browser.driver.get(joinLink);
        confirmationPage = AppPage.createCorrectLoginPageType(browser)
                           .loginAsJoiningInstructor(TestProperties.TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.TEST_INSTRUCTOR_PASSWORD);

        InstructorHomePage instructorHome = confirmationPage.clickConfirmButton();
        instructorHome.verifyContains("You have used an invalid join link: /page/instructorCourseJoin?key="
                                      + invalidEncryptedKey);

        ______TS("Click join link then confirm: success: valid key");

        String courseId = testData.courses.get("ICJConfirmationUiT.CS1101").getId();
        String instructorEmail = testData.instructors.get("ICJConfirmationUiT.instr.CS1101").email;

        String regkey = BackDoor.getEncryptedKeyForInstructor(courseId, instructorEmail);
        joinLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                        .withRegistrationKey(regkey)
                                        .toAbsoluteString();

        browser.driver.get(joinLink);
        confirmationPage = AppPage.getNewPageInstance(browser, InstructorCourseJoinConfirmationPage.class);

        // test content here to make test finish faster
        ______TS("test instructor confirmation page content");
        // this test uses accounts from test.properties

        // This is the full HTML verification for Instructor Course Join Confirmation Page,
        // the rest can all be verifyMainHtml
        confirmationPage.verifyHtml("/instructorCourseJoinConfirmationHTML.html");

        confirmationPage.clickConfirmButton();

        ______TS("Already joined, no confirmation page");

        browser.driver.get(joinLink);
        instructorHome = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        instructorHome.waitForTextsForAllStatusMessagesToUserEquals(
                TestProperties.TEST_INSTRUCTOR_ACCOUNT + " has already joined this course");
    }

}
