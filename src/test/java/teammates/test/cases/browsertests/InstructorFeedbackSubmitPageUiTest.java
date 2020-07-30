package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.FeedbackSubmitPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_SUBMISSION_PAGE}.
 */
public class InstructorFeedbackSubmitPageUiTest extends BaseLegacyUiTestCase {
    private FeedbackSubmitPage submitPage;

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    protected void refreshTestData() {
        testData = loadDataBundle("/InstructorFeedbackSubmitPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testClosedSessionSubmitAction();
        testSubmitAction();
        testModifyData();
        // No links to test
        testQuestionTypesSubmitAction();
    }

    @Test
    public void testAddingEditingAndDeletingFeedbackParticipantComments() {
        testAddCommentsToQuestionsWithoutResponses();
        testAddCommentsToQuestionsWithResponses();
        testEditCommentsActionAfterAddingComments();
        testDeleteCommentsActionAfterEditingComments();
    }

    private void testContent() throws Exception {
        ______TS("Awaiting session");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Awaiting Session");

        // This is the full HTML verification for Instructor Feedback Submit Page, the rest can all be verifyMainHtml
        submitPage.verifyHtml("/instructorFeedbackSubmitPageAwaiting.html");

        ______TS("Open session");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageOpen.html");

        ______TS("Open session with helper view");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr2", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageOpenWithHelperView.html");

        ______TS("Grace period session");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Grace Period Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageGracePeriod.html");

        ______TS("Closed session");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageClosed.html");

        ______TS("Empty session");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Empty Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageEmpty.html");
    }

    private void testClosedSessionSubmitAction() {
        ______TS("test submitting for closed session");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");

        assertFalse(submitPage.isElementEnabled("response_submit_button"));
    }

    private void testSubmitAction() throws Exception {
        ______TS("create new responses");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        assertTrue(submitPage.isConfirmationEmailBoxTicked());

        submitPage.fillResponseRichTextEditor(1, 0, "Test Self Feedback");
        submitPage.selectRecipient(2, 0, "Alice Betsy</option></td></div>'\"");
        submitPage.fillResponseRichTextEditor(2, 0, "Response to Alice.");
        submitPage.selectRecipient(2, 1, "Drop out");
        submitPage.fillResponseRichTextEditor(2, 1, "Response to student who is going to drop out.");
        submitPage.selectRecipient(2, 2, "Extra guy");
        submitPage.fillResponseRichTextEditor(2, 2, "Response to extra guy.");
        submitPage.fillResponseTextBox(13, 0, "1");
        submitPage.fillResponseTextBox(18, 0, "100");
        submitPage.fillResponseTextBox(18, 1, "100");
        submitPage.fillResponseTextBox(18, 2, "100");
        submitPage.fillResponseTextBox(19, 0, 0, "70");
        submitPage.fillResponseTextBox(19, 0, 1, "30");

        // Test partial response for question
        submitPage.fillResponseRichTextEditor(4, 1, "Feedback to Instructor 3");
        submitPage.selectRecipient(6, 0, "Teammates Test2");
        submitPage.chooseMcqOption(6, 0, "Algo");
        submitPage.selectRecipient(8, 0, "Teammates Test2");
        submitPage.toggleMsqOption(8, 0, "UI");
        submitPage.toggleMsqOption(8, 0, "Design");

        // Just check that some of the responses persisted.
        FeedbackQuestionAttributes fq =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 2);
        FeedbackQuestionAttributes fqPartial =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 6);
        FeedbackQuestionAttributes fqMcq =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 8);
        FeedbackQuestionAttributes fqMsq =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 10);
        FeedbackQuestionAttributes fqNumscale =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 15);
        FeedbackQuestionAttributes fqConstSumRecipient =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 20);
        FeedbackQuestionAttributes fqConstSumOption =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 21);

        assertNull(BackDoor.getFeedbackResponse(
                               fq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.alice.b@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(
                               fqPartial.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr2@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(
                               fqMcq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr2@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(
                               fqMsq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr2@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(
                               fqNumscale.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(
                               fqConstSumRecipient.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(
                               fqConstSumOption.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));

        submitPage.submitWithoutConfirmationEmail();

        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "3, 5, 7, 9, 10, 11, 12, 14, "
                        + "15, 16, 17, 20, 21, 22, 23.");

        assertNotNull(BackDoor.getFeedbackResponse(
                                   fq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.alice.b@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(
                                   fqPartial.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr3@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(
                                   fqMcq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr2@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(
                                   fqMsq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr2@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(
                                   fqNumscale.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(
                                   fqConstSumOption.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePartiallyFilled.html");

        ______TS("edit existing response");

        // Test editing an existing response + fill up rest of responses at the same time
        String editedResponse = "Edited response to Alice.";
        submitPage.fillResponseRichTextEditor(2, 0, editedResponse);
        submitPage.fillResponseRichTextEditor(3, 0, "Feedback to instructors");
        submitPage.fillResponseRichTextEditor(4, 1, "Feedback to instructor 2.");
        submitPage.fillResponseRichTextEditor(4, 2, "Feedback to instructor 4.");
        submitPage.fillResponseRichTextEditor(4, 3, "Feedback to instructor 5.");

        submitPage.chooseMcqOption(5, 0, "UI");
        submitPage.chooseMcqOption(6, 0, "UI"); // Changed from "Algo" to "UI"
        submitPage.selectRecipient(6, 1, "Teammates Test3");
        submitPage.chooseMcqOption(6, 1, "UI");
        submitPage.selectRecipient(6, 2, "Teammates Test4");
        submitPage.chooseMcqOption(6, 2, "UI");

        // Click on "None of the above", the option will be deselected when another option is clicked
        submitPage.toggleMsqOption(7, 0, "");
        submitPage.toggleMsqOption(7, 0, "UI");
        submitPage.toggleMsqOption(7, 0, "Algo");
        submitPage.toggleMsqOption(7, 0, "Design");
        submitPage.toggleMsqOption(8, 0, "UI");
        submitPage.toggleMsqOption(8, 0, "Algo");
        submitPage.toggleMsqOption(8, 0, "Design");
        submitPage.selectRecipient(8, 1, "Teammates Test3");
        submitPage.toggleMsqOption(8, 1, "Design");
        submitPage.selectRecipient(8, 2, "Teammates Test4");
        submitPage.toggleMsqOption(8, 2, "UI");

        submitPage.chooseMcqOption(9, 0, "Drop out (Team 2)");
        submitPage.toggleMsqOption(10, 0, "Alice Betsy</option></td></div>'\" (Team 1</td></div>'\")");
        submitPage.toggleMsqOption(10, 0, "Benny Charles (Team 1</td></div>'\")");
        submitPage.toggleMsqOption(10, 0, "Charlie Davis (Team 2)");
        submitPage.toggleMsqOption(10, 0, "Extra guy (Team 2)");

        submitPage.chooseMcqOption(11, 0, "Team 2");
        submitPage.toggleMsqOption(12, 0, "Team 1</td></div>'\"");
        submitPage.toggleMsqOption(12, 0, "Team 3");

        submitPage.fillResponseTextBox(13, 0, "5");
        submitPage.selectRecipient(14, 0, "Teammates Test2");
        submitPage.fillResponseTextBox(14, 0, "1.5");
        submitPage.selectRecipient(14, 1, "Teammates Test3");
        submitPage.fillResponseTextBox(14, 1, "2");
        submitPage.selectRecipient(14, 2, "Teammates Test4");
        submitPage.fillResponseTextBox(14, 2, "3.5");

        submitPage.chooseMcqOption(15, 0, "Teammates Test2");
        submitPage.toggleMsqOption(16, 0, "Teammates Test");
        submitPage.toggleMsqOption(16, 0, "Teammates Test3");

        // Just check the edited responses, and two new response.
        assertNull(BackDoor.getFeedbackResponse(fq.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSumOption.getId(),
                "IFSubmitUiT.instr@gmail.tmt", "Team 1</td></div>'\""));
        assertNull(BackDoor.getFeedbackResponse(fqConstSumOption.getId(),
                "IFSubmitUiT.instr@gmail.tmt", "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSumOption.getId(),
                "IFSubmitUiT.instr@gmail.tmt", "Team 3"));

        // checks that "" can be submitted and other choices reset when "" is clicked
        submitPage.toggleMsqOption(17, 0, "Team 3");
        submitPage.toggleMsqOption(17, 0, "");

        assertFalse(submitPage.isConfirmationEmailBoxTicked());
        submitPage.submitWithoutConfirmationEmail();

        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "20, 21, 22, 23.");
        assertEquals("<p>" + editedResponse + "</p>",
                    BackDoor.getFeedbackResponse(
                                 fq.getId(), "IFSubmitUiT.instr@gmail.tmt",
                                 "IFSubmitUiT.alice.b@gmail.tmt").getResponseDetails().getAnswerString());

        fq = BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 7);
        assertNotNull(BackDoor.getFeedbackResponse(
                                   fq.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));
        assertEquals("UI",
                     BackDoor.getFeedbackResponse(
                                  fqMcq.getId(), "IFSubmitUiT.instr@gmail.tmt",
                                  "IFSubmitUiT.instr2@gmail.tmt").getResponseDetails().getAnswerString());

        FeedbackMsqResponseDetails frMsq =
                (FeedbackMsqResponseDetails) BackDoor.getFeedbackResponse(
                        fqMsq.getId(), "IFSubmitUiT.instr@gmail.tmt",
                        "IFSubmitUiT.instr2@gmail.tmt").getResponseDetails();

        assertFalse(frMsq.getAnswers().contains("UI"));
        assertTrue(frMsq.getAnswers().contains("Algo"));
        assertFalse(frMsq.getAnswers().contains("Design"));

        FeedbackNumericalScaleResponseDetails frNumscale =
                (FeedbackNumericalScaleResponseDetails) BackDoor.getFeedbackResponse(
                         fqNumscale.getId(), "IFSubmitUiT.instr@gmail.tmt",
                         "IFSubmitUiT.instr@gmail.tmt").getResponseDetails();

        assertEquals("5", frNumscale.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSumOption.getId(), "IFSubmitUiT.instr@gmail.tmt",
                         "IFSubmitUiT.instr@gmail.tmt").getResponseDetails();

        assertEquals("70, 30", frConstSum.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum0 =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSumRecipient.getId(), "IFSubmitUiT.instr@gmail.tmt",
                        "Team 1</td></div>'\"").getResponseDetails();

        assertEquals("100", frConstSum0.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum1 =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSumRecipient.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 2").getResponseDetails();

        assertEquals("100", frConstSum1.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum2 =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSumRecipient.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 3").getResponseDetails();

        assertEquals("100", frConstSum2.getAnswerString());

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageFullyFilled.html");
    }

    private void testAddCommentsToQuestionsWithoutResponses() {
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();

        submitPage.addFeedbackParticipantComment("-5-0", "Comment without response");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, "
                        + "11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23.");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();
        submitPage.verifyCommentRowMissing("-5-0");
    }

    private void testAddCommentsToQuestionsWithResponses() {
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();

        submitPage.selectRecipient(6, 0, "Teammates Test2");
        submitPage.chooseMcqOption(6, 0, "Algo");
        submitPage.selectRecipient(6, 1, "Teammates Test3");
        submitPage.chooseMcqOption(6, 1, "UI");
        submitPage.selectRecipient(6, 2, "Teammates Test4");
        submitPage.chooseMcqOption(6, 2, "UI");
        submitPage.chooseMcqOption(9, 0, "Drop out (Team 2)");

        submitPage.addFeedbackParticipantComment("-6-0", "New MCQ Comment 1");
        submitPage.addFeedbackParticipantComment("-6-1", "New MCQ Comment 2");
        submitPage.addFeedbackParticipantComment("-9-0", "New MCQ Comment 3");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS
                        + "1, 2, 3, 4, 5, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23.");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();
        submitPage.verifyCommentRowContent("-6-0", "New MCQ Comment 1");
        submitPage.verifyCommentRowContent("-6-1", "New MCQ Comment 2");
        submitPage.verifyCommentRowContent("-9-0", "New MCQ Comment 3");

    }

    private void testEditCommentsActionAfterAddingComments() {
        ______TS("edit comments on responses and verify added comments action");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();

        submitPage.editFeedbackParticipantComment("-6-0", "Edited MCQ Comment 1");
        submitPage.editFeedbackParticipantComment("-6-1", "Edited MCQ Comment 2");
        submitPage.editFeedbackParticipantComment("-9-0", "Edited MCQ Comment 3");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS
                        + "1, 2, 3, 4, 5, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23.");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();
        submitPage.verifyCommentRowContent("-6-0", "Edited MCQ Comment 1");
        submitPage.verifyCommentRowContent("-6-1", "Edited MCQ Comment 2");
        submitPage.verifyCommentRowContent("-9-0", "Edited MCQ Comment 3");
    }

    private void testDeleteCommentsActionAfterEditingComments() {
        ______TS("delete comments on responses and verify edited comments action");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.waitForPageToLoad();

        // mcq questions comments
        submitPage.deleteFeedbackResponseComment("-6-0");
        submitPage.verifyCommentRowMissing("-6-0");
        submitPage.deleteFeedbackResponseComment("-6-1");
        submitPage.verifyCommentRowMissing("-6-1");
        submitPage.deleteFeedbackResponseComment("-9-0");
        submitPage.verifyCommentRowMissing("-9-0");
    }

    /**
     *  Tests the behavior of different question types.
     *  Test response validation on client side as well, if any.
     */
    private void testQuestionTypesSubmitAction() {
        ______TS("test submit actions for different question types.");

        testEssaySubmitAction();
        testMcqSubmitAction();
        testMsqSubmitAction();
        testNumScaleSubmitAction();
        testConstSumSubmitAction();
        testContribSubmitAction();
    }

    private void testEssaySubmitAction() {
        ______TS("test submit actions for essay question.");

        // Nothing much to test for input validation.
        // Test fields are disabled when session is closed.
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
        submitPage.waitForAndDismissAlertModal();

        // Test input disabled
        int qnNumber = 1;
        int responseNumber = 0;

        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                    + qnNumber + "-" + responseNumber));

        // Test that the recipient selection is disabled and not visible
        assertFalse(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
    }

    private void testMcqSubmitAction() {
        ______TS("test submit actions for mcq.");

        // Test input disabled
        int qnNumber = 2;
        int responseNumber = 0;

        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                    + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                     + qnNumber + "-" + responseNumber));

        // Test that the recipient selection is disabled and not visible
        assertFalse(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
    }

    private void testMsqSubmitAction() {
        ______TS("test submit actions for msq.");

        // Test input disabled
        int qnNumber = 3;
        int responseNumber = 0;

        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                    + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                     + qnNumber + "-" + responseNumber));

        // Test that the recipient selection is disabled and not visible
        assertFalse(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
    }

    private void testNumScaleSubmitAction() {
        ______TS("test submit actions for numscale questions.");

        // Test input disabled
        int qnNumber = 4;
        int responseNumber = 0;

        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                    + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                     + qnNumber + "-" + responseNumber));

        // Test that the recipient selection is disabled and not visible
        assertFalse(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));

        // Test input entered are valid numbers for the question.
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");

        qnNumber = 14;
        responseNumber = 0;

        // 1 decimal allowed, not a valid response
        submitPage.fillResponseTextBox(qnNumber, responseNumber, "2.5");
        assertEquals("2.5", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        // > 3 decimals allowed but still not a valid response
        submitPage.fillResponseTextBox(qnNumber, responseNumber, "1.123456");
        assertEquals("1.123456", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        // users not allowed to type letters but if somehow in, not a valid response
        submitPage.fillResponseTextBox(qnNumber, responseNumber, "ABCD");
        assertEquals("", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        // if < minimum, textbox will be set to minimum
        submitPage.fillResponseTextBox(qnNumber, responseNumber, "0");
        assertEquals("1", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        /* Note: Negative will be tested properly on Chrome or Safari but not on Firefox,
         * In the Firefox version that we are using for tests, Firefox does not allow for
         * the negative sign to be input so the test below is passing since trying to input
         * -1 will result in 1 being input.
         */
        submitPage.fillResponseTextBox(qnNumber, responseNumber, "-1");
        assertEquals("1", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        /* However, if we test -1.23, on Chrome and Safari it will be changed to 1 but on
         * Firefox it will be 1.23 and not the expected 1. Tests are disabled until
         * the Firefox version that we use for testing allows for this so that
         * developers will not randomly pass or fail depending on browser used for testing
         */
        // submitPage.fillResponseTextBox(qnNumber, responseNumber, "-1.23");
        // assertEquals("1", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        // if > maximum, int or with decimals, textbox will be set to maximum
        submitPage.fillResponseTextBox(qnNumber, responseNumber, "6");
        assertEquals("5", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));

        submitPage.fillResponseTextBox(qnNumber, responseNumber, "6.78");
        assertEquals("5", submitPage.getResponseTextBoxValue(qnNumber, responseNumber));
    }

    private void testConstSumSubmitAction() {
        ______TS("test submit actions for constsum questions.");

        // Test input disabled
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
        submitPage.waitForAndDismissAlertModal();

        int qnNumber = 5;
        int responseNumber = 0;

        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                    + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                     + qnNumber + "-" + responseNumber));

        // Test that the recipient selection is disabled and not visible
        assertFalse(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");

        // Test instructions displayed for filling the form
        qnNumber = 18;
        assertEquals("Total points distributed should add up to 400.",
                     submitPage.getConstSumInstruction(qnNumber));

        qnNumber = 19;
        assertEquals("Total points distributed should add up to 100.",
                     submitPage.getConstSumInstruction(qnNumber));

        qnNumber = 20;
        assertEquals("Total points distributed should add up to 400.\n"
                     + "Every recipient should be allocated different number of points.",
                     submitPage.getConstSumInstruction(qnNumber));

        qnNumber = 21;
        assertEquals("Total points distributed should add up to 100.\n"
                    + "Every option should be allocated different number of points.",
                    submitPage.getConstSumInstruction(qnNumber));

        qnNumber = 22;
        assertEquals("Total points distributed should add up to 400.\n"
                    + "At least one recipient should be allocated different number of points.",
                    submitPage.getConstSumInstruction(qnNumber));

        qnNumber = 23;
        assertEquals("Total points distributed should add up to 100.\n"
                    + "At least one option should be allocated different number of points.",
                    submitPage.getConstSumInstruction(qnNumber));

        // Test messages for const sum (to recipient) qn without restriction
        qnNumber = 18;

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "");
        assertEquals("",
                submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "110");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "110");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "110");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "110");
        assertEquals("Actual total is 440! Remove the extra 40 points allocated.",
                submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "0");
        assertEquals("Actual total is 330! Distribute the remaining 70 points.",
                submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 18."
                        + " To skip a distribution question, leave the boxes blank.");

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");
        assertEquals("All points distributed!", submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "9, 20, 21, 22, 23.");

        // Test messages for const sum (to option) qn without restriction
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 19;

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "");
        assertEquals("",
                submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "80");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "30");
        assertEquals("Actual total is 110! Remove the extra 10 points allocated.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        assertEquals("Actual total is 30! Distribute the remaining 70 points.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 19."
                        + " To skip a distribution question, leave the boxes blank.");

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "70");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "30");
        assertEquals("All points distributed!", submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "9, 20, 21, 22, 23.");

        // Test messages for const sum (to recipient) qn with uneven distribution
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 20;

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "");
        assertEquals("", submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "80");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "120");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "100");
        assertEquals("Actual total is 300! Distribute the remaining 100 points.\n"
                     + "All allocated points are different!",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "100");
        assertEquals("Actual total is 320! Distribute the remaining 80 points.\n"
                     + "Multiple recipients are given same points! eg. 100.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "110");
        assertEquals("Actual total is 330! Distribute the remaining 70 points.\n"
                     + "All allocated points are different!",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "400");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "400");
        assertEquals("Actual total is 900! Remove the extra 500 points allocated.\n"
                     + "Multiple recipients are given same points! eg. 400.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 1, 0, "410");
        assertEquals("Actual total is 910! Remove the extra 510 points allocated.\n"
                     + "All allocated points are different!",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "50");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "50");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "200");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");
        assertEquals("All points distributed!\n"
                     + "Multiple recipients are given same points! eg. 50.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 20."
                        + " To skip a distribution question, leave the boxes blank.");

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "50");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "90");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "160");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "9, 21, 22, 23.");

        // Test messages for const sum (to options) qn with uneven distribution
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 21;

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "");
        assertEquals("", submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "25");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "40");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "25");
        assertEquals("Actual total is 90! Distribute the remaining 10 points.\n"
                     + "Multiple options are given same points! eg. 25.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "30");
        assertEquals("Actual total is 95! Distribute the remaining 5 points.\n"
                     + "All allocated points are different!",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 2, "40");
        assertEquals("Actual total is 110! Remove the extra 10 points allocated.\n"
                        + "Multiple options are given same points! eg. 40.",
                submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 2, "60");
        assertEquals("Actual total is 130! Remove the extra 30 points allocated.\n"
                     + "All allocated points are different!",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 21."
                        + " To skip a distribution question, leave the boxes blank.");

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "10");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "30");
        assertEquals("All points distributed!\n"
                        + "All allocated points are different!",
                submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "9, 22, 23.");

        // Test messages for const sum (to recipients) qn with uneven distribution for at least some recipients
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 22;

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "");
        assertEquals("", submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "95");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "95");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "95");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "95");
        assertEquals("Actual total is 380! Distribute the remaining 20 points.\n"
                        + "All recipients are given 95 points. "
                        + "Please allocate different points to at least one recipient.",
                submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");
        assertEquals("Actual total is 385! Distribute the remaining 15 points.\n"
                     + "At least one recipient has been allocated different number of points.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "105");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "105");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "105");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "105");
        assertEquals("Actual total is 420! Remove the extra 20 points allocated.\n"
                     + "All recipients are given 105 points. "
                     + "Please allocate different points to at least one recipient.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 3, 0, "110");
        assertEquals("Actual total is 425! Remove the extra 25 points allocated.\n"
                     + "At least one recipient has been allocated different number of points.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");
        assertEquals("All points distributed!\n"
                     + "All recipients are given 100 points. "
                     + "Please allocate different points to at least one recipient.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 22."
                        + " To skip a distribution question, leave the boxes blank.");

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "110");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "120");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "130");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "40");
        assertEquals("All points distributed!\n"
                        + "At least one recipient has been allocated different number of points.",
                submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "9, 23.");

        // Test messages for const sum (to options) qn with uneven distribution for at least some options
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 23;

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "");
        assertEquals("", submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "25");
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "25");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "25");
        assertEquals("Actual total is 75! Distribute the remaining 25 points.\n"
                        + "All options are given 25 points. "
                        + "Please allocate different points to at least one option.",
                submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 1, "40");
        assertEquals("Actual total is 90! Distribute the remaining 10 points.\n"
                     + "At least one option has been allocated different number of points.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "40");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "40");
        assertEquals("Actual total is 120! Remove the extra 20 points allocated.\n"
                        + "All options are given 40 points. "
                        + "Please allocate different points to at least one option.",
                submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "30");
        assertEquals("Actual total is 110! Remove the extra 10 points allocated.\n"
                     + "At least one option has been allocated different number of points.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 23."
                        + " To skip a distribution question, leave the boxes blank.");

        submitPage.fillResponseTextBox(qnNumber, 0, 1, "20");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "50");
        assertEquals("All points distributed!\n"
                        + "At least one option has been allocated different number of points.",
                submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                Const.StatusMessages.FEEDBACK_UNANSWERED_QUESTIONS + "9.");
    }

    private void testContribSubmitAction() {
        ______TS("test submit actions for contribution questions.");

        // No tests from instructor since contribution questions are only from students to own team members.
        // Test by logging in as student instead.

        // Test input disabled
        submitPage = loginToStudentFeedbackSubmitPage("Danny", "Closed Session");

        int qnNumber = 1;
        int responseNumber = 0;

        submitPage.waitForAndDismissAlertModal();
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                    + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-"
                                                     + qnNumber + "-" + responseNumber));

        // Test that the recipient selection is disabled and not visible
        assertFalse(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-"
                                                     + qnNumber + "-" + responseNumber));
    }

    private void testModifyData() throws Exception {
        ______TS("modify data");

        // Next, we edit some student data to cover editing of students after creating the responses.

        // move one student out of Team 2 into a new team, should not cause the existing response to disappear
        StudentAttributes extraGuy = testData.students.get("ExtraGuy");
        moveToTeam(extraGuy, "New Team");

        // delete one student, should remove (hide on page render; not deleted) the response made to him,
        // and change the number of options in the recipient dropdown list.
        StudentAttributes dropOutGuy = testData.students.get("DropOut");
        String backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course, dropOutGuy.email);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageModified.html");
    }

    private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(String instructorName, String fsName) {
        AppUrl editUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
                          .withUserId(testData.instructors.get(instructorName).googleId)
                          .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                          .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPageOld(editUrl, FeedbackSubmitPage.class);
    }

    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
                          .withUserId(testData.students.get(studentName).googleId)
                          .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                          .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPageOld(editUrl, FeedbackSubmitPage.class);
    }

    private void moveToTeam(StudentAttributes student, String newTeam) {
        String backDoorOperationStatus;
        student.team = newTeam;
        backDoorOperationStatus = BackDoor.editStudent(student.email, student);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }

}
