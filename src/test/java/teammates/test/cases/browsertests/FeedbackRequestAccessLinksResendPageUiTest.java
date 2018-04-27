package teammates.test.cases.browsertests;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.FeedbackRequestResendAccessLinksPage;

/**
 * Loads the FeedbackRequestLinksResendPage for the tester to do a visual inspection.
 */
public class FeedbackRequestAccessLinksResendPageUiTest extends BaseUiTestCase {

    private FeedbackRequestResendAccessLinksPage requestResendAccessLinksPage;
    private String studentEmailAddress;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRequestAccessLinksResendPageUiTest.json");

        // Use the 1st student account injected for this test

        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        studentEmailAddress = student1GoogleId + "@gmail.com";
        testData.accounts.get("alice.tmms").googleId = student1GoogleId;
        testData.accounts.get("alice.tmms").email = studentEmailAddress;
        testData.students.get("alice.tmms@SHomeUiT.CS2104").email = studentEmailAddress;
        testData.students.get("alice.tmms@SHomeUiT.CS1101").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS1101").email = studentEmailAddress;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").email = studentEmailAddress;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").email = studentEmailAddress;

        removeAndRestoreDataBundle(testData);

        FeedbackSessionAttributes gracedFeedbackSession =
                BackDoor.getFeedbackSession("SHomeUiT.CS2104", "Graced Feedback Session");

        gracedFeedbackSession.setStartTime(Instant.now().minus(Duration.ofDays(10)));
        gracedFeedbackSession.setSessionVisibleFromTime(Instant.now().minus(Duration.ofDays(10)));
        gracedFeedbackSession.setEndTime(Instant.now().plus(Duration.ofDays(10)));

        BackDoor.editFeedbackSession(gracedFeedbackSession);
    }

    @Test
    public void allTests() {
        testInvalidEmailAddress();
        testValidEmailWithFeedbackSessionsInRecentSixMonths();
    }

    private void testInvalidEmailAddress() {
        FieldValidator validator = new FieldValidator();
        ______TS("Invalid Email Address");

        String emailAddress = "InvalidEmail";

        requestResendAccessLinksPage = getHomePage().clickRequestResendLink();

        requestResendAccessLinksPage.fillEmailAddress(emailAddress);
        requestResendAccessLinksPage.clickSubmitButton();
        requestResendAccessLinksPage.waitForTextsForAllStatusMessagesToUserEquals(
                validator.getInvalidityInfoForEmail(emailAddress));
    }

    private void testValidEmailWithFeedbackSessionsInRecentSixMonths() {
        ______TS("Valid Email Address, user with email has feedback sessions in recent six month");

        requestResendAccessLinksPage.fillEmailAddress(studentEmailAddress);
        requestResendAccessLinksPage.clickSubmitButton();
        requestResendAccessLinksPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.FEEDBACK_SESSION_ACCESS_LINKS_RESENT);
    }
}
