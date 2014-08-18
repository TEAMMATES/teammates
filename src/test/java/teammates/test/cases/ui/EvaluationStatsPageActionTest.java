package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.EvaluationStatsPageAction;
import teammates.ui.controller.EvaluationStatsPageData;

public class EvaluationStatsPageActionTest extends BaseActionTest {
    
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_STATS_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("typical: instructor accesses stats of his/her course");
        EvaluationAttributes accessableEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        String[] submissionParams = new String[] { Const.ParamsNames.EVALUATION_NAME, accessableEvaluation.name,
                                          Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId};
        
        EvaluationStatsPageAction a = getAction(addUserIdToParams(instructorId, submissionParams));
        AjaxResult r = (AjaxResult)a.executeAndPostProcess();
        EvaluationStatsPageData data = (EvaluationStatsPageData) r.data;
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_EVAL_STATS+"?error=false&user=idOfInstructor1OfCourse1", r.getDestinationWithParams());
        assertEquals(data.evaluationDetails.stats.expectedTotal,5);
        assertEquals(data.evaluationDetails.stats.submittedTotal,3);
        assertEquals("", r.getStatusMessage());
        
        ______TS("fail: instructor accesses stats of non-existent evaluation");
        String nonexistentEvaluation = "nonexistentEvaluation";
        submissionParams = new String[] { Const.ParamsNames.EVALUATION_NAME, nonexistentEvaluation,
                                          Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId};
        boolean doesThrowUnauthorizedAccessException = false;
        String exceptionMessage = "";
        
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        try {
            r = (AjaxResult)a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            doesThrowUnauthorizedAccessException = true;
            exceptionMessage = e.getMessage();
        }
        
        assertEquals(true, doesThrowUnauthorizedAccessException);
        assertEquals("Trying to access system using a non-existent evaluation entity", exceptionMessage);
        
    }
    
    private EvaluationStatsPageAction getAction(String... params) throws Exception{
        return (EvaluationStatsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
