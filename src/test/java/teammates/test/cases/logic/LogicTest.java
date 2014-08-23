package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.UserType;
import teammates.logic.api.Logic;
import teammates.logic.core.SubmissionsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.util.TestHelper;

public class LogicTest extends BaseComponentTestCase {

    private static final Logic logic = new Logic();
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(Logic.class);
        removeAndRestoreTypicalDataInDatastore();
    }

    @SuppressWarnings("unused")
    private void ____USER_level_methods___________________________________() {
    }

    @Test
    public void testGetLoginUrl() {
        gaeSimulation.logoutUser();
        assertEquals("/_ah/login?continue=www.abc.com",
                Logic.getLoginUrl("www.abc.com"));
    }

    @Test
    public void testGetLogoutUrl() {
        gaeSimulation.loginUser("any.user");
        assertEquals("/_ah/logout?continue=www.def.com",
                Logic.getLogoutUrl("www.def.com"));
    }
    
    //TODO: test isUserLoggedIn method

    @Test
    public void testGetCurrentUser() throws Exception {

        ______TS("admin+instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse2");
        gaeSimulation.loginAsAdmin(instructor.googleId);
        // also make this user a student of another course
        StudentAttributes instructorAsStudent = new StudentAttributes(
                "Section 1", "Team 1", "Instructor As Student", "instructorasstudent@yahoo.com", "", course.id);
        instructorAsStudent.googleId = instructor.googleId;
        logic.createStudentWithoutDocument(instructorAsStudent);

        UserType user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(true, user.isStudent);

        ______TS("unregistered");

        gaeSimulation.loginUser("unknown");

        user = logic.getCurrentUser();
        assertEquals("unknown", user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        gaeSimulation.logoutUser();
        assertEquals(null, logic.getCurrentUser());
    }

    @SuppressWarnings("unused")
    private void ____SUBMISSION_level_methods_______________________________() {
    }

    @Test
    public void testCreateSubmission() {
        // method not implemented
    }


    @Test
    public void testGetSubmissionsForEvaluationFromStudent() throws Exception {
        
        ______TS("typical case");

        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation2InCourse1");
        // reuse this evaluation data to create a new one
        evaluation.name = "new evaluation";
        logic.createEvaluationWithoutSubmissionQueue(evaluation);
        // this is the student we are going to check
        StudentAttributes student = dataBundle.students.get("student3InCourse1");
    
        List<SubmissionAttributes> submissions = logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, student.email);
        // there should be 4 submissions as this student is in a 4-person team
        assertEquals(4, submissions.size());
        // verify they all belong to this student
        for (SubmissionAttributes s : submissions) {
            assertEquals(evaluation.courseId, s.course);
            assertEquals(evaluation.name, s.evaluation);
            assertEquals(student.email, s.reviewer);
            assertEquals(student.name, s.details.reviewerName);
            assertEquals(logic.getStudentForEmail(evaluation.courseId, s.reviewee).name,
                    s.details.revieweeName);
        }
    
        ______TS("orphan submissions");
    
        //Move student to a new team
        student.team = "Team 1.3";
        logic.updateStudentWithoutDocument(student.email, student);
        
        submissions = logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, student.email);
        //There should be 1 submission as he is now in a 1-person team.
        //   Orphaned submissions from previous team should not be returned.
                assertEquals(1, submissions.size());
                
        // Move the student out and move in again
        student.team = "Team 1.4";
        logic.updateStudentWithoutDocument(student.email, student);
        student.team = "Team 1.3";
        logic.updateStudentWithoutDocument(student.email, student);
        submissions = logic.getSubmissionsForEvaluationFromStudent(evaluation.courseId,
                evaluation.name, student.email);
        assertEquals(1, submissions.size());
    
        ______TS("null parameters");
        
        try {
            logic.getSubmissionsForEvaluationFromStudent("valid.course.id", "valid evaluation name", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("course/evaluation/student does not exist");
    
        
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                "non-existent", evaluation.name, student.email ).size());
    
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, "non-existent", student.email ).size());
    
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, "non-existent" ).size());
    }

    @Test
    public void testHasStudentSubmittedEvaluation() throws Exception {
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
    
        ______TS("student has submitted");

        assertEquals(true, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, student.email));
    
        ______TS("student has not submitted");
    
        // create a new evaluation reusing data from previous one
        evaluation.name = "New evaluation";
        logic.createEvaluation(evaluation);
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, student.email));
    
        ______TS("null parameters");
    
        try {
            logic.hasStudentSubmittedEvaluation("valid.course.id", "valid evaluation name", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent course/evaluation/student");
    
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                "non-existent-course", evaluation.name, student.email));
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, "non-existent-eval", student.email));
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, "non-existent@student"));
    
    }

    @Test
    public void testUpdateSubmissions() throws Exception {

        ______TS("typical cases");        

        ArrayList<SubmissionAttributes> submissionContainer = new ArrayList<SubmissionAttributes>();

        // try without empty list. Nothing should happen
        logic.updateSubmissions(submissionContainer);

        SubmissionAttributes sub1 = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");

        SubmissionAttributes sub2 = dataBundle.submissions
                .get("submissionFromS2C1ToS1C1");

        // checking editing of one of the submissions
        TestHelper.alterSubmission(sub1);

        submissionContainer.add(sub1);
        logic.updateSubmissions(submissionContainer);

        TestHelper.verifyPresentInDatastore(sub1);
        TestHelper.verifyPresentInDatastore(sub2);

        // check editing both submissions
        TestHelper.alterSubmission(sub1);
        TestHelper.alterSubmission(sub2);

        submissionContainer = new ArrayList<SubmissionAttributes>();
        submissionContainer.add(sub1);
        submissionContainer.add(sub2);
        logic.updateSubmissions(submissionContainer);

        TestHelper.verifyPresentInDatastore(sub1);
        TestHelper.verifyPresentInDatastore(sub2);

        ______TS("non-existent evaluation");

        // already tested under testUpdateSubmission()

        ______TS("null parameter");

        try {
            logic.updateSubmissions(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }



    @Test
    public void testDeleteSubmission() {
        // method not implemented
    }
    
    /* TODO: implement tests for the following :
     * 1. getFeedbackSessionDetails()
     * 2. getFeedbackSessionsListForInstructor()
     */

    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(Logic.class);
    }

}
