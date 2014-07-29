package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.EvaluationsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;

public class EvaluationsEmailTaskQueueTest extends
        BaseComponentUsingTaskQueueTestCase {
    
    private static final Logic logic = new Logic();
    private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();

    @SuppressWarnings("serial")
    public static class EvaluationsEmailTaskQueueCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_EVAL));
            assertNotNull(paramMap.get(ParamsNames.SUBMISSION_EVAL));

            assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_COURSE));
            assertNotNull(paramMap.get(ParamsNames.SUBMISSION_COURSE));
            
            EvaluationsEmailTaskQueueCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(EvaluationsEmailTaskQueueCallback.class);
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
    public void testEvaluationsPublishEmail() throws Exception{
        EvaluationsEmailTaskQueueCallback.resetTaskCount();

        ______TS("Publish evaluation and send email");
        
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        eval.endTime = TimeHelper.getHoursOffsetToCurrentTime(0);
        evaluationsLogic.updateEvaluation(eval);

        int counter = 0;

        while(counter != 10){
            EvaluationsEmailTaskQueueCallback.resetTaskCount();
            evaluationsLogic.publishEvaluation(eval.courseId, eval.name);
            if(EvaluationsEmailTaskQueueCallback.verifyTaskCount(1)){
                break;
            }
            counter = 0;
        }
        if(counter == 10){
            assertEquals(EvaluationsEmailTaskQueueCallback.taskCount, 1);
        }

        ______TS("Try to publish non-existent evaluation");
        
        EvaluationsEmailTaskQueueCallback.resetTaskCount();
        eval.endTime = TimeHelper.getHoursOffsetToCurrentTime(0);
        evaluationsLogic.updateEvaluation(eval);
        try {
            evaluationsLogic.publishEvaluation("non-existent-course", "non-existent-evaluation");
        } catch (Exception e) {
            assertEquals("Trying to edit non-existent evaluation non-existent-course/non-existent-evaluation", 
                    e.getMessage());
        }
        if(!EvaluationsEmailTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(EvaluationsEmailTaskQueueCallback.taskCount, 0);
        }
        
    }
    
    @Test
    public void testEvaluationsRemindEmail() throws Exception {
        
        EvaluationsEmailTaskQueueCallback.resetTaskCount();

        ______TS("Send evaluation reminder email");
        
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        int counter = 0;

        while(counter != 10){
            EvaluationsEmailTaskQueueCallback.resetTaskCount();
            logic.sendReminderForEvaluation(eval.courseId, eval.name);
            if(EvaluationsEmailTaskQueueCallback.verifyTaskCount(1)){
                break;
            }
            counter++;
        }
        if(counter == 10){  
            assertEquals(EvaluationsEmailTaskQueueCallback.taskCount, 1);
        }
        
        ______TS("Try to send reminder for null evaluation");
        
        EvaluationsEmailTaskQueueCallback.resetTaskCount();
        eval = dataBundle.evaluations.get("evaluation1InCourse1");
        try {
            logic.sendReminderForEvaluation(eval.courseId, null);
        } catch (AssertionError a) {
            assertEquals("The supplied parameter was null\n", a.getMessage());
        }
        if(!EvaluationsEmailTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(EvaluationsEmailTaskQueueCallback.taskCount, 0);
        }
    }
}
