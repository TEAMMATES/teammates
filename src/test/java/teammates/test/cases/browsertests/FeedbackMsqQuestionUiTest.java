package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for multiple choice (multiple answers) questions.
 */
public class FeedbackMsqQuestionUiTest extends FeedbackQuestionUiTest {
    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackMsqQuestionUiTest.json");
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

        //TODO: move/create other MSQ question related UI tests here.
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

        ______TS("MSQ: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);

        ______TS("empty options");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus("Too little choices for Multiple-choice (multiple answers) question. "
                                      + "Minimum number of options is: 2.");

        ______TS("remove when 1 left");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        feedbackEditPage.clickRemoveMsqOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-0--1"));
        feedbackEditPage.clickRemoveMsqOptionLinkForNewQuestion(0);
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus("Too little choices for Multiple-choice (multiple answers) question. "
                                      + "Minimum number of options is: 2.");

        ______TS("select Add Other Option");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Msq with other option");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());

        assertTrue(feedbackEditPage.isElementPresent("msqOtherOptionFlag--1"));
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();
        assertTrue(feedbackEditPage.isElementSelected("msqOtherOptionFlag--1"));
        feedbackEditPage.clickAddQuestionButton();
    }

    @Override
    public void testCustomizeOptions() {

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");

        feedbackEditPage.fillMsqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "Choice 2");

        ______TS("MSQ: add msq option");

        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-2--1"));
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-2--1"));

        ______TS("MSQ: remove msq option");

        feedbackEditPage.fillMsqOptionForNewQuestion(2, "Choice 3");
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-1--1"));
        feedbackEditPage.clickRemoveMsqOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-1--1"));

        ______TS("MSQ: add msq option after remove");

        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-3--1"));
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.fillMsqOptionForNewQuestion(4, "Choice 5");
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-4--1"));
    }

    @Override
    public void testAddQuestionAction() throws Exception {

        ______TS("MSQ: add question action success");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("msq qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudents();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {

        ______TS("MSQ: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("edited msq qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        feedbackEditPage.clickRemoveMsqOptionLink(0, 1);
        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionEditSuccess.html");

        ______TS("MSQ: edit to generated options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("generated msq qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementVisible("msqAddOptionLink-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
                FeedbackParticipantType.NONE.toString());
        assertFalse(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(1);
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());
        assertFalse(feedbackEditPage.isElementVisible("msqAddOptionLink-1"));

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        assertFalse(feedbackEditPage.isElementEnabled("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox-1"));
        assertFalse(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());

        ______TS("MSQ: change generated type");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.selectMsqGenerateOptionsFor("teams", 1);
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS + "-1",
                FeedbackParticipantType.TEAMS.toString());

    }

    @Override
    public void testDeleteQuestionAction() {

        ______TS("MSQ: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MSQ: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

}
