package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.logic.core.TeamEvalResult.NA;

import java.lang.reflect.Method;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.logic.core.TeamEvalResult;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;
import teammates.test.cases.BaseComponentTestCase;

public class EvaluationsLogicTest extends BaseComponentTestCase{
	
	//TODO: add missing tests. Some of the test content can be transferred from LogicTest.
	
	private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsLogic submissionsLogic = new SubmissionsLogic();
	private static final CoursesLogic coursesLogic = new CoursesLogic();
	private static final StudentsLogic studentsLogic = new StudentsLogic();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		gaeSimulation.resetDatastore();
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
		assertEquals(5, emailsSent.size());

		List<StudentAttributes> studentList = logic.getStudentsForCourse(e.courseId);

		for (StudentAttributes s : studentList) {
			String errorMessage = "No email sent to " + s.email;
			MimeMessage emailToStudent = LogicTest.getEmailToStudent(s, emailsSent);
			assertTrue(errorMessage, emailToStudent != null);
			assertContains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_PUBLISHED,
					emailToStudent.getSubject());
			assertContains(e.name, emailToStudent.getSubject());
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
		eval.instructions = eval.instructions + "x";
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
	public void testActivateReadyEvaluations() throws Exception {
		
		restoreTypicalDataInDatastore();
		DataBundle dataBundle = getTypicalDataBundle();
		
		______TS("0 evaluations activated");
		// ensure all existing evaluations are already activated.
		for (EvaluationAttributes e : dataBundle.evaluations.values()) {
			e.activated = true;
			evaluationsLogic.updateEvaluation(e);
			assertTrue(evaluationsLogic.getEvaluation(e.courseId, e.name).getStatus() != EvalStatus.AWAITING);
		}
		List<MimeMessage> emailsSent = evaluationsLogic.activateReadyEvaluations();
		assertEquals(0, emailsSent.size());
		
		______TS("typical case, two evaluations activated");
		// Reuse an existing evaluation to create a new one that is ready to
		// activate. Put this evaluation in a negative time zone.
		EvaluationAttributes evaluation1 = dataBundle.evaluations
				.get("evaluation1InCourse1");
		String nameOfEvalInCourse1 = "new-eval-in-course-1-tARE";
		evaluation1.name = nameOfEvalInCourse1;

		evaluation1.activated = false;

		double timeZone = -1.0;
		evaluation1.timeZone = timeZone;

		evaluation1.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(0, timeZone);
		evaluation1.endTime = TimeHelper.getDateOffsetToCurrentTime(2);

		evaluationsLogic.createEvaluationCascade(evaluation1);
		assertEquals("This evaluation is not ready to activate as expected "+ evaluation1.toString(),
				true, 
				evaluationsLogic.getEvaluation(evaluation1.courseId, evaluation1.name).isReadyToActivate());

		// Create another evaluation in another course in similar fashion.
		// Put this evaluation in a positive time zone.
		// This one too is ready to activate.
		EvaluationAttributes evaluation2 = dataBundle.evaluations
				.get("evaluation1InCourse2");
		evaluation2.activated = false;
		String nameOfEvalInCourse2 = "new-evaluation-in-course-2-tARE";
		evaluation2.name = nameOfEvalInCourse2;

		timeZone = 2.0;
		evaluation2.timeZone = timeZone;

		evaluation2.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
				0, timeZone);
		evaluation2.endTime = TimeHelper.getDateOffsetToCurrentTime(2);

		evaluationsLogic.createEvaluationCascade(evaluation2);
		assertEquals("This evaluation is not ready to activate as expected "+ evaluation2.toString(),
				true, 
				evaluationsLogic.getEvaluation(evaluation2.courseId, evaluation2.name).isReadyToActivate());
		
		//Create a course to hold the orphan evaluation.
		String IdOftemporaryCourse = "non-existent-course-BDLT-causes-EDNEE";
		coursesLogic.createCourse(IdOftemporaryCourse, "Course to be deleted soon");
		
		// Create an orphan evaluation (this should be ignored by SUT)
		EvaluationAttributes orphan = new EvaluationAttributes();
		orphan.name = "Orphan Evaluation";
		orphan.courseId = IdOftemporaryCourse;
		orphan.timeZone = evaluation2.timeZone;
		orphan.startTime = evaluation2.startTime;
		orphan.endTime = evaluation2.endTime;
		orphan.activated = evaluation2.activated;
		orphan.published = evaluation2.published;
		evaluationsLogic.createEvaluationCascade(orphan);
		
		//make the evaluation an orphan by deleting the course
		new CoursesDb().deleteCourse(IdOftemporaryCourse);
		
		assertEquals("This evaluation is not ready to activate as expected "+ orphan.toString(),
				true, 
				evaluationsLogic.getEvaluation(orphan.courseId, orphan.name).isReadyToActivate());

		emailsSent = evaluationsLogic.activateReadyEvaluations();
		int course1StudentCount = studentsLogic.getStudentsForCourse(
				evaluation1.courseId).size();
		int course2StudentCount = studentsLogic.getStudentsForCourse(
				evaluation2.courseId).size();

		assertEquals(course1StudentCount + course2StudentCount,	emailsSent.size());

		//ensure both evaluations are activated now
		emailsSent = evaluationsLogic.activateReadyEvaluations();
		assertEquals(0, emailsSent.size());

	}

	@Test
	public void testSendRemindersForClosingEvaluations() throws Exception {
		DataBundle dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
		
		______TS("typical case, 0 evaluations closing soon");
		List<MimeMessage> emailsSent = evaluationsLogic
				.sendRemindersForClosingEvaluations();
		assertEquals(0, emailsSent.size());
		
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
		evaluationsLogic.createEvaluationCascade(evaluation1);

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

		evaluationsLogic.createEvaluationCascade(evaluation2);
		
		//Create a course to hold the orphan evaluation.
		String IdOftemporaryCourse = "non-existent-course-BDLT-causes-EDNEE";
		coursesLogic.createCourse(IdOftemporaryCourse, "Course to be deleted soon");
		
		// Create an orphan evaluation (this should be ignored by SUT)
		EvaluationAttributes orphan = new EvaluationAttributes();
		orphan.name = "Orphan Evaluation";
		orphan.courseId = IdOftemporaryCourse;
		orphan.timeZone = evaluation2.timeZone;
		orphan.startTime = evaluation2.startTime;
		orphan.endTime = evaluation2.endTime;
		orphan.activated = evaluation2.activated;
		orphan.published = evaluation2.published;
		evaluationsLogic.deleteEvaluationCascade(orphan.courseId, orphan.name);
		evaluationsLogic.createEvaluationCascade(orphan);
		
		//make the evaluation an orphan by deleting the course
		new CoursesDb().deleteCourse(IdOftemporaryCourse);
		
		emailsSent = evaluationsLogic.sendRemindersForClosingEvaluations();

		int course1StudentCount = studentsLogic.getStudentsForCourse(
				evaluation1.courseId).size();
		int course2StudentCount = studentsLogic.getStudentsForCourse(
				evaluation2.courseId).size();

		assertEquals(course1StudentCount + course2StudentCount,
				emailsSent.size());

		for (MimeMessage m : emailsSent) {
			String subject = m.getSubject();
			assertTrue(subject.contains(evaluation1.name)
					|| subject.contains(evaluation2.name));
			assertTrue(subject.contains(Emails.SUBJECT_PREFIX_STUDENT_EVALUATION_CLOSING));
		}

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
	}

}
