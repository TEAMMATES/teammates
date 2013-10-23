package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.FieldValidator.FieldType;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseInstructorEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	InstructorsLogic instructorsLogic;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
		instructorsLogic = InstructorsLogic.inst();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor.courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructor.googleId,
				Const.ParamsNames.INSTRUCTOR_NAME, instructor.name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, "newEmail@email.com"
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception {
		InstructorAttributes instructorToEdit = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructorToEdit.googleId;
		
		String courseId = instructorToEdit.courseId;	
		String adminUserId = "admin.user";
		
		gaeSimulation.loginAsInstructor(instructorId);
		
		______TS("Not enough parameters");

		verifyAssumptionFailure();
		verifyAssumptionFailure(
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorId);
		
		______TS("Typical case: edit instructor successfully");
		
		String newInstructorEmail = "newEmail@email.com";
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, instructorToEdit.name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail
		};
		Action action = getAction(submissionParams);
		RedirectResult result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, result.getStatusMessage());
		
		InstructorAttributes editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
		assertEquals(newInstructorEmail, editedInstructor.email);
		
		String expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorToEdit.name + "</span>"
				+ " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
				+ "New Name: " + instructorToEdit.name + "<br>New Email: " + newInstructorEmail;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Failure case: edit failed due to invalid parameters");
		
		String invalidEmail = "wrongEmail.com";
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, instructorToEdit.name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail
		};
		
		action = getAction(submissionParams);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(true, result.isError);
		String expectedErrorMessage = (new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail);
		assertEquals(expectedErrorMessage, result.getStatusMessage());
		
		AssertHelper.assertContains(expectedErrorMessage, action.getLogMessage());
		
		______TS("Masquerade mode: edit instructor successfully");
		
		gaeSimulation.loginAsAdmin(adminUserId);
		
		newInstructorEmail = "newEmail@email.com";
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, instructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, instructorToEdit.name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail
		};
		
		action = getAction(addUserIdToParams(instructorId, submissionParams));
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, result.getStatusMessage());
		
		editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
		assertEquals(newInstructorEmail, editedInstructor.email);
		
		expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorToEdit.name + "</span>"
				+ " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
				+ "New Name: " + instructorToEdit.name + "<br>New Email: " + newInstructorEmail;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
	}
	
	private Action getAction(String... parameters) throws Exception {
		return gaeSimulation.getActionObject(uri, parameters);
	}

}
