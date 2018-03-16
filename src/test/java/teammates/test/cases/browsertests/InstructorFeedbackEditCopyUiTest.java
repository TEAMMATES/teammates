package teammates.test.cases.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_COPY}.
 */
public class InstructorFeedbackEditCopyUiTest extends BaseUiTestCase {
    private String instructorId;
    private String courseId;
    private String feedbackSessionName;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackEditCopyTest.json");
        removeAndRestoreDataBundle(testData);
        instructorId = testData.accounts.get("instructorWithSessions").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }

    @Test
    public void allTests() throws Exception {
        InstructorFeedbackEditPage feedbackEditPage = getFeedbackEditPage();

        ______TS("Submit empty course list");
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.getFsCopyToModal().waitForModalToLoad();

        // Full HTML verification already done in InstructorFeedbackEditPageUiTest
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditCopyPage.html");

        feedbackEditPage.getFsCopyToModal().clickSubmitButton();
        feedbackEditPage.getFsCopyToModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(feedbackEditPage.getFsCopyToModal().isFormSubmissionStatusMessageVisible());
        feedbackEditPage.getFsCopyToModal().verifyStatusMessage(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED);

        feedbackEditPage.getFsCopyToModal().clickCloseButton();

        ______TS("Copying fails due to fs with same name in course selected");
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.getFsCopyToModal().waitForModalToLoad();
        feedbackEditPage.getFsCopyToModal().fillFormWithAllCoursesSelected(feedbackSessionName);

        feedbackEditPage.getFsCopyToModal().clickSubmitButton();
        feedbackEditPage.getFsCopyToModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(feedbackEditPage.getFsCopyToModal().isFormSubmissionStatusMessageVisible());

        feedbackEditPage.getFsCopyToModal()
                        .verifyStatusMessage(
                                 String.format(Const.StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS,
                                               feedbackSessionName,
                                               testData.courses.get("course").getId()));

        // Full HTML verification already done in InstructorFeedbackEditPageUiTest
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditCopyFail.html");

        feedbackEditPage.getFsCopyToModal().clickCloseButton();

        ______TS("Copying fails due to fs with invalid name");
        String invalidNameforFs = "Invalid name | for feedback session";
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.getFsCopyToModal().waitForModalToLoad();
        feedbackEditPage.getFsCopyToModal().fillFormWithAllCoursesSelected(invalidNameforFs);

        feedbackEditPage.getFsCopyToModal().clickSubmitButton();

        feedbackEditPage.getFsCopyToModal().waitForFormSubmissionErrorMessagePresence();
        assertTrue(feedbackEditPage.getFsCopyToModal().isFormSubmissionStatusMessageVisible());
        feedbackEditPage.getFsCopyToModal().verifyStatusMessage(
                "\"" + invalidNameforFs + "\" is not acceptable to TEAMMATES as a/an "
                + "feedback session name because it contains invalid characters. "
                + "A/An feedback session name must start with an alphanumeric character, "
                + "and cannot contain any vertical bar (|) or percent sign (%).");

        feedbackEditPage.getFsCopyToModal().clickCloseButton();

        ______TS("Successful case");
        feedbackEditPage.clickFsCopyButton();
        feedbackEditPage.getFsCopyToModal().waitForModalToLoad();
        feedbackEditPage.getFsCopyToModal().fillFormWithAllCoursesSelected("New name!");

        feedbackEditPage.getFsCopyToModal().clickSubmitButton();
        feedbackEditPage.waitForPageToLoad();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED);
        feedbackEditPage.waitForElementPresence(By.id("table-sessions"));

        // Full HTML verification already done in InstructorFeedbackEditPageUiTest
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackEditCopySuccess.html");

    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                                             .withUserId(instructorId)
                                             .withCourseId(courseId)
                                             .withSessionName(feedbackSessionName)
                                             .withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }

}
