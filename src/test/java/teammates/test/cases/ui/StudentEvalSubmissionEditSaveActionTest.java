package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.StudentEvalSubmissionEditSaveAction;

public class StudentEvalSubmissionEditSaveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    String studentId = dataBundle.students.get("student1InCourse1").googleId;
    EvaluationsDb evaluationsDb = new EvaluationsDb();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_SAVE;
    }

    @Test
    public void testValidation() throws Exception {
        gaeSimulation.loginAsStudent(studentId);
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        ______TS("typical success case");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
                Const.ParamsNames.TEAM_NAME, sub.team,
                Const.ParamsNames.TO_EMAIL, sub.reviewee,
                Const.ParamsNames.POINTS, Integer.toString(sub.points),
                Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
                Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
        };
        StudentEvalSubmissionEditSaveAction a = getAction(submissionParams);
        ActionResult r = a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.STUDENT_HOME_PAGE+"?"+Const.ParamsNames.CHECK_PERSISTENCE_EVALUATION+"=idOfTypicalCourse1evaluation1+In+Course1"+
                "&error=false&user="+student1InCourse1.googleId,r.getDestinationWithParams());
        
        assertFalse(r.isError);
        assertEquals("Your submission for evaluation1 In Course1 in course idOfTypicalCourse1 has been saved successfully", 
                     r.getStatusMessage());
        
        ______TS("empty point field");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
                Const.ParamsNames.TEAM_NAME, sub.team,
                Const.ParamsNames.TO_EMAIL, sub.reviewee,
                Const.ParamsNames.POINTS, "",
                Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
                Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
        };
        a = getAction(submissionParams);
        r = a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE
                        + "?courseid=" + eval.courseId
                        + "&evaluationname=evaluation1+In+Course1"
                        + "&user=" + student1InCourse1.googleId
                        + "&error=true",
                r.getDestinationWithParams());
        
        assertEquals("Please give contribution scale to everyone", r.getStatusMessage());    
        assertTrue(r.isError);
        
        ______TS("multiple empty point field");
        
        SubmissionAttributes sub2 = dataBundle.submissions.get("submissionFromS1C1ToS1C1");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
                Const.ParamsNames.TEAM_NAME, sub.team,
                Const.ParamsNames.TO_EMAIL, sub.reviewee,
                Const.ParamsNames.TO_EMAIL, sub2.reviewee,
                Const.ParamsNames.POINTS, "",
                Const.ParamsNames.POINTS, "",
                Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
                Const.ParamsNames.JUSTIFICATION, sub2.justification.toString(),
                Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString(),
                Const.ParamsNames.COMMENTS, sub2.p2pFeedback.toString()
        };
        a = getAction(submissionParams);
        r = a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE
                        + "?courseid=" + eval.courseId
                        + "&evaluationname=evaluation1+In+Course1"
                        + "&user=" + student1InCourse1.googleId
                        + "&error=true",
                r.getDestinationWithParams());
        assertEquals("Please give contribution scale to everyone", r.getStatusMessage());
        assertTrue(r.isError);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        //TODO: implement this
        //TODO: ensure uneditable if not OPEN
        gaeSimulation.loginAsStudent(studentId);
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String submissionFailMessage = new String();
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
                Const.ParamsNames.TEAM_NAME, sub.team,
                Const.ParamsNames.TO_EMAIL, sub.reviewee,
                Const.ParamsNames.POINTS, sub.points+"",
                Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
                Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
                
                };
        StudentEvalSubmissionEditSaveAction a = getAction(submissionParams);
        ActionResult r = a.executeAndPostProcess();
        
        assertEquals("Your submission for evaluation1 In Course1 in course idOfTypicalCourse1 has been saved successfully", r.getStatusMessage());
        ______TS("closed");
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-10);
        
        evaluationsDb.updateEvaluation(eval);
        assertEquals(EvalStatus.CLOSED, eval.getStatus());
         
        try{
            r = a.executeAndPostProcess();
        }
        catch(UnauthorizedAccessException e){
            submissionFailMessage = e.getMessage();
        }
        assertEquals(Const.Tooltips.EVALUATION_STATUS_CLOSED, submissionFailMessage);
        
    
        ______TS("published");
    
        eval.published = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        try{
            r = a.executeAndPostProcess();
        }
        catch(UnauthorizedAccessException e){
            submissionFailMessage = e.getMessage();
        }
        assertEquals(Const.Tooltips.EVALUATION_STATUS_PUBLISHED, submissionFailMessage);
    
        ______TS("awaiting");
    
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        eval.setDerivedAttributes();
        assertEquals(EvalStatus.AWAITING, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        try{
            r = a.executeAndPostProcess();
        }
        catch(UnauthorizedAccessException e){
            submissionFailMessage = e.getMessage();
        }
        assertEquals(Const.Tooltips.EVALUATION_STATUS_AWAITING, submissionFailMessage);
        
        ______TS("opened");
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        evaluationsDb.updateEvaluation(eval);
        
        assertEquals(EvalStatus.OPEN, eval.getStatus());

        r = a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.STUDENT_HOME_PAGE+"?"+Const.ParamsNames.CHECK_PERSISTENCE_EVALUATION+"=idOfTypicalCourse1evaluation1+In+Course1"+
                "&error=false&user="+student1InCourse1.googleId,r.getDestinationWithParams());
        
        assertFalse(r.isError);
        
        ______TS("Null parameters");
        submissionParams = new String[]{};
    
        a = getAction(submissionParams);
        r = a.executeAndPostProcess();
        
        assertEquals(Const.StatusMessages.EVALUATION_REQUEST_EXPIRED, r.getStatusMessage());
    }
    
    private StudentEvalSubmissionEditSaveAction getAction(String... params) throws Exception{
        return (StudentEvalSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
    
}
