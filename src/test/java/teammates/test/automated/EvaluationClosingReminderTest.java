package teammates.test.automated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.automated.EmailAction;
import teammates.logic.automated.EvaluationClosingMailAction;
import teammates.logic.core.Emails;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.Emails.EmailType;
import teammates.logic.core.EvaluationsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;

public class EvaluationClosingReminderTest extends BaseComponentUsingTaskQueueTestCase {
    
    private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static final StudentsLogic studentsLogic = new StudentsLogic();
    private static final InstructorsLogic instructorsLogic = new InstructorsLogic();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    /*
     * Implemented as recommended in App Engine docs :
     *         https://developers.google.com/appengine/docs/java/tools/localunittesting#Java_Writing_deferred_task_tests
     * 
     */
    @SuppressWarnings("serial")
    public static class EvaluationClosingCallback extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
            
            EmailType typeOfMail = EmailType.valueOf((String) paramMap.get(ParamsNames.EMAIL_TYPE));
            assertEquals(EmailType.EVAL_CLOSING, typeOfMail);
            
            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_EVAL));
            String evaluation = (String) paramMap.get(ParamsNames.EMAIL_EVAL);
            assertTrue(evaluation.equals("new-eval-in-course-1-tSRFCE") || 
                       evaluation.equals("new-evaluation-in-course-2-tARE"));

            assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
            String courseId = (String) paramMap.get(ParamsNames.EMAIL_COURSE);
            assertTrue(courseId.equals("idOfTypicalCourse1") || 
                       courseId.equals("idOfTypicalCourse2"));
            
            EvaluationClosingCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(EvaluationClosingCallback.class);
        gaeSimulation.resetDatastore();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testAdditionOfTaskToTaskQueue() throws Exception {
        EvaluationClosingCallback.resetTaskCount();
        
        ______TS("typical case, 0 evaluations closing soon");
        evaluationsLogic.scheduleRemindersForClosingEvaluations();

        if(!EvaluationClosingCallback.verifyTaskCount(0)){
            assertEquals(EvaluationClosingCallback.taskCount, 0);
        }
        
        ______TS("typical case, two evaluations closing soon");
        // Reuse an existing evaluation to create a new one that is
        // closing in 24 hours.
        EvaluationAttributes evaluation1 = dataBundle.evaluations
                .get("evaluation1InCourse1");
        String nameOfEvalInCourse1 = "new-eval-in-course-1-tSRFCE";
        evaluation1.name = nameOfEvalInCourse1;

        evaluation1.activated = true;

        double timeZone = 0.0;
        evaluation1.timeZone = timeZone;
        evaluation1.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        evaluation1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(evaluation1);

        // Create another evaluation in another course in similar fashion.
        // This one too is closing in 24 hours.
        EvaluationAttributes evaluation2 = dataBundle.evaluations
                .get("evaluation1InCourse2");
        evaluation2.activated = true;
        String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tARE";
        evaluation2.name = nameOfEvalInCourse2;

        evaluation2.timeZone = 0.0;

        evaluation2.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        evaluation2.endTime = TimeHelper.getDateOffsetToCurrentTime(1);

        evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(evaluation2);
        
        // Create an evaluation which is not closing in 24 hours
        // as a control
        EvaluationAttributes evaluation3 = dataBundle.evaluations
                .get("evaluation2InCourse1");
        evaluation3.activated = true;
        String nameOfEval2InCourse1 = "eval-not-closing-in-24";
        evaluation3.name = nameOfEval2InCourse1;

        evaluation3.timeZone = 0.0;

        evaluation3.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        evaluation3.endTime = TimeHelper.getDateOffsetToCurrentTime(2);

        evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(evaluation3);
        
        int counter = 0;

        while(counter != 10){
            EvaluationClosingCallback.resetTaskCount();
            evaluationsLogic.scheduleRemindersForClosingEvaluations();
            //Expected is 2 because only 2 active evaluations closing within 24 hours
            if(EvaluationClosingCallback.verifyTaskCount(2)){
                break;
            }
            counter++;
        }
        
        assertEquals(EvaluationClosingCallback.taskCount, 2);
        
        evaluationsLogic.deleteEvaluationCascade(evaluation1.courseId, evaluation1.name);
        evaluationsLogic.deleteEvaluationCascade(evaluation2.courseId, evaluation2.name);   
    }

    @Test
    @SuppressWarnings("deprecation")
    private void testEvaluationClosingMailAction() throws Exception{        
        // Reuse an existing evaluation to create a new one that is
        // closing in 24 hours.
        ______TS("MimeMessage test : testing for number and content of emails generated");
        EvaluationAttributes evaluation1 = dataBundle.evaluations
                .get("evaluation1InCourse1");
        String nameOfEvalInCourse1 = "new-eval-in-course-1-tSRFCE";
        evaluation1.name = nameOfEvalInCourse1;

        evaluation1.activated = true;

        double timeZone = 0.0;
        evaluation1.timeZone = timeZone;
        evaluation1.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        evaluation1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);

        evaluationsLogic.createEvaluationCascadeWithoutSubmissionQueue(evaluation1);
        
        //Prepare parameter map to be used with EvaluationClosingMailAction
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.EMAIL_TYPE, EmailType.EVAL_CLOSING.toString());
        paramMap.put(ParamsNames.EMAIL_EVAL, evaluation1.name);
        paramMap.put(ParamsNames.EMAIL_COURSE, evaluation1.courseId);
        
        EmailAction evalClosingAction = new EvaluationClosingMailAction(paramMap);
        List<MimeMessage> preparedEmails = evalClosingAction.getPreparedEmailsAndPerformSuccessOperations();
        
        int course1StudentCount = studentsLogic.getStudentsForCourse(
                evaluation1.courseId).size();
        int course1InstructorCount = instructorsLogic.getInstructorsForCourse(
                evaluation1.courseId).size();
        
        assertEquals(course1StudentCount + course1InstructorCount,
                preparedEmails.size());
        
        for (MimeMessage m : preparedEmails) {
            String subject = m.getSubject();
            assertTrue(subject.contains(evaluation1.name));
            assertTrue(subject.contains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING));
        }
        
        evaluationsLogic.deleteEvaluationCascade(evaluation1.courseId, evaluation1.name);
    }
}
