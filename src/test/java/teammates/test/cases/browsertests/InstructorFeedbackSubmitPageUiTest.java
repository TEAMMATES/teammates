package teammates.test.cases.browsertests;

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
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE}.
 */
public class InstructorFeedbackSubmitPageUiTest extends BaseUiTestCase {
    private FeedbackSubmitPage submitPage;

    @Override
    protected void prepareTestData() {
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

        // Test partial response for question
        submitPage.fillResponseRichTextEditor(4, 1, "Feedback to Instructor 3");
        submitPage.selectRecipient(6, 0, "Teammates Test2");
        submitPage.chooseMcqOption(6, 0, "Algo");
        submitPage.selectRecipient(8, 0, "Teammates Test2");
        submitPage.toggleMsqOption(8, 0, "UI");
        submitPage.toggleMsqOption(8, 0, "Design");

        submitPage.fillResponseTextBox(17, 0, 0, "90");
        submitPage.fillResponseTextBox(17, 0, 1, "10");

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
        FeedbackQuestionAttributes fqConstSum =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 19);
        FeedbackQuestionAttributes fqConstSum2 =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104", "First Session", 20);

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
                               fqConstSum.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));

        submitPage.submitWithoutConfirmationEmail();

        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

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
                                   fqConstSum.getId(), "IFSubmitUiT.instr@gmail.tmt", "IFSubmitUiT.instr@gmail.tmt"));

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

        submitPage.fillResponseTextBox(17, 0, 0, "70");
        submitPage.fillResponseTextBox(17, 0, 1, "30");

        submitPage.fillResponseTextBox(18, 0, 0, "90");
        submitPage.fillResponseTextBox(18, 1, 0, "110");
        submitPage.fillResponseTextBox(18, 2, 0, "100");

        submitPage.fillResponseTextBox(19, 0, 0, "85");
        submitPage.fillResponseTextBox(19, 1, 0, "110");
        submitPage.fillResponseTextBox(19, 2, 0, "105");

        submitPage.fillResponseTextBox(20, 0, 0, "35");
        submitPage.fillResponseTextBox(20, 0, 1, "40");
        submitPage.fillResponseTextBox(20, 0, 2, "25");
        submitPage.fillResponseTextBox(20, 1, 0, "10");
        submitPage.fillResponseTextBox(20, 1, 1, "50");
        submitPage.fillResponseTextBox(20, 1, 2, "40");
        submitPage.fillResponseTextBox(20, 2, 0, "15");
        submitPage.fillResponseTextBox(20, 2, 1, "35");
        submitPage.fillResponseTextBox(20, 2, 2, "50");

        submitPage.fillResponseTextBox(22, 0, 0, "35");
        submitPage.fillResponseTextBox(22, 0, 1, "40");
        submitPage.fillResponseTextBox(22, 0, 2, "25");
        submitPage.fillResponseTextBox(22, 1, 0, "10");
        submitPage.fillResponseTextBox(22, 1, 1, "50");
        submitPage.fillResponseTextBox(22, 1, 2, "40");
        submitPage.fillResponseTextBox(22, 2, 0, "15");
        submitPage.fillResponseTextBox(22, 2, 1, "35");
        submitPage.fillResponseTextBox(22, 2, 2, "50");

        // Just check the edited responses, and two new response.
        assertNull(BackDoor.getFeedbackResponse(fq.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 1</td></div>'\""));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 3"));

        // checks that "" can be submitted and other choices reset when "" is clicked
        submitPage.toggleMsqOption(23, 0, "Team 3");
        submitPage.toggleMsqOption(23, 0, "");

        assertFalse(submitPage.isConfirmationEmailBoxTicked());
        submitPage.submitWithoutConfirmationEmail();

        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
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

        assertFalse(frMsq.contains("UI"));
        assertTrue(frMsq.contains("Algo"));
        assertFalse(frMsq.contains("Design"));

        FeedbackNumericalScaleResponseDetails frNumscale =
                (FeedbackNumericalScaleResponseDetails) BackDoor.getFeedbackResponse(
                         fqNumscale.getId(), "IFSubmitUiT.instr@gmail.tmt",
                         "IFSubmitUiT.instr@gmail.tmt").getResponseDetails();

        assertEquals("5", frNumscale.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSum.getId(), "IFSubmitUiT.instr@gmail.tmt",
                         "IFSubmitUiT.instr@gmail.tmt").getResponseDetails();

        assertEquals("70, 30", frConstSum.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum0 =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSum2.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 1</td></div>'\"").getResponseDetails();

        assertEquals("90", frConstSum0.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum1 =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSum2.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 2").getResponseDetails();

        assertEquals("110", frConstSum1.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum2 =
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(
                         fqConstSum2.getId(), "IFSubmitUiT.instr@gmail.tmt", "Team 3").getResponseDetails();

        assertEquals("100", frConstSum2.getAnswerString());

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageFullyFilled.html");
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

        // Test messages for different values entered
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 17;
        assertEquals("All points distributed!", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "80");
        assertEquals("Over allocated 10 points.", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        assertEquals("70 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "");
        assertEquals("Please distribute 100 points among the above options.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        // Test error message when submitting
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "10");
        assertEquals("90 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 0));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 17, 18, 19."
                        + " To skip a distribution question, leave the boxes blank.");

        // Test error message for const sum (to recipient) qn with uneven distribution
        qnNumber = 19;
        assertEquals("100 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "105");
        assertEquals("80 points left to distribute. The same "
                     + "amount of points should not be given multiple times.",
                     submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "106");
        assertEquals("79 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "155");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "155");
        assertEquals("Over allocated 15 points. The same "
                     + "amount of points should not be given multiple times.",
                     submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "154");
        assertEquals("Over allocated 14 points.", submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "50");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "50");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "200");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");
        assertEquals("The same amount of points should not be given multiple times.",
                     submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.submitWithoutConfirmationEmail();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(
                "Please fix the error(s) for distribution question(s) 17, 18, 19."
                        + " To skip a distribution question, leave the boxes blank.");

        // Test error message for const sum (to options) qn with uneven distribution
        qnNumber = 20;
        assertEquals("All points distributed!", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "25");
        assertEquals("10 points left to distribute. The same "
                     + "amount of points should not be given multiple times.",
                     submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "26");
        assertEquals("9 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "50");
        assertEquals("Over allocated 15 points.", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "50");
        assertEquals("Over allocated 25 points. The same "
                     + "amount of points should not be given multiple times.",
                     submitPage.getConstSumMessage(qnNumber, 0));

        // Test error message for const sum (to recipients) qn with uneven distribution for at least some recipients
        qnNumber = 21;
        submitPage.fillResponseTextBox(21, 0, 0, "95");
        submitPage.fillResponseTextBox(21, 1, 0, "95");
        submitPage.fillResponseTextBox(21, 2, 0, "95");
        submitPage.fillResponseTextBox(21, 3, 0, "96");
        assertEquals("19 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "95");
        assertEquals("20 points left to distribute. Different "
                        + "amount of points should be given to some options.",
                submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "105");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "105");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "105");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "105");
        assertEquals("Over allocated 20 points. Different "
                        + "amount of points should be given to some options.",
                submitPage.getConstSumMessage(qnNumber, 3));
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "110");
        assertEquals("Over allocated 25 points.", submitPage.getConstSumMessage(qnNumber, 3));

        submitPage.fillResponseTextBox(qnNumber, 0, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 1, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 2, 0, "100");
        submitPage.fillResponseTextBox(qnNumber, 3, 0, "100");
        assertEquals("Different amount of points should be given to some options.",
                submitPage.getConstSumMessage(qnNumber, 3));

        // Test error message for const sum (to options) qn with uneven distribution for at least some  options
        qnNumber = 22;
        assertEquals("All points distributed!", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "25");
        assertEquals("10 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "25");
        assertEquals("25 points left to distribute. Different "
                        + "amount of points should be given to some options.",
                submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "60");
        assertEquals("Over allocated 10 points.", submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "60");
        submitPage.fillResponseTextBox(qnNumber, 0, 2, "60");
        assertEquals("Over allocated 80 points. Different "
                        + "amount of points should be given to some options.",
                submitPage.getConstSumMessage(qnNumber, 0));

        // For other const sum question, just test one message.
        qnNumber = 18;
        assertEquals("100 points left to distribute.", submitPage.getConstSumMessage(qnNumber, 3));
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

    private void moveToTeam(StudentAttributes student, String newTeam) {
        String backDoorOperationStatus;
        student.team = newTeam;
        backDoorOperationStatus = BackDoor.editStudent(student.email, student);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }

}
