package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.EmailAccount;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorEvalResultsPage;
import teammates.test.pageobjects.InstructorEvalsPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.pageobjects.StudentEvalEditPage;

import com.google.appengine.api.datastore.Text;

/**
 * Covers the 'Evaluations' page for instructors. 
 * SUT is {@link InstructorEvalsPage}.
 */
public class InstructorEvalsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorEvalsPage evalsPage;
    private static DataBundle testData;
    /** This contains data for the new evaluation to be created during testing */
    private static EvaluationAttributes newEval;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorEvalsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        
        int limitStringLengthForDatastore = 500;
        
        newEval = new EvaluationAttributes();
        newEval.courseId = "CEvalUiT.CS1101";
        newEval.name = "New Evaluation";
        newEval.startTime = TimeHelper.convertToDate("2016-04-01 11:59 PM UTC");
        newEval.endTime = TimeHelper.convertToDate("2016-04-30 11:59 PM UTC");
        newEval.gracePeriod = 10;
        newEval.instructions = new Text(StringHelper.generateStringOfLength(limitStringLengthForDatastore+1));
        newEval.p2pEnabled = true;
        newEval.published = false;
        newEval.activated = false;
        newEval.timeZone = 5.75;
        browser = BrowserPool.getBrowser(true);
        
    }
    
    //@Test
    //Test deprecated along with creation of evaluations.
    public void allTests() throws Exception{
        testContent();
        
        testEditLink();
        testViewResultsLink();
        testPreviewLink();
        
        testInputValidation();
        
        testAddAction();
        testPublishAction();
        testRemindAction();
        testDeleteAction(); 
    }

    public void testContent() throws Exception{
        
        ______TS("no courses");
        
        InstructorFeedbacksPage feedbacksPage;
        
        feedbacksPage = getEvalsPageForInstructor(testData.accounts.get("instructorWithoutCourses").googleId);
        feedbacksPage.verifyHtmlMainContent("/instructorEvalEmptyAll.html");
        
        ______TS("no evaluations");
        
        feedbacksPage = getEvalsPageForInstructor(testData.accounts.get("instructorWithoutEvals").googleId);
        feedbacksPage.verifyHtmlMainContent("/instructorEvalEmptyEval.html");

        ______TS("typical view, sort by deadline (default)");
        
        feedbacksPage = getEvalsPageForInstructor(testData.accounts.get("instructorWithEvals").googleId);
        feedbacksPage.verifyHtmlAjax("/instructorEvalByDeadline.html");

        ______TS("sort by name");
        
        evalsPage.sortByName()
            .verifyTablePattern(1,"First Eval{*}Second Eval{*}Third Eval");
        
        evalsPage.sortByName()
            .verifyTablePattern( 1,"Third Eval{*}Second Eval{*}First Eval");
        
        ______TS("sort by course id");
        
        evalsPage.sortById()
        .verifyTablePattern(0,"CEvalUiT.CS1101{*}CEvalUiT.CS2104{*}CEvalUiT.CS2104");
        
        evalsPage.verifyHtmlAjax("/instructorEvalById.html");
        
        evalsPage.sortById()
            .verifyTablePattern(0,"CEvalUiT.CS2104{*}CEvalUiT.CS2104{*}CEvalUiT.CS1101");
    
    }
    
    public void testEditLink(){
        //TODO: implement this (also check for disabling of the link at right times)
    }
    
    public void testViewResultsLink(){
        
        ______TS("view results link always enabled");
        
        String courseId = "CEvalUiT.CS2104";
        String evalName = "First Eval";
        
        InstructorEvalResultsPage resultPage = evalsPage.loadViewResultsLink(courseId, evalName);
        resultPage.verifyIsCorrectPage();
        
        evalsPage = resultPage.goToPreviousPage(InstructorEvalsPage.class);
    }

    public void testPreviewLink(){

        ______TS("preview link always enabled");
        
        String courseId = "CEvalUiT.CS2104";
        String evalName = "First Eval";
        
        StudentEvalEditPage previewPage = evalsPage.loadPreviewLink(courseId, evalName);
        previewPage.verifyHtmlMainContent("/studentEvalEditPagePreview.html");
        previewPage.closeCurrentWindowAndSwitchToParentWindow();
    }
    
    public void testInputValidation() {
        
        ______TS("client-side input validation");
        
        EvaluationAttributes eval = newEval;
        
        // Empty name, closing date
        
        evalsPage.waitForElementPresence(By.id("button_submit"), 15);
        evalsPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FIELDS_EMPTY, evalsPage.getStatus());
        
        //TODO: The client-side validation tests below should be covered in JS tests, not as UI tests.
        // They are to be removed after confirming coverage by JS tests.
        
        // Empty closing date
        evalsPage.fillEvalName("Some value");
        evalsPage.clickSubmitButton();
        assertEquals(Const.StatusMessages.FIELDS_EMPTY, evalsPage.getStatus());
        
        // Empty name
        evalsPage.addEvaluation(eval.courseId, "", eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions.getValue(), eval.gracePeriod, eval.timeZone);
        assertEquals(Const.StatusMessages.FIELDS_EMPTY, evalsPage.getStatus());
        
        // Empty instructions
        evalsPage.addEvaluation(eval.courseId, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, "", eval.gracePeriod, eval.timeZone);
        assertEquals(Const.StatusMessages.FIELDS_EMPTY, evalsPage.getStatus());

        // Invalid name
        evalsPage.addEvaluation(eval.courseId, eval.name+"!@#$%^&*()_+", eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions.getValue(), eval.gracePeriod, eval.timeZone);
        assertEquals(Const.StatusMessages.EVALUATION_NAMEINVALID, evalsPage.getStatus());
        
        // Invalid schedule
        evalsPage.addEvaluation(eval.courseId, eval.name, eval.endTime, eval.startTime, eval.p2pEnabled, eval.instructions.getValue(), eval.gracePeriod, eval.timeZone);
        assertEquals(Const.StatusMessages.EVALUATION_SCHEDULEINVALID.replace("<br />", "\n"), evalsPage.getStatus());
        
    }

    public void testAddAction() throws Exception{
        
        ______TS("typical success case");
        
        EvaluationAttributes eval = newEval;
        evalsPage.addEvaluation(eval.courseId, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions.getValue(), eval.gracePeriod, eval.timeZone);
        evalsPage.verifyStatus(Const.StatusMessages.EVALUATION_ADDED);
        EvaluationAttributes savedEvaluation = BackDoor.getEvaluation(eval.courseId, eval.name);
        //Note: This can fail at times because Firefox fails to choose the correct value from the dropdown.
        //  in that case, rerun in Chrome.
        assertEquals(eval.toString(), savedEvaluation.toString());
        evalsPage.sortByName()
            .verifyHtmlAjax("/instructorEvalAddSuccess.html");

        ______TS("duplicate evalution name");

        evalsPage.addEvaluation(eval.courseId, eval.name, eval.startTime, eval.endTime, eval.p2pEnabled, eval.instructions.getValue(), eval.gracePeriod, eval.timeZone);
        assertEquals(Const.StatusMessages.EVALUATION_EXISTS, evalsPage.getStatus());
        
    }

    
    public void testPublishAction(){
        
        evalsPage.loadEvaluationsTab(); //refresh the page
        
        ______TS("CLOSED: publish link clickable");
    
        String courseId = testData.evaluations.get("closedEval").courseId;
        String evalName = testData.evaluations.get("closedEval").name;
        
        evalsPage.clickAndCancel(evalsPage.getPublishLink(courseId, evalName));
        assertEquals(false, BackDoor.getEvaluation(courseId, evalName).published);
        
        evalsPage.clickAndConfirm(evalsPage.getPublishLink(courseId, evalName));
        evalsPage.verifyStatus(Const.StatusMessages.EVALUATION_PUBLISHED);
        assertEquals(true, BackDoor.getEvaluation(courseId, evalName).published);
        
        
        ______TS("PUBLISHED: unpublish link clickable");
        
        courseId = testData.evaluations.get("publishedEval").courseId;
        evalName = testData.evaluations.get("publishedEval").name;
        
        evalsPage.clickAndCancel(evalsPage.getUnpublishLink(courseId, evalName));
        assertEquals(true, BackDoor.getEvaluation(courseId, evalName).published);
        
        evalsPage.clickAndConfirm(evalsPage.getUnpublishLink(courseId, evalName));
        assertEquals(Const.StatusMessages.EVALUATION_UNPUBLISHED, evalsPage.getStatus());
        assertEquals(false, BackDoor.getEvaluation(courseId, evalName).published);
        
        //TODO: ensure that publish link is unclickable in AWAITING, OPEN states
        
    }

    public void testRemindAction() throws Exception{
        
        evalsPage.loadEvaluationsTab();
        
        ______TS("CLOSED: remind link unclickable");
        
        String courseId = testData.evaluations.get("closedEval").courseId;
        String evalName = testData.evaluations.get("closedEval").name;
        
        evalsPage.verifyUnclickable(evalsPage.getRemindLink(courseId, evalName));
        
    
        ______TS("PUBLISHED: remind link unclickable");
        
        courseId = testData.evaluations.get("publishedEval").courseId;
        evalName = testData.evaluations.get("publishedEval").name;
        
        evalsPage.verifyUnclickable(evalsPage.getRemindLink(courseId, evalName));
        
        ______TS("AWAITING: remind link unclickable");
        
        courseId = newEval.courseId;
        evalName = newEval.name;
        
        evalsPage.verifyUnclickable(evalsPage.getRemindLink(courseId, evalName));
        
        ______TS("OPEN: remind link clickable");
    
        courseId = testData.evaluations.get("openEval").courseId;
        evalName = testData.evaluations.get("openEval").name;
        
        evalsPage.loadEvaluationsTab(); //refresh the page
        evalsPage.clickAndCancel(evalsPage.getRemindLink(courseId, evalName));
        
        evalsPage.clickAndConfirm(evalsPage.getRemindLink(courseId, evalName));
        
        if(!TestProperties.inst().isDevServer()) {
            ThreadHelper.waitFor(5000); //wait for the emails to reach the mail box
            assertEquals(courseId,
                    EmailAccount.getEvaluationReminderFromGmail(
                            testData.students.get("alice.tmms@CEvalUiT.CS2104").email, 
                            TestProperties.inst().TEST_STUDENT1_PASSWORD, 
                            courseId, 
                            evalName));
        }
    }

    public void testDeleteAction() throws Exception{
        
        String courseId = newEval.courseId;
        String evalName = newEval.name;
        
        evalsPage.loadEvaluationsTab(); //refresh the page
        evalsPage.clickAndCancel(evalsPage.getDeleteLink(courseId, evalName));
        assertNotNull(null, BackDoor.getEvaluation(courseId, evalName));
    
        evalsPage.clickAndConfirm(evalsPage.getDeleteLink(courseId, evalName));
        evalsPage.verifyHtmlAjax("/instructorEvalDeleteSuccessful.html");
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private InstructorFeedbacksPage getEvalsPageForInstructor(String instructorId) {
        Url evalPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE).withUserId(instructorId);
        return loginAdminToPage(browser, evalPageLink, InstructorFeedbacksPage.class);
    }

}