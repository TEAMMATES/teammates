package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentEvalResultsPageAction;


public class StudentEvalResultsPageActionTest extends BaseActionTest {

    DataBundle dataBundle;
    EvaluationsDb evaluationsDb = new EvaluationsDb();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.published = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentId = student1InCourse1.googleId;
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name
                };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        
        ______TS("Student not part of course, redirect to home page.");
        gaeSimulation.loginUser("unreg.user");
        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
        verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
        
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);

        gaeSimulation.loginAsInstructor(instructorId);
        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
        verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
        
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
        
        
        
        
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        String recentlyJoinedUserId = "recentlyJoined.student";
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        String[] submissionParams = new String[]{};
        
        
        ______TS("Student just join course but affected by eventual consistency");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.CHECK_PERSISTENCE_COURSE, eval.courseId
                };
        
        gaeSimulation.loginUser(recentlyJoinedUserId);
        StudentEvalResultsPageAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);
        
        String expectedStatusMessage = "Updating of the course data on our servers is currently in progress "
                + "and will be completed in a few minutes. "
                + "<br>Please wait few minutes to view the evaluation again.";
        assertEquals(expectedStatusMessage, r.getStatusMessage());
        
        
        //TODO: implement this
        //TODO: ensure results are not viewable when not PUBLISHED
        
        
    }

    private StudentEvalResultsPageAction getAction(String... params) throws Exception{
        return (StudentEvalResultsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
    
}
