package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for numerical scale questions.
 */
public class FeedbackNumScaleQuestionUiTest extends FeedbackQuestionUiTest {
    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackNumScaleQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);

        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }

    @BeforeClass
    public void classSetup() {
        feedbackEditPage = getFeedbackEditPage(instructorId, courseId, feedbackSessionName);
    }

    @Test
    public void allTests() throws Exception {
        testEditPage();

        //TODO: move/create other NumScale question related UI tests here.
        //i.e. results page, submit page.
    }

    private void testEditPage() throws Exception {
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
    }

    @Override
    public void testNewQuestionFrame() {
        ______TS("NUMSCALE: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("NUMSCALE");

        assertTrue(feedbackEditPage.verifyNewNumScaleQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("empty options");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("NumScale qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion("");
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion("");
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion("");

        assertEquals("[Please enter valid numbers for all the options.]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());

        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEqualsErrorTexts(
                "Please enter valid options. The min/max/step cannot be empty.");

        ______TS("invalid options");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("NumScale qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion("1");
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion("0.3");
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion("5");

        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());

        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEqualsErrorTexts(
                "Please enter valid options. The interval is not divisible by the specified increment.");

        ______TS("possible floating point error");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("NumScale qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion("1");
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion("0.001");
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion("5555");

        assertEquals("[Based on the above settings, acceptable responses are: 1, 1.001, 1.002, ..., "
                             + "5554.998, 5554.999, 5555]",
                     feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());

        ______TS("more than three dp step rounding test");

        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion("1002");
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion("1.00123456789");

        assertEquals("[Based on the above settings, acceptable responses are: 1, 2.001, 3.002, ..., "
                             + "999.998, 1000.999, 1002]",
                     feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());

        ______TS("NUMSCALE: min >= max test");
        //Tests javascript that automatically makes max = min+1 when max is <= min.
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion(1);
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion(1);
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion(5);
        assertEquals("[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());

        feedbackEditPage.fillMinNumScaleBoxForNewQuestion(6);
        assertEquals("7", feedbackEditPage.getMaxNumScaleBoxForNewQuestion());

        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion(6);
        assertEquals("7", feedbackEditPage.getMaxNumScaleBoxForNewQuestion());

        //Reset values
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion(1);
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion(5);
    }

    @Override
    public void testCustomizeOptions() {
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("NumScale qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertEquals("[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion(0.3);
        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion(5);
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion(6);
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion(0.001);
        assertEquals("[Based on the above settings, acceptable responses are: 5, 5.001, 5.002, ..., 5.998, 5.999, 6]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());
        feedbackEditPage.fillMinNumScaleBoxForNewQuestion(0);
        feedbackEditPage.fillMaxNumScaleBoxForNewQuestion(1);
        feedbackEditPage.fillStepNumScaleBoxForNewQuestion(0.1);
        assertEquals("[Based on the above settings, acceptable responses are: 0, 0.1, 0.2, ..., 0.8, 0.9, 1]",
                feedbackEditPage.getNumScalePossibleValuesStringForNewQuestion());
    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("NUMSCALE: add question action success");

        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("NUMSCALE: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("edited numscale qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.fillMinNumScaleBox(3, 1);
        feedbackEditPage.fillMaxNumScaleBox(4, 1);
        feedbackEditPage.fillStepNumScaleBox(0.002, 1);
        assertEquals("[Based on the above settings, acceptable responses are: 3, 3.002, 3.004, ..., 3.996, 3.998, 4]",
                feedbackEditPage.getNumScalePossibleValuesString(1));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionEditSuccess.html");
    }

    @Override
    public void testDeleteQuestionAction() {
        ______TS("NUMSCALE: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("NUMSCALE: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

}
