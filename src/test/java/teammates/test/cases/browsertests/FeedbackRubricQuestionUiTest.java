package teammates.test.cases.browsertests;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for rubric questions.
 */
public class FeedbackRubricQuestionUiTest extends FeedbackQuestionUiTest {

    private static final int NEW_QUESTION_INDEX = -1;

    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRubricQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);

        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").getId();
        feedbackSessionName = testData.feedbackSessions.get("openSession").getFeedbackSessionName();
    }

    @BeforeClass
    public void classSetup() {
        feedbackEditPage = getFeedbackEditPage();
    }

    @Test
    public void allTests() throws Exception {
        testRubricWeightsFeature();
        testEditPage();
        testInstructorSubmitPage();
        testStudentSubmitPage();
        testStudentResultsPage();
        testInstructorResultsPage();
    }

    private void testStudentResultsPage() throws Exception {
        ______TS("test rubric question simple student results page");

        StudentFeedbackResultsPage simpleResultsPage =
                                        loginToStudentFeedbackResultsPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        simpleResultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageRubric.html");

        ______TS("test rubric question extended student results page");

        StudentFeedbackResultsPage extendedResultsPage =
                loginToStudentFeedbackResultsPage("alice.tmms@FRubricQnUiT.CS2104", "openSession4");
        extendedResultsPage.verifyHtmlMainContent("/studentExtendedFeedbackResultsPageRubric.html");

    }

    private void testInstructorResultsPage() throws Exception {
        ______TS("Test instructor results page for rubric question with weights attached");

        // Question view
        InstructorFeedbackResultsPage instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2",
                                                                 false, "question");
        instructorResultsPage.loadResultQuestionPanel(1);
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricQuestionView.html");

        // Giver Recipient Question View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "giver-recipient-question");
        instructorResultsPage.loadResultSectionPanel(0, 1);
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricGRQView.html");

        // Giver Question Recipient View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "giver-question-recipient");
        instructorResultsPage.loadResultSectionPanel(0, 1);
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricGQRView.html");

        // Recipient Question Giver View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "recipient-question-giver");
        instructorResultsPage.loadResultSectionPanel(0, 1);
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricRQGView.html");

        // Recipient Giver Question View
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession2", false,
                                                                 "recipient-giver-question");
        instructorResultsPage.loadResultSectionPanel(0, 1);
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRubricRGQView.html");

        ______TS("Test instructor result page for Rubric question without weights attached");

        // Question view
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("teammates.test.instructor", "openSession3",
                                                                 false, "question");
        instructorResultsPage.clickShowStats();
        instructorResultsPage.loadResultQuestionPanel(1);
        instructorResultsPage.verifyHtmlMainContent(
                "/instructorFeedbackResultsPageRubricQuestionViewWithoutWeightsAttached.html");
    }

    private void testInstructorSubmitPage() {

        ______TS("test rubric question input disabled for closed session");

        FeedbackSubmitPage submitPage = loginToInstructorFeedbackSubmitPage("teammates.test.instructor", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                                                     + "-" + qnNumber + "-" + responseNumber + "-" + rowNumber));

        ______TS("test rubric question submission");
        // Done in testStudentSubmitPage

    }

    private void testStudentSubmitPage() throws Exception {

        ______TS("test rubric question input disabled for closed session");

        FeedbackSubmitPage submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "closedSession");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                                                     + "-" + qnNumber + "-" + responseNumber + "-" + rowNumber));

        ______TS("test rubric question submission");

        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        assertTrue(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE
                                                    + "-" + qnNumber + "-" + responseNumber + "-" + rowNumber));

        // Select table cell

        submitPage.clickRubricRadio(1, 0, 0, 1);

        submitPage.clickRubricRadio(1, 1, 0, 1);
        submitPage.clickRubricRadio(1, 1, 1, 0);

        // Submit
        submitPage.clickSubmitButton();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

        // Go back to submission page and verify html
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRubricQnUiT.CS2104", "openSession2");
        submitPage.waitForCellHoverToDisappear();
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageRubricSuccess.html");

        // Submit another feedback response using another student's account
        submitPage = loginToStudentFeedbackSubmitPage("benny.tmms@FRubricQnUiT.CS2104", "openSession2");

        submitPage.clickRubricRadio(1, 0, 0, 0);
        submitPage.clickRubricRadio(1, 0, 1, 1);

        submitPage.clickRubricRadio(1, 1, 0, 0);
        submitPage.clickRubricRadio(1, 1, 1, 0);

        submitPage.clickSubmitButton();

        submitPage = loginToStudentFeedbackSubmitPage("colin.tmms@FRubricQnUiT.CS2104", "openSession2");

        submitPage.clickRubricRadio(1, 0, 0, 1);
        submitPage.clickRubricRadio(1, 0, 1, 0);

        submitPage.clickSubmitButton();
    }

    private void testEditPage() throws Exception {
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
        testInputJsValidationForRubricQuestion();
    }

    @Override
    public void testNewQuestionFrame() {
        ______TS("RUBRIC: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");
        assertTrue(feedbackEditPage.verifyNewRubricQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);

        ______TS("empty sub question test");
        int questionNum = -1;
        int subQuestionIndex = 1;

        feedbackEditPage.fillRubricSubQuestionBox("", questionNum, subQuestionIndex);
        feedbackEditPage.clickAddQuestionButton();
        assertTrue(feedbackEditPage.isRubricSubQuestionBoxFocused(questionNum, subQuestionIndex));
        feedbackEditPage.fillRubricSubQuestionBox("sub question text", questionNum, subQuestionIndex);
        feedbackEditPage.clickAddRubricRowLink(questionNum);

        subQuestionIndex += 1;

        feedbackEditPage.clickAddQuestionButton();
        assertTrue(feedbackEditPage.isRubricSubQuestionBoxFocused(questionNum, subQuestionIndex));
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(questionNum, subQuestionIndex);

        ______TS("Front-end validation for empty weight");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("empty weight test");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("", 0, 3);
        feedbackEditPage.clickAddQuestionButton();
        // Checks if the empty weight box is focused after clicking on 'Save question' button.
        // If it is, that means the front-end validation works and the question is not submitted.
        assertTrue(feedbackEditPage.isRubricWeightBoxFocused(questionNum, 0, 3));
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0", 0, 3);

        // Check if the weight cells added by 'Add Column' button are 'required' or not.
        feedbackEditPage.clickAddRubricColLink(questionNum);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("", 1, 4);
        feedbackEditPage.clickAddQuestionButton();
        assertTrue(feedbackEditPage.isRubricWeightBoxFocused(questionNum, 1, 4));
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0", 1, 4);

        // Check if the weight cells added by 'Add Row' button are 'required' or not.
        feedbackEditPage.clickAddRubricRowLink(questionNum);
        subQuestionIndex++;
        feedbackEditPage.fillRubricSubQuestionBox("New Sub question", questionNum, subQuestionIndex);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("", subQuestionIndex, 1);
        feedbackEditPage.clickAddQuestionButton();
        assertTrue(feedbackEditPage.isRubricWeightBoxFocused(questionNum, subQuestionIndex, 1));

        // Check if the 'required' attribute is removed and the question is successfully added or not,
        // after the checkbox is unchecked.
        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    @Override
    public void testCustomizeOptions() {

        // TODO somebody do this?

    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("RUBRIC: add question action success");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("RUBRIC qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("RUBRIC: edit question success");

        // Click edit button
        feedbackEditPage.clickEditQuestionButton(1);

        // Check that fields are editable
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEdit.html");

        feedbackEditPage.fillQuestionTextBox("edited RUBRIC qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check question text is updated
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSuccess.html");

        ______TS("RUBRIC: edit sub-questions success");
        feedbackEditPage.clickEditQuestionButton(1);

        // Edit sub-question for row 1
        feedbackEditPage.fillRubricSubQuestionBox("New(0) sub-question text", 1, 0);

        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(1) sub-question text", 1, 2);

        // Remove existing sub-questions
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 0);
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);

        // Add new sub-question
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(2) sub-question text", 1, 3);

        // Remove new sub-question
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 2);

        // Should end up with 1 question

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditSubQuestionSuccess.html");

        ______TS("RUBRIC: edit choices success");
        feedbackEditPage.clickEditQuestionButton(1);

        // Edit choice for col 1
        feedbackEditPage.fillRubricChoiceBox("New(0) choice", 1, 0);

        // Add new choice
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(1) choice", 1, 4);

        // Remove existing choice
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 0);

        // Add new choice
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(2) choice", 1, 5);

        // Remove new choice
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 4);

        // Should end up with 4 choices, including (1) and (2)

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditChoiceSuccess.html");

        ______TS("RUBRIC: edit weight success");
        feedbackEditPage.clickEditQuestionButton(1);

        // Edit the weight of the first choice
        feedbackEditPage.clickAssignWeightsCheckbox(1);
        feedbackEditPage.fillRubricWeightBox("2.25", 1, 0, 0);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditWeightSuccess.html");

        ______TS("RUBRIC: edit descriptions success");
        feedbackEditPage.clickEditQuestionButton(1);

        // Edit description for 0-0
        feedbackEditPage.fillRubricDescriptionBox("New(0) description", 1, 0, 0);

        // Edit description for a new row, to test if the js generated html works.
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New sub-question text", 1, 1);

        feedbackEditPage.fillRubricDescriptionBox("New(1) description", 1, 1, 0);

        // Should end up with 2 rubric descriptions, (0) and (1)

        feedbackEditPage.clickSaveExistingQuestionButton(1);

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionEditDescriptionSuccess.html");

        ______TS("RUBRIC: move rubric column success");
        feedbackEditPage.clickEditQuestionButton(1);
        String[] col0 = feedbackEditPage.getRubricColValues(1, 0);
        String[] col1 = feedbackEditPage.getRubricColValues(1, 1);
        String[] col2 = feedbackEditPage.getRubricColValues(1, 2);
        String[] col3 = feedbackEditPage.getRubricColValues(1, 3);

        feedbackEditPage.verifyRubricColumnsMovability(1, new int[] {0, 1, 2, 3});
        feedbackEditPage.clickAddRubricColLink(1); // new column index 4

        String[] col4 = feedbackEditPage.getRubricColValues(1, 4);

        feedbackEditPage.verifyRubricQuestion(1, new int[] {0, 1, 2, 3, 4}, col0, col1, col2, col3, col4);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 4);
        feedbackEditPage.verifyRubricQuestion(1, new int[] {0, 1, 2, 3}, col0, col1, col2, col3);

        // checking move column for new column and row
        feedbackEditPage.clickAddRubricColLink(1); // new column index 5
        feedbackEditPage.clickAddRubricRowLink(1); // new row index 2
        feedbackEditPage.fillRubricSubQuestionBox("SubQn 2", 1, 2);

        col0 = new String[] {"Col 0 Choice", "Col 0, SubQn 0", "0.10", "Col 0, SubQn 1", "0.10", "Col 0, SubQn 2", "0.10"};
        col1 = new String[] {"Col 1 Choice", "Col 1, SubQn 0", "0.20", "Col 1, SubQn 1", "0.20", "Col 1, SubQn 2", "0.20"};
        col2 = new String[] {"Col 2 Choice", "Col 2, SubQn 0", "0.30", "Col 2, SubQn 1", "0.30", "Col 2, SubQn 2", "0.30"};
        col3 = new String[] {"Col 3 Choice", "Col 3, SubQn 0", "0.40", "Col 3, SubQn 1", "0.40", "Col 3, SubQn 2", "0.40"};
        String[] col5 =
                new String[] {"Col 5 Choice", "Col 5, SubQn 0", "0.50", "Col 5, SubQn 1", "0.50", "Col 5, SubQn 2", "0.50"};
        int[] colIndexes = {0, 1, 2, 3, 5};

        feedbackEditPage.fillAllRubricColumns(1, colIndexes, col0, col1, col2, col3, col5);

        // move last column to first
        moveRubricColumn(1, colIndexes, 4, 0, col0, col1, col2, col3, col5);
        // move second column to last
        moveRubricColumn(1, colIndexes, 1, 4, col5, col0, col1, col2, col3);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionMoveColumnSuccess.html");

        // check buttons for new rubric question
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Edit rubric question 2");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details about this new question");
        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();
        feedbackEditPage.clickAddRubricColLink(-1);
        feedbackEditPage.clickAddRubricRowLink(-1);
        feedbackEditPage.fillRubricSubQuestionBox("SubQn 2", -1, 2);

        feedbackEditPage.fillAllRubricColumns(-1, new int[] {0, 1, 2, 3, 4}, col0, col1, col2, col3, col4);

        feedbackEditPage.verifyRubricQuestion(-1, new int[] {0, 1, 2, 3, 4}, col0, col1, col2, col3, col4);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(-1, 4);
        feedbackEditPage.clickAddRubricColLink(-1);
        feedbackEditPage.fillRubricColumn(-1, 5, col5);
        feedbackEditPage.verifyRubricQuestion(-1, new int[] {0, 1, 2, 3, 5}, col0, col1, col2, col3, col5);

        // move last column to first
        moveRubricColumn(-1, colIndexes, 4, 0, col0, col1, col2, col3, col5);
        // move second column to last
        moveRubricColumn(-1, colIndexes, 1, 4, col5, col0, col1, col2, col3);

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionMoveColumnNewQuestionSuccess.html");
        feedbackEditPage.clickDeleteQuestionLink(2);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    /**
     * Moves a rubric column to the left/right. Accomplishes this by clicking move
     * column left/right buttons and also verifies the rubric question after each move.
     * @param qnNumber question number.
     * @param colIndexes An array containing column indexes in the order displayed in the UI.
     * @param from Index of {@code colIndexes} array which corresponds to the column which is to be moved.
     * @param to Index of {@code colIndexes} array which corresponds to the location to which the column needs to be moved.
     * @param columns Varargs parameter, where each parameter is {@code String[]} which denotes values
     *         of a rubric column. Column values must be given in the order displayed in the UI.
     */
    private void moveRubricColumn(int qnNumber, int[] colIndexes, int from, int to, String[]... columns) {
        Assumption.assertEquals(colIndexes.length, columns.length);
        Assumption.assertTrue(from >= 0 && from < colIndexes.length);
        Assumption.assertTrue(to >= 0 && to < colIndexes.length);

        // This determines which column needs to be swapped and compared with the current column.
        // Value 1 indicates column needs to be moved right. Swaps and comparisons happen accordingly.
        // Value -1 indicates column needs to be moved left. Swaps and comparisons happen accordingly.
        int offset = from < to ? 1 : -1;
        int i = from;

        while (i != to) {
            if (offset > 0) {
                assertTrue(feedbackEditPage.moveRubricColRight(qnNumber, colIndexes[i]));
            } else {
                assertTrue(feedbackEditPage.moveRubricColLeft(qnNumber, colIndexes[i]));
            }

            // swap current column with column
            // to the left/right depending on offset
            String[] temp = columns[i];
            columns[i] = columns[i + offset];
            columns[i + offset] = temp;

            feedbackEditPage.verifyRubricQuestion(qnNumber, colIndexes, columns);
            i += offset;
        }
    }

    @Override
    public void testDeleteQuestionAction() {
        ______TS("RUBRIC: qn delete then cancel");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("RUBRIC: qn delete then accept");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    private void testInputJsValidationForRubricQuestion() {
        // this tests whether the JS validation disallows empty rubric options

        ______TS("JS validation test");

        // add a new question
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");

        // start editing it
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("RUBRIC qn JS validation test");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.clickEditQuestionButton(1);

        // try to remove everything
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 1);
        feedbackEditPage.clickRemoveRubricRowLinkAndConfirm(1, 0);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 3);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 2);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 1);
        feedbackEditPage.clickRemoveRubricColLinkAndConfirm(1, 0);

        // TODO check if the rubric column and link is indeed empty

        // add something so that we know that the elements are still there
        // and so that we don't get empty sub question error
        feedbackEditPage.fillRubricSubQuestionBox("New sub-question text", 1, 0);
        feedbackEditPage.fillRubricDescriptionBox("New(0) description", 1, 0, 0);

        feedbackEditPage.clickSaveExistingQuestionButton(1);

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little choices for Rubric question. Minimum number of options is: 2");
    }

    private void testRubricWeightsFeature() throws Exception {
        testRubricWeightsFeature_shouldToggleStateCorrectly();
        testRubricWeightsFeature_shouldHaveCorrectDefaultValue();
        testRubricWeightsFeature_newQuestion_shouldAddWeightsCorrectly();
        testRubricWeightsFeature_existingQuestion_shouldAddWeightsCorrectly();
    }

    private void testRubricWeightsFeature_shouldToggleStateCorrectly() {

        ______TS("Rubric: Weight cells are visible when assigneWeights checkbox is clicked?");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");

        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();
        // Check if the weight cells are visible or not.
        assertTrue(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 0, 0).isDisplayed());
        assertTrue(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 0, 1).isDisplayed());
        assertTrue(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 1, 0).isDisplayed());
        assertTrue(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 1, 1).isDisplayed());

        // Uncheck checkboxes for consistency among other tests,
        // otherwise these settings will persist after cancelling the question form
        // Uncheck the 'Choices are weighted' checkbox.
        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();
        // Check weight cells are hidden when checkbox is unchecked.
        assertFalse(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 0, 0).isDisplayed());
        assertFalse(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 0, 1).isDisplayed());
        assertFalse(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 1, 0).isDisplayed());
        assertFalse(feedbackEditPage.getRubricWeightBox(NEW_QUESTION_INDEX, 1, 1).isDisplayed());

        // Cancel question
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private void testRubricWeightsFeature_shouldHaveCorrectDefaultValue() throws Exception {

        ______TS("Rubric: Check default weight values");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");
        feedbackEditPage.fillQuestionTextBox("Rubric weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();

        // Fill choices and check corresponding weight values
        feedbackEditPage.verifyFieldValue("rubricWeight--1-0-0", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-0-1", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-0-2", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-0-3", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-1-0", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-1-1", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-1-2", "0");
        feedbackEditPage.verifyFieldValue("rubricWeight--1-1-3", "0");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        // verify html page
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRubricQuestionWeightAddSuccess.html");

        // Delete the question
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
    }

    private void testRubricWeightsFeature_newQuestion_shouldAddWeightsCorrectly() throws Exception {
        ______TS("Success: Add weights for default subQuestions and choices");
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("RUBRIC");
        feedbackEditPage.fillQuestionTextBox("Rubric weight feature", NEW_QUESTION_INDEX);
        feedbackEditPage.fillQuestionDescription("More details", NEW_QUESTION_INDEX);
        feedbackEditPage.clickAssignWeightsCheckboxForNewQuestion();

        // Fill existing weight cells.
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.1", 0, 0);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.2", 0, 1);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.3", 0, 2);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.4", 0, 3);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.1", 1, 0);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.2", 1, 1);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.3", 1, 2);
        feedbackEditPage.fillRubricWeightBoxForNewQuestion("0.4", 1, 3);

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        FeedbackQuestionAttributes question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        assertNotNull(question);

        // Check that weights have been added correctly
        FeedbackRubricQuestionDetails questionDetails = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        List<List<Double>> weights = questionDetails.getRubricWeights();
        // Weight list should contain 2 list (one for each subquestion) containing 4 weights each.
        assertEquals(2, weights.size());
        assertEquals(4, weights.get(0).size());
        assertEquals(4, weights.get(1).size());
        // Check weight values.
        assertEquals(0.1, weights.get(0).get(0));
        assertEquals(0.2, weights.get(0).get(1));
        assertEquals(0.3, weights.get(0).get(2));
        assertEquals(0.4, weights.get(0).get(3));
        assertEquals(0.1, weights.get(1).get(0));
        assertEquals(0.2, weights.get(1).get(1));
        assertEquals(0.3, weights.get(1).get(2));
        assertEquals(0.4, weights.get(1).get(3));
    }

    private void testRubricWeightsFeature_existingQuestion_shouldAddWeightsCorrectly() throws Exception {
        ______TS("Success: Add weights for subquestion added by 'Add Row' button");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("New(2) sub question", 1, 2);

        // Fill weight cells added with the new sub question
        feedbackEditPage.fillRubricWeightBox("0.5", 1, 2, 0);
        feedbackEditPage.fillRubricWeightBox("0.4", 1, 2, 1);
        feedbackEditPage.fillRubricWeightBox("0.3", 1, 2, 2);
        feedbackEditPage.fillRubricWeightBox("0.2", 1, 2, 3);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check that weights have been added successfully
        FeedbackQuestionAttributes question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        FeedbackRubricQuestionDetails questionDetails = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        List<List<Double>> weights = questionDetails.getRubricWeights();
        assertEquals(3, weights.size());
        assertEquals(4, weights.get(2).size());
        // Check weights for each cell
        assertEquals(0.5, weights.get(2).get(0));
        assertEquals(0.4, weights.get(2).get(1));
        assertEquals(0.3, weights.get(2).get(2));
        assertEquals(0.2, weights.get(2).get(3));

        ______TS("Success: Add weights for subquestion added by 'Add Column' button");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddRubricColLink(1);
        feedbackEditPage.fillRubricChoiceBox("New(4) choice", 1, 4);

        // Fill weight cells added with the new sub question
        feedbackEditPage.fillRubricWeightBox("1.0", 1, 0, 4);
        feedbackEditPage.fillRubricWeightBox("1.5", 1, 1, 4);
        feedbackEditPage.fillRubricWeightBox("2.0", 1, 2, 4);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        // Check that weights have been added successfully
        question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        questionDetails = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        weights = questionDetails.getRubricWeights();
        // All subquestions should have 5 cells attached after adding an additional column.
        assertEquals(3, weights.size());
        assertEquals(5, weights.get(0).size());
        assertEquals(5, weights.get(1).size());
        assertEquals(5, weights.get(2).size());
        // Check weights for each cell
        assertEquals(1.0, weights.get(0).get(4));
        assertEquals(1.5, weights.get(1).get(4));
        assertEquals(2.0, weights.get(2).get(4));

        ______TS("Failure: Add weights for empty sub question");
        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddRubricRowLink(1);
        feedbackEditPage.fillRubricSubQuestionBox("                      ", 1, 3); // Empty sub question

        // Fill weight cells added with the new sub question
        feedbackEditPage.fillRubricWeightBox("0", 1, 3, 0);
        feedbackEditPage.fillRubricWeightBox("1", 1, 3, 1);
        feedbackEditPage.fillRubricWeightBox("2", 1, 3, 2);
        feedbackEditPage.fillRubricWeightBox("3", 1, 3, 3);
        feedbackEditPage.fillRubricWeightBox("4", 1, 3, 4);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.FeedbackQuestion.RUBRIC_ERROR_EMPTY_SUB_QUESTION,
                Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT);

        // Check that weights for empty sub question have not been added.
        question = BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1);
        questionDetails = (FeedbackRubricQuestionDetails) question.getQuestionDetails();
        weights = questionDetails.getRubricWeights();
        // The size of the weight list should remain same if adding weights for empty sub question fails.
        assertEquals(3, weights.size());

        // Delete the question
        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).withUserId(instructorId)
                .withCourseId(courseId).withSessionName(feedbackSessionName).withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }

    private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(
            String instructorName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.instructors.get(instructorName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, FeedbackSubmitPage.class);
    }

    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, FeedbackSubmitPage.class);
    }

    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(
            String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, StudentFeedbackResultsPage.class);
    }

    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPageWithViewType(
            String instructorName, String fsName, boolean needAjax, String viewType) {
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                    .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());

        if (needAjax) {
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, String.valueOf(needAjax));
        }

        if (viewType != null) {
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }

        return loginAdminToPage(editUrl, InstructorFeedbackResultsPage.class);
    }

}
