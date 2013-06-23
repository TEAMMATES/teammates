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
import teammates.storage.api.EvaluationsDb;
import teammates.ui.controller.ControllerServlet;

public class InstructorEvalPublishActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorEvalPublish";
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String	unregUserId = "unreg.user";
		String 	adminUserId = "admin.user";
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
		makeEvaluationClosed(evaluationInCourse1);
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		EvaluationAttributes evaluationInOtherCourse = dataBundle.evaluations.get("evaluation1InCourse2");
		makeEvaluationClosed(evaluationInOtherCourse);
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, evaluationInCourse1.courseId,
				Common.PARAM_EVALUATION_NAME, evaluationInCourse1.name 
		};
		
		______TS("not-logged-in users cannot access");
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("non-registered users cannot access");
		
		loginUser(unregUserId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("students cannot access");
		
		loginAsStudent(student1InCourse1.googleId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId,submissionParams));
		
		______TS("instructor of the course can access");
		
		loginAsInstructor(instructor1OfCourse1.googleId);
		verifyCanAccess(submissionParams);
		
		______TS("instructor of others courses cannot access");
		
		InstructorAttributes instructorOfOtherCourse = dataBundle.instructors.get("instructor1OfCourse2");
		verifyCannotMasquerade(addUserIdToParams(instructorOfOtherCourse.googleId,submissionParams));
		
		String[] submissionParamsForOtherCourse = new String[]{
				Common.PARAM_COURSE_ID, evaluationInOtherCourse.courseId,
				Common.PARAM_EVALUATION_NAME, evaluationInOtherCourse.name 
		};
		verifyCannotAccess(submissionParamsForOtherCourse);
		
		______TS("admin can masquerade");
		
		loginAsAdmin(adminUserId);
		//not checking for non-masquerade mode because admin may not be an instructor
		verifyCanMasquerade(addUserIdToParams(instructorOfOtherCourse.googleId,submissionParamsForOtherCourse));
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}

	private void makeEvaluationClosed(EvaluationAttributes eval) throws Exception {
		eval.startTime = Common.getDateOffsetToCurrentTime(-2);
		eval.endTime = Common.getDateOffsetToCurrentTime(-1);
		eval.activated = true;
		eval.published = false;
		assertEquals(EvalStatus.CLOSED, eval.getStatus());
		new EvaluationsDb().updateEvaluation(eval);
		
	}
	

}
