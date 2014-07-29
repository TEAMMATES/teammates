package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

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
import teammates.test.pageobjects.InstructorEvalResultsPage;
import teammates.test.pageobjects.InstructorEvalSubmissionEditPage;
import teammates.test.pageobjects.InstructorEvalsPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;

import com.google.appengine.api.datastore.Text;

/**
 * Covers editing of evaluation submissions by the instructor.
 * SUT: {@link InstructorEvalSubmissionEditPage}.
 */
public class InstructorEvalSubmissionEditPageUiTest extends BaseUiTestCase{
    private static Browser browser;
    private static InstructorEvalSubmissionEditPage editPage;
    private static DataBundle testData;
    
    private static EvaluationAttributes eval;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorEvalSubmissionEditPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        eval = testData.evaluations.get("First Eval");
    }
    
    @Test
    public void testAll() throws Exception{
        testContent();
        //no links to test
        //no input validation to check
        testSubmitAction();
    }
    
    public void testContent() {
        
        //TODO: test content for student without team and 1-person team
        
        ______TS("content: edit page for p2p disabled eval, reached via direct link");
        
        EvaluationAttributes p2pDisabledEval = testData.evaluations.get("Second Eval");
        
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT)
            .withUserId(testData.instructors.get("CESubEditUiT.instructor").googleId)
            .withCourseId(p2pDisabledEval.courseId)
            .withEvalName(p2pDisabledEval.name)
            .withStudentEmail(testData.students.get("Charlie").email);
        editPage = loginAdminToPage(browser, editUrl, InstructorEvalSubmissionEditPage.class);
        editPage.verifyHtmlMainContent("/instructorEvalSubmissionP2PDisabled.html");
        
        
        ______TS("content: typical edit page, reached via resulst page");
        
        Url resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE)
            .withUserId(testData.instructors.get("CESubEditUiT.instructor").googleId)
            .withCourseId(eval.courseId)
            .withEvalName(eval.name);
        InstructorEvalResultsPage resultsPage = loginAdminToPage(browser, resultsUrl, InstructorEvalResultsPage.class);
        editPage = resultsPage.clickEditLinkForStudent("Charlie");
        editPage.verifyHtmlMainContent("/instructorEvalSubmissionEdit.html");
        
    }

    public void testSubmitAction() throws Exception{
        
        ______TS("submit action");
        
        SubmissionAttributes[] subs = new SubmissionAttributes[3];
        subs[0] = testData.submissions.get("CharlieCharlie");
        subs[1] = testData.submissions.get("CharlieDanny");
        subs[2] = testData.submissions.get("CharlieEmily");
        for(int i=0; i<3; i++){
            subs[i].points-=10;
            subs[i].justification = new Text(subs[i].justification.getValue()+"(edited)");
            subs[i].p2pFeedback= new Text(subs[i].p2pFeedback.getValue()+"(edited)");
        }
        
        editPage.setValuesForSubmission(0, subs[0]);
        editPage.setValuesForSubmission(1, subs[1]);
        editPage.setValuesForSubmission(2, subs[2]);
        
        InstructorFeedbacksPage resultsPage = editPage.submit();
        String expectedStatus = String.format(
                Const.StatusMessages.INSTRUCTOR_EVALUATION_SUBMISSION_RECEIVED,
                testData.students.get("Charlie").name,
                eval.name,
                eval.courseId)
                .replace("<br />", "\n")
                .replace("<span class='color_red bold'>", "")
                .replace("</span>", "");
        resultsPage.verifyStatus(expectedStatus);

        verifyUpdatePersisted("Charlie", "Charlie", subs[0]);
        verifyUpdatePersisted("Charlie", "Danny", subs[1]);
        verifyUpdatePersisted("Charlie", "Emily", subs[2]);
        
    }

    private void verifyUpdatePersisted(String reviewerName, String revieweeName, SubmissionAttributes expected) {
        SubmissionAttributes actual = BackDoor.getSubmission(eval.courseId, eval.name, testData.students.get(reviewerName).email, testData.students.get(revieweeName).email);
        assertEquals(expected.toString(), actual.toString());
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}