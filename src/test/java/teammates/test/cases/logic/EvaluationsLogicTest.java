package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.logic.core.TeamEvalResult.NA;

import java.lang.reflect.Method;
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
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.logic.core.TeamEvalResult;
import teammates.storage.api.EvaluationsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class EvaluationsLogicTest extends BaseComponentTestCase{
	
	//TODO: add missing tests. Some of the test content can be transferred from LogicTest.
	
	/* TODO: implement tests for the following:
	 * 1. getEvaluationsListForInstructor()
	 * 2. getEvaluationsDetailsForCourseAndEval()
	 */
	
	private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsLogic submissionsLogic = new SubmissionsLogic();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		gaeSimulation.resetDatastore();
		turnLoggingUp(EvaluationsLogic.class);
	}
	
	@Test
	public void testCreateEvaluationCascadeWithSubmissionQueue() throws Exception{
		restoreTypicalDataInDatastore();
		
		______TS("case : create a valid evaluation");
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
		
		______TS("case : try to create an invalid evaluation");
		evaluationsLogic
			.deleteEvaluationCascade(createdEval.courseId, createdEval.name);
		createdEval.startTime = null;
		try {
			evaluationsLogic.createEvaluationCascade(createdEval);
			signalFailureToDetectException("Expected failure not encountered");
		} catch (AssertionError e) {
			ignoreExpectedException();
		}
		
		retrievedEval = evaluationsLogic
				.getEvaluation(createdEval.courseId, createdEval.name);
		assertNull(retrievedEval);
	}
	
	@Test
	public void testGetEvaluationsClosingWithinTimeLimit() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("case : no evaluations closing within a certain period");
		DataBundle dataBundle = getTypicalDataBundle();
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		int numberOfHoursToTimeLimit = 2; //arbitrary number of hours
		
		eval.timeZone = 0;
		eval.endTime = TimeHelper.getHoursOffsetToCurrentTime(numberOfHoursToTimeLimit);
		evaluationsLogic.updateEvaluation(eval);
		
		List<EvaluationAttributes> evaluationsList = evaluationsLogic
				.getEvaluationsClosingWithinTimeLimit(numberOfHoursToTimeLimit-1);
		assertEquals(0, evaluationsList.size());
		
		______TS("case : 1 evaluation closing within a certain period");
		evaluationsList = evaluationsLogic
				.getEvaluationsClosingWithinTimeLimit(numberOfHoursToTimeLimit);
		assertEquals(1, evaluationsList.size());
		assertEquals(eval.name, evaluationsList.get(0).name);
		
	}
	
	@Test
	public void testGetReadyEvaluations() throws Exception {
		______TS("no evaluations activated");
		// ensure there are no existing evaluations ready for activation
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		BackDoorLogic backdoor = new BackDoorLogic();
		for (EvaluationAttributes e : dataBundle.evaluations.values()) {
			e.activated = true;
			backdoor.updateEvaluation(e);
			assertTrue(backdoor.getEvaluation(e.courseId, e.name).getStatus() != EvalStatus.AWAITING);
		}
		assertEquals(0, evaluationsLogic.getReadyEvaluations().size());

		______TS("typical case, two evaluations activated");
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

		backdoor.createEvaluation(evaluation);

		// Verify that there are no unregistered students.
		// TODO: this should be removed after absorbing registration reminder
		// into the evaluation opening alert.
		CourseDetailsBundle course1 = backdoor.getCourseDetails(evaluation.courseId);
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

		backdoor.createEvaluation(evaluation);

		// Verify that there are no unregistered students
		// TODO: this should be removed after absorbing registration reminder
		// into the evaluation opening alert.
		CourseDetailsBundle course2 = backdoor.getCourseDetails(evaluation.courseId);
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
		backdoor.createEvaluation(evaluation);

		// verify number of ready evaluations.
		assertEquals(2, evaluationsLogic.getReadyEvaluations().size());

		// Other variations of ready/not-ready states should be checked at
		// Evaluation level
	}
	
	@Test
	public void testAddSubmissionsForIncomingMember() throws Exception {

		______TS("typical case");

		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();

		CourseAttributes course = dataBundle.courses.get("typicalCourse1");
		EvaluationAttributes evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		EvaluationAttributes evaluation2 = dataBundle.evaluations
				.get("evaluation2InCourse1");
		StudentAttributes student = dataBundle.students.get("student1InCourse1");

		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", student.team);

		// We have a 5-member team and a 1-member team.
		// Therefore, we expect (5*5)+(1*1)=26 submissions.
		List<SubmissionAttributes> submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(26, submissions.size());
		
		// Check the same for the other evaluation, to detect any state leakage
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", student.team);
		submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(26, submissions.size());
		
		______TS("moving to new team");
		
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation1.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation1.name);
		assertEquals(27, submissions.size());
		
		// Check the same for the other evaluation
		invokeAddSubmissionsForIncomingMember(course.id,
				evaluation2.name, "incoming@student.com", "new team");
		//There should be one more submission now.
		submissions = submissionsLogic.getSubmissionsForEvaluation(course.id, evaluation2.name);
		assertEquals(27, submissions.size());

		//TODO: test invalid inputs

	}
	
	@Test
	public void testSendEvaluationPublishedEmails() throws Exception {
		// private method. no need to check for authentication.
		Logic logic = new Logic();
		
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();

		EvaluationAttributes e = dataBundle.evaluations
				.get("evaluation1InCourse1");

		List<MimeMessage> emailsSent = invokeSendEvaluationPublishedEmails(
				e.courseId, e.name);
		assertEquals(8, emailsSent.size());

		List<StudentAttributes> studentList = logic.getStudentsForCourse(e.courseId);
		
		for (StudentAttributes s : studentList) {
			String errorMessage = "No email sent to " + s.email;
			MimeMessage emailToStudent = LogicTest.getEmailToStudent(s, emailsSent);
			assertTrue(errorMessage, emailToStudent != null);
			AssertHelper.assertContains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED,
					emailToStudent.getSubject());
			AssertHelper.assertContains(e.name, emailToStudent.getSubject());
		}
	}
	
	@Test
	public void testUpdateEvaluation() throws Exception {
		
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		
		______TS("typical case");

		EvaluationAttributes eval = new EvaluationAttributes();
		eval = dataBundle.evaluations.get("evaluation1InCourse1");
		eval.gracePeriod = eval.gracePeriod + 1;
		eval.instructions = new Text(eval.instructions + "x");
		eval.p2pEnabled = (!eval.p2pEnabled);
		eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
		eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
		eval.timeZone = 0;
		evaluationsLogic.updateEvaluation(eval);

		LogicTest.verifyPresentInDatastore(eval);
		
		______TS("typicla case: derived attributes ignored");
		
		eval.published = !eval.published;
		eval.activated = !eval.activated;
		
		evaluationsLogic.updateEvaluation(eval);
		
		//flip values back because they are ignored by the SUT
		eval.published = !eval.published;
		eval.activated = !eval.activated;

		LogicTest.verifyPresentInDatastore(eval);
		
		______TS("state change PUBLISHED --> OPEN ");
		
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
		LogicTest.verifyPresentInDatastore(eval);
		
		//then, make it OPEN
		eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute*(eval.gracePeriod-1), 0);
		evaluationsLogic.updateEvaluation(eval);
		
		//check if derived attributes are set correctly
		eval.published = false;
		LogicTest.verifyPresentInDatastore(eval);
		assertEquals(EvalStatus.OPEN, eval.getStatus());
		
		//Other state changes are tested at lower levels
		
	}
	
	@Test
	public void testCalculateTeamResult() throws Exception {

		TeamDetailsBundle teamDetails = new TeamDetailsBundle();
		StudentAttributes s1 = new StudentAttributes("t1|s1|e1@c", "course1");
		teamDetails.students.add(s1);
		StudentAttributes s2 = new StudentAttributes("t1|s2|e2@c", "course1");
		teamDetails.students.add(s2);
		StudentAttributes s3 = new StudentAttributes("t1|s3|e3@c", "course1");
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
		// claimedToInstructor 	[ 92, 100, 108]
		// 						[ 95, 100, 105]
		// 						[ 97, 100, 103]
		// ===============
		// unbiased [ NA, 96, 104]
		// 			[ 95, NA, 105]
		// 			[ 98, 102, NA]
		// ===============
		// perceivedToInstructor [ 97, 99, 105]
		// ===============
		// perceivedToStudents 	[116, 118, 126]
		// 						[213, 217, 230]
		// 						[309, 316, 335]
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
	
	private void invokeAddSubmissionsForIncomingMember(String courseId,
			String evaluationName, String studentEmail, String newTeam)throws Exception {
		Method privateMethod = EvaluationsLogic.class.getDeclaredMethod(
				"addSubmissionsForIncomingMember", new Class[] { String.class,
						String.class, String.class, String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] {courseId,
				 evaluationName,  studentEmail, newTeam };
		privateMethod.invoke(EvaluationsLogic.inst(), params);
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
	
	
	@SuppressWarnings("unchecked")
	private List<MimeMessage> invokeSendEvaluationPublishedEmails(
			String courseId, String evaluationName) throws Exception {
		Method privateMethod = EvaluationsLogic.class.getDeclaredMethod(
				"sendEvaluationPublishedEmails", new Class[] { String.class,
						String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { courseId, evaluationName };
		return (List<MimeMessage>) privateMethod.invoke(EvaluationsLogic.inst(), params);
	}
	
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		turnLoggingDown(EvaluationsLogic.class);
	}
}
