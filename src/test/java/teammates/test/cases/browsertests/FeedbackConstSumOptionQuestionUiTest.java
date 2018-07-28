package teammates.test.cases.browsertests;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackConstantSumDistributePointsType;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for constant sum (options) questions.
 */
public class FeedbackConstSumOptionQuestionUiTest extends FeedbackQuestionUiTest {

    private static final int NEW_QUESTION_INDEX = -1;
    private static final String QN_TYPE = "constSum";

    private InstructorFeedbackEditPage feedbackEditPage;

    private String courseId;
    private String feedbackSessionName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackConstSumOptionQuestionUiTest.json");
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

        //TODO: move/create other ConstSumOption question related UI tests here.
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
        testDisableConstSumPointsAction();
        testUiConsistencyForNewQuestion();
    }

    @Override
    public void testNewQuestionFrame() {
        ______TS("CONSTSUM-option: new question (frame) link");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("empty options");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("ConstSum-option qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        feedbackEditPage.fillConstSumPointsBoxForNewQuestion("");
        assertEquals("100", feedbackEditPage.getConstSumPointsBoxForNewQuestion());

        feedbackEditPage.selectConstSumPointsOptionsForNewQuestion("PerOption");
        feedbackEditPage.fillConstSumPointsForEachOptionBoxForNewQuestion("");
        assertEquals("100", feedbackEditPage.getConstSumPointsForEachOptionBoxForNewQuestion());

        feedbackEditPage.clickAddQuestionButton();

        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little options for Distribute points (among options) question. Minimum number of options is: 2.");

        ______TS("remove when 1 left");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test const sum question");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        assertTrue(feedbackEditPage.verifyNewConstSumQuestionFormIsDisplayed());

        feedbackEditPage.clickRemoveConstSumOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));

        // TODO: Check that after deleting, the value is cleared
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-0--1"));
        feedbackEditPage.clickRemoveConstSumOptionLinkForNewQuestion(0);
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-0--1"));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Too little options for Distribute points (among options) question. Minimum number of options is: 2.");

        ______TS("duplicate options");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test duplicate options");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        feedbackEditPage.fillConstSumOptionForNewQuestion(0, "duplicate option");
        feedbackEditPage.fillConstSumOptionForNewQuestion(1, "duplicate option");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.FeedbackQuestion.CONST_SUM_ERROR_DUPLICATE_OPTIONS);
    }

    @Override
    public void testCustomizeOptions() {
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");

        feedbackEditPage.fillConstSumOptionForNewQuestion(0, "Option 1");
        feedbackEditPage.fillConstSumOptionForNewQuestion(1, "Option 2");

        ______TS("CONST SUM: add option");

        assertFalse(feedbackEditPage.isElementPresent("constSumOptionRow-2--1"));
        feedbackEditPage.clickAddMoreConstSumOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-2--1"));

        ______TS("CONST SUM: remove option");

        feedbackEditPage.fillConstSumOptionForNewQuestion(2, "Option 3");
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));
        feedbackEditPage.clickRemoveConstSumOptionLinkForNewQuestion(1);
        assertFalse(feedbackEditPage.isElementPresent("constSumOptionRow-1--1"));

        ______TS("CONST SUM: add option after remove");

        feedbackEditPage.clickAddMoreConstSumOptionLinkForNewQuestion();
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-3--1"));
        feedbackEditPage.clickAddMoreConstSumOptionLinkForNewQuestion();
        feedbackEditPage.fillConstSumOptionForNewQuestion(4, "Option 5");
        assertTrue(feedbackEditPage.isElementPresent("constSumOptionRow-4--1"));
    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("CONST SUM: add question action success");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("const sum qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectRecipientsToBeStudentsAndWaitForVisibilityMessageToLoad();
        feedbackEditPage.fillConstSumPointsBoxForNewQuestion("30");
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS + "-1",
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        assertEquals("30", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("30", feedbackEditPage.getConstSumPointsForEachOptionBox(1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("CONST SUM: edit question success");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillQuestionTextBox("edited const sum qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);
        feedbackEditPage.selectConstSumPointsOptions("PerOption", 1);
        feedbackEditPage.fillConstSumPointsForEachOptionBox("200", 1);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        assertEquals("200", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("200", feedbackEditPage.getConstSumPointsForEachOptionBox(1));

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackConstSumOptionQuestionEditSuccess.html");

        ______TS("CONST SUM: edit question failure due to duplicate options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.fillConstSumOption(0, "duplicate option", 1);
        feedbackEditPage.fillConstSumOption(1, "duplicate option", 1);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(
                Const.FeedbackQuestion.CONST_SUM_ERROR_DUPLICATE_OPTIONS);

        ______TS("CONST SUM: edit to force uneven distribution for all options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS + "-1",
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());
        assertFalse(feedbackEditPage.isElementEnabled("constSumDistributePointsSelect-1"));
        feedbackEditPage.clickDistributePointsOptionsCheckbox(1);
        assertTrue(feedbackEditPage.isElementEnabled("constSumDistributePointsSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS + "-1",
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        assertEquals("200", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("200", feedbackEditPage.getConstSumPointsForEachRecipientBox(1));

        ______TS("CONST SUM: edit to force uneven distribution for at least some options");

        feedbackEditPage.clickEditQuestionButton(1);
        assertTrue(feedbackEditPage.isElementEnabled("constSumDistributePointsSelect-1"));
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS + "-1",
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());
        feedbackEditPage.selectConstSumDistributePointsOptions("At least some options", 1);
        feedbackEditPage.verifyFieldValue(
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS + "-1",
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);

        assertEquals("200", feedbackEditPage.getConstSumPointsBox(1));
        assertEquals("200", feedbackEditPage.getConstSumPointsForEachRecipientBox(1));
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
        feedbackEditPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }

    /**
     * Tests that distribute points among options (new and existing) can be reordered using drag and drop mechanism.
     * @throws Exception when option is not draggable
     */
    private void testReorderOptions() throws Exception {

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Test question text");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.clickAddMoreConstSumOptionLinkForNewQuestion();
        feedbackEditPage.clickAddMoreConstSumOptionLinkForNewQuestion();

        feedbackEditPage.fillConstSumOptionForNewQuestion(0, "Choice 1");
        feedbackEditPage.fillConstSumOptionForNewQuestion(1, "Choice 2");
        feedbackEditPage.fillConstSumOptionForNewQuestion(2, "Choice 3");
        feedbackEditPage.fillConstSumOptionForNewQuestion(3, "Choice 4");

        ______TS("CONSTSUM: reorder existing options");

        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, NEW_QUESTION_INDEX, 2, 0);
        feedbackEditPage.clickAddQuestionButton();
        JSONObject constSumQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                                                                    .questionMetaData.getValue());
        assertEquals("[\"Choice 3\",\"Choice 1\",\"Choice 2\",\"Choice 4\"]",
                     constSumQuestionDetails.get("constSumOptions").toString());

        ______TS("CONSTSUM: add option and reorder");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickAddMoreConstSumOptionLink(1);
        feedbackEditPage.fillConstSumOption(4, "New Choice", 1);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        constSumQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                                                            .questionMetaData.getValue());
        assertEquals("[\"Choice 3\",\"New Choice\",\"Choice 1\",\"Choice 2\",\"Choice 4\"]",
                     constSumQuestionDetails.get("constSumOptions").toString());

        ______TS("CONSTSUM: delete option and reorder");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickRemoveConstSumOptionLink(2, 1);
        feedbackEditPage.fillConstSumOption(1, "Old Choice", 1);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        constSumQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                                                            .questionMetaData.getValue());
        assertEquals("[\"Choice 3\",\"Choice 4\",\"Old Choice\",\"Choice 2\"]",
                     constSumQuestionDetails.get("constSumOptions").toString());

        ______TS("CONSTSUM: add, delete and reorder options");

        feedbackEditPage.clickEditQuestionButton(1);
        feedbackEditPage.clickRemoveConstSumOptionLink(2, 1);
        feedbackEditPage.clickAddMoreConstSumOptionLink(1);
        feedbackEditPage.clickAddMoreConstSumOptionLink(1);
        feedbackEditPage.fillConstSumOption(4, "New Choice", 1);
        feedbackEditPage.fillConstSumOption(5, "Newer Choice", 1);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 5, 0);
        feedbackEditPage.dragAndDropQuestionOption(QN_TYPE, 1, 4, 1);
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        constSumQuestionDetails = new JSONObject(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1)
                                                            .questionMetaData.getValue());
        assertEquals("[\"Newer Choice\",\"New Choice\",\"Choice 3\",\"Choice 4\",\"Choice 2\"]",
                     constSumQuestionDetails.get("constSumOptions").toString());

        ______TS("CONSTSUM: delete question");

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickCancel();
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

    }

    /**
     * The element {@code constSumOption_Option} hides, and element {@code constSumOption_Recipient}
     * becomes visible when a CONSTSUM_RECIPIENT question is added.<br>
     * Check that the changes are reverted when a CONSTSUM_OPTION question is added.
     */
    private void testUiConsistencyForNewQuestion() {
        ______TS("CONSTSUM-option after CONSTSUM-recipient: Check ui consistency success case");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_RECIPIENT");
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");

        assertTrue(feedbackEditPage.isElementVisible(By.id("constSumPointsTotal--1")));
        assertTrue(feedbackEditPage.isElementVisible(By.id("constSumOption_Option--1")));
        assertFalse(feedbackEditPage.isElementVisible(By.id("constSumOption_Recipient--1")));
        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

    }

    /**
     * Tests that the constSumPoints* number fields gets disabled, if the corresponding radio button is not checked.
     */
    private void testDisableConstSumPointsAction() {
        ______TS("Success case: CONSTSUM-option points to distribute field disables when radio button is unchecked");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionTypeAndWaitForNewQuestionPanelReady("CONSTSUM_OPTION");

        // Make sure that constSumPointsTotal radio button is selected by default
        assertTrue(browser.driver.findElement(By.id("constSumPointsTotal--1")).isSelected());
        // Verify that constSumPointsForEachOption field is disabled
        feedbackEditPage.verifyUnclickable(browser.driver.findElement(By.id("constSumPointsForEachOption--1")));
        // Select constSumPointsPerOption radio button
        feedbackEditPage.selectConstSumPointsOptionsForNewQuestion("PerOption");
        // Verify that constSumPoints field is disabled
        feedbackEditPage.verifyUnclickable(browser.driver.findElement(By.id("constSumPoints--1")));
        // Select constSumPointsTotal radio button.
        feedbackEditPage.selectConstSumPointsOptionsForNewQuestion("Total");
        // Verify that constSumPointsForEachOption field is disabled
        feedbackEditPage.verifyUnclickable(browser.driver.findElement(By.id("constSumPointsForEachOption--1")));

        feedbackEditPage.clickDiscardChangesLinkForNewQuestion();
        feedbackEditPage.waitForConfirmationModalAndClickOk();

    }
}
