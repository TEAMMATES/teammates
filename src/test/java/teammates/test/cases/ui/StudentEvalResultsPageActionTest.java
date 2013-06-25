package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.EvaluationsLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.EvaluationsDb;
import teammates.test.cases.common.EvaluationAttributesTest;
import teammates.ui.controller.Action;
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentHomePageAction;
import teammates.ui.controller.StudentHomePageData;

public class StudentEvalResultsPageActionTest extends BaseActionTest {

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
		URI = "/page/studentEvalResults";
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
		
		______TS("OPEN evaluation");
		
		EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
		assertEquals(EvalStatus.OPEN, eval.getStatus());
		checkAccessControlForEval(eval, false);
		
		______TS("CLOSED evaluation");
		
		eval.endTime = Common.getDateOffsetToCurrentTime(-1);
		assertEquals(EvalStatus.CLOSED, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		checkAccessControlForEval(eval, false);
		
		______TS("PUBLISHED evaluation");
		
		eval.published = true;
		assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
		evaluationsDb.updateEvaluation(eval);
		checkAccessControlForEval(eval, true);
		
		//TODO: test for other states too
		
	}

	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
		
	}

	private Action getAction(String... params) throws Exception{
			return (Action) (super.getActionObject(params));
	}

	private void checkAccessControlForEval(EvaluationAttributes eval, boolean isPublished)
			throws Exception {
		String courseId = eval.courseId;
		String evalName = eval.name;
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, courseId,
				Common.PARAM_EVALUATION_NAME, evalName};
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		loginUser(unregUserId);
		//if the user is not a student of the course, we redirect to home page.
		verifyRedirectTo(Common.PAGE_STUDENT_HOME, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		loginAsStudent(studentId);
		if(isPublished){
			verifyCanAccess(submissionParams);
		}else {
			verifyCannotAccess(submissionParams);
		}
		
		verifyCannotMasquerade(addUserIdToParams(otherStudentId,submissionParams));
		
		loginAsInstructor(instructorId);
		//if the user is not a student of the course, we redirect to home page.
		verifyRedirectTo(Common.PAGE_STUDENT_HOME, submissionParams);
		verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
		
		loginAsAdmin(adminUserId);
		//not checking for non-masquerade mode because admin may not be a student
		verifyCanMasquerade(addUserIdToParams(studentId,submissionParams));
	}
	
}
