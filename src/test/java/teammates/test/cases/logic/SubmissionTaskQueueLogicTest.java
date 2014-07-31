package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Date;
import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;

public class SubmissionTaskQueueLogicTest extends
        BaseComponentUsingTaskQueueTestCase {
    
    private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static final SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

    @SuppressWarnings("serial")
    public static class SubmissionTaskQueueCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_EVAL));
            String evaluation = (String) paramMap.get(ParamsNames.SUBMISSION_EVAL);

            assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_COURSE));
            String courseId = (String) paramMap.get(ParamsNames.SUBMISSION_COURSE);
            
            if (courseId.equals("idOfTypicalCourse1")) {
                assertTrue(evaluation.equals("Basic Computing Evaluation1"));
                assertTrue(courseId.equals("idOfTypicalCourse1"));
            } else {
                assertTrue(evaluation.equals("Basic Computing Evaluation2"));
                assertTrue(courseId.equals("idOfTypicalCourse2"));
            }
            
            SubmissionTaskQueueCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(SubmissionTaskQueueCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
        turnLoggingUp(EvaluationsLogic.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(EvaluationsLogic.class);
    }
    
    @Test
    public void testSchedulingOfCreationOfSubmissions() throws Exception{
        
        ______TS("scheduling test case : create a valid evaluation");
        EvaluationAttributes createdEval = new EvaluationAttributes();
        createdEval.courseId = "idOfTypicalCourse2";
        createdEval.name = "Basic Computing Evaluation2";
        createdEval.instructions = new Text("Instructions to student.");
        createdEval.startTime = new Date();
        createdEval.endTime = new Date();

        int counter = 0;
        while(counter != 10){
            SubmissionTaskQueueCallback.resetTaskCount();
            evaluationsLogic.createEvaluationCascade(createdEval);
            if(SubmissionTaskQueueCallback.verifyTaskCount(1)){
                break;
            }
            counter++;
        }
        assertEquals(SubmissionTaskQueueCallback.taskCount, 1);
        
        ______TS("scheduling test case : try to create an invalid evaluation");
        evaluationsLogic
            .deleteEvaluationCascade(createdEval.courseId, createdEval.name);
        createdEval.startTime = null;
        
        SubmissionTaskQueueCallback.resetTaskCount();
        try {
            evaluationsLogic.createEvaluationCascade(createdEval);
            signalFailureToDetectException("Expected failure not encountered");
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        
        if(!SubmissionTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(SubmissionTaskQueueCallback.taskCount, 0);
        }
    }
    
    @Test
    private void testCreationOfSubmissions() throws Exception {
        
        ______TS("creation test case : verify submissions of a valid evaluation");
        EvaluationAttributes createdEval = new EvaluationAttributes();
        createdEval.courseId = "idOfTypicalCourse1";
        createdEval.name = "Basic Computing Evaluation1";
        createdEval.instructions = new Text("Instructions to student.");
        createdEval.startTime = new Date();
        createdEval.endTime = new Date();
        evaluationsLogic.createEvaluationCascade(createdEval);
        
        evaluationsLogic.createSubmissionsForEvaluation(createdEval);
        /*
         * Expected number = Subs for team 1 + team2 
         *         =[4*3(unique-pair-wise) + 4(self-eval)] + 1 (self-eval)
         *      =17
         */
        int expectedNumberOfSubmissions = 17;
        int actualNumberOfSubmissions = submissionsLogic
                .getSubmissionsForEvaluation(createdEval.courseId, createdEval.name).size();
        assertEquals(expectedNumberOfSubmissions, actualNumberOfSubmissions);
    }
}
