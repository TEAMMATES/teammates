package teammates.test.cases.browsertests;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.FeedbackSubmitPage;
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
        testStudentSubmitPage();

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
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(-1);
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
        // checking inputs after enabling max selectable restriction
        // scrollToElement(feedbackEditPage.getMsqMaxSelectableChoicesElement(qnNumber));
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // checking inputs when max = 4, 4 options
        // are present and an option is removed
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, 4);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.clickRemoveMsqOptionLink(0, qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(3, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));

        // add an option back
        feedbackEditPage.clickAddMoreMsqOptionLink(qnNumber);
        feedbackEditPage.fillMsqOption(qnNumber, 4, "X");
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(3, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));

        // enable min selectable restriction only
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);

        // check inputs after enabling min selectable restriction
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // checking inputs when min = 4, 4 options
        // are present and an option is removed
        feedbackEditPage.setMsqMinSelectableChoices(qnNumber, 4);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.clickRemoveMsqOptionLink(1, qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(3, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // add an option back
        feedbackEditPage.clickAddMoreMsqOptionLink(qnNumber);
        feedbackEditPage.fillMsqOption(qnNumber, 5, "Y");
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(3, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // enable min and max selectable restrictions
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);

        // check inputs after enabling min and max selectable restriction
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // if max is decreased from 3 to 2, min should also decrease
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, 2);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(2, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // checking inputs when min = 4, max = 4,
        // 4 options are present and an option is removed
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, 4);
        feedbackEditPage.setMsqMinSelectableChoices(qnNumber, 4);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.clickRemoveMsqOptionLink(2, qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(3, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));
        assertEquals(3, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // add an option back
        feedbackEditPage.clickAddMoreMsqOptionLink(qnNumber);
        feedbackEditPage.fillMsqOption(qnNumber, 6, "Z");
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(3, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));
        assertEquals(3, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));

        // disable min and max selectable restrictions
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
    }

    // assumes student is already selected
    private void checkMinMaxSelectableRestrictionsForAllGenerateOptionSelections(int qnNumber) {
        // Check min/max selectable restrictions for MSQ
        // question with options generated from students
        checkMinMaxSelectableRestrictionForGeneratedOption(qnNumber);

        // Check min/max selectable restrictions for
        // MSQ question with options generated from teams
        feedbackEditPage.selectMsqGenerateOptionsFor("teams", qnNumber);
        checkMinMaxSelectableRestrictionForGeneratedOption(qnNumber);

        // enable min and max selectable restrictions,
        // change generated options for selection
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
        feedbackEditPage.selectMsqGenerateOptionsFor("students", qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // disable min and max selectable restrictions
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
    }

    private void checkMinMaxSelectableRestrictionForGeneratedOption(int qnNumber) {
        int numOfOptions = feedbackEditPage.getNumOfMsqOptions(qnNumber);

        // checking inputs after enabling max selectable restriction
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // enable min selectable restriction only
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);
        feedbackEditPage.toggleMsqMinSelectableChoices(qnNumber);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // enable min and max selectable restrictions
        feedbackEditPage.toggleMsqMaxSelectableChoices(qnNumber);

        // check inputs after enabling min and max selectable restriction
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);

        // set max = numOfOptions and min = numOfOptions,
        // then decreasing max must decrease min too
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, numOfOptions);
        feedbackEditPage.setMsqMinSelectableChoices(qnNumber, numOfOptions);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        feedbackEditPage.setMsqMaxSelectableChoices(qnNumber, numOfOptions - 1);
        feedbackEditPage.verifyMsqMinMaxSelectableChoices(qnNumber);
        assertEquals(numOfOptions - 1, feedbackEditPage.getMsqMinSelectableChoices(qnNumber));
        assertEquals(numOfOptions - 1, feedbackEditPage.getMsqMaxSelectableChoices(qnNumber));

        // disable min and max selectable restrictions
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

    /**
     * Test submission of MSQ questions.
     */
    public void testStudentSubmitPage() {
        addQuestionsForStudentSubmitTest();

        FeedbackSubmitPage submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FMsqQnUiT.CS2104", "openSession");
        List<String> choiceNames = new ArrayList<>();

        for (String key : testData.students.keySet()) {
            StudentAttributes student = testData.students.get(key);

            choiceNames.add(student.name + " (" + student.team + ')');
        }

        // Submit Q1 with 1 option checked
        submitPage.toggleMsqOption(1, 0, choiceNames.get(0));
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_MSQ_MIN_CHECK, 1, 2));

        // Submit Q1 with 2 options checked
        submitPage.toggleMsqOption(1, 0, choiceNames.get(2));
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        submitPage.waitForConfirmationModalAndClickOk();

        // Submit Q1 with 3 options checked
        submitPage.toggleMsqOption(1, 0, choiceNames.get(3));
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        submitPage.waitForConfirmationModalAndClickOk();

        // Submit Q1 with 4 options checked
        submitPage.toggleMsqOption(1, 0, choiceNames.get(4));
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_MSQ_MAX_CHECK, 1, 3));

        // Check 3 options only for Q1 to avoid status errors
        submitPage.toggleMsqOption(1, 0, choiceNames.get(4));

        // Submit Q2 1st recipient with 2 options checked
        submitPage.toggleMsqOption(2, 0, "B");
        submitPage.toggleMsqOption(2, 0, "C");
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_MSQ_MIN_CHECK, 2, 3));

        // Submit Q2 1st recipient with 3 options checked
        submitPage.toggleMsqOption(2, 0, "E");
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        submitPage.waitForConfirmationModalAndClickOk();

        // Submit Q2 1st recipient with 4 options checked
        submitPage.toggleMsqOption(2, 0, "A");
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_MSQ_MAX_CHECK, 2, 3));
    }

    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(editUrl, FeedbackSubmitPage.class);
    }

    /**
     * Adds 2 MSQ questions to FS to test min
     * and max selectable choices restriction.
     */
    private void addQuestionsForStudentSubmitTest() {
        // 1. With generated options, min = 2, max = 3
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("With generated options, min = 2, max = 3");
        feedbackEditPage.clickGenerateMsqOptionsCheckbox(-1);
        feedbackEditPage.toggleMsqMaxSelectableChoices(-1);
        feedbackEditPage.setMsqMaxSelectableChoices(-1, 3);
        feedbackEditPage.toggleMsqMinSelectableChoices(-1);
        feedbackEditPage.setMsqMinSelectableChoices(-1, 2);
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.selectRecipientToBe(FeedbackParticipantType.NONE, -1);
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);

        // 2. Custom options, min = 3, max = 3
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("MSQ");
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Custom options, min = 3, max = 3");
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.clickAddMoreMsqOptionLinkForNewQuestion();
        feedbackEditPage.fillMsqOptionForNewQuestion(0, "A");
        feedbackEditPage.fillMsqOptionForNewQuestion(1, "B");
        feedbackEditPage.fillMsqOptionForNewQuestion(2, "C");
        feedbackEditPage.fillMsqOptionForNewQuestion(3, "D");
        feedbackEditPage.fillMsqOptionForNewQuestion(4, "E");
        feedbackEditPage.toggleMsqMaxSelectableChoices(-1);
        feedbackEditPage.setMsqMaxSelectableChoices(-1, 3);
        feedbackEditPage.toggleMsqMinSelectableChoices(-1);
        feedbackEditPage.setMsqMinSelectableChoices(-1, 3);
        feedbackEditPage.enableOtherFeedbackPathOptionsForNewQuestion();
        feedbackEditPage.selectGiverToBeStudents();
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
    }
}
