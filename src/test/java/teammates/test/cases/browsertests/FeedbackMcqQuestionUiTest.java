package teammates.test.cases.browsertests;

import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for multiple choice (single answer) questions.
 */
public class FeedbackMcqQuestionUiTest extends FeedbackQuestionUiTest {

    private static final int NEW_QUESTION_INDEX = -1;

    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackMcqQuestionUiTest.json");
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

        //TODO: move/create other MCQ question related UI tests here.
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

        ______TS("MCQ: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        ______TS("MCQ: Check UI after cancelling and add new MCQ question again");

        feedbackEditPage.clickGenerateMcqOptionsCheckbox(NEW_QUESTION_INDEX);
        assertFalse(feedbackEditPage.isElementVisible("mcqChoiceTable--1"));

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());
        assertFalse(feedbackEditPage.isElementVisible("mcqChoiceTable--1"));
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect--1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMcqOptionsCheckbox--1"));

        feedbackEditPage.clickGenerateMcqOptionsCheckbox(NEW_QUESTION_INDEX); //Make the generate options checkbox unchecked

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
                "Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.");

        ______TS("remove when 1 left");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveMcqOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        feedbackEditPage.clickRemoveMcqOptionLinkForNewQuestion(0);
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.");

        ______TS("remove when 1 left and select Add Other Option");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertTrue(feedbackEditPage.verifyNewMcqQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveMcqOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0--1"));

        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little choices for Multiple-choice (single answer) question. Minimum number of options is: 2.");
    }

    @Override
    public void testCustomizeOptions() {

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");

        feedbackEditPage.fillMcqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMcqOptionForNewQuestion(1, "Choice 2");

        ______TS("MCQ: add mcq option");

        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-2--1"));
        feedbackEditPage.clickAddMoreMcqOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-2--1"));

        ______TS("MCQ: remove mcq option");

        feedbackEditPage.fillMcqOptionForNewQuestion(2, "Choice 3");
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));
        feedbackEditPage.clickRemoveMcqOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-1--1"));

        ______TS("MCQ: add mcq option after remove");

        feedbackEditPage.clickAddMoreMcqOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-3--1"));
        feedbackEditPage.clickAddMoreMcqOptionLinkForNewQuestion();
        feedbackEditPage.fillMcqOptionForNewQuestion(4, "Choice 5");
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-4--1"));
    }

    @Override
    public void testAddQuestionAction() throws Exception {

        ______TS("MCQ: add question action success");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("mcq qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad();

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        //NOTE: Tests feedback giver/recipient and visibility options are copied from previous question.
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {

        ______TS("MCQ: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("edited mcq qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        feedbackEditPage.clickRemoveMcqOptionLink(0, 1);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionEditSuccess.html");

        ______TS("MCQ: edit to generated options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("generated mcq qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        assertTrue(feedbackEditPage.isElementVisible("mcqAddOptionLink-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.NONE.toString());
        assertFalse(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.clickGenerateMcqOptionsCheckbox(1);
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());
        assertFalse(feedbackEditPage.isElementVisible("mcqAddOptionLink-1"));

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        assertFalse(feedbackEditPage.isElementPresent("mcqOptionRow-0-1"));
        assertFalse(feedbackEditPage.isElementEnabled("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMcqOptionsCheckbox-1"));
        assertFalse(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.STUDENTS.toString());

        ______TS("MCQ: change generated type to students (excluding self)");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.selectMcqGenerateOptionsFor("students (excluding self)", 1);
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toString());

        ______TS("MCQ: change generated type to teams");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.selectMcqGenerateOptionsFor("teams", 1);
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.TEAMS.toString());

        ______TS("MCQ: change generated type to teams (excluding self)");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementSelected("generateMcqOptionsCheckbox-1"));
        assertTrue(feedbackEditPage.isElementEnabled("mcqGenerateForSelect-1"));
        feedbackEditPage.selectMcqGenerateOptionsFor("teams (excluding self)", 1);
        feedbackEditPage.verifyFieldValue(
                "mcqGenerateForSelect-1",
                FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toString());
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS + "-1",
                FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toString());

    }

    @Override
    public void testDeleteQuestionAction() {

        ______TS("MCQ: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("MCQ: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMcqWeightsFeature_shouldToggleStateCorrectly() {

        ______TS("MCQ: Weight cells are visible when assigneWeights checkbox is clicked?");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");

        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        // Check if the MCQ weights column are visible or not.
        assertTrue(feedbackEditPage.getMcqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        // Check the 'other' option is disabled and otherWeight cell is hidden
        assertFalse(feedbackEditPage.isMcqOtherOptionCheckboxChecked(NEW_QUESTION_INDEX));
        assertFalse(feedbackEditPage.getMcqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        ______TS("MCQ: Other weight Cell is visible when other option and weights both are enabled");

        // Check Assign MCQ weights Enabled but other option disabled
        assertTrue(feedbackEditPage.isMcqHasAssignWeightCheckboxChecked(NEW_QUESTION_INDEX));
        assertFalse(feedbackEditPage.isMcqOtherOptionCheckboxChecked(NEW_QUESTION_INDEX));

        // Assign Other option and test that other weight cell is displayed.
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();
        assertTrue(feedbackEditPage.getMcqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        // Uncheck checkboxes for consistency among other tests,
        // otherwise these settings will persist after cancelling the question form
        // Uncheck the 'Choices are weighted' checkbox.
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();
        assertFalse(feedbackEditPage.getMcqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());
        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        assertFalse(feedbackEditPage.getMcqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());

        // Cancel question
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    @Test
    public void testMcqWeightsFeature_shouldHaveCorrectDefaultValue() throws Exception {

        ______TS("MCQ: Weight cells are hidden by default");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        // Check if the 'Choices are weighted' checkbox unchecked by default.
        assertFalse(feedbackEditPage.isMcqHasAssignWeightCheckboxChecked(NEW_QUESTION_INDEX));
        // Check if the MCQ weights column hidden by default
        assertFalse(feedbackEditPage.getMcqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        assertFalse(feedbackEditPage.getMcqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        ______TS("MCQ: Check Mcq weight default value");
        feedbackEditPage.fillQuestionTextBox("MCQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();

        // Fill MCQ choices and check corresponding weight values
        feedbackEditPage.fillMcqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.verifyFieldValue("mcqWeight-0--1", "0");
        feedbackEditPage.fillMcqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.verifyFieldValue("mcqWeight-1--1", "0");

        // Verify MCQ other weight default value
        feedbackEditPage.verifyFieldValue("mcqOtherWeight--1", "0");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        // verify html page
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionWeightAddSuccess.html");

        // Delete the question
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    @Test
    public void testMcqWeightsFeature_shouldValidateFieldCorrectly() {
        ______TS("MCQ: Test front-end validation for empty weights");
        // Add a question with valid weights to check front-end validation
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();
        feedbackEditPage.fillQuestionTextBox("MCQ weight front-end validation", NEW_QUESTION_INDEX);
        feedbackEditPage.fillMcqOption(NEW_QUESTION_INDEX, 0, "Choice 1");
        feedbackEditPage.fillMcqOption(NEW_QUESTION_INDEX, 1, "Choice 2");
        feedbackEditPage.fillMcqWeightBox(NEW_QUESTION_INDEX, 0, "1");
        feedbackEditPage.fillMcqWeightBox(NEW_QUESTION_INDEX, 1, "2");
        feedbackEditPage.fillMcqOtherWeightBox(NEW_QUESTION_INDEX, "1");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // Edit the question to enter invalid weight values
        feedbackEditPage.clickEditQuestionButton(1);

        // Check validation for MCQ weight cells
        feedbackEditPage.fillMcqWeightBox(1, 0, "");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMcqWeightBoxFocused(1, 0));
        feedbackEditPage.fillMcqWeightBox(1, 0, "0");

        // Check validation for empty other weight
        feedbackEditPage.fillMcqOtherWeightBox(1, "");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMcqOtherWeightBoxFocused(1));
        feedbackEditPage.fillMcqOtherWeightBox(1, "0");

        // Check validation for the weight cells added by 'add option' button
        feedbackEditPage.clickAddMoreMcqOptionLink(1);
        assertTrue(feedbackEditPage.isElementPresent(By.id("mcqWeight-2-1")));
        feedbackEditPage.fillMcqOption(1, 2, "Choice 3");
        feedbackEditPage.fillMcqWeightBox(1, 2, "");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMcqWeightBoxFocused(1, 2));
        feedbackEditPage.clickRemoveMcqOptionLink(2, 1);
        feedbackEditPage.clickDiscardChangesLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();

        ______TS("Test front-end validation for negative weights");
        feedbackEditPage.clickEditQuestionButton(1);
        // Check validation for mcq weight box
        feedbackEditPage.fillMcqWeightBox(1, 0, "-2");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.isMcqWeightBoxFocused(1, 0);
        feedbackEditPage.fillMcqWeightBox(1, 0, "2");

        // Check validation for other weight
        feedbackEditPage.fillMcqOtherWeightBox(1, "-3");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.isMcqOtherWeightBoxFocused(1);
        feedbackEditPage.fillMcqOtherWeightBox(1, "2");

        // Check validation for weight cells added by 'Add Option' button
        feedbackEditPage.clickAddMoreMcqOptionLink(1);
        assertTrue(feedbackEditPage.isElementPresent(By.id("mcqWeight-2-1")));
        feedbackEditPage.fillMcqOption(1, 2, "Choice 3");
        feedbackEditPage.fillMcqWeightBox(1, 2, "-5");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertTrue(feedbackEditPage.isMcqWeightBoxFocused(1, 2));
        feedbackEditPage.clickRemoveMcqOptionLink(2, 1);

        // Delete the question.
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMcqWeightsFeature_shouldAddWeightsCorrectly() throws Exception {
        ______TS("Success: Add weights");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.fillQuestionTextBox("MCQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();

        // Fill MCQ choices and corresponding weight values
        feedbackEditPage.fillMcqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMcqWeightBox(NEW_QUESTION_INDEX, 0, "1");
        feedbackEditPage.fillMcqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.fillMcqWeightBox(NEW_QUESTION_INDEX, 1, "2");
        feedbackEditPage.fillMcqOtherWeightBox(NEW_QUESTION_INDEX, "3");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        FeedbackQuestionAttributes question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        assertNotNull(question);

        // Check that weights have been added correctly
        FeedbackMcqQuestionDetails questionDetails = (FeedbackMcqQuestionDetails) question.getQuestionDetails();
        List<Double> mcqWeights = questionDetails.getMcqWeights();
        assertEquals(2, mcqWeights.size());
        assertEquals(1.0, mcqWeights.get(0));
        assertEquals(2.0, mcqWeights.get(1));
        assertEquals(3.0, questionDetails.getMcqOtherWeight());

        ______TS("MCQ: Add more weights");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreMcqOptionLink(1);
        assertTrue(feedbackEditPage.isElementPresent("mcqOption-2-1"));
        assertTrue(feedbackEditPage.isElementPresent("mcqWeight-2-1"));
        feedbackEditPage.fillMcqOption(1, 2, "Choice 3");
        feedbackEditPage.fillMcqWeightBox(1, 2, "4");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check that weights have been added correctly
        question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        questionDetails = (FeedbackMcqQuestionDetails) question.getQuestionDetails();
        mcqWeights = questionDetails.getMcqWeights();
        assertEquals(3, mcqWeights.size());
        assertEquals(4.0, mcqWeights.get(2));

        ______TS("MCQ: Failed to add weight due to invalid choice value");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreMcqOptionLink(1);

        // Test for choice that has only spaces
        assertTrue(feedbackEditPage.isElementPresent("mcqOption-3-1"));
        assertTrue(feedbackEditPage.isElementPresent("mcqWeight-3-1"));
        feedbackEditPage.fillMcqOption(1, 3, "                           "); // Choices with only spaces
        feedbackEditPage.fillMcqWeightBox(1, 3, "5");

        // The question form removes invalid choices,
        // So, the corresponding weights should be removed as well, without throwing an error.
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check that the invalid choice and the corresponding weight cell is removed from the question.
        assertFalse(feedbackEditPage.isElementPresent("mcqOption-3-1"));
        assertFalse(feedbackEditPage.isElementPresent("mcqWeight-3-1"));

        // Check that weight has not been added to the question.
        question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        questionDetails = (FeedbackMcqQuestionDetails) question.getQuestionDetails();
        mcqWeights = questionDetails.getMcqWeights();
        // Size of weight list should remain 3 as weight has not been added.
        assertEquals(3, mcqWeights.size());

        // Delete the question.
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMcqWeightsFeature_shouldEditWeightsCorrectly() throws Exception {
        // Add a question to test edit weight feature.
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.fillQuestionTextBox("MCQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();

        // Fill MCQ choices and check corresponding weight values
        feedbackEditPage.fillMcqOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillMcqWeightBox(NEW_QUESTION_INDEX, 0, "1");
        feedbackEditPage.fillMcqOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.fillMcqWeightBox(NEW_QUESTION_INDEX, 1, "2");
        feedbackEditPage.fillMcqOtherWeightBox(NEW_QUESTION_INDEX, "3");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        ______TS("MCQ: Edit weights success");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillMcqWeightBox(1, 0, "1.55");
        feedbackEditPage.fillMcqWeightBox(1, 1, "2.77");
        feedbackEditPage.fillMcqOtherWeightBox(1, "3.66");
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // verify weights for the question
        feedbackEditPage.verifyFieldValue("mcqWeight-0-1", "1.55");
        feedbackEditPage.verifyFieldValue("mcqWeight-1-1", "2.77");
        feedbackEditPage.verifyFieldValue("mcqOtherWeight-1", "3.66");

        // verify html page
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackMcqQuestionWeightEditSuccess.html");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test
    public void testMcqWeightFeature_shouldNotHaveWeightsForGenerateOptions() {
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("MCQ");
        feedbackEditPage.fillQuestionTextBox("MCQ weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickMcqAssignWeightCheckboxForNewQuestion();
        feedbackEditPage.clickAddMcqOtherOptionCheckboxForNewQuestion();

        ______TS("MCQ: Test that selecting generated option hides mcq weights cells and checkbox");

        // Test visibility of mcq weights and checkbox before selecting mcq generated option.
        assertTrue(feedbackEditPage.getMcqHasAssignWeightsCheckbox(NEW_QUESTION_INDEX).isDisplayed());
        assertTrue(feedbackEditPage.getMcqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        assertTrue(feedbackEditPage.getMcqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        feedbackEditPage.clickGenerateMcqOptionsCheckbox(NEW_QUESTION_INDEX);

        // Check that 'choices are weighted' checkbox is hidden
        assertFalse(feedbackEditPage.getMcqHasAssignWeightsCheckbox(NEW_QUESTION_INDEX).isDisplayed());
        assertFalse(feedbackEditPage.getMcqWeightsColumn(NEW_QUESTION_INDEX).isDisplayed());
        assertFalse(feedbackEditPage.getMcqOtherWeightBox(NEW_QUESTION_INDEX).isDisplayed());

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        ______TS("MCQ: Test mcq weight have default values after edit to generated options");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickGenerateMcqOptionsCheckbox(1); // Uncheck the generated option checkbox

        // Check that weight fields are again visible after unchecking generated option,
        // and also check that all the fields have the default values stored.
        assertFalse(feedbackEditPage.isMcqHasAssignWeightCheckboxChecked(1));
        assertFalse(feedbackEditPage.isMcqOtherOptionCheckboxChecked(1));

        // Add One option and check MCQ assign weight checkbox to check the mcq weight field value.
        feedbackEditPage.clickAddMoreMcqOptionLink(1);
        feedbackEditPage.clickMcqHasAssignWeightsCheckbox(1);
        assertTrue(feedbackEditPage.getMcqWeightBox(1, 0).isDisplayed());
        feedbackEditPage.verifyFieldValue("mcqWeight-0-1", "0");

        // Check the other option to test the value of other weight
        feedbackEditPage.clickAddMcqOtherOptionCheckbox(1);
        assertTrue(feedbackEditPage.getMcqOtherWeightBox(1).isDisplayed());
        feedbackEditPage.verifyFieldValue("mcqOtherWeight-1", "0");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Test(priority = 1)
    public void testMcqWeightsFeature_instructorResultsPageQuestionView_showStatistics() throws Exception {
        // Create the Action URI for 'MCQ Weights Session' to show the result page.
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                .withUserId("FMcqQnUiT.instructor")
                .withCourseId("FMcqQnUiT.CS2104")
                .withSessionName(testData.feedbackSessions.get("mcqSession").getFeedbackSessionName())
                .withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question");

        InstructorFeedbackResultsPage instructorResultsPage =
                loginAdminToPage(editUrl, InstructorFeedbackResultsPage.class);

        ______TS("Show statistics for mcq question without weights enabled");

        instructorResultsPage.clickShowStats();
        instructorResultsPage.loadResultQuestionPanel(1);
        assertEquals(instructorResultsPage.showStatsCheckbox.getAttribute("checked"), "true");
        assertTrue(instructorResultsPage.verifyAllStatsVisibility());
        instructorResultsPage.verifyHtmlMainContent(
                "/instructorFeedbackResultsPageMcqQuestionViewWithoutWeightsAttached.html");
        instructorResultsPage.clickShowStats(); // This will collapse the particular question 1 panel

        ______TS("Show statistics for MCQ question with weights attached");

        instructorResultsPage.clickShowStats();
        instructorResultsPage.loadResultQuestionPanel(2);
        assertEquals(instructorResultsPage.showStatsCheckbox.getAttribute("checked"), "true");
        assertTrue(instructorResultsPage.verifyAllStatsVisibility());
        instructorResultsPage.verifyHtmlMainContent(
                "/instructorFeedbackResultsPageMcqQuestionViewWithWeightsAttached.html");
    }
}
