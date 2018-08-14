package teammates.test.cases.browsertests;

import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for multiple choice (multiple answers) questions.
 */
public class FeedbackMsqQuestionUiTest extends FeedbackQuestionUiTest {

    private static final int NEW_QUESTION_INDEX = -1;
    private static final String QN_TYPE = "msq";

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
        testReorderOptions();
    }

    @Override
    public void testNewQuestionFrame() {

        ______TS("MSQ: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());

        ______TS("MSQ: Check UI after cancelling and add new MSQ question again");

        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX);
        assertFalse(feedbackEditPage.isElementVisible("msqChoiceTable--1"));

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());
        assertFalse(feedbackEditPage.isElementVisible("msqChoiceTable--1"));
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect--1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox--1"));

        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX); //Make the generate options checkbox unchecked

    }

    @Override
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);

        ______TS("empty options");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little choices for Multiple-choice (multiple answers) question. Minimum number of options is: 2.");

        ______TS("remove when 1 left");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        feedbackEditPage.clickRemoveMsqOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-0--1"));
        feedbackEditPage.clickRemoveMsqOptionLinkForNewQuestion(0);
        assertTrue(feedbackEditPage.isElementPresent("msqOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little choices for Multiple-choice (multiple answers) question. Minimum number of options is: 2.");

        ______TS("select Add Other Option");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Msq with other option");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertTrue(feedbackEditPage.verifyNewMsqQuestionFormIsDisplayed());

        assertTrue(feedbackEditPage.isElementPresent("msqOtherOptionFlag--1"));
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();
        assertTrue(feedbackEditPage.isElementSelected("msqOtherOptionFlag--1"));
        feedbackEditPage.clickAddQuestionButton();

        ______TS("Check that Max/Min selectable choices cannot be blank");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "Choice 2");

        feedbackEditPage.toggleMsqMaxSelectableChoices(NEW_QUESTION_INDEX);
        feedbackEditPage.fillMsqMaxSelectableChoices(NEW_QUESTION_INDEX, "");
        assertFalse(feedbackEditPage.isInputElementValid(
                feedbackEditPage.getMsqMaxSelectableChoicesBox(NEW_QUESTION_INDEX)));
        feedbackEditPage.fillMsqMaxSelectableChoices(NEW_QUESTION_INDEX, "2");
        assertTrue(feedbackEditPage.isInputElementValid(
                feedbackEditPage.getMsqMaxSelectableChoicesBox(NEW_QUESTION_INDEX)));
        feedbackEditPage.toggleMsqMaxSelectableChoices(NEW_QUESTION_INDEX);

        feedbackEditPage.toggleMsqMinSelectableChoices(NEW_QUESTION_INDEX);
        feedbackEditPage.fillMsqMinSelectableChoices(NEW_QUESTION_INDEX, "");
        assertFalse(feedbackEditPage.isInputElementValid(
                feedbackEditPage.getMsqMinSelectableChoicesBox(NEW_QUESTION_INDEX)));
        feedbackEditPage.fillMsqMinSelectableChoices(NEW_QUESTION_INDEX, "1");
        assertTrue(feedbackEditPage.isInputElementValid(
                feedbackEditPage.getMsqMinSelectableChoicesBox(NEW_QUESTION_INDEX)));
        feedbackEditPage.toggleMsqMinSelectableChoices(NEW_QUESTION_INDEX);

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

    }

    @Override
    public void testCustomizeOptions() {

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");

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
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
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
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionEditSuccess.html");

        ______TS("MSQ: edit to generated options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("generated msq qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementVisible("msqAddOptionLink-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.NONE.toString());
        assertFalse(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(1);
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());
        assertFalse(feedbackEditPage.isElementVisible("msqAddOptionLink-1"));

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        assertFalse(feedbackEditPage.isElementPresent("msqOptionRow-0-1"));
        assertFalse(feedbackEditPage.isElementEnabled("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox-1"));
        assertFalse(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());

        ______TS("MSQ: change generated type to students (excluding self)");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.selectMsqGenerateOptionsFor("students (excluding self)", 1);
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toString());

        ______TS("MSQ: change generated type to teams");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.selectMsqGenerateOptionsFor("teams", 1);
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.TEAMS.toString());

        ______TS("MSQ: change generated type to teams (excluding self)");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMsqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("msqGenerateForSelect-1"));
        feedbackEditPage.selectMsqGenerateOptionsFor("teams (excluding self)", 1);
        feedbackEditPage.verifyFieldValue(
                "msqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toString());

        ______TS("MSQ: min/max selectable options");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check min/max selectable restrictions for
        // new MSQ question with custom options
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Custom options");
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "A");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "B");
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.fillMsqOptionForNewQuestion(2, "C");
        feedbackEditPage.fillMsqOptionForNewQuestion(3, "D");
        checkMinMaxSelectableRestrictionForCustomOptions(-1);

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // Check min/max selectable restrictions for
        // existing MSQ question with custom options
        feedbackEditPage.clickEditQuestionButton(2);
        checkMinMaxSelectableRestrictionForCustomOptions(2);
        feedbackEditPage.clickDeleteQuestionLink(2);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);

        // Check min/max selectable restrictions for new
        // MSQ question with options generated from students
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Msq generated options");
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX);
        checkMinMaxSelectableRestrictionsForAllGenerateOptionSelections(-1);

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // Check min/max selectable restrictions for existing
        // MSQ question with options generated from students
        feedbackEditPage.clickEditQuestionButton(2);
        checkMinMaxSelectableRestrictionsForAllGenerateOptionSelections(2);
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
    }

    private void checkMinMaxSelectableRestrictionForCustomOptions(int qnNumber) {
        // checking inputs after enabling maxSelectableChoices restriction
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        int numOfOptions = feedbackEditPage.getNumOfMsqOptions(qnNumber);
        int maxSelectableChoices = numOfOptions;

        // when maxSelectableChoices = numOfOptions and
        // an option is removed, maxSelectableChoices should decrease
        feedbackEditPage.fillMsqMaxSelectableChoices(qnNumber, String.valueOf(maxSelectableChoices));
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
        feedbackEditPage.fillMsqMinSelectableChoices(qnNumber, String.valueOf(minSelectableChoices));
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
        feedbackEditPage.fillMsqMaxSelectableChoices(qnNumber, String.valueOf(--maxSelectableChoices));
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(--minSelectableChoices, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // when minSelectableChoices = maxSelectableChoices = numOfOptions and an option
        // is removed, both minSelectableChoices and maxSelectableChoices should decrease
        maxSelectableChoices = numOfOptions;
        minSelectableChoices = numOfOptions;
        feedbackEditPage.fillMsqMaxSelectableChoices(qnNumber, String.valueOf(maxSelectableChoices));
        feedbackEditPage.fillMsqMinSelectableChoices(qnNumber, String.valueOf(minSelectableChoices));
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
        feedbackEditPage.fillMsqMaxSelectableChoices(qnNumber, String.valueOf(numOfOptions));
        feedbackEditPage.fillMsqMinSelectableChoices(qnNumber, String.valueOf(numOfOptions));
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.fillMsqMaxSelectableChoices(qnNumber, String.valueOf(numOfOptions - 1));
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
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    /**
     * Tests that MSQ options (new and existing) can be reordered using drag and drop mechanism.
     * @throws Exception when option is not draggable
     */
    private void testReorderOptions() throws Exception {

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();

        feedbackEditPage.fillMsqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.fillMsqOptionForNewQuestion(2, "Choice 3");
        feedbackEditPage.fillMsqOptionForNewQuestion(3, "Choice 4");

        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 0, "10");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 1, "20");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 2, "30");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 3, "40");

        ______TS("MSQ: reorder existing options");

        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, NEW_QUESTION_INDEX, 2, 0);
        feedbackEditPage.clickAddQuestionButton();
        JSONObject msqQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                .questionMetaData.getValue());
        assertEquals("[\"Choice 3\",\"Choice 1\",\"Choice 2\",\"Choice 4\"]",
                msqQuestionDetails.get("msqChoices").toString());

        ______TS("MSQ: add option and reorder");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        feedbackEditPage.fillMsqOption(1, 4, "New Choice");
        feedbackEditPage.fillMsqWeightBox(1, 4, "50");
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        msqQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                .questionMetaData.getValue());
        assertEquals("[\"Choice 3\",\"New Choice\",\"Choice 1\",\"Choice 2\",\"Choice 4\"]",
                msqQuestionDetails.get("msqChoices").toString());
        assertEquals("[30,50,10,20,40]", msqQuestionDetails.get("msqWeights").toString());

        ______TS("MSQ: delete option and reorder");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickRemoveMsqOptionLink(2, 1);
        feedbackEditPage.fillMsqOption(1, 1, "Old Choice");
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        msqQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                .questionMetaData.getValue());
        assertEquals("[\"Choice 3\",\"Choice 4\",\"Old Choice\",\"Choice 2\"]",
                msqQuestionDetails.get("msqChoices").toString());
        assertEquals("[30,40,50,20]", msqQuestionDetails.get("msqWeights").toString());

        ______TS("MSQ: add, delete and reorder options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickRemoveMsqOptionLink(2, 1);
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        feedbackEditPage.fillMsqOption(1, 4, "New Choice");
        feedbackEditPage.fillMsqOption(1, 5, "Newer Choice");
        feedbackEditPage.fillMsqWeightBox(1, 4, "50");
        feedbackEditPage.fillMsqWeightBox(1, 5, "60");
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 5, 0);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        msqQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                .questionMetaData.getValue());
        assertEquals("[\"Newer Choice\",\"New Choice\",\"Choice 3\",\"Choice 4\",\"Choice 2\"]",
                msqQuestionDetails.get("msqChoices").toString());
        assertEquals("[60,50,30,40,20]", msqQuestionDetails.get("msqWeights").toString());

        ______TS("MSQ: weights should be reordered when checkbox is unticked");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        feedbackEditPage.fillMsqOption(1, 5, "Newest Choice");
        feedbackEditPage.fillMsqWeightBox(1, 5, "70");
        feedbackEditPage.clickMsqHasAssignWeightsCheckbox(1);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 0);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 3, 1);
        feedbackEditPage.clickMsqHasAssignWeightsCheckbox(1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        msqQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
               .questionMetaData.getValue());
        assertEquals("[\"Choice 2\",\"Choice 3\",\"Newer Choice\",\"New Choice\",\"Choice 4\",\"Newest Choice\"]",
                    msqQuestionDetails.get("msqChoices").toString());
        assertEquals("[20,30,60,50,40,70]", msqQuestionDetails.get("msqWeights").toString());

        ______TS("MSQ: delete question");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMsqWeightsFeature_shouldToggleStateCorrectly() {

        ______TS("MSQ: Weight cells are visible when assigneWeights checkbox is clicked?");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");

        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        // Check if the MSQ weights column are visible or not.
        assertTrue(feedbackEditPage.getMsqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        // Check the 'other' option is disabled and otherWeight cell is hidden
        assertFalse(feedbackEditPage.isMsqOtherOptionCheckboxChecked(NEW_QUESTION_INDEX));
        assertFalse(feedbackEditPage.getMsqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        ______TS("MSQ: Other weight Cell is visible when other option and weights both are enabled");

        // Check Assign MSQ weights Enabled but other option disabled
        assertTrue(feedbackEditPage.isMsqHasAssignWeightCheckboxChecked(NEW_QUESTION_INDEX));
        assertFalse(feedbackEditPage.isMsqOtherOptionCheckboxChecked(NEW_QUESTION_INDEX));

        // Assign Other option and test that other weight cell is displayed.
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();
        assertTrue(feedbackEditPage.getMsqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        // Uncheck checkboxes for consistency among other tests,
        // otherwise these settings will persist after cancelling the question form
        // Uncheck the 'Choices are weighted' checkbox.
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();
        assertFalse(feedbackEditPage.getMsqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());
        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        assertFalse(feedbackEditPage.getMsqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());

        // Cancel question
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    @Test
    public void testMsqWeightsFeature_shouldHaveCorrectDefaultValue() throws Exception {

        ______TS("MSQ: Weight cells are hidden by default");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        // Check if the 'Choices are weighted' checkbox unchecked by default.
        assertFalse(feedbackEditPage.isMsqHasAssignWeightCheckboxChecked(NEW_QUESTION_INDEX));
        // Check if the MSQ weights column hidden by default
        assertFalse(feedbackEditPage.getMsqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        assertFalse(feedbackEditPage.getMsqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        ______TS("MSQ: Check Msq weight default value");
        feedbackEditPage.fillQuestionTextBox("MSQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();

        // Fill MSQ choices and check corresponding weight values
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.verifyFieldValue("msqWeight-0--1", "0");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.verifyFieldValue("msqWeight-1--1", "0");

        // Verify MSQ other weight default value
        feedbackEditPage.verifyFieldValue("msqOtherWeight--1", "0");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        // verify html page
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionWeightAddSuccess.html");

        // Delete the question
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    @Test
    public void testMsqWeightsFeature_shouldValidateFieldCorrectly() {
        ______TS("MSQ: Test front-end validation for empty weights");
        // Add a question with valid weights to check front-end validation
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();
        feedbackEditPage.fillQuestionTextBox("MSQ weight front-end validation", NEW_QUESTION_INDEX);
        feedbackEditPage.fillMsqOption(NEW_QUESTION_INDEX, 0, "Choice 1");
        feedbackEditPage.fillMsqOption(NEW_QUESTION_INDEX, 1, "Choice 2");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 0, "1");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 1, "2");
        feedbackEditPage.fillMsqOtherWeightBox(NEW_QUESTION_INDEX, "1");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // Edit the question to enter invalid weight values
        feedbackEditPage.clickEditQuestionButton(1);

        // Check validation for MSQ weight cells
        feedbackEditPage.fillMsqWeightBox(1, 0, "");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMsqWeightBoxFocused(1, 0));
        feedbackEditPage.fillMsqWeightBox(1, 0, "0");

        // Check validation for empty other weight
        feedbackEditPage.fillMsqOtherWeightBox(1, "");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMsqOtherWeightBoxFocused(1));
        feedbackEditPage.fillMsqOtherWeightBox(1, "0");

        // Check validation for the weight cells added by 'add option' button
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        assertTrue(feedbackEditPage.isElementPresent(By.id("msqWeight-2-1")));
        feedbackEditPage.fillMsqOption(1, 2, "Choice 3");
        feedbackEditPage.fillMsqWeightBox(1, 2, "");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMsqWeightBoxFocused(1, 2));
        feedbackEditPage.clickRemoveMsqOptionLink(2, 1);
        feedbackEditPage.clickDiscardChangesLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        ______TS("Test front-end validation for negative weights");
        feedbackEditPage.clickEditQuestionButton(1);
        // Check validation for Msq weight box
        feedbackEditPage.fillMsqWeightBox(1, 0, "-2");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.isMsqWeightBoxFocused(1, 0);
        feedbackEditPage.fillMsqWeightBox(1, 0, "2");

        // Check validation for other weight
        feedbackEditPage.fillMsqOtherWeightBox(1, "-3");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.isMsqOtherWeightBoxFocused(1);
        feedbackEditPage.fillMsqOtherWeightBox(1, "2");

        // Check validation for weight cells added by 'Add Option' button
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        assertTrue(feedbackEditPage.isElementPresent(By.id("msqWeight-2-1")));
        feedbackEditPage.fillMsqOption(1, 2, "Choice 3");
        feedbackEditPage.fillMsqWeightBox(1, 2, "-5");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMsqWeightBoxFocused(1, 2));
        feedbackEditPage.clickRemoveMsqOptionLink(2, 1);

        // Delete the question.
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMsqWeightsFeature_shouldAddWeightsCorrectly() throws Exception {
        ______TS("Success: Add weights");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBox("MSQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();

        // Fill MSQ choices and corresponding weight values
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 0, "1");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 1, "2");
        feedbackEditPage.fillMsqOtherWeightBox(NEW_QUESTION_INDEX, "3");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        FeedbackQuestionAttributes question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        assertNotNull(question);

        // Check that weights have been added correctly
        FeedbackMsqQuestionDetails questionDetails = (FeedbackMsqQuestionDetails) question.getQuestionDetails();
        List<Double> msqWeights = questionDetails.getMsqWeights();
        assertEquals(2, msqWeights.size());
        assertEquals(1.0, msqWeights.get(0));
        assertEquals(2.0, msqWeights.get(1));
        assertEquals(3.0, questionDetails.getMsqOtherWeight());

        ______TS("MSQ: Add more weights");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        assertTrue(feedbackEditPage.isElementPresent("msqOption-2-1"));
        assertTrue(feedbackEditPage.isElementPresent("msqWeight-2-1"));
        feedbackEditPage.fillMsqOption(1, 2, "Choice 3");
        feedbackEditPage.fillMsqWeightBox(1, 2, "4");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check that weights have been added correctly
        question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        questionDetails = (FeedbackMsqQuestionDetails) question.getQuestionDetails();
        msqWeights = questionDetails.getMsqWeights();
        assertEquals(3, msqWeights.size());
        assertEquals(4.0, msqWeights.get(2));

        ______TS("MSQ: Failed to add weight due to invalid choice value");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreMsqOptionLink(1);

        // Test for choice that has only spaces
        assertTrue(feedbackEditPage.isElementPresent("msqOption-3-1"));
        assertTrue(feedbackEditPage.isElementPresent("msqWeight-3-1"));
        feedbackEditPage.fillMsqOption(1, 3, "                           "); // Choices with only spaces
        feedbackEditPage.fillMsqWeightBox(1, 3, "5");

        // The question form removes invalid choices,
        // So, the corresponding weights should be removed as well, without throwing an error.
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check that the invalid choice and the corresponding weight cell is removed from the question.
        assertFalse(feedbackEditPage.isElementPresent("msqOption-3-1"));
        assertFalse(feedbackEditPage.isElementPresent("msqWeight-3-1"));

        // Check that weight has not been added to the question.
        question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        questionDetails = (FeedbackMsqQuestionDetails) question.getQuestionDetails();
        msqWeights = questionDetails.getMsqWeights();
        // Size of weight list should remain 3 as weight has not been added.
        assertEquals(3, msqWeights.size());

        // Delete the question.
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMsqWeightsFeature_shouldEditWeightsCorrectly() throws Exception {
        // Add a question to test edit weight feature.
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBox("MSQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();

        // Fill MSQ choices and check corresponding weight values
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 0, "1");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.fillMsqWeightBox(NEW_QUESTION_INDEX, 1, "2");
        feedbackEditPage.fillMsqOtherWeightBox(NEW_QUESTION_INDEX, "3");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        ______TS("MSQ: Edit weights success");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillMsqWeightBox(1, 0, "1.55");
        feedbackEditPage.fillMsqWeightBox(1, 1, "2.77");
        feedbackEditPage.fillMsqOtherWeightBox(1, "3.66");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // verify weights for the question
        feedbackEditPage.verifyFieldValue("msqWeight-0-1", "1.55");
        feedbackEditPage.verifyFieldValue("msqWeight-1-1", "2.77");
        feedbackEditPage.verifyFieldValue("msqOtherWeight-1", "3.66");

        // verify html page
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMsqQuestionWeightEditSuccess.html");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMsqWeightsFeature_shouldNotHaveWeightsForGenerateOptions() {
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MSQ");
        feedbackEditPage.fillQuestionTextBox("MSQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMsqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMsqOtherOptionCheckboxForNewQuestion();

        ______TS("MSQ: Test that selecting generated option hides msq weights cells and checkbox");
        // Test visibility of msq weights and checkbox before selecting msq generated option.
        assertTrue(feedbackEditPage.getMsqHasAssignWeightsCheckbox(NEW_QUESTION_INDEX).isDisplayed());
        assertTrue(feedbackEditPage.getMsqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        assertTrue(feedbackEditPage.getMsqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        feedbackEditPage.clickGenerateMsqOptionsCheckbox(NEW_QUESTION_INDEX);

        // Check that 'choices are weighted' checkbox is hidden
        assertFalse(feedbackEditPage.getMsqHasAssignWeightsCheckbox(NEW_QUESTION_INDEX).isDisplayed());
        assertFalse(feedbackEditPage.getMsqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        assertFalse(feedbackEditPage.getMsqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        ______TS("MSQ: Test msq weight have default values after edit to generated options");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(1); // Uncheck the generated option checkbox

        // Check that weight fields are again visible after unchecking generated option,
        // and also check that all the fields have the default values stored.
        assertFalse(feedbackEditPage.isMsqHasAssignWeightCheckboxChecked(1));
        assertFalse(feedbackEditPage.isMsqOtherOptionCheckboxChecked(1));

        // Add One option and check MSQ assign weight checkbox to check the msq weight field value.
        feedbackEditPage.clickAddMoreMsqOptionLink(1);
        feedbackEditPage.clickMsqHasAssignWeightsCheckbox(1);
        assertTrue(feedbackEditPage.getMsqWeightBox(1, 0).isDisplayed());
        feedbackEditPage.verifyFieldValue("msqWeight-0-1", "0");

        // Check the other option to test the value of other weight
        feedbackEditPage.clickAddMsqOtherOptionCheckbox(1);
        assertTrue(feedbackEditPage.getMsqOtherWeightBox(1).isDisplayed());
        feedbackEditPage.verifyFieldValue("msqOtherWeight-1", "0");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test(priority = 1)
    public void testMcqWeightsFeature_instructorResultsPageQuestionView_showStatistics() throws Exception {

        DataBundle msqWeightsTestData = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(msqWeightsTestData);
        // Create the Action URI for 'MCQ Weights Session' to show the result page.
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                .withUserId("FSQTT.idOfInstructor1OfCourse1")
                .withCourseId("FSQTT.idOfTypicalCourse1")
                .withSessionName(msqWeightsTestData.feedbackSessions.get("msqSession").getFeedbackSessionName())
                .withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question");

        InstructorFeedbackResultsPage instructorResultsPage =
                loginAdminToPage(editUrl, InstructorFeedbackResultsPage.class);

        ______TS("Show statistics for mcq question without weights enabled");

        instructorResultsPage.clickShowStats();
        instructorResultsPage.loadResultQuestionPanel(1);
        assertEquals(instructorResultsPage.showStatsCheckbox.getAttribute("checked"), "true");
        assertTrue(instructorResultsPage.verifyAllStatsVisibility());
        instructorResultsPage.verifyHtmlMainContent(
                "/instructorFeedbackResultsPageMsqQuestionViewWithoutWeightsAttached.html");
        instructorResultsPage.clickShowStats(); // This will collapse the particular question 1 panel

        ______TS("Show statistics for MCQ question with weights attached");

        instructorResultsPage.clickShowStats();
        instructorResultsPage.loadResultQuestionPanel(3);
        assertEquals(instructorResultsPage.showStatsCheckbox.getAttribute("checked"), "true");
        assertTrue(instructorResultsPage.verifyAllStatsVisibility());
        instructorResultsPage.verifyHtmlMainContent(
                "/instructorFeedbackResultsPageMsqQuestionViewWithWeightsAttached.html");
    }
}
