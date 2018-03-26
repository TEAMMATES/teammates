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

    private static final int NEW_QUESTION_INDEX = -1;

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

        ______TS("MSQ: Check UI after cancelling and add new MSQ question again");

        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX);
        assertFalse(feedbackEditPage.isElementVisible("msqChoiceTable--1"));

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());
        assertFalse(feedbackEditPage.isElementVisible("msqChoiceTable--1"));
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect--1"));

        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX); //Make the generate options checkbox unchecked

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

        ______TS("MSQ: min/max selectable options");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check min/max selectable restrictions for
        // new MSQ question with custom options
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Custom options");
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "A");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "B");
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.fillMsqOptionForNewQuestion(2, "C");
        feedbackEditPage.fillMsqOptionForNewQuestion(3, "D");
        checkMinMaxSelectableRestrictionForCustomOptions(-1);

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // Check min/max selectable restrictions for
        // existing MSQ question with custom options
        feedbackEditPage.clickEditQuestionButton(2);
        checkMinMaxSelectableRestrictionForCustomOptions(2);
        feedbackEditPage.clickDeleteQuestionLink(2);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);

        // Check min/max selectable restrictions for new
        // MSQ question with options generated from students
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Msq generated options");
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX);
        checkMinMaxSelectableRestrictionsForAllGenerateOptionSelections(-1);

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // Check min/max selectable restrictions for existing
        // MSQ question with options generated from students
        feedbackEditPage.clickEditQuestionButton(2);
        checkMinMaxSelectableRestrictionsForAllGenerateOptionSelections(2);
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
    }

    private void checkMinMaxSelectableRestrictionForCustomOptions(int qnNumber) {
        // checking inputs after enabling maxSelectableChoices restriction
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        int numOfOptions = feedbackEditPage.getNumOfMsqOptions(qnNumber);
        int maxSelectableChoices = numOfOptions;

        // when maxSelectableChoices = numOfOptions and
        // an option is removed, maxSelectableChoices should decrease
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, maxSelectableChoices);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.clickRemoveMsqOptionLink(0, qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(--maxSelectableChoices, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));

        // add an option back, maxSelectableChoices should remain same
        feedbackEditPage.clickAddMoreMsqOptionLink(qnNumber);
        feedbackEditPage.fillMsqOption(qnNumber, numOfOptions, "X");
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(maxSelectableChoices, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));

        // enable minSelectableChoices restriction only
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);

        // check inputs after enabling minSelectableChoices
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        int minSelectableChoices = numOfOptions;

        // when minSelectableChoices = numOfOptions and
        // an option is removed, minSelectableChoices should decrease
        feedbackEditPage.setMsqMinSelectableChoices(qnNumber, minSelectableChoices);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.clickRemoveMsqOptionLink(1, qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(--minSelectableChoices, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // add an option back, minSelectableChoices should remain same
        feedbackEditPage.clickAddMoreMsqOptionLink(qnNumber);
        feedbackEditPage.fillMsqOption(qnNumber, numOfOptions + 1, "Y");
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(minSelectableChoices, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // enable minSelectableChoices and maxSelectableChoices
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);

        // check inputs after enabling minSelectableChoices and maxSelectableChoices
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // if maxSelectableChoices = minSelectableChoices and
        // maxSelectableChoices is decreased, minSelectableChoices should also decrease
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, --maxSelectableChoices);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(--minSelectableChoices, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // when minSelectableChoices = maxSelectableChoices = numOfOptions and an option
        // is removed, both minSelectableChoices and maxSelectableChoices should decrease
        maxSelectableChoices = numOfOptions;
        minSelectableChoices = numOfOptions;
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, maxSelectableChoices);
        feedbackEditPage.setMsqMinSelectableChoices(qnNumber, minSelectableChoices);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.clickRemoveMsqOptionLink(2, qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(--maxSelectableChoices, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));
        assertEquals(--minSelectableChoices, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // add an option back, both minSelectableChoices and maxSelectableChoices should remain same
        feedbackEditPage.clickAddMoreMsqOptionLink(qnNumber);
        feedbackEditPage.fillMsqOption(qnNumber, numOfOptions + 2, "Z");
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(maxSelectableChoices, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));
        assertEquals(minSelectableChoices, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // disable minSelectableChoices and maxSelectableChoices
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
    }

    /**
     * Assumes student is already selected.
     * @param qnNumber question number.
     */
    private void checkMinMaxSelectableRestrictionsForAllGenerateOptionSelections(int qnNumber) {
        // Check minSelectableChoices/maxSelectableChoices restrictions
        // for MSQ question with options generated from students
        checkMinMaxSelectableRestrictionForGeneratedOption(qnNumber);

        // Check minSelectableChoices/maxSelectableChoices
        // restrictions for MSQ question with options generated from teams
        feedbackEditPage.selectMsqGenerateOptionsFor("teams", qnNumber);
        checkMinMaxSelectableRestrictionForGeneratedOption(qnNumber);

        // enable minSelectableChoices/maxSelectableChoices
        // restrictions, change generated options for selection
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
        feedbackEditPage.selectMsqGenerateOptionsFor("students", qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // disable minSelectableChoices and maxSelectableChoices
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
    }

    private void checkMinMaxSelectableRestrictionForGeneratedOption(int qnNumber) {
        int numOfOptions = feedbackEditPage.getNumOfMsqOptions(qnNumber);

        // checking inputs after enabling maxSelectableChoices
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // enable minSelectableChoices restriction only
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // enable minSelectableChoices and maxSelectableChoices restrictions
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);

        // check inputs after enabling minSelectableChoices and maxSelectableChoices
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // set maxSelectableChoices = numOfOptions and minSelectableChoices = numOfOptions,
        // then decreasing maxSelectableChoices must decrease minSelectableChoices too
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, numOfOptions);
        feedbackEditPage.setMsqMinSelectableChoices(qnNumber, numOfOptions);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, numOfOptions - 1);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(numOfOptions - 1, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));
        assertEquals(numOfOptions - 1, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));

        // disable minSelectableChoices and maxSelectableChoices
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
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
