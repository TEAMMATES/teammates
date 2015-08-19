package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Calendar;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSessionNotVisiblePage;
import teammates.test.pageobjects.FeedbackSubmitPage;

/**
 * Tests 'Submit Feedback' view of students.
 *
 * The first team is named "Team >'"< 1" to test cases where a HTML character exists in the team name.
 *
 * SUT: {@link StudentFeedbackSubmitPage}.
 */
public class StudentFeedbackSubmitPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private FeedbackSubmitPage submitPage;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentFeedbackSubmitPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testSubmitAction();
        testInputValidation();
        testLinks();
        testModifyData();
    }

    private void testLinks() {
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Awaiting Session");
        submitPage.linkOnHomeLink();
        submitPage = submitPage.goToPreviousPage(FeedbackSubmitPage.class);
        submitPage.linkOnProfileLink();
        submitPage = submitPage.goToPreviousPage(FeedbackSubmitPage.class);
        submitPage.linkOnCommentsLink();

        submitPage.logout();
        submitPage = loginToStudentFeedbackSubmitPage(testData.students.get("DropOut"), "Open Session");
        submitPage.clickAndCancel(browser.driver.findElement(By.id("studentHomeNavLink")));
        submitPage.clickAndCancel(browser.driver.findElement(By.id("studentProfileNavLink")));
        submitPage.clickAndCancel(browser.driver.findElement(By.id("studentCommentsNavLink")));
    }

    private void testContent() {
        ______TS("unreg student");

        submitPage = loginToStudentFeedbackSubmitPage(testData.students.get("DropOut"), "Open Session");

        // This is the full HTML verification for Unregistered Student Feedback Submit Page, the rest can all be verifyMainHtml
        submitPage.verifyHtml("/unregisteredStudentFeedbackSubmitPageOpen.html");

        ______TS("Awaiting session");

        // this session contains questions to instructors, and since instr3 is not displayed to students,
        // student cannot submit to instr3
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Awaiting Session");

        // This is the full HTML verification for Registered Student Feedback Submit Page, the rest can all be verifyMainHtml
        submitPage.verifyHtml("/studentFeedbackSubmitPageAwaiting.html");

        ______TS("Open session");

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageOpen.html");

        ______TS("Grace period session");

        FeedbackSessionAttributes fs = BackDoor.getFeedbackSession("SFSubmitUiT.CS2104",
                                                                   "Grace Period Session");

        Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endDate.add(Calendar.MINUTE, -1);
        fs.gracePeriod = 10;
        fs.endTime = endDate.getTime();
        BackDoor.editFeedbackSession(fs);
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Grace Period Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageGracePeriod.html");

        ______TS("Closed session");

        // see comment for awaiting session
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Closed Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageClosed.html");

        ______TS("Empty session");

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Empty Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageEmpty.html");
        
        ______TS("Not yet visible session");
        
        FeedbackSessionNotVisiblePage fsNotVisiblePage;
        fsNotVisiblePage = loginToStudentFeedbackSubmitPageFeedbackSessionNotVisible("Alice", "Not Yet Visible Session");
        fsNotVisiblePage.verifyHtmlMainContent("/studentFeedbackSubmitPageNotYetVisible.html");
        
    }

    private void testSubmitAction() {

        ______TS("create new responses");

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");

        submitPage.fillResponseTextBox(1, 0, "Test Self Feedback");
        submitPage.selectRecipient(2,0,"Benny Charles");
        submitPage.fillResponseTextBox(2, 0, "Response to Benny.");
        submitPage.selectRecipient(2, 1, "Drop out");
        submitPage.fillResponseTextBox(2, 1, "Response to student who is going to drop out.");
        submitPage.selectRecipient(2, 2, "Extra guy");
        submitPage.fillResponseTextBox(2, 2, "Response to extra guy.");
        submitPage.fillResponseTextBox(14, 0, "1");

        // Test partial response for question
        submitPage.fillResponseTextBox(4, 1, "Feedback to team 3");
        submitPage.chooseMcqOption(7, 0, "Algo");
        submitPage.toggleMsqOption(9, 0, "UI");
        submitPage.toggleMsqOption(9, 0, "Design");

        submitPage.fillResponseTextBox(18, 0, 0, "90");
        submitPage.fillResponseTextBox(18, 0, 1, "10");

        submitPage.chooseContribOption(20, 0, "Equal share");

        // Just check that some of the responses persisted.
        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                     "First Session", 2);
        FeedbackQuestionAttributes fqPartial = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                            "First Session", 4);
        FeedbackQuestionAttributes fqMcq = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                        "First Session", 8);
        FeedbackQuestionAttributes fqMsq = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                        "First Session", 10);
        FeedbackQuestionAttributes fqNumscale = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                             "First Session", 15);
        FeedbackQuestionAttributes fqConstSum = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                             "First Session", 19);
        FeedbackQuestionAttributes fqConstSum2 = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                              "First Session", 20);
        FeedbackQuestionAttributes fqContrib = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104",
                                                                            "First Session", 21);

        assertNull(BackDoor.getFeedbackResponse(fq.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "SFSubmitUiT.benny.c@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "Team 3"));
        assertNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqNumscale.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqContrib.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqContrib.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "SFSubmitUiT.benny.c@gmail.tmt"));

        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                     submitPage.getStatus());

        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "SFSubmitUiT.benny.c@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "Team 3"));
        assertNotNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "Team 2"));
        assertNotNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "Team 2"));
        assertNotNull(BackDoor.getFeedbackResponse(fqNumscale.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqConstSum.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqContrib.getId(),
                                                   "SFSubmitUiT.alice.b@gmail.tmt",
                                                   "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqContrib.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "SFSubmitUiT.benny.c@gmail.tmt"));

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPagePartiallyFilled.html");

        ______TS("test toggle radio button");

        submitPage.chooseMcqOption(7, 1, "UI");
        submitPage.chooseMcqOption(7, 1, "Algo");
        submitPage.chooseMcqOption(7, 1, "Algo"); // toggle 'Algo' radio option

        submitPage.clickSubmitButton();

        assertNull(BackDoor.getFeedbackResponse(fqMcq.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "Team 3"));

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPagePartiallyFilled.html");

        ______TS("edit existing response");

        // Test editing an existing response
        // + fill up rest of responses at the same time
        String editedResponse = "Edited response to Benny.";
        submitPage.fillResponseTextBox(2, 0, editedResponse);
        submitPage.fillResponseTextBox(3, 0, "Feedback to instructors");
        submitPage.fillResponseTextBox(4, 1, "Feedback to team 2.");
        submitPage.fillResponseTextBox(5, 0, "Feedback to teammate.");

        submitPage.chooseMcqOption(6, 0, "UI");
        submitPage.chooseMcqOption(7, 0, "UI"); // Changed from "Algo" to "UI"
        submitPage.chooseMcqOption(7, 1, "UI");

        submitPage.toggleMsqOption(8, 0, "UI");
        submitPage.toggleMsqOption(8, 0, "Algo");
        submitPage.toggleMsqOption(8, 0, "Design");
        submitPage.toggleMsqOption(9, 0, "UI");
        submitPage.toggleMsqOption(9, 0, "Algo");
        submitPage.toggleMsqOption(9, 0, "Design");
        submitPage.toggleMsqOption(9, 1, "Design");

        submitPage.chooseMcqOption(10, 0, "Drop out (Team 2)");
        submitPage.toggleMsqOption(11, 0, "Alice Betsy (Team >'\"< 1)");
        submitPage.toggleMsqOption(11, 0, "Benny Charles (Team >'\"< 1)");
        submitPage.toggleMsqOption(11, 0, "Charlie Davis (Team 2)");
        submitPage.toggleMsqOption(11, 0, "Extra guy (Team 2)");

        submitPage.chooseMcqOption(12, 0, "Team 2");
        submitPage.toggleMsqOption(13, 0, "Team >'\"< 1");
        submitPage.toggleMsqOption(13, 0, "Team 3");

        submitPage.fillResponseTextBox(14, 0, "5");
        submitPage.fillResponseTextBox(15, 0, "1.5");
        submitPage.fillResponseTextBox(15, 1, "2.5");

        submitPage.chooseMcqOption(16, 0, "Teammates Test2");
        submitPage.toggleMsqOption(17, 0, "Teammates Test");
        submitPage.toggleMsqOption(17, 0, "Teammates Test3");

        submitPage.fillResponseTextBox(18, 0, 0, "70");
        submitPage.fillResponseTextBox(18, 0, 1, "30");

        submitPage.fillResponseTextBox(19, 0, 0, "90");
        submitPage.fillResponseTextBox(19, 1, 0, "110");

        submitPage.chooseContribOption(20, 1, "0%");

        // Just check the edited responses, and two new response.
        assertNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                                                "SFSubmitUiT.alice.b@gmail.tmt",
                                                "Team 3"));

        submitPage.clickSubmitButton();

        //check new response
        fqPartial = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104", "First Session", 4);
        assertNotNull(BackDoor.getFeedbackResponse(fqPartial.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "Team 2"));

        //check edited
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());
        assertEquals(editedResponse,BackDoor.getFeedbackResponse(fq.getId(),
                                                                 "SFSubmitUiT.alice.b@gmail.tmt",
                                                                 "SFSubmitUiT.benny.c@gmail.tmt").responseMetaData.getValue());

        assertEquals("UI", BackDoor.getFeedbackResponse(fqMcq.getId(),
                                                        "SFSubmitUiT.alice.b@gmail.tmt",
                                                        "Team 2").getResponseDetails().getAnswerString());

        FeedbackMsqResponseDetails frMsq =
                (FeedbackMsqResponseDetails) BackDoor.getFeedbackResponse(fqMsq.getId(),
                                                                          "SFSubmitUiT.alice.b@gmail.tmt",
                                                                          "Team 2").getResponseDetails();
        assertFalse(frMsq.contains("UI"));
        assertTrue(frMsq.contains("Algo"));
        assertFalse(frMsq.contains("Design"));

        FeedbackNumericalScaleResponseDetails frNumscale = (FeedbackNumericalScaleResponseDetails)
                BackDoor.getFeedbackResponse(fqNumscale.getId(), "SFSubmitUiT.alice.b@gmail.tmt",
                                            "SFSubmitUiT.alice.b@gmail.tmt").getResponseDetails();
        assertEquals("5", frNumscale.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum = (FeedbackConstantSumResponseDetails)
                BackDoor.getFeedbackResponse(fqConstSum.getId(), "SFSubmitUiT.alice.b@gmail.tmt",
                                             "SFSubmitUiT.alice.b@gmail.tmt").getResponseDetails();
        assertEquals("70, 30", frConstSum.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum2_1 = (FeedbackConstantSumResponseDetails)
                BackDoor.getFeedbackResponse(fqConstSum2.getId(), "SFSubmitUiT.alice.b@gmail.tmt",
                                             "Team 2").getResponseDetails();
        assertEquals("90", frConstSum2_1.getAnswerString());

        FeedbackConstantSumResponseDetails frConstSum2_2 = (FeedbackConstantSumResponseDetails)
                BackDoor.getFeedbackResponse(fqConstSum2.getId(), "SFSubmitUiT.alice.b@gmail.tmt",
                                             "Team 3").getResponseDetails();
        assertEquals("110", frConstSum2_2.getAnswerString());

        FeedbackContributionResponseDetails frContrib = (FeedbackContributionResponseDetails)
                BackDoor.getFeedbackResponse(fqContrib.getId(), "SFSubmitUiT.alice.b@gmail.tmt",
                                             "SFSubmitUiT.alice.b@gmail.tmt").getResponseDetails();
        assertEquals("100", frContrib.getAnswerString());

        FeedbackContributionResponseDetails frContrib_1 = (FeedbackContributionResponseDetails)
                BackDoor.getFeedbackResponse(fqContrib.getId(), "SFSubmitUiT.alice.b@gmail.tmt",
                                             "SFSubmitUiT.benny.c@gmail.tmt").getResponseDetails();
        assertEquals("0", frContrib_1.getAnswerString());

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");

        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageFullyFilled.html");

        ______TS("create new response for unreg student");

        submitPage = loginToStudentFeedbackSubmitPage(testData.students.get("DropOut"), "Open Session");

        submitPage.fillResponseTextBox(1, 0, "Test Self Feedback");
        submitPage.selectRecipient(2,0,"Benny Charles");
        submitPage.fillResponseTextBox(2, 0, "Response to Benny.");
        submitPage.selectRecipient(2, 1, "Alice Betsy");
        submitPage.fillResponseTextBox(2, 1, "Response to student who is number 1.");
        submitPage.selectRecipient(2, 2, "Extra guy");
        submitPage.fillResponseTextBox(2, 2, "Response to extra guy.");
        submitPage.fillResponseTextBox(14, 0, "1");

        // Test partial response for question
        submitPage.fillResponseTextBox(4, 1, "Feedback to team 3");
        submitPage.chooseMcqOption(7, 0, "Algo");
        submitPage.toggleMsqOption(9, 0, "UI");
        submitPage.toggleMsqOption(9, 0, "Design");

        submitPage.fillResponseTextBox(18, 0, 0, "90");
        submitPage.fillResponseTextBox(18, 0, 1, "10");

        submitPage.chooseContribOption(20, 0, "Equal share");

        assertNull(BackDoor.getFeedbackResponse(fq.getId(), "drop.out@gmail.tmt", "SFSubmitUiT.benny.c@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqPartial.getId(), "drop.out@gmail.tmt", "Team 3"));
        assertNull(BackDoor.getFeedbackResponse(fqMcq.getId(), "drop.out@gmail.tmt", "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqMsq.getId(), "drop.out@gmail.tmt", "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqNumscale.getId(), "drop.out@gmail.tmt", "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum.getId(), "drop.out@gmail.tmt", "drop.out@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqContrib.getId(), "drop.out@gmail.tmt", "drop.out@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fqContrib.getId(), "drop.out@gmail.tmt", "SFSubmitUiT.benny.c@gmail.tmt"));

        submitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());
        submitPage.verifyHtmlMainContent("/unregisteredStudentFeedbackSubmitPagePartiallyFilled.html");

        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "SFSubmitUiT.benny.c@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqPartial.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "Team 3"));
        assertNotNull(BackDoor.getFeedbackResponse(fqMcq.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "Team 2"));
        assertNotNull(BackDoor.getFeedbackResponse(fqMsq.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "Team 2"));
        assertNotNull(BackDoor.getFeedbackResponse(fqNumscale.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqConstSum.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqContrib.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "SFSubmitUiT.alice.b@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fqContrib.getId(), "SFSubmitUiT.alice.b@gmail.tmt", "SFSubmitUiT.benny.c@gmail.tmt"));
    }

    private void testInputValidation() throws Exception {
        ______TS("Test InputValidation lower than Min value");

        // this should not give any error since the value will be automatically adjusted before the form is submitted
        // adjusted value should be 1
        submitPage.logout();
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.fillResponseTextBox(14, 0, "");
        submitPage.fillResponseTextBox(14, 0, "0");
        submitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());

        FeedbackQuestionAttributes fqNumscale = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104", "First Session", 15);

        FeedbackResponseAttributes frNumscale = BackDoor.getFeedbackResponse(fqNumscale.getId(),
                                                                             "SFSubmitUiT.alice.b@gmail.tmt",
                                                                             "SFSubmitUiT.alice.b@gmail.tmt");

        assertEquals("1",frNumscale.getResponseDetails().getAnswerString());

        ______TS("Test InputValidation Over Max value");

        // this should not give any error since the value will be automatically adjusted before the form is submitted
        // adjusted value should be 5
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.fillResponseTextBox(14, 0, "50000");
        submitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());

        fqNumscale = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104", "First Session", 15);

        frNumscale = BackDoor.getFeedbackResponse(fqNumscale.getId(),
                                                  "SFSubmitUiT.alice.b@gmail.tmt",
                                                  "SFSubmitUiT.alice.b@gmail.tmt");

        assertEquals("5",frNumscale.getResponseDetails().getAnswerString());

        ______TS("Test InputValidation extreme negative value");

        /* Attention: in safari or chrome, negative sign "-" can be input so the result will be adjusted to 1
         *            However, in firefox, the sign "-" can not be typed into the text box so no negative
         *            value can be input
         */
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.fillResponseTextBox(14, 0, "-99999");
        submitPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, submitPage.getStatus());

        fqNumscale = BackDoor.getFeedbackQuestion("SFSubmitUiT.CS2104", "First Session", 15);

        frNumscale = BackDoor.getFeedbackResponse(fqNumscale.getId(),
                                                  "SFSubmitUiT.alice.b@gmail.tmt",
                                                  "SFSubmitUiT.alice.b@gmail.tmt");

        assertEquals("5",frNumscale.getResponseDetails().getAnswerString());

        ______TS("write response without specifying recipient");
        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");

        submitPage.selectRecipient(2, 2, "");
        submitPage.fillResponseTextBox(2, 2, "Response to no recipient");
        submitPage.clickSubmitButton();
        assertEquals("You did not specify a recipient for your response in question(s) 2.", submitPage.getStatus());
    }


    private void testModifyData() throws EnrollException {
        ______TS("modify data");

        // Next, we edit some student data to cover editing of students
        // after creating the responses.

        // move one student out of Team 2 into a new team
        // This should cause the page to render an extra response box for
        // the team question.
        StudentAttributes extraGuy = testData.students.get("ExtraGuy");
        moveToTeam(extraGuy, "New Team");

        // delete one student
        // This should remove (hide on page render; not deleted) the response made to him,
        // and change the number of options in the recipient dropdown list.
        StudentAttributes dropOutGuy = testData.students.get("DropOut");
        String backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
                dropOutGuy.email);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);

        // move Benny out of Team >'"< 1 into team 2 and change her email
        // This should cause the team mates question to disappear completely as
        // no one else is in Team >'"< 1, but other responses to Benny should remain.
        StudentAttributes Benny = testData.students.get("Benny");
        moveToTeam(Benny, "Team 2");

        submitPage = loginToStudentFeedbackSubmitPage("Alice", "Open Session");
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageModified.html");
    }

    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(StudentAttributes s, String fsDataId) {
        String submitUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                             .withCourseId(s.course)
                                             .withStudentEmail(s.email)
                                             .withSessionName(testData.feedbackSessions.get(fsDataId).feedbackSessionName)
                                             .withRegistrationKey(BackDoor.getKeyForStudent(s.course, s.email))
                                .toString();
        browser.driver.get(submitUrl);

        return AppPage.getNewPageInstance(browser, FeedbackSubmitPage.class);
    }
    
    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(String studentName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                        .withUserId(testData.students.get(studentName).googleId)
                                        .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                                        .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);

        return loginAdminToPage(browser, editUrl, FeedbackSubmitPage.class);
    }
    
    private FeedbackSessionNotVisiblePage loginToStudentFeedbackSubmitPageFeedbackSessionNotVisible(String studentName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                        .withUserId(testData.students.get(studentName).googleId)
                                        .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                                        .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);

        return loginAdminToPage(browser, editUrl, FeedbackSessionNotVisiblePage.class);
    }

    private void moveToTeam(StudentAttributes student, String newTeam) {
        String backDoorOperationStatus;
        student.team = newTeam;
        backDoorOperationStatus = BackDoor.editStudent(student.email, student);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
