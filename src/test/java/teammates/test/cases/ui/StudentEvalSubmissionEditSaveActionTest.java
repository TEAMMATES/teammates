package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.ControllerServlet;

public class StudentEvalSubmissionEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String studentId;
	String otherStudentId;
	String adminUserId;
	EvaluationsDb evaluationsDb = new EvaluationsDb();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT_HANDLER;
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		studentId = student1InCourse1.googleId;
		
		otherStudentId = dataBundle.students.get("student2InCourse1").googleId;
		
		adminUserId = "admin.user";
		
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		assertEquals(EvalStatus.OPEN, eval.getStatus());
		SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS2C1");
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, eval.courseId,
				Common.PARAM_EVALUATION_NAME, eval.name,
				Common.PARAM_FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
				Common.PARAM_TEAM_NAME, sub.team,
				Common.PARAM_TO_EMAIL, sub.reviewee,
				Common.PARAM_POINTS, sub.points+"",
				Common.PARAM_JUSTIFICATION, sub.justification.toString(),
				Common.PARAM_COMMENTS, sub.p2pFeedback.toString()
			};
		
		verifyUnaccessibleWithoutLogin(submissionParams);
		verifyUnaccessibleForUnregisteredUsers(submissionParams);
		
		verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
		verifyUnaccessibleForDifferentStudentOfTheSameCourses(submissionParams);
		
		verifyUnaccessibleForInstructors(submissionParams);
		verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
	}

	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		//TODO: ensure uneditable if not OPEN
		
	}
	
}
