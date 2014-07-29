package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorEvalEditPageAction;
import teammates.ui.controller.InstructorEvalEditPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorEvalEditPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        
        ______TS("Typical case, open edit page for existing evaluation");
        
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");

        gaeSimulation.loginAsInstructor(instructorId);
        
        InstructorEvalEditPageAction validPageAction = getAction(
                Const.ParamsNames.COURSE_ID,evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME,evaluationInCourse1.name);
        ShowPageResult successPageResult = getShowPageResult(validPageAction);
        
        //Verify URL and user status messages
        assertEquals(
                Const.ViewURIs.INSTRUCTOR_EVAL_EDIT+"?error="+successPageResult.isError+"&user="+instructorId,
                successPageResult.getDestinationWithParams());
        assertEquals(false,successPageResult.isError);
        assertEquals("", successPageResult.getStatusMessage());
        
        //Verify InstructorEvalEditPageData contents
        InstructorEvalEditPageData pageData = (InstructorEvalEditPageData) successPageResult.data;
        
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(evaluationInCourse1.name, pageData.evaluation.name);
        assertEquals(evaluationInCourse1.courseId, pageData.evaluation.courseId);
        
        ______TS("Error case, edit a non-existing evaluation");
        
        String evalName = "DoesNotExist";
        EvaluationAttributes nonExistingEvaluation = dataBundle.evaluations.get(evalName);
        ShowPageResult nonExistingEvalPageResult = null;
        
        assertNull(nonExistingEvaluation);
        
        try {
            InstructorEvalEditPageAction nonExistingEvalPageAction = getAction(
                    Const.ParamsNames.COURSE_ID,evaluationInCourse1.courseId,
                    Const.ParamsNames.EVALUATION_NAME,evalName);
            nonExistingEvalPageResult = getShowPageResult(nonExistingEvalPageAction);
        } catch(UnauthorizedAccessException e) {
            assertEquals("Trying to access system using a non-existent evaluation entity", e.getMessage());
        }
        
        assertNull(nonExistingEvalPageResult);
        
    }
    
    private InstructorEvalEditPageAction getAction(String... params) throws Exception{
        return (InstructorEvalEditPageAction) (gaeSimulation.getActionObject(uri, params));
    }

}
