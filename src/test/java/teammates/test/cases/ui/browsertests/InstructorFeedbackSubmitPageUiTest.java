package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.FeedbackSubmitPage;

/**
 * Tests 'Submit Feedback' view of instructors.
 * SUT: {@link FeedbackSubmitPage}.
 */
public class InstructorFeedbackSubmitPageUiTest extends BaseUiTestCase {

    private static DataBundle testData;
    private static Browser browser;
    private FeedbackSubmitPage submitPage;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorFeedbackSubmitPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        browser = BrowserPool.getBrowser();        
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
    
    private void testContent() {
        
        ______TS("Awaiting session");
        
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Awaiting Session");
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
    
        ______TS("Private session");
        
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Private Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePrivate.html");
    
    }
    
    private void testClosedSessionSubmitAction(){
        
        ______TS("test submitting for closed session");
    
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
       
        assertFalse(submitPage.isElementEnabled("response_submit_button"));
        
    }
    
    private void testSubmitAction(){
        
        ______TS("create new responses");

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        
        submitPage.fillResponseTextBox(1, 0, "Test Self Feedback");
        submitPage.fillResponseTextBox(2, 0, "Response to Alice.");
        submitPage.selectRecipient(2, 1, "Drop out");
        submitPage.fillResponseTextBox(2, 1, "Response to student who is going to drop out.");
        submitPage.selectRecipient(2, 2, "Extra guy");
        submitPage.fillResponseTextBox(2, 2, "Response to extra guy.");
        submitPage.fillResponseTextBox(13, 0, "1");
        
        // Test partial response for question        
        submitPage.fillResponseTextBox(4, 1, "Feedback to Instructor 3");
        submitPage.chooseMcqOption(6, 0, "Algo");
        submitPage.toggleMsqOption(8, 0, "UI");
        submitPage.toggleMsqOption(8, 0, "Design");
        
        submitPage.fillResponseTextBox(17, 0, 0, "90");
        submitPage.fillResponseTextBox(17, 0, 1, "10");
        
        // Just check that some of the responses persisted.
        FeedbackQuestionAttributes fq =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 2);
        FeedbackQuestionAttributes fqPartial =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 6);
        FeedbackQuestionAttributes fqMcq =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 8);
        FeedbackQuestionAttributes fqMsq =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 10);
        FeedbackQuestionAttributes fqNumscale =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 15);
        FeedbackQuestionAttributes fqConstSum =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 19);
        FeedbackQuestionAttributes fqConstSum2 =
                BackDoor.getFeedbackQuestion("IFSubmitUiT.CS2104",
                        "First Session", 20);
        
        assertNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.alice.b@gmail.com"));
        assertNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr2@gmail.com"));
        assertNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr2@gmail.com"));
        assertNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr2@gmail.com"));
        assertNull(BackDoor.getFeedbackResponse(fqNumscale.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr@gmail.com"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr@gmail.com"));
        
        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                submitPage.getStatus());
        
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.alice.b@gmail.com"));
        assertNotNull(BackDoor.getFeedbackResponse(fqPartial.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr3@gmail.com"));
        assertNotNull(BackDoor.getFeedbackResponse(fqMcq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr2@gmail.com"));
        assertNotNull(BackDoor.getFeedbackResponse(fqMsq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr2@gmail.com"));
        assertNotNull(BackDoor.getFeedbackResponse(fqNumscale.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr@gmail.com"));
        assertNotNull(BackDoor.getFeedbackResponse(fqConstSum.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr@gmail.com"));
        
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPagePartiallyFilled.html");
                
        ______TS("edit existing response");        
        
        // Test editing an existing response 
        // + fill up rest of responses at the same time
        String editedResponse = "Edited response to Alice.";
        submitPage.fillResponseTextBox(2, 0, editedResponse);
        submitPage.fillResponseTextBox(3, 0, "Feedback to instructors");
        submitPage.fillResponseTextBox(4, 1, "Feedback to instructor 2.");
        submitPage.fillResponseTextBox(4, 2, "Feedback to instructor 4.");
        submitPage.fillResponseTextBox(4, 3, "Feedback to instructor 5.");
        
        submitPage.chooseMcqOption(5, 0, "UI");
        submitPage.chooseMcqOption(6, 0, "UI"); // Changed from "Algo" to "UI"
        submitPage.chooseMcqOption(6, 1, "UI");
        submitPage.chooseMcqOption(6, 2, "UI");
        
        submitPage.toggleMsqOption(7, 0, "UI");
        submitPage.toggleMsqOption(7, 0, "Algo");
        submitPage.toggleMsqOption(7, 0, "Design");
        submitPage.toggleMsqOption(8, 0, "UI");
        submitPage.toggleMsqOption(8, 0, "Algo");
        submitPage.toggleMsqOption(8, 0, "Design");
        submitPage.toggleMsqOption(8, 1, "Design");
        submitPage.toggleMsqOption(8, 2, "UI");
        
        submitPage.chooseMcqOption(9, 0, "Drop out (Team 2)");
        submitPage.toggleMsqOption(10, 0, "Alice Betsy (Team 1)");
        submitPage.toggleMsqOption(10, 0, "Benny Charles (Team 1)");
        submitPage.toggleMsqOption(10, 0, "Charlie Davis (Team 2)");
        submitPage.toggleMsqOption(10, 0, "Extra guy (Team 2)");
        
        submitPage.chooseMcqOption(11, 0, "Team 2");
        submitPage.toggleMsqOption(12, 0, "Team 1");
        submitPage.toggleMsqOption(12, 0, "Team 3");
        
        submitPage.fillResponseTextBox(13, 0, "5"); 
        submitPage.fillResponseTextBox(14, 0, "1.5"); 
        submitPage.fillResponseTextBox(14, 1, "2"); 
        submitPage.fillResponseTextBox(14, 2, "3.5"); 
        
        submitPage.chooseMcqOption(15, 0, "Teammates Test2");
        submitPage.toggleMsqOption(16, 0, "Teammates Test");
        submitPage.toggleMsqOption(16, 0, "Teammates Test3");
        
        submitPage.fillResponseTextBox(17, 0, 0, "70");
        submitPage.fillResponseTextBox(17, 0, 1, "30");
        
        submitPage.fillResponseTextBox(18, 0, 0, "90");
        submitPage.fillResponseTextBox(18, 1, 0, "110");
        submitPage.fillResponseTextBox(18, 2, 0, "100");
        
        // Just check the edited responses, and two new response.
        assertNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "Team 1"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "Team 2"));
        assertNull(BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "Team 3"));
        
        submitPage.clickSubmitButton();

        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,
                submitPage.getStatus());
        assertEquals(editedResponse,
                BackDoor.getFeedbackResponse(fq.getId(),
                    "IFSubmitUiT.instr@gmail.com",
                    "IFSubmitUiT.alice.b@gmail.com").getResponseDetails().getAnswerString());
        
        fq = BackDoor.getFeedbackQuestion(
                "IFSubmitUiT.CS2104",
                "First Session", 7);
        assertNotNull(BackDoor.getFeedbackResponse(fq.getId(),
                "IFSubmitUiT.instr@gmail.com",
                "IFSubmitUiT.instr@gmail.com"));
        assertEquals("UI",
                BackDoor.getFeedbackResponse(fqMcq.getId(),
                    "IFSubmitUiT.instr@gmail.com",
                    "IFSubmitUiT.instr2@gmail.com").getResponseDetails().getAnswerString());
        
        FeedbackMsqResponseDetails frMsq = 
                (FeedbackMsqResponseDetails) BackDoor.getFeedbackResponse(fqMsq.getId(),
                        "IFSubmitUiT.instr@gmail.com",
                        "IFSubmitUiT.instr2@gmail.com").getResponseDetails();
        assertFalse(frMsq.contains("UI"));
        assertTrue(frMsq.contains("Algo"));
        assertFalse(frMsq.contains("Design"));
        
        FeedbackNumericalScaleResponseDetails frNumscale = 
                (FeedbackNumericalScaleResponseDetails) BackDoor.getFeedbackResponse(fqNumscale.getId(),
                        "IFSubmitUiT.instr@gmail.com",
                        "IFSubmitUiT.instr@gmail.com").getResponseDetails();
        assertEquals("5", frNumscale.getAnswerString());
        
        FeedbackConstantSumResponseDetails frConstSum = 
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(fqConstSum.getId(),
                        "IFSubmitUiT.instr@gmail.com",
                        "IFSubmitUiT.instr@gmail.com").getResponseDetails();
        assertEquals("70, 30", frConstSum.getAnswerString());
        
        FeedbackConstantSumResponseDetails frConstSum2_0 = 
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                        "IFSubmitUiT.instr@gmail.com",
                        "Team 1").getResponseDetails();
        assertEquals("90", frConstSum2_0.getAnswerString());
        
        FeedbackConstantSumResponseDetails frConstSum2_1 = 
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                        "IFSubmitUiT.instr@gmail.com",
                        "Team 2").getResponseDetails();
        assertEquals("110", frConstSum2_1.getAnswerString());
        
        FeedbackConstantSumResponseDetails frConstSum2_2 = 
                (FeedbackConstantSumResponseDetails) BackDoor.getFeedbackResponse(fqConstSum2.getId(),
                        "IFSubmitUiT.instr@gmail.com",
                        "Team 3").getResponseDetails();
        assertEquals("100", frConstSum2_2.getAnswerString());
        
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageFullyFilled.html");
    }
    
    /**
     *  Tests the behavior of different question types.
     *  Test response validation on client side as well, if any.
     */
    private void testQuestionTypesSubmitAction(){
        ______TS("test submit actions for different question types.");
        
        testEssaySubmitAction();
        testMcqSubmitAction();
        testMsqSubmitAction();
        testNumScaleSubmitAction();
        testConstSumSubmitAction();
        testContribSubmitAction();
    }
    
    private void testEssaySubmitAction(){
        ______TS("test submit actions for essay question.");
        
        //Nothing much to test for input validation.
        //Test fields are disabled when session is closed.
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
        
        //Test input disabled
        int qnNumber = 1;
        int responseNumber = 0;
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));

        //TODO: test that the recipient selection is also disabled
    }
    
    private void testMcqSubmitAction(){
        ______TS("test submit actions for mcq.");
        
        //Test input disabled
        int qnNumber = 2;
        int responseNumber = 0;
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));

        //TODO: test that the recipient selection is also disabled
    }
    
    private void testMsqSubmitAction(){
        ______TS("test submit actions for msq.");
        
        //Test input disabled
        int qnNumber = 3;
        int responseNumber = 0;
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        
        //TODO: test that the recipient selection is also disabled
    }
    
    private void testNumScaleSubmitAction(){
        ______TS("test submit actions for numscale questions.");
        
        //Test input disabled
        int qnNumber = 4;
        int responseNumber = 0;
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));

        //TODO: test that the recipient selection is also disabled
        
        //Test input entered are valid numbers for the question.
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 14;
        responseNumber = 0;
        
        submitPage.fillResponseTextBox(14, 0, "2.5");
        assertEquals("2.5",submitPage.getResponseTextBoxValue(14, 0));
        
        submitPage.fillResponseTextBox(14, 0, "ABCD");
        assertEquals("",submitPage.getResponseTextBoxValue(14, 0));
        
        fillResponseTextBoxWithRecheck(14, 0, "0", "1");
        
        submitPage.fillResponseTextBox(14, 0, "-1");
        assertEquals("1",submitPage.getResponseTextBoxValue(14, 0));
        
        fillResponseTextBoxWithRecheck(14, 0, "6", "5");
        
        //TODO: test for stronger step validation.
        
    }
    
    private void testConstSumSubmitAction(){
        ______TS("test submit actions for constsum questions.");
        
        //Test input disabled
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Closed Session");
        int qnNumber = 5;
        int responseNumber = 0;
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        
        //Test messages for different values entered
        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        qnNumber = 17;
        assertEquals("All points distributed!",submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "80");
        assertEquals("Over allocated 10 points",submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 0, "");
        assertEquals("70 points left to distribute.",submitPage.getConstSumMessage(qnNumber, 0));
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "");
        assertEquals("Please distribute 100 points among the above options.",submitPage.getConstSumMessage(qnNumber, 0));
        
        //Test error message when submitting
        submitPage.fillResponseTextBox(qnNumber, 0, 1, "10");
        assertEquals("90 points left to distribute.",submitPage.getConstSumMessage(qnNumber, 0));
        
        submitPage.clickSubmitButton();
        assertEquals("Please distribute all the points for distribution questions."
                + " To skip a distribution question, leave the boxes blank.",
                submitPage.getStatus());
        
        //For other const sum question, just test one message.
        qnNumber = 18;
        assertEquals("100 points left to distribute.",submitPage.getConstSumMessage(qnNumber, 3));
        
    }
    
    private void testContribSubmitAction(){
        ______TS("test submit actions for contribution questions.");
        
        //No tests from instructor since contribution questions are only from students to own team members.
        //Test by logging in as student instead.
        
        
        //Test input disabled
        submitPage = loginToStudentFeedbackSubmitPage("Danny", "Closed Session");
        int qnNumber = 1;
        int responseNumber = 0;
        assertTrue(submitPage.isNamedElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnNumber + "-" + responseNumber));
        
    }
    
    private void fillResponseTextBoxWithRecheck(int qnNumber, int responseNumber, String text, String expected) {
        int counter = 0;
        while(counter != 100){
            submitPage.fillResponseTextBox(qnNumber, responseNumber, text);
            if(expected.equals(submitPage.getResponseTextBoxValue(qnNumber, responseNumber))){
                return;
            }
            counter++;
            browser.driver.switchTo().window("");
        }
        assertEquals(expected ,submitPage.getResponseTextBoxValue(qnNumber, responseNumber));
    }
    
    private void testModifyData() throws EnrollException{
        ______TS("modify data");
        
        // Next, we edit some student data to cover editing of students
        // after creating the responses.

        // move one student out of Team 2 into a new team
        // This should not cause the existing response to disappear
        StudentAttributes extraGuy = testData.students.get("ExtraGuy");
        moveToTeam(extraGuy, "New Team");

        // delete one student
        // This should remove (hide on page render; not deleted) the response made to him,
        // and change the number of options in the recipient dropdown list.
        StudentAttributes dropOutGuy = testData.students.get("DropOut");
        String backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
                dropOutGuy.email);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);

        submitPage = loginToInstructorFeedbackSubmitPage("IFSubmitUiT.instr", "Open Session");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageModified.html");
    }

    private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(
            String instructorName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.instructors.get(instructorName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        return loginAdminToPage(browser, editUrl, FeedbackSubmitPage.class);
    }
    
    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                .withUserId(testData.students.get(studentName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        return loginAdminToPage(browser, editUrl, FeedbackSubmitPage.class);
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