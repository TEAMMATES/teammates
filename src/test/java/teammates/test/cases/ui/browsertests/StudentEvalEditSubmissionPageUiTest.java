package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentEvalEditPage;
import teammates.test.pageobjects.StudentHomePage;

/**
 * Tests 'Edit Evaluation' view of students.
 * SUT: {@link StudentEvalEditPage}.
 */
public class StudentEvalEditSubmissionPageUiTest extends BaseUiTestCase {

    private static DataBundle testData;
    private static DataBundle testDataExtra;
    private static Browser browser;
    private StudentEvalEditPage editPage;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        
        printTestClassHeader();
        testData = loadDataBundle("/StudentEvalEditSubmissionPageUiTest.json");
        testDataExtra = loadDataBundle("/StudentEvalEditSubmissionPageUiTestExtra.json");
        removeAndRestoreTestDataOnServer(testData);     
        browser = BrowserPool.getBrowser();    
    }

    
    @Test 
    public void testAll() throws Exception{
        
        testPendingEvaluation();
        testEditingSubmission();
        testP2PDisabledEvaluation();
        testNotOpenEvaluation();        
    }
    
 
    public void testPendingEvaluation() throws Exception{
        
        ______TS("content");
        
        editPage = loginToEvalEditPage("Charlie", "First Eval");
        editPage.verifyHtmlMainContent("/StudentEvalEditPendingHTML.html");
        
        ______TS("links");
        
        //No links to check. 
        
        ______TS("input validation");
        
        //No input validation to check.
        
        ______TS("action: submit");  
        
        EvaluationAttributes eval = testData.evaluations.get("First Eval");
        SubmissionAttributes[] subs = new SubmissionAttributes[4];
        subs[0] = testDataExtra.submissions.get("CharlieCharlie");
        subs[1] = testDataExtra.submissions.get("CharlieDanny");
        subs[2] = testDataExtra.submissions.get("CharlieEmily");
        subs[3] = testDataExtra.submissions.get("CharlieNewGuy");

        editPage.fillSubmissionValues(0, subs[0]);
        editPage.fillSubmissionValues(1, subs[1]);
        editPage.fillSubmissionValues(2, subs[2]);
        editPage.fillSubmissionValues(3, subs[3]);
        
        StudentHomePage homePage = editPage.submit();
        homePage.verifyStatus(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED, 
                                            eval.name, eval.courseId).replace("<br />", "\n"));
        
        //confirm values were saved
        String charlieEmail = testData.students.get("Charlie").email;
        String dannyEmail = testData.students.get("Danny").email;
        String emilyEmail = testData.students.get("Emily").email;
        String newGuyEmail = testData.students.get("NewGuy").email;
        
        verifyEditSaved(subs[0], BackDoor.getSubmission(eval.courseId, eval.name, charlieEmail, charlieEmail));
        verifyEditSaved(subs[1], BackDoor.getSubmission(eval.courseId, eval.name, charlieEmail, dannyEmail));
        verifyEditSaved(subs[2], BackDoor.getSubmission(eval.courseId, eval.name, charlieEmail, emilyEmail));
        verifyEditSaved(subs[3], BackDoor.getSubmission(eval.courseId, eval.name, charlieEmail, newGuyEmail));
        
        
        editPage = loginToEvalEditPage("Charlie", eval.name);
        editPage.verifyHtmlMainContent("/StudentEvalEditPendingSubimittedHTML.html");

    }


    public void testEditingSubmission() throws Exception{
        
        EvaluationAttributes eval = testData.evaluations.get("First Eval");
        editPage = loginToEvalEditPage("Danny", eval.name);
        
        ______TS("content");
        
        editPage.verifyHtmlMainContent("/StudentEvalEditSubmittedHTML.html");
    
        ______TS("action: submit after editing");
        
        SubmissionAttributes[] subs = new SubmissionAttributes[4];
        subs[0] = testDataExtra.submissions.get("DannyDannyNew");
        subs[1] = testDataExtra.submissions.get("DannyCharlieNew");
        subs[2] = testDataExtra.submissions.get("DannyEmilyNew");
        subs[3] = testDataExtra.submissions.get("DannyNewGuy");

        
        editPage.fillSubmissionValues(0, subs[0]);
        editPage.fillSubmissionValues(1, subs[1]);
        editPage.fillSubmissionValues(2, subs[2]);
        editPage.fillSubmissionValues(3, subs[3]);
        StudentHomePage homePage = editPage.submit();
        homePage.verifyStatus(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED, 
                                            eval.name, eval.courseId).replace("<br />", "\n"));
        
        //confirm new values were saved        
        String dannyEmail = testData.students.get("Danny").email;
        String charlieEmail = testData.students.get("Charlie").email;
        String emilyEmail = testData.students.get("Emily").email;
        String newguyEmail = testData.students.get("NewGuy").email;
        
        verifyEditSaved(subs[0], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, dannyEmail));
        verifyEditSaved(subs[1], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, charlieEmail));
        verifyEditSaved(subs[2], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, emilyEmail));
        verifyEditSaved(subs[3], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, newguyEmail));
        
        editPage = loginToEvalEditPage("Danny", eval.name);
        editPage.verifyHtmlMainContent("/StudentEvalEditResubmittedHTML.html");
        
        //TODO: more tests are needed to cover the disabling of editing when the evaluation is CLOSED.
        // In particular, timezone differences should be considered in such testing. Currently, these
        // tests are done in AllAccessControlUiTest class.
        
        ______TS("action: clear all submitted data then submit");  
        
        editPage.clearSubmittedData(0);
        editPage.clearSubmittedData(1);
        editPage.clearSubmittedData(2);
        editPage.clearSubmittedData(3);
        
        homePage = editPage.submit();
        homePage.verifyStatus(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED, 
                                            eval.name, eval.courseId).replace("<br />", "\n"));
        
        verifySubmissionEmpty(BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, dannyEmail));
        verifySubmissionEmpty(BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, charlieEmail));
        verifySubmissionEmpty(BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, emilyEmail));
        verifySubmissionEmpty(BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, newguyEmail));
        
        editPage = loginToEvalEditPage("Danny", eval.name);
        editPage.verifyHtmlMainContent("/StudentEvalEditDataClearedHTML.html");
        
        
        
        ______TS("action: submission in grace period");
        
        editPage.logout();
        Calendar endDate = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        eval.timeZone = 0;
        endDate.add(Calendar.MINUTE, -1);
        eval.endTime = endDate.getTime();
        eval.gracePeriod = 10;
        BackDoor.editEvaluation(eval);
        editPage = loginToEvalEditPage("Danny", eval.name);
        
        editPage.fillSubmissionValues(0, subs[0]);
        editPage.fillSubmissionValues(1, subs[1]);
        editPage.fillSubmissionValues(2, subs[2]);
        editPage.fillSubmissionValues(3, subs[3]);
        homePage = editPage.submit();
        homePage.verifyStatus(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED, 
                                            eval.name, eval.courseId).replace("<br />", "\n"));
        
        verifyEditSaved(subs[0], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, dannyEmail));
        verifyEditSaved(subs[1], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, charlieEmail));
        verifyEditSaved(subs[2], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, emilyEmail));
        verifyEditSaved(subs[3], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, newguyEmail));
        
    }

  
    public void testP2PDisabledEvaluation() throws Exception{
        
        EvaluationAttributes eval = testData.evaluations.get("Second Eval");
        editPage = loginToEvalEditPage("Danny", eval.name);
        
        ______TS("content");
        
        editPage.verifyHtmlMainContent("/StudentEvalEditP2PDisabled.html");
        
        ______TS("action: submit");
        
        StudentHomePage homePage = editPage.submit();
        homePage.verifyStatus(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED,
                                            eval.name, eval.courseId).replace("<br />", "\n"));
        
    }
    
   
    public void testNotOpenEvaluation() throws Exception{
        EvaluationAttributes eval = testData.evaluations.get("Closed Unpublished Eval");
        editPage = loginToEvalEditPage("Danny", eval.name);
        
        ______TS("content");
        
        editPage.verifyHtmlMainContent("/StudentEvalEditEntryFieldsDisabled.html");
        
        ______TS("action: submit");
        
        editPage.submitUnsuccessfully().verifyStatus(String.format(Const.StatusMessages.EVALUATION_NOT_OPEN, 
                                                                   eval.name, eval.courseId).replace("<br />", "\n"));
    
        //TODO: test for evaluation that closed while the student was editing submission.
    }
    
    private void verifyEditSaved(SubmissionAttributes expected, SubmissionAttributes actual) {
        assertEquals((expected.points + "").trim(), actual.points + "");
        assertEquals(expected.justification.getValue().trim(), actual.justification.getValue());
        assertEquals(expected.p2pFeedback.getValue().trim(), actual.p2pFeedback.getValue());
    }
    
    private void verifySubmissionEmpty(SubmissionAttributes actual) {
        assertEquals((-101 + "").trim(), actual.points + "");
        assertEquals("", actual.justification.getValue());
        assertEquals("", actual.p2pFeedback.getValue());
    }

    private StudentEvalEditPage loginToEvalEditPage(String studentName,    String evalName) {
        
        Url editUrl = createUrl(Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE)
                      .withUserId(testData.students.get(studentName).googleId)
                      .withCourseId(testData.evaluations.get(evalName).courseId)
                      .withEvalName(testData.evaluations.get(evalName).name);
        
        return loginAdminToPage(browser, editUrl, StudentEvalEditPage.class);
    }

  

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}