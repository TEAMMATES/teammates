package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.api.Logic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.util.TestHelper;

public class SubmissionsLogicTest extends BaseComponentTestCase{
    
    //TODO: add missing test cases. Some of the test content can be transferred from LogicTest.
    
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(SubmissionsLogic.class);
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws Exception{
        testUpdateSubmission();
        testGetSubmissionsForEvaluation();
    }

    public void testGetSubmissionsForEvaluation() throws Exception {
        Logic logic = new Logic();

        ______TS("typical case");

        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        // reuse this evaluation data to create a new one
        evaluation.name = "new evaluation";
        logic.createEvaluationWithoutSubmissionQueue(evaluation);

        HashMap<String, SubmissionAttributes> submissions = invokeGetSubmissionsForEvaluation(
                evaluation.courseId, evaluation.name);
        // Team 1.1 has 4 students, Team 1.2 has only 1 student.
        // There should be 4*4+1=17 submissions.
        assertEquals(17, submissions.keySet().size());
        // verify they all belong to this evaluation
        for (String key : submissions.keySet()) {
            assertEquals(evaluation.courseId, submissions.get(key).course);
            assertEquals(evaluation.name, submissions.get(key).evaluation);
        }
        
        ______TS("orphan submissions");
        
        // move student from Team 1.1 to Team 1.2
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        student.team = "Team 1.2";
        StudentsLogic.inst().updateStudentCascadeWithoutDocument(student.email, student);

        // Now, team 1.1 has 3 students, team 1.2 has 2 student.
        // There should be 3*3+2*2=13 submissions if no orphans are returned.
        submissions = invokeGetSubmissionsForEvaluation(evaluation.courseId,
                evaluation.name);
        assertEquals(13, submissions.keySet().size());
        
        // Check if the returned submissions match the current team structure
        List<StudentAttributes> students = logic
                .getStudentsForCourse(evaluation.courseId);
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInEvaluation(
                evaluation.name, students, new ArrayList<SubmissionAttributes>(
                        submissions.values()));

        ______TS("evaluation in empty course");
        
        logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        String idOfEmptyCourse = "emptycourse1";
        logic.createCourseAndInstructor("instructor1", idOfEmptyCourse, "Course 1");
        evaluation.courseId = idOfEmptyCourse;
        logic.createEvaluation(evaluation);

        submissions = invokeGetSubmissionsForEvaluation(evaluation.courseId,
                evaluation.name);
        assertEquals(0, submissions.keySet().size());
        
        logic.deleteInstructor(idOfEmptyCourse, "instructor@email.com");
        logic.deleteAccount("instructor 1");

        ______TS("non-existent course/evaluation");

        assertEquals(0, invokeGetSubmissionsForEvaluation(evaluation.courseId, "non-existent").size());
        assertEquals(0, invokeGetSubmissionsForEvaluation("non-existent", evaluation.name).size());

        // no need to check for invalid parameters as it is a private method
    }
    
    public void testUpdateSubmission() throws Exception {

        SubmissionAttributes s = new SubmissionAttributes();
        s.course = "idOfTypicalCourse1";
        s.evaluation = "evaluation1 In Course1";
        s.reviewee = "student1InCourse1@gmail.com";
        s.reviewer = "student1InCourse1@gmail.com";
        
        ______TS("typical case");

        SubmissionAttributes sub1 = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");

        TestHelper.alterSubmission(sub1);
        submissionsLogic.updateSubmission(sub1);
        TestHelper.verifyPresentInDatastore(sub1);

        ______TS("null parameter");
        
        // private method, not tested.

        ______TS("non-existent evaluation");

        sub1.evaluation = "non-existent";
        
        try {
            submissionsLogic.updateSubmission(sub1);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            ignoreExpectedException();
        }

    }

    
    @SuppressWarnings("unchecked")
    private static HashMap<String, SubmissionAttributes> invokeGetSubmissionsForEvaluation(
            String courseId, String evaluationName) throws Exception {
        Method privateMethod = SubmissionsLogic.class.getDeclaredMethod(
                "getSubmissionsForEvaluationAsMap", new Class[] { String.class,
                        String.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { courseId, evaluationName };
        return (HashMap<String, SubmissionAttributes>) privateMethod.invoke(SubmissionsLogic.inst(),
                params);
    }
    
    
    @AfterClass()
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(SubmissionsLogic.class);
    }


}
