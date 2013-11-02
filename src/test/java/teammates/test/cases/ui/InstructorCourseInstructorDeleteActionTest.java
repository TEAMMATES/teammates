package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseInstructorDeleteActionTest extends BaseActionTest {

	DataBundle dataBundle;
	InstructorsLogic instructorsLogic;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
		instructorsLogic = InstructorsLogic.inst();
	}
	
	@Test
	public void testAccessControl() throws Exception {
		
		InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor.courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructor.googleId,
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception {
		
		InstructorAttributes loginInstructor = dataBundle.instructors.get("instructor1OfCourse1");
		String loginInstructorId = loginInstructor.googleId;

		String courseId = loginInstructor.courseId;	
		String adminUserId = "admin.user";

		gaeSimulation.loginAsInstructor(loginInstructorId);
		
		______TS("Not enough parameters");

		verifyAssumptionFailure();
		verifyAssumptionFailure(
				Const.ParamsNames.COURSE_ID, courseId);
		
		______TS("Typical case: delete instructor successfully");
		
		String instructorIdToDelete = dataBundle.instructors.get("instructor2OfCourse1").googleId;
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorIdToDelete
		};
		
		Action action = getAction(submissionParams);
		RedirectResult result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, result.getStatusMessage());

		assertEquals(false, instructorsLogic.isInstructorOfCourse(instructorIdToDelete, courseId));
		
		String expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorIdToDelete + "</span>"
				+ " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());

		______TS("Success: delete own instructor role from course, redirect back to courses page");
		
		instructorIdToDelete = loginInstructorId;
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorIdToDelete
		};
		
		action = getAction(submissionParams);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, result.getStatusMessage());

		assertEquals(false, instructorsLogic.isInstructorOfCourse(instructorIdToDelete, courseId));
		
		expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorIdToDelete + "</span>"
				+ " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Masquerade mode: delete instructor failed due to last instructor in course");

		InstructorAttributes instructorToDelete = dataBundle.instructors.get("instructor4");
		instructorIdToDelete = instructorToDelete.googleId;
		courseId = instructorToDelete.courseId;	
		
		gaeSimulation.loginAsAdmin(adminUserId);
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorIdToDelete
		};
		
		action = getAction(addUserIdToParams(instructorIdToDelete, submissionParams));
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(true, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED, result.getStatusMessage());

		assertEquals(true, instructorsLogic.isInstructorOfCourse(instructorIdToDelete, courseId));
		
		expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorIdToDelete + "</span>"
				+ " in Course <span class=\"bold\">[" + courseId + "]</span> could not be deleted "
				+ "as there is only one instructor left.<br>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
	}
	
	private Action getAction(String... params) throws Exception{
			return gaeSimulation.getActionObject(uri, params);
	}
	

}
