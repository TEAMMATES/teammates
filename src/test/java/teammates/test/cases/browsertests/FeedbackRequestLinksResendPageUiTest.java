package teammates.test.cases.browsertests;

import java.util.Calendar;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.FeedbackRequestResendLinksPage;

/**
 * Loads the FeedbackRequestLinksResendPage for the tester to do a visual inspection.
 */
public class FeedbackRequestLinksResendPageUiTest extends BaseUiTestCase {

    private FeedbackRequestResendLinksPage requestResendLinksPage;
    private String studentEmailAddress;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRequestLinksResendPageUiTest.json");

        // use the 1st student account injected for this test

        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        testData.accounts.get("alice.tmms").googleId = student1GoogleId;
        testData.accounts.get("alice.tmms").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS2104").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS1101").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS1101").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").email = student1Email;

        studentEmailAddress = student1Email;

        removeAndRestoreDataBundle(testData);

        FeedbackSessionAttributes gracedFeedbackSession =
                BackDoor.getFeedbackSession("SHomeUiT.CS2104", "Graced Feedback Session");

        Calendar fsCalendar = Calendar.getInstance();
        fsCalendar.add(Calendar.DATE, -10);
        gracedFeedbackSession.setStartTime(fsCalendar.getTime().toInstant());
        gracedFeedbackSession.setSessionVisibleFromTime(fsCalendar.getTime().toInstant());
        fsCalendar.add(Calendar.DATE, +20);
        gracedFeedbackSession.setEndTime(fsCalendar.getTime().toInstant());

        BackDoor.editFeedbackSession(gracedFeedbackSession);
    }

    @Test
    public void allTests() {
        testInvalidEmailAddress();
        testValidEmailWithFeedbackSessionsInRecentSixMonths();
    }

    private void testInvalidEmailAddress() {
        ______TS("Invalid Email Address");

        String emailAddress = "InvalidEmail";

        requestResendLinksPage = getHomePage().clickRequestResendLink();

        requestResendLinksPage.fillEmailAddress(emailAddress);
        requestResendLinksPage.clickSubmitButton();
        requestResendLinksPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_SESSION_RESEND_LINKS_INVALID_EMAIL);
    }

    private void testValidEmailWithFeedbackSessionsInRecentSixMonths() {
        ______TS("Valid Email Address, user with email has feedback sessions in recent one month");

        requestResendLinksPage.fillEmailAddress(studentEmailAddress);
        requestResendLinksPage.clickSubmitButton();
        requestResendLinksPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_SESSION_LINKS_RESENT);
    }
}
