package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.logic.core.TeamEvalResult.NA;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.logic.core.TeamEvalResult;
import teammates.storage.api.EvaluationsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;
import static teammates.logic.core.TeamEvalResult.NSB;
import static teammates.logic.core.TeamEvalResult.NSU;

public class EvaluationsLogicTest extends BaseComponentTestCase{
    
    DataBundle dataBundle = getTypicalDataBundle();

    private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
    private static final SubmissionsLogic submissionsLogic = new SubmissionsLogic();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.resetDatastore();
        turnLoggingUp(EvaluationsLogic.class);
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws Exception{
        
        testSendReminderForEvaluation();
        
        testIsEvaluationCompletedByStudent();
        testIsEvaluationExists();
        testGetEvaluation();
        testGetEvaluationsForCourse();
        testGetEvaluationsClosingWithinTimeLimit();
        testGetEvaluationsDetailsForInstructor();
        testGetEvaluationsListForInstructor();
        testGetEvaluationsDetailsForCourse();
        testGetEvaluationsDetailsForCourseAndEval();
        testGetEvaluationResultSummaryAsCsv();
        testGetEvaluationResultForStudent();
        testGetEvaluationResult();
        
        testPublishAndUnpublishEvaluation();
        testSendEvaluationPublishedEmails();
        testSetEvaluationActivationStatus();
        
        testAdjustSubmissionsForNewStudentInEvaluation();
        testGetReadyEvaluations();
        
        
        testCalculateTeamResult();
        
        testCreateEvaluationCascadeWithSubmissionQueue();
        testCreateSubmissionsForEvaluation();
        testUpdateEvaluation();
        testDeleteEvaluationCascade();
        
        
    }

    public void testCreateEvaluationCascadeWithSubmissionQueue() throws Exception{
    
        ______TS("Typical case : create a valid evaluation");

        EvaluationAttributes createdEval = new EvaluationAttributes();
        createdEval.courseId = "Computing104";
        createdEval.name = "Basic Computing Evaluation1";
        createdEval.instructions = new Text("Instructions to student.");
        createdEval.startTime = new Date();
        createdEval.endTime = new Date();
        evaluationsLogic.createEvaluationCascade(createdEval);
        
        EvaluationAttributes retrievedEval = evaluationsLogic
                .getEvaluation(createdEval.courseId, createdEval.name);
        assertEquals(createdEval.toString(), retrievedEval.toString());
        
        ______TS("Failure case: create a duplicate evaluation");

        EvaluationAttributes duplicateEval = dataBundle.evaluations.get("evaluation2InCourse1");
        try {
            evaluationsLogic.createEvaluationCascade(duplicateEval);   
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains("Trying to create a Evaluation that exists:", e.getMessage());
        }
     
        ______TS("Failure case : try to create an invalid evaluation");
        
        evaluationsLogic
            .deleteEvaluationCascade(createdEval.courseId, createdEval.name);
        createdEval.startTime = null;
        try {
            evaluationsLogic.createEvaluationCascade(createdEval);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        
        retrievedEval = evaluationsLogic
                .getEvaluation(createdEval.courseId, createdEval.name);
        assertNull(retrievedEval);

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.createEvaluationCascade(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testCreateSubmissionsForEvaluation() throws Exception {
        
        EvaluationAttributes createdEval = new EvaluationAttributes();
        createdEval.courseId = "Computing 104";
        createdEval.name = "Basic Computing Evaluation1";
        createdEval.instructions = new Text("Instructions to student.");
        createdEval.startTime = new Date();
        createdEval.endTime = new Date();

        ______TS("Failure case : try to create submissions for invalid course");
        
        try {
            evaluationsLogic.createSubmissionsForEvaluation(createdEval);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("Failure case : try to create submissions for invalid evaluation");
        
        createdEval.courseId = "idOfTypicalCourse1";
        try {
            evaluationsLogic.createSubmissionsForEvaluation(createdEval);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist :", e.getMessage());
        }

        ______TS("Typical case: create submissions successfully for evaluation");

        evaluationsLogic.createEvaluationCascade(createdEval);
        evaluationsLogic.createSubmissionsForEvaluation(createdEval);
        assertEquals(17, submissionsLogic.getSubmissionsForEvaluation(createdEval.courseId, createdEval.name).size());
        evaluationsLogic.deleteEvaluationCascade(createdEval.courseId, createdEval.name);
        
    }


    public void testGetEvaluation() throws Exception {

        ______TS("Typical case");

        EvaluationAttributes expected = dataBundle.evaluations
                .get("evaluation2InCourse1");
        EvaluationAttributes actual = evaluationsLogic.getEvaluation(expected.courseId,
                expected.name);
        TestHelper.verifySameEvaluationData(expected, actual);

        ______TS("Failure case: null parameters");

        try {
            evaluationsLogic.getEvaluation("valid.course.id", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            AssertHelper.assertContains("Supplied parameter was null", a.getMessage());
        }

        ______TS("Failure case: non-existent course or evaluation");

        assertNull(evaluationsLogic.getEvaluation("non-existent", expected.name));
        assertNull(evaluationsLogic.getEvaluation(expected.courseId, "non-existent"));

    }

    public void testGetEvaluationsForCourse() throws Exception {

        ______TS("Typical case");

        EvaluationAttributes expectedEvaluation1 = dataBundle.evaluations.get("evaluation1InCourse1");
        EvaluationAttributes expectedEvaluation2 = dataBundle.evaluations.get("evaluation2InCourse1");
        List<EvaluationAttributes> evaluationsList = evaluationsLogic.getEvaluationsForCourse(expectedEvaluation1.courseId);
        TestHelper.verifySameEvaluationData(expectedEvaluation2, evaluationsList.get(0));
        TestHelper.verifySameEvaluationData(expectedEvaluation1, evaluationsList.get(1));

        ______TS("Boundary case: course with no evaluations");

        evaluationsList = evaluationsLogic.getEvaluationsForCourse("idOfCourseNoEvals");
        assertEquals(0, evaluationsList.size());

        ______TS("Failure case: non-existent course");

        assertEquals(0, evaluationsLogic.getEvaluationsForCourse("non-existent").size());

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.getEvaluationsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
   
    public void testGetEvaluationsClosingWithinTimeLimit() throws Exception {
        
        ______TS("Typical case : no evaluations closing within a certain period");
        
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse2");
        int numberOfHoursToTimeLimit = 2; //arbitrary number of hours
        
        eval.timeZone = 0;
        eval.endTime = TimeHelper.getHoursOffsetToCurrentTime(numberOfHoursToTimeLimit);
        evaluationsLogic.updateEvaluation(eval);
        
        List<EvaluationAttributes> evaluationsList = evaluationsLogic
                .getEvaluationsClosingWithinTimeLimit(numberOfHoursToTimeLimit-1);
        assertEquals(0, evaluationsList.size());
        
        ______TS("Typical case : 1 evaluation closing within a certain period");
        
        evaluationsList = evaluationsLogic
                .getEvaluationsClosingWithinTimeLimit(numberOfHoursToTimeLimit);
        assertEquals(1, evaluationsList.size());
        assertEquals(eval.name, evaluationsList.get(0).name);
        
    }
    
    public void testGetEvaluationsDetailsForInstructor() throws Exception {
    
        ______TS("Typical case: instructor has 3 evaluations");
            
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        ArrayList<EvaluationDetailsBundle> evalList = evaluationsLogic
                .getEvaluationsDetailsForInstructor(instructor.googleId);
        // 2 Evals from Course 1, 1 Eval from Course  2
        assertEquals(3, evalList.size());
        EvaluationAttributes evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        for (EvaluationDetailsBundle edd : evalList) {
            if(edd.evaluation.name.equals(evaluation.name)){
                //We have, 4 students in Team 1.1 and 1 student in Team 1.2
                //Only 3 have submitted.
                TestHelper.verifySameEvaluationData(edd.evaluation, evaluation);
                assertEquals(5,edd.stats.expectedTotal);
                assertEquals(3,edd.stats.submittedTotal);
            }
        }
    
        ______TS("Typical case: instructor has 1 evaluation");

        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse2");
        evalList = evaluationsLogic.getEvaluationsDetailsForInstructor(instructor2.googleId);
        assertEquals(1, evalList.size());
        for (EvaluationDetailsBundle edd : evalList) {
            assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructor2.googleId, edd.evaluation.courseId));
        }
    
        ______TS("Typical case: instructor has 0 evaluations");
    
        InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
        evalList = evaluationsLogic.getEvaluationsDetailsForInstructor(instructor4.googleId);
        assertEquals(0, evalList.size());
    
        ______TS("Failure case: null parameters");
    
        try {
            evaluationsLogic.getEvaluationsDetailsForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    
        ______TS("Failure case: non-existent instructor");
        
        evalList = evaluationsLogic.getEvaluationsDetailsForInstructor("non-existent");
        assertEquals(0, evalList.size());

    }

    public void testGetEvaluationsListForInstructor() throws Exception {
        
        ______TS("Typical case: instructor has 3 evaluations");
            
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        ArrayList<EvaluationAttributes> evalList = evaluationsLogic
                .getEvaluationsListForInstructor(instructor.googleId);

        // 2 Evals from Course 1, 1 Eval from Course  2
        assertEquals(3, evalList.size());
        EvaluationAttributes evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        for (EvaluationAttributes evalAttr : evalList) {
            if(evalAttr.name.equals(evaluation.name)){
                //We have, 4 students in Team 1.1 and 1 student in Team 1.2
                //Only 3 have submitted.
                TestHelper.verifySameEvaluationData(evalAttr, evaluation);
            }
        }
        
        ______TS("Typical case: instructor has 1 evaluation");

        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse2");
        evalList = evaluationsLogic.getEvaluationsListForInstructor(instructor2.googleId);
        assertEquals(1, evalList.size());
        for (EvaluationAttributes evalAttr : evalList) {
            assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructor2.googleId, evalAttr.courseId));
        }
    
        ______TS("Typical case: instructor has 0 evaluations");
    
        InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
        evalList = evaluationsLogic.getEvaluationsListForInstructor(instructor4.googleId);
        assertEquals(0, evalList.size());
    
        ______TS("Failure case: null parameters");
    
        try {
            evaluationsLogic.getEvaluationsListForInstructor(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    
        ______TS("Failure case: non-existent instructor");
        
        evalList = evaluationsLogic.getEvaluationsListForInstructor("non-existent");
        assertEquals(0, evalList.size());

    }

    public void testGetEvaluationsDetailsForCourse() throws Exception {

        ______TS("Typical case");

        EvaluationAttributes expectedEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        List<EvaluationDetailsBundle> evaluationsList = evaluationsLogic.getEvaluationsDetailsForCourse(expectedEvaluation.courseId);
        
        assertEquals(2, evaluationsList.size());
        
        for(EvaluationDetailsBundle edd : evaluationsList){
            if(edd.evaluation.name.equals(expectedEvaluation.name)){
                TestHelper.verifySameEvaluationData(edd.evaluation, expectedEvaluation);
                assertEquals(5, edd.stats.expectedTotal);
                assertEquals(3, edd.stats.submittedTotal);
            }
        }
        
        ______TS("Boundary case: course with no evaluations");

        evaluationsList = evaluationsLogic.getEvaluationsDetailsForCourse("idOfCourseNoEvals");
        assertEquals(0, evaluationsList.size());

        ______TS("Failure case: non-existent course");
        
        assertEquals(0, evaluationsLogic.getEvaluationsDetailsForCourse("non-existent").size());

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.getEvaluationsDetailsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testGetEvaluationsDetailsForCourseAndEval() throws Exception {

        EvaluationAttributes expectedEvaluation = new EvaluationAttributes();
        expectedEvaluation.courseId = "Computing 104";
        expectedEvaluation.name = "Basic Computing Evaluation1";
        expectedEvaluation.instructions = new Text("Instructions to student.");
        expectedEvaluation.startTime = new Date();
        expectedEvaluation.endTime = new Date();

        ______TS("Failure case : try to find details an evaluation in invalid course");
        
        try {
            evaluationsLogic.getEvaluationsDetailsForCourseAndEval(expectedEvaluation);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("Failure case : try to find details for invalid evaluation");
        
        expectedEvaluation.courseId = "idOfTypicalCourse1";
        try {
            evaluationsLogic.getEvaluationsDetailsForCourseAndEval(expectedEvaluation);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist :", e.getMessage());
        }

        ______TS("Typical case: evaluation in a course with 5 students");
        expectedEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        EvaluationDetailsBundle evaluationDetails = evaluationsLogic.getEvaluationsDetailsForCourseAndEval(expectedEvaluation);
    
        TestHelper.verifySameEvaluationData(evaluationDetails.evaluation, expectedEvaluation);
        assertEquals(5, evaluationDetails.stats.expectedTotal);
        assertEquals(3, evaluationDetails.stats.submittedTotal);

    }

    @SuppressWarnings("deprecation")
    public void testGetReadyEvaluations() throws Exception {
        
        ______TS("No evaluations activated");
        // ensure there are no existing evaluations ready for activation
        for (EvaluationAttributes e : evaluationsDb.getAllEvaluations()) {
            e.activated = true;
            evaluationsLogic.updateEvaluation(e);
            assertTrue(evaluationsLogic.getEvaluation(e.courseId, e.name).getStatus() != EvalStatus.AWAITING);
        }
        assertEquals(0, evaluationsLogic.getReadyEvaluations().size());

        ______TS("Typical case: two evaluations activated");
        
        // Reuse an existing evaluation to create a new one that is ready to
        // activate. Put this evaluation in a negative time zone.
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        String nameOfEvalInCourse1 = "new-evaluation-in-course-1-tGRE";
        evaluation.name = nameOfEvalInCourse1;

        evaluation.activated = false;

        double timeZone = -1.0;
        evaluation.timeZone = timeZone;

        evaluation.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(0,
                timeZone);
        evaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(2);

        evaluationsLogic.createEvaluationCascade(evaluation);

        // Verify that there are no unregistered students.
        // TODO: this should be removed after absorbing registration reminder
        // into the evaluation opening alert.
        CourseDetailsBundle course1 = coursesLogic.getCourseDetails(evaluation.courseId);
        assertEquals(0, course1.stats.unregisteredTotal);

        // Create another evaluation in another course in similar fashion.
        // Put this evaluation in a positive time zone.
        // This one too is ready to activate.
        evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        evaluation.activated = false;
        String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tGRE";
        evaluation.name = nameOfEvalInCourse2;

        timeZone = 2.0;
        evaluation.timeZone = timeZone;

        evaluation.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(0,
                timeZone);
        evaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(2);

        evaluationsLogic.createEvaluationCascade(evaluation);

        // Verify that there are no unregistered students
        // TODO: this should be removed after absorbing registration reminder
        // into the evaluation opening alert.
        CourseDetailsBundle course2 = coursesLogic.getCourseDetails(evaluation.courseId);
        assertEquals(0, course2.stats.unregisteredTotal);

        // Create another evaluation not ready to be activated yet.
        // Put this evaluation in same time zone.
        evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        evaluation.activated = false;
        evaluation.name = "new evaluation - start time in future";

        timeZone = 0.0;
        evaluation.timeZone = timeZone;

        int oneSecondInMs = 1000;
        evaluation.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                oneSecondInMs, timeZone);
        evaluationsLogic.createEvaluationCascade(evaluation);

        // verify number of ready evaluations.
        assertEquals(2, evaluationsLogic.getReadyEvaluations().size());

        // Other variations of ready/not-ready states should be checked at
        // Evaluation level
    }
    
    public void testGetEvaluationResult() throws Exception {
    
        ______TS("Typical case");
    
        // reconfigure points of an existing evaluation in the datastore
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
    
        // @formatter:off
        TestHelper.setPointsForSubmissions(new int[][] { 
                { 100, 100, 100, 100 },
                { 110, 110, NSU, 110 }, 
                { NSB, NSB, NSB, NSB },
                { 70, 80, 110, 120 } });
        // @formatter:on
    
        EvaluationResultsBundle result = evaluationsLogic.getEvaluationResult(course.id,
                evaluation.name);
        print(result.toString());
    
        // no need to sort, the result should be sorted by default
    
        // check for evaluation details
        assertEquals(evaluation.courseId, result.evaluation.courseId);
        assertEquals(evaluation.name, result.evaluation.name);
        assertEquals(evaluation.startTime, result.evaluation.startTime);
        assertEquals(evaluation.endTime, result.evaluation.endTime);
        assertEquals(evaluation.gracePeriod, result.evaluation.gracePeriod);
        assertEquals(evaluation.instructions, result.evaluation.instructions);
        assertEquals(evaluation.timeZone, result.evaluation.timeZone, 0.1);
        assertEquals(evaluation.p2pEnabled, result.evaluation.p2pEnabled);
        assertEquals(evaluation.published, result.evaluation.published);
    
        // check number of teams
        assertEquals(2, result.teamResults.size());
    
        // check students in team 1.1
        TeamResultBundle team1_1 = result.teamResults.get("Team 1.1");
        assertEquals(4, team1_1.studentResults.size());
    
        int S1_POS = 0;
        int S2_POS = 1;
        int S3_POS = 2;
        int S4_POS = 3;
    
        
        StudentResultBundle srb1 = team1_1.studentResults.get(S1_POS);
        StudentResultBundle srb2 = team1_1.studentResults.get(S2_POS);
        StudentResultBundle srb3 = team1_1.studentResults.get(S3_POS);
        StudentResultBundle srb4 = team1_1.studentResults.get(S4_POS);
        
        StudentAttributes s1 = srb1.student;
        StudentAttributes s2 = srb2.student;
        StudentAttributes s3 = srb3.student;
        StudentAttributes s4 = srb4.student;
    
        assertEquals("student1InCourse1", s1.googleId);
        assertEquals("student2InCourse1", s2.googleId);
        assertEquals("student3InCourse1", s3.googleId);
        assertEquals("student4InCourse1", s4.googleId);
    
        // check self-evaluations of some students
        assertEquals(s1.name, srb1.getSelfEvaluation().details.revieweeName);
        assertEquals(s1.name, srb1.getSelfEvaluation().details.reviewerName);
        assertEquals(s3.name, srb3.getSelfEvaluation().details.revieweeName);
        assertEquals(s3.name, srb3.getSelfEvaluation().details.reviewerName);
    
        // check individual values for s1
        assertEquals(100, srb1.summary.claimedFromStudent);
        assertEquals(100, srb1.summary.claimedToInstructor);
        assertEquals(90, srb1.summary.perceivedToStudent);
        assertEquals(90, srb1.summary.perceivedToInstructor);
        // check some more individual values
        assertEquals(110, srb2.summary.claimedFromStudent);
        assertEquals(NSB, srb3.summary.claimedToInstructor);
        assertEquals(95, srb4.summary.perceivedToStudent);
        assertEquals(96, srb2.summary.perceivedToInstructor);
    
        // check outgoing submissions (s1 more intensely than others)
    
        assertEquals(4, srb1.outgoing.size());
    
        SubmissionAttributes s1_s1 = srb1.outgoing.get(S1_POS);
        assertEquals(100, s1_s1.details.normalizedToInstructor);
        String expected = "justification of student1InCourse1 rating to student1InCourse1";
        assertEquals(expected, s1_s1.justification.getValue());
        expected = "student1InCourse1 view of team dynamics";
        assertEquals(expected, s1_s1.p2pFeedback.getValue());
    
        SubmissionAttributes s1_s2 = srb1.outgoing.get(S2_POS);
        assertEquals(100, s1_s2.details.normalizedToInstructor);
        expected = "justification of student1InCourse1 rating to student2InCourse1";
        assertEquals(expected, s1_s2.justification.getValue());
        expected = "comments from student1InCourse1 to student2InCourse1";
        assertEquals(expected, s1_s2.p2pFeedback.getValue());
    
        assertEquals(100, srb1.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(100, srb1.outgoing.get(S4_POS).details.normalizedToInstructor);
    
        assertEquals(NSU, srb2.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(100, srb2.outgoing.get(S4_POS).details.normalizedToInstructor);
        assertEquals(NSB, srb3.outgoing.get(S2_POS).details.normalizedToInstructor);
        assertEquals(84, srb4.outgoing.get(S2_POS).details.normalizedToInstructor);
    
        // check incoming submissions (s2 more intensely than others)
    
        assertEquals(4, srb1.incoming.size());
        assertEquals(90, srb1.incoming.get(S1_POS).details.normalizedToStudent);
        assertEquals(100, srb1.incoming.get(S4_POS).details.normalizedToStudent);
    
        SubmissionAttributes s2_s1 = srb1.incoming.get(S2_POS);
        assertEquals(96, s2_s1.details.normalizedToStudent);
        expected = "justification of student2InCourse1 rating to student1InCourse1";
        assertEquals(expected, s2_s1.justification.getValue());
        expected = "comments from student2InCourse1 to student1InCourse1";
        assertEquals(expected, s2_s1.p2pFeedback.getValue());
        assertEquals(115, srb2.incoming.get(S4_POS).details.normalizedToStudent);
    
        SubmissionAttributes s3_s1 = srb1.incoming.get(S3_POS);
        assertEquals(113, s3_s1.details.normalizedToStudent);
        assertEquals("", s3_s1.justification.getValue());
        assertEquals("", s3_s1.p2pFeedback.getValue());
        assertEquals(113, srb3.incoming.get(S3_POS).details.normalizedToStudent);
    
        assertEquals(108, srb4.incoming.get(S3_POS).details.normalizedToStudent);
    
        // check team 1.2
        TeamResultBundle team1_2 = result.teamResults.get("Team 1.2");
        assertEquals(1, team1_2.studentResults.size());
        StudentResultBundle team1_2studentResult = team1_2.studentResults.get(0);
        assertEquals(NSB, team1_2studentResult.summary.claimedFromStudent);
        assertEquals(1, team1_2studentResult.outgoing.size());
        assertEquals(NSB, team1_2studentResult.summary.claimedToInstructor);
        assertEquals(NSB, team1_2studentResult.outgoing.get(0).points);
        assertEquals(NA, team1_2studentResult.incoming.get(0).details.normalizedToStudent);
        
        
        ______TS("Failure case: null parameters");
    
        try {
            evaluationsLogic.getEvaluationResult("valid.course.id", null);
            signalFailureToDetectException();;
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    
        ______TS("Failure case: non-existent course / eval");
        
        try {
            evaluationsLogic.getEvaluationResult("non-existent-course", evaluation.name);
            signalFailureToDetectException();;
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        try {
            evaluationsLogic.getEvaluationResult(course.id, "non-existent-eval");
            signalFailureToDetectException();;
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        /*
        ______TS("data used in UI tests");
    
        // @formatter:off
    
        TestHelper.createNewEvaluationWithSubmissions("courseForTestingER", "Eval 1",
                new int[][] { 
                { 110, 100, 110 }, 
                {  90, 110, NSU },
                {  90, 100, 110 } });
        // @formatter:on
    
        result = evaluationsLogic.getEvaluationResult("courseForTestingER", "Eval 1");
        print(result.toString());
        */
    }

    public void testGetEvaluationResultForStudent() throws Exception {
    
        CourseAttributes course;
        EvaluationAttributes evaluation;
        String student1email;
    
        ______TS("Typical case");
    
        // reconfigure points of an existing evaluation in the datastore
        course = dataBundle.courses.get("typicalCourse1");
        evaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        student1email = "student1InCourse1@gmail.com";
    
        // @formatter:off
        TestHelper.setPointsForSubmissions(new int[][] 
                { { 100, 100, 100, 100 },
                  { 110, 110, NSU, 110 }, 
                  { NSB, NSB, NSB, NSB },
                  { 70, 80, 110, 120 } });
        // @formatter:on

        // "idOfCourse1OfInstructor1", "evaluation1 In Course1",
    
        StudentResultBundle result = evaluationsLogic.getEvaluationResultForStudent(course.id,
                evaluation.name, student1email);
    
        // expected result:
        // [100, 100, 100, 100]
        // [100, 100, NSU, 100]
        // [NSB, NSB, NSB, NSB]
        // [74, 84, 116, 126]
        // =======================
        // [91, 96, 114, 100]
        // =======================
        // [91, 96, 114, 100]
        // [105, 110, 131, 115]
        // [91, 96, 114, 100]
        // [86, 91, 108, 95]
    
        // check calculated values
        assertEquals(student1email, result.getOwnerEmail());
        assertEquals(100, result.summary.claimedFromStudent);
        assertEquals(100, result.summary.claimedToInstructor);
        assertEquals(90, result.summary.perceivedToInstructor);
        assertEquals(90, result.summary.perceivedToStudent);
        int teamSize = 4;
    
        // check size of submission lists
        assertEquals(teamSize, result.outgoing.size());
        assertEquals(teamSize, result.incoming.size());
        assertEquals(teamSize, result.selfEvaluations.size());
    
        // check reviewee of incoming
        assertEquals("student1InCourse1@gmail.com",
                result.outgoing.get(0).reviewee);
        assertEquals("student2InCourse1@gmail.com",
                result.outgoing.get(1).reviewee);
        assertEquals("student3InCourse1@gmail.com",
                result.outgoing.get(2).reviewee);
        assertEquals("student4InCourse1@gmail.com",
                result.outgoing.get(3).reviewee);
    
        // check sorting of 'incoming' (should be sorted feedback)
        String feedback1 = result.incoming.get(0).p2pFeedback.getValue();
        String feedback2 = result.incoming.get(1).p2pFeedback.getValue();
        String feedback3 = result.incoming.get(2).p2pFeedback.getValue();
        String feedback4 = result.incoming.get(3).p2pFeedback.getValue();
        assertTrue(0 > feedback1.compareTo(feedback2));
        assertTrue(0 > feedback2.compareTo(feedback3));
        assertTrue(0 > feedback3.compareTo(feedback4));
    
        // check reviewer of outgoing
        assertEquals("student3InCourse1@gmail.com",
                result.incoming.get(0).reviewer);
        assertEquals("student2InCourse1@gmail.com",
                result.incoming.get(1).reviewer);
        assertEquals("student4InCourse1@gmail.com",
                result.incoming.get(2).reviewer);
        assertEquals("student1InCourse1@gmail.com",
                result.incoming.get(3).reviewer);
    
        // check some random values from submission lists
        assertEquals(100, result.outgoing.get(1).points); // reviewee=student2
        assertEquals(NSB, result.incoming.get(0).points); // reviewer=student3
        assertEquals(113, result.incoming.get(0).details.normalizedToStudent); // reviewer=student3
        assertEquals(
                "justification of student1InCourse1 rating to student1InCourse1",
                result.selfEvaluations.get(0).justification.getValue()); // student2
    
        ______TS("Failure case: null parameter");
    
        try {
            evaluationsLogic.getEvaluationResultForStudent("valid.course.id", "valid evaluation name", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    
        ______TS("Failure case: non-existent course / eval / student");
    
        try{
            evaluationsLogic.getEvaluationResultForStudent("non-existent-course", evaluation.name, student1email);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e){
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
    
        try{
            evaluationsLogic.getEvaluationResultForStudent(course.id, "non-existent-eval", student1email);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e){
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
    
        try {
            evaluationsLogic.getEvaluationResultForStudent(course.id, evaluation.name,
                    "non-existent@email.com");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
    
        ______TS("student added after evaluation");
    
        // testcreateStudentWithSubmissionAdjustment verifies adding student mid-evaluation creates
        //   additional submissions correctly. No need to check here.
    
    }

    public void testGetEvaluationResultSummaryAsCsv() throws Exception {
    
        ______TS("Typical case");
    
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        String export = evaluationsLogic.getEvaluationResultSummaryAsCsv(eval.courseId, instructor.email, eval.name);
        
        // This is what export should look like:
        // ==================================
        //Course,,idOfTypicalCourse1
        //Evaluation Name,,evaluation1 In Course1
        //
        //Team,,Student,,Claimed,,Perceived,,Received
        //Team 1.1,,student1 In Course1,,100,,100,,100,100,-9999
        //Team 1.1,,student2 In Course1,,-999,,100,,100,-9999,-9999
        //Team 1.1,,student3 In Course1,,-999,,100,,100,-9999,-9999
        //Team 1.1,,student4 In Course1,,-999,,100,,100,-9999,-9999
        //Team 1.2,,student5 In Course1,,-999,,-9999,,
        
        String[] exportLines = export.split(Const.EOL);
        assertEquals("Course,\"" + eval.courseId + "\"", exportLines[0]);
        assertEquals("Evaluation Name,\"" + eval.name + "\"", exportLines[1]);
        assertEquals("", exportLines[2]);
        assertEquals("Team,Student,Claimed,Perceived,Received", exportLines[3]);
        assertEquals("\"Team 1.1\",\"student1 In Course1\",\"100\",\"100\",\"100,100,N/A\"", exportLines[4]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Not Submitted\",\"100\",\"100,N/A,N/A\"", exportLines[5]);
        assertEquals("\"Team 1.1\",\"student3 In Course1\",\"Not Submitted\",\"100\",\"100,N/A,N/A\"", exportLines[6]);
        assertEquals("\"Team 1.1\",\"student4 In Course1\",\"Not Submitted\",\"100\",\"100,N/A,N/A\"", exportLines[7]);
        assertEquals("\"Team 1.2\",\"student5 In Course1\",\"Not Submitted\",\"N/A\",\"\"", exportLines[8]);
        
        ______TS("Failure case: non-existent Course/Eval");
        
        try {
            evaluationsLogic.getEvaluationResultSummaryAsCsv("non-existent-course", instructor.email, eval.name);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }

        try {
            evaluationsLogic.getEvaluationResultSummaryAsCsv(eval.courseId, instructor.email, "non-existent-eval");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }

        ______TS("Failure case: null parameters");
        
        try {
            evaluationsLogic.getEvaluationResultSummaryAsCsv(null, instructor.email, eval.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        try {
            evaluationsLogic.getEvaluationResultSummaryAsCsv(eval.courseId, instructor.email, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testIsEvaluationCompletedByStudent() throws Exception {

        ______TS("Typical case");

        EvaluationAttributes evaluation1InCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        String student1Email = "student1InCourse1@gmail.com";
        String student2Email = "student2InCourse1@gmail.com";

        assertTrue(evaluationsLogic.isEvaluationCompletedByStudent(evaluation1InCourse1, student1Email));
        assertFalse(evaluationsLogic.isEvaluationCompletedByStudent(evaluation1InCourse1, student2Email));

        ______TS("Failure case: non-existent student");

        assertFalse(evaluationsLogic.isEvaluationCompletedByStudent(evaluation1InCourse1, "non-existent@email.com"));

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.isEvaluationCompletedByStudent(evaluation1InCourse1, null);
            signalFailureToDetectException();
        } catch (AssertionError e){
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testIsEvaluationExists() throws Exception {

        ______TS("Typical case");

        EvaluationAttributes evaluation1InCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");

        assertTrue(evaluationsLogic.isEvaluationExists(evaluation1InCourse1.courseId, evaluation1InCourse1.name));

        ______TS("Failure case: non-existent course / eval");

        assertFalse(evaluationsLogic.isEvaluationExists("non-existent-course", evaluation1InCourse1.name));
        assertFalse(evaluationsLogic.isEvaluationExists(evaluation1InCourse1.courseId, "non-existent-eval"));

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.isEvaluationExists(null, evaluation1InCourse1.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }

    public void testUpdateEvaluation() throws Exception {
        
        ______TS("Typical case");

        EvaluationAttributes eval = new EvaluationAttributes();
        eval = dataBundle.evaluations.get("evaluation1InCourse1");
        eval.gracePeriod = eval.gracePeriod + 1;
        eval.instructions = new Text(eval.instructions + "x");
        eval.p2pEnabled = (!eval.p2pEnabled);
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.timeZone = 0;
        eval.activated = true;
        evaluationsLogic.updateEvaluation(eval);

        TestHelper.verifyPresentInDatastore(eval);
        
        ______TS("Typical case: derived attributes ignored");
        
        eval.published = !eval.published;
        eval.activated = !eval.activated;
        
        evaluationsLogic.updateEvaluation(eval);
        
        //flip values back because they are ignored by the SUT
        eval.published = !eval.published;
        eval.activated = !eval.activated;

        TestHelper.verifyPresentInDatastore(eval);
        
        ______TS("State change PUBLISHED --> OPEN ");
        
        int milliSecondsPerMinute = 60*1000;
        
        //first, make it PUBLISHED
        eval.timeZone = 0;
        eval.gracePeriod = 15;
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute, 0);
        eval.published = true;
        eval.activated = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval); //We use *Db object here because we want to persist derived attributes
        TestHelper.verifyPresentInDatastore(eval);
        
        //then, make it OPEN
        eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute*(eval.gracePeriod-1), 0);
        evaluationsLogic.updateEvaluation(eval);
        
        //check if derived attributes are set correctly
        eval.published = false;
        TestHelper.verifyPresentInDatastore(eval);
        assertEquals(EvalStatus.OPEN, eval.getStatus());
        
        //Other state changes are tested at lower levels
        

        ______TS("Failure case: invalid parameters");

        // make the evaluation invalid (end time is before start time)
        eval.timeZone = 0;
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        try {
            evaluationsLogic.updateEvaluation(eval);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals("The end time for this evaluation cannot be earlier than the start time.", e.getMessage());
            // set the values back
            eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        }

        // Checking for other type of invalid parameter situations
        // is done in EvaluationDataTest
        
    }

    public void testPublishAndUnpublishEvaluation() throws Exception {

        ______TS("Typical cases");

        EvaluationAttributes eval1 = dataBundle.evaluations.get("evaluation1InCourse1");
        // ensure not published yet
        assertEquals(false,
                evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);
        // ensure CLOSED
        eval1.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        assertEquals(EvalStatus.CLOSED, eval1.getStatus());
        
        evaluationsLogic.updateEvaluation(eval1);

        evaluationsLogic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,
                evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);

        evaluationsLogic.unpublishEvaluation(eval1.courseId, eval1.name);
        assertEquals(false,
                evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);

        
        ______TS("Rare case: trying to publish an already published evaluation");
        
        //Publish evaluation once
        evaluationsLogic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);
        
        //Publish the same evaluation again
        evaluationsLogic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);

        
        ______TS("Failure case: not ready for publishing");

        // make the evaluation OPEN
        eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        assertEquals(EvalStatus.OPEN, eval1.getStatus());
        evaluationsLogic.updateEvaluation(eval1);

        try {
            evaluationsLogic.publishEvaluation(eval1.courseId, eval1.name);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(Const.StatusCodes.PUBLISHED_BEFORE_CLOSING,
                    e.errorCode);
        }

        // ensure evaluation stays in the same state
        assertEquals(EvalStatus.OPEN,
                evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).getStatus());

        

        ______TS("Try to unpublish an already unpublished evaluation");

        //Close and publish the evaluation first
        eval1.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        assertEquals(EvalStatus.CLOSED, eval1.getStatus());
        evaluationsLogic.updateEvaluation(eval1);
        
        evaluationsLogic.publishEvaluation(eval1.courseId, eval1.name);
        assertEquals(true,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);
        
        //Unpublish the evaluation
        evaluationsLogic.unpublishEvaluation(eval1.courseId, eval1.name);
        assertEquals(false,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);
        
        //Try to unpublish it again
        evaluationsLogic.unpublishEvaluation(eval1.courseId, eval1.name);
        assertEquals(false,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).published);
        
        
        
        ______TS("Failure: non-existent course / eval");

        try {
            evaluationsLogic.publishEvaluation("non-existent-course", eval1.name);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to edit non-existent ", e.getMessage());
        }

        try {
            evaluationsLogic.unpublishEvaluation(eval1.courseId, "non-existent-eval");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to edit non-existent ", e.getMessage());
        }

        ______TS("Failure: null parameter");
        
        try {
            evaluationsLogic.publishEvaluation(null, eval1.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        try {
            evaluationsLogic.unpublishEvaluation(eval1.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }

    }

    public void testDeleteEvaluationCascade() throws Exception {
    
        ______TS("Typical cases");
    
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse2");
        try {
            evaluationsLogic.createEvaluationCascade(eval);
        } catch (EntityAlreadyExistsException e) {
            // if its already there, no problem
            ignoreExpectedException();
            evaluationsLogic.updateEvaluation(eval);
        }
        // verify there are submissions under this evaluation
        SubmissionAttributes submission = dataBundle.submissions
                .get("submissionFromS1C2ToS2C2");
        TestHelper.verifyPresentInDatastore(submission);
    
        evaluationsLogic.deleteEvaluationCascade(eval.courseId, eval.name);
        TestHelper.verifyAbsentInDatastore(eval);
        // verify submissions are deleted too
        ArrayList<SubmissionAttributes> submissionsOfEvaluation = new ArrayList<SubmissionAttributes>(dataBundle.submissions.values());
        for (SubmissionAttributes s : submissionsOfEvaluation) {
            if (s.evaluation.equals(eval.name)) {
                TestHelper.verifyAbsentInDatastore(s);
            }
        }
        
        ______TS("Failure case: non-existent course / eval");
    
        // should fail silently
        evaluationsLogic.deleteEvaluationCascade("non-existent", eval.name);
        evaluationsLogic.deleteEvaluationCascade(eval.courseId, "non-existent");
        
        ______TS("Failure case: null parameters");
    
        try {
            evaluationsLogic.deleteEvaluationCascade("valid.course.id", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testAdjustSubmissionsForNewStudentInEvaluation() throws Exception {

        ______TS("Typical case");
        
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        EvaluationAttributes evaluation1 = dataBundle.evaluations
                .get("evaluation1InCourse1");
        EvaluationAttributes evaluation2 = dataBundle.evaluations
                .get("evaluation2InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(course.id,
                "incoming@student.com", student.team, evaluation1.name);

        // We have a 5-member team and a 1-member team.
        // Therefore, we expect (5*5)+(1*1)=26 submissions.
        List<SubmissionAttributes> submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
        assertEquals(26, submissions.size());
        
        // Check the same for the other evaluation, to detect any state leakage
        evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(course.id,
                "incoming@student.com", student.team, evaluation2.name);
        submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
        assertEquals(26, submissions.size());
        
        ______TS("Moving to new team");
        
        evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(course.id,
                "incoming@student.com", "new team", evaluation1.name);
        //There should be one more submission now.
        submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
        assertEquals(27, submissions.size());
        
        // Check the same for the other evaluation
        evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(course.id,
                "incoming@student.com", "new team", evaluation2.name);
        //There should be one more submission now.
        submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
        assertEquals(27, submissions.size());

        ______TS("Failure case: invalid parameter");

        try {
            evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(course.id,
                "incoming.student.com", "new team", evaluation2.name);
            signalFailureToDetectException();
        } catch(InvalidParametersException e) {
            AssertHelper.assertContains("Invalid email address", e.getMessage());
        }

        try {
            evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation("invalid id",
                "incoming@student.com", "new team", evaluation2.name);
            signalFailureToDetectException();
        } catch(InvalidParametersException e) {
            AssertHelper.assertContains("is not acceptable to TEAMMATES as a Course ID"
                                        + " because it is not in the correct format", e.getMessage());
        }

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(course.id,
                null, "new team", evaluation2.name);
            signalFailureToDetectException();
        } catch (AssertionError e){
            AssertHelper.assertContains("Non-null value expected", e.getMessage());
        }
    }
    
    public void testSendEvaluationPublishedEmails() throws Exception {
        
        ______TS("Typical case");

        EvaluationAttributes eval = dataBundle.evaluations
                .get("evaluation1InCourse1");

        List<MimeMessage> emailsSent = evaluationsLogic.sendEvaluationPublishedEmails(
                eval.courseId, eval.name);
        assertEquals(9, emailsSent.size());

        List<StudentAttributes> studentList = studentsLogic.getStudentsForCourse(eval.courseId);
        
        for (StudentAttributes s : studentList) {
            String errorMessage = "No email sent to " + s.email;
            MimeMessage emailToStudent = TestHelper.getEmailToStudent(s, emailsSent);
            assertTrue(errorMessage, emailToStudent != null);
            AssertHelper.assertContains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED,
                    emailToStudent.getSubject());
            AssertHelper.assertContains(eval.name, emailToStudent.getSubject());
        }

        ______TS("Failure case: non-existent course / eval");

        try {
            evaluationsLogic.sendEvaluationPublishedEmails("non-existent-course", eval.name);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to edit non-existent ", e.getMessage());
        }

        try {
            evaluationsLogic.sendEvaluationPublishedEmails(eval.courseId, "non-existent-eval");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to edit non-existent ", e.getMessage());
        }

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.sendEvaluationPublishedEmails(null, eval.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testSendReminderForEvaluation() throws Exception {
       
        ______TS("Typical case");
        
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");

        List<MimeMessage> emailsSent = evaluationsLogic.sendReminderForEvaluation(
                eval.courseId, eval.name);
        assertEquals(8, emailsSent.size());

        List<StudentAttributes> studentList = studentsLogic.getStudentsForCourse(eval.courseId);
        
        for (StudentAttributes s : studentList) {
            String errorMessage = "No email sent to " + s.email;
            MimeMessage emailToStudent = TestHelper.getEmailToStudent(s, emailsSent);
            if(s.email.equals(student1.email)){
                assertTrue(emailToStudent == null);
            } else {
                assertTrue(errorMessage, emailToStudent != null);
                AssertHelper.assertContains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_REMINDER,
                    emailToStudent.getSubject());
                AssertHelper.assertContains(eval.name, emailToStudent.getSubject());
            }
        }

        ______TS("Failure case: non-existent course / eval");

        try {
            evaluationsLogic.sendEvaluationPublishedEmails("non-existent-course", eval.name);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to edit non-existent ", e.getMessage());
        }

        try {
            evaluationsLogic.sendEvaluationPublishedEmails(eval.courseId, "non-existent-eval");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to edit non-existent ", e.getMessage());
        }

        ______TS("Failure case: null parameter");

        try {
            evaluationsLogic.sendEvaluationPublishedEmails(null, eval.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }
    
    public void testSetEvaluationActivationStatus() throws Exception {
        
        EvaluationAttributes eval1 = dataBundle.evaluations.get("evaluation1InCourse1");
        // set activation status to true
        evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, eval1.name, true);
        
        ______TS("Typical cases");
        
        evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, eval1.name, false);
        assertEquals(false,
                evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).activated);

        evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, eval1.name, true);
        assertEquals(true,
                evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).activated);
        
        ______TS("Rare case: trying to activate an already activated evaluation");
            
        //Activate the same evaluation again
        evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, eval1.name, true);
        assertEquals(true,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).activated);

        ______TS("Rare case: trying to deactivate an already deactivated evaluation");
                
        //Deactivate the evaluation
        evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, eval1.name, false);
        assertEquals(false,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).activated);
        
        //Try to deactivate it again
        evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, eval1.name, false);
        assertEquals(false,evaluationsLogic.getEvaluation(eval1.courseId, eval1.name).activated);
    
        ______TS("Failure: non-existent course / eval");

        try {
            evaluationsLogic.setEvaluationActivationStatus("non-existent-course", eval1.name, true);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to update non-existent ", e.getMessage());
        }

        try {
            evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, "non-existent-eval", false);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Trying to update non-existent ", e.getMessage());
        }

        ______TS("Failure: null parameter");
        
        try {
            evaluationsLogic.setEvaluationActivationStatus(null, eval1.name, true);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
        
        try {
            evaluationsLogic.setEvaluationActivationStatus(eval1.courseId, null, false);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Supplied parameter was null", e.getMessage());
        }
    }

    public void testCalculateTeamResult() throws Exception {

        TeamDetailsBundle teamDetails = new TeamDetailsBundle();
        StudentAttributes s1 = new StudentAttributes("sect1", "t1", "s1", "e1@c", "", "course1");
        teamDetails.students.add(s1);
        StudentAttributes s2 = new StudentAttributes("sect1", "t1", "s2", "e2@c", "", "course1");
        teamDetails.students.add(s2);
        StudentAttributes s3 = new StudentAttributes("sect1", "t1", "s3", "e3@c", "", "course1");
        teamDetails.students.add(s3);
        
        TeamResultBundle teamEvalResultBundle = new TeamResultBundle(
                teamDetails.students);

        SubmissionAttributes s1_to_s1 = createSubmission(1, 1);
        SubmissionAttributes s1_to_s2 = createSubmission(1, 2);
        SubmissionAttributes s1_to_s3 = createSubmission(1, 3);

        SubmissionAttributes s2_to_s1 = createSubmission(2, 1);
        SubmissionAttributes s2_to_s2 = createSubmission(2, 2);
        SubmissionAttributes s2_to_s3 = createSubmission(2, 3);

        SubmissionAttributes s3_to_s1 = createSubmission(3, 1);
        SubmissionAttributes s3_to_s2 = createSubmission(3, 2);
        SubmissionAttributes s3_to_s3 = createSubmission(3, 3);

        // These additions are randomly ordered to ensure that the
        // method works even when submissions are added in random order

        StudentResultBundle srb1 = teamEvalResultBundle
                .getStudentResult(s1.email);
        StudentResultBundle srb2 = teamEvalResultBundle
                .getStudentResult(s2.email);
        StudentResultBundle srb3 = teamEvalResultBundle
                .getStudentResult(s3.email);
        
        srb1.outgoing.add(s1_to_s2.getCopy());
        srb1.incoming.add(s2_to_s1.getCopy());
        srb1.incoming.add(s3_to_s1.getCopy());
        srb3.outgoing.add(s3_to_s3.getCopy());
        srb2.outgoing.add(s2_to_s1.getCopy());
        srb1.outgoing.add(s1_to_s3.getCopy());
        srb2.incoming.add(s3_to_s2.getCopy());
        srb2.outgoing.add(s2_to_s3.getCopy());
        srb3.outgoing.add(s3_to_s1.getCopy());
        srb2.incoming.add(s2_to_s2.getCopy());
        srb3.incoming.add(s1_to_s3.getCopy());
        srb1.outgoing.add(s1_to_s1.getCopy());
        srb3.incoming.add(s2_to_s3.getCopy());
        srb3.outgoing.add(s3_to_s2.getCopy());
        srb2.incoming.add(s1_to_s2.getCopy());
        srb1.incoming.add(s1_to_s1.getCopy());
        srb2.outgoing.add(s2_to_s2.getCopy());
        srb3.incoming.add(s3_to_s3.getCopy());
        

        TeamEvalResult teamResult = invokeCalculateTeamResult(teamEvalResultBundle);
        // note the pattern in numbers. due to the way we generate submissions,
        // 110 means it is from s1 to s1 and
        // should appear in the 1,1 location in the matrix.
        // @formatter:off
        int[][] expected = { 
                { 110, 120, 130 }, 
                { 210, 220, 230 },
                { 310, 320, 330 } };
        assertEquals(TeamEvalResult.pointsToString(expected),
                TeamEvalResult.pointsToString(teamResult.claimed));

        // expected result
        // claimedToInstructor     [ 92, 100, 108]
        //                         [ 95, 100, 105]
        //                         [ 97, 100, 103]
        // ===============
        // unbiased [ NA, 96, 104]
        //             [ 95, NA, 105]
        //             [ 98, 102, NA]
        // ===============
        // perceivedToInstructor [ 97, 99, 105]
        // ===============
        // perceivedToStudents     [116, 118, 126]
        //                         [213, 217, 230]
        //                         [309, 316, 335]
        // @formatter:on

        int S1_POS = 0;
        int S2_POS = 1;
        int S3_POS = 2;

        // verify incoming and outgoing do not refer to same copy of submissions
        srb1.sortIncomingByStudentNameAscending();
        srb1.sortOutgoingByStudentNameAscending();
        srb1.incoming.get(S1_POS).details.normalizedToStudent = 0;
        srb1.outgoing.get(S1_POS).details.normalizedToStudent = 1;
        assertEquals(0, srb1.incoming.get(S1_POS).details.normalizedToStudent);
        assertEquals(1, srb1.outgoing.get(S1_POS).details.normalizedToStudent);

        invokePopulateTeamResult(teamEvalResultBundle, teamResult);
        
        s1 = teamEvalResultBundle.studentResults.get(S1_POS).student;
        assertEquals(110, srb1.summary.claimedFromStudent);
        assertEquals(92, srb1.summary.claimedToInstructor);
        assertEquals(116, srb1.summary.perceivedToStudent);
        assertEquals(97, srb1.summary.perceivedToInstructor);
        assertEquals(92, srb1.outgoing.get(S1_POS).details.normalizedToInstructor);
        assertEquals(100, srb1.outgoing.get(S2_POS).details.normalizedToInstructor);
        assertEquals(108, srb1.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(s1.name, srb1.incoming.get(S1_POS).details.revieweeName);
        assertEquals(s1.name, srb1.incoming.get(S1_POS).details.reviewerName);
        assertEquals(116, srb1.incoming.get(S1_POS).details.normalizedToStudent);
        assertEquals(119, srb1.incoming.get(S2_POS).details.normalizedToStudent);
        assertEquals(125, srb1.incoming.get(S3_POS).details.normalizedToStudent);
        assertEquals(NA, srb1.incoming.get(S1_POS).details.normalizedToInstructor);
        assertEquals(95, srb1.incoming.get(S2_POS).details.normalizedToInstructor);
        assertEquals(98, srb1.incoming.get(S3_POS).details.normalizedToInstructor);

        s2 = teamEvalResultBundle.studentResults.get(S2_POS).student;
        assertEquals(220, srb2.summary.claimedFromStudent);
        assertEquals(100, srb2.summary.claimedToInstructor);
        assertEquals(217, srb2.summary.perceivedToStudent);
        assertEquals(99, srb2.summary.perceivedToInstructor);
        assertEquals(95, srb2.outgoing.get(S1_POS).details.normalizedToInstructor);
        assertEquals(100, srb2.outgoing.get(S2_POS).details.normalizedToInstructor);
        assertEquals(105, srb2.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(213, srb2.incoming.get(S1_POS).details.normalizedToStudent);
        assertEquals(217, srb2.incoming.get(S2_POS).details.normalizedToStudent);
        assertEquals(229, srb2.incoming.get(S3_POS).details.normalizedToStudent);
        assertEquals(96, srb2.incoming.get(S1_POS).details.normalizedToInstructor);
        assertEquals(NA, srb2.incoming.get(S2_POS).details.normalizedToInstructor);
        assertEquals(102, srb2.incoming.get(S3_POS).details.normalizedToInstructor);

        s3 = teamEvalResultBundle.studentResults.get(S3_POS).student;
        assertEquals(330, srb3.summary.claimedFromStudent);
        assertEquals(103, srb3.summary.claimedToInstructor);
        assertEquals(334, srb3.summary.perceivedToStudent);
        assertEquals(104, srb3.summary.perceivedToInstructor);
        assertEquals(97, srb3.outgoing.get(S1_POS).details.normalizedToInstructor);
        assertEquals(100, srb3.outgoing.get(S2_POS).details.normalizedToInstructor);
        assertEquals(103, srb3.outgoing.get(S3_POS).details.normalizedToInstructor);
        assertEquals(310, srb3.incoming.get(S1_POS).details.normalizedToStudent);
        assertEquals(316, srb3.incoming.get(S2_POS).details.normalizedToStudent);
        assertEquals(334, srb3.incoming.get(S3_POS).details.normalizedToStudent);
        assertEquals(104, srb3.incoming.get(S1_POS).details.normalizedToInstructor);
        assertEquals(105, srb3.incoming.get(S2_POS).details.normalizedToInstructor);
        assertEquals(NA, srb3.incoming.get(S3_POS).details.normalizedToInstructor);

    }
    
    private static SubmissionAttributes createSubmission(int from, int to) {
        SubmissionAttributes submission = new SubmissionAttributes();
        submission.course = "course1";
        submission.evaluation = "eval1";
        submission.points = from * 100 + to * 10;
        submission.reviewer = "e" + from + "@c";
        submission.details.reviewerName = "s" + from;
        submission.reviewee = "e" + to + "@c";
        submission.details.revieweeName = "s" + to;
        return submission;
    }
    
    private TeamEvalResult invokeCalculateTeamResult(TeamResultBundle team)
            throws Exception {
        Method privateMethod = EvaluationsLogic.class.getDeclaredMethod(
                "calculateTeamResult", new Class[] { TeamResultBundle.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { team };
        return (TeamEvalResult) privateMethod.invoke(EvaluationsLogic.inst(), params);
    }

    private void invokePopulateTeamResult(TeamResultBundle team,
            TeamEvalResult teamResult) throws Exception {
        Method privateMethod = EvaluationsLogic.class.getDeclaredMethod(
                "populateTeamResult", new Class[] { TeamResultBundle.class,
                        TeamEvalResult.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { team, teamResult };
        privateMethod.invoke(EvaluationsLogic.inst(), params);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(EvaluationsLogic.class);
    }
}
