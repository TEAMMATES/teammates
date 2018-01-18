package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for constant sum (recipients) questions.
 */
public class FeedbackConstSumRecipientQuestionUiTest extends FeedbackQuestionUiTest {
    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackConstSumRecipientQuestionUiTest.json");
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

        //TODO: move/create other ConstSumRecipient question related UI tests here.
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
        ______TS("CONSTSUM-recipient: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONSTSUM_RECIPIENT");
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("CONST SUM:input validation");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("ConstSum-recipient qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        feedbackEditPage.fillConstSumPointsBoxForNewQuestion("");
        assertEquals("1", feedbackEditPage.getConstSumPointsBoxForNewQuestion());

        feedbackEditPage.fillConstSumPointsForEachRecipientBoxForNewQuestion("");
        assertEquals("1", feedbackEditPage.getConstSumPointsForEachRecipientBoxForNewQuestion());

        assertFalse(feedbackEditPage.isElementVisible("constSumOptionTable--1"));

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertEquals("", feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

    }

    @Override
    public void testCustomizeOptions() {
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("CONSTSUM_RECIPIENT");

        ______TS("CONST SUM: set points options");

        feedbackEditPage.selectConstSumPointsOptionsForNewQuestion("PerRecipient");
        feedbackEditPage.fillConstSumPointsForEachRecipientBoxForNewQuestion("30");

    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("CONST SUM: add question action success");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("const sum qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        assertEquals("30", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("30", feedbackEditPage.getConstSumPointsForEachRecipientBox(1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumRecipientQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("CONST SUM: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("edited const sum qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.fillConstSumPointsBox("200", 1);
        feedbackEditPage.selectConstSumPointsOptions("Total", 1);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        assertEquals("200", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("200", feedbackEditPage.getConstSumPointsForEachRecipientBox(1));

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumRecipientQuestionEditSuccess.html");
    }

    @Override
    public void testDeleteQuestionAction() {
        ______TS("CONSTSUM: qn delete then cancel");
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("CONSTSUM: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

}
