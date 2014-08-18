package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentEvalSubmissionEditPageAction;

public class StudentEvalSubmissionEditPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    EvaluationsDb evaluationsDb = new EvaluationsDb();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        String recentlyJoinedUserId = dataBundle.students.get("student5InCourse1").googleId;
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        String[] submissionParams = new String[]{};
        
        // delete student entity to fabricate an "eventual consistency" problem
        StudentsLogic.inst().deleteStudentsForGoogleIdAndCascade(recentlyJoinedUserId);
        
        ______TS("Student just join course but affected by eventual consistency");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, eval.courseId
                };
        
        gaeSimulation.loginUser(recentlyJoinedUserId);
        StudentEvalSubmissionEditPageAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);
        
        String expectedStatusMessage = "Updating of the course data on our servers is currently in progress "
                + "and will be completed in a few minutes. "
                + "<br>Please wait a few minutes to submit the evaluation again.";
        assertEquals(expectedStatusMessage, r.getStatusMessage());
        
        //TODO: implement this
        
    }
    
    private StudentEvalSubmissionEditPageAction getAction(String... params) throws Exception{
        return (StudentEvalSubmissionEditPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
