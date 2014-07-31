package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorEvalEditPage;

/**
 * Tests 'Edit evaluation' functionality for instructors.
 * SUT: {@link InstructorEvalEditPage}.
 */
public class InstructorEvalEditPageUiTest extends BaseUiTestCase {
    
    private static DataBundle testData;
    private static Browser browser;
    private static InstructorEvalEditPage editPage;
    
    private static EvaluationAttributes existingEval;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorEvalEditPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        existingEval = testData.evaluations.get("evaluation");
        
    }
    
    @Test
    public void runTestsInOrder() throws Exception{
        testContent();
        testInputValidation();
        //testCancelAction();
        testEditAction();
    }
    
    public void testContent() throws Exception{
        
        ______TS("content: summary view");
        
        gotoInstructorEvalEditPage();
        editPage.verifyHtmlMainContent("/instructorEvalEdit.html");
    }
    
    public void testEditAction() throws Exception{
        gotoInstructorEvalEditPage();
        
        ______TS("action: edit with valid parameters");
        
        existingEval.p2pEnabled = !existingEval.p2pEnabled; 
        existingEval.instructions = new Text("Instructions to student."); 
        existingEval.gracePeriod = existingEval.gracePeriod + 5;
        existingEval.startTime = TimeHelper.convertToDate("2012-04-01 11:59 PM UTC"); 
        existingEval.endTime = TimeHelper.convertToDate("2015-04-01 10:00 PM UTC"); 
        
        editPage.submitUpdate(
                existingEval.startTime, 
                existingEval.endTime, 
                existingEval.p2pEnabled, 
                existingEval.instructions.getValue(), 
                existingEval.gracePeriod)
                .verifyStatus(Const.StatusMessages.EVALUATION_EDITED);
        
        EvaluationAttributes updated = BackDoor.getEvaluation(existingEval.courseId, existingEval.name);
        assertEquals(existingEval.toString(), updated.toString());
    }
    
    public void testCancelAction(){
        //TODO: Cancel button to be removed from the Evaluation Edit page in the future
    }

    private void testInputValidation() {

        existingEval.p2pEnabled = !existingEval.p2pEnabled; 
        existingEval.instructions = new Text("Instructions to student."); 
        existingEval.gracePeriod = existingEval.gracePeriod + 5;
        
        ______TS("input: testing with invalid time");
        
        existingEval.startTime = TimeHelper.convertToDate("2012-04-01 11:59 PM UTC"); 
        existingEval.endTime = TimeHelper.convertToDate("2012-03-01 10:00 PM UTC"); 
        
        String invalidTimeStatusMessage = "The evaluation schedule (start/deadline) is not valid.\n" +
                "The start time should be in the future, and the deadline should be after start time.";
        
        editPage.submitUpdate(
                existingEval.startTime, 
                existingEval.endTime, 
                existingEval.p2pEnabled, 
                existingEval.instructions.getValue(), 
                existingEval.gracePeriod)
                .verifyStatus(invalidTimeStatusMessage);
        
    }
    
    private void gotoInstructorEvalEditPage() throws Exception{
        String instructorId = testData.instructors.get("instructor").googleId;
        Url editPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_PAGE)
        .withUserId(instructorId)
        .withCourseId(existingEval.courseId)
        .withEvalName(existingEval.name);
        
        editPage = loginAdminToPage(browser, editPageUrl, InstructorEvalEditPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}