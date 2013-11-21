package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCourseInstructorAddAction;
import teammates.ui.controller.RedirectResult;


public class InstructorCourseInstructorAddActionTest extends BaseActionTest {

	DataBundle dataBundle;
	InstructorsLogic instructorsLogic;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
		instructorsLogic = InstructorsLogic.inst();
	}
	
	@Test
	public void testAccessControl() throws Exception {
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
				Const.ParamsNames.INSTRUCTOR_ID, "ICIAAT.instructorId",
				Const.ParamsNames.INSTRUCTOR_NAME, "Instructor Name",
				Const.ParamsNames.INSTRUCTOR_EMAIL, "instructor@email.com"};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception {

		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		String courseId = instructor1OfCourse1.courseId;
		
		String adminUserId = "admin.user";
		
		______TS("Not enough parameters");
		
		gaeSimulation.loginAsInstructor(instructorId);
		
		verifyAssumptionFailure();
		verifyAssumptionFailure(
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, "ICIAAT.instructorId");
		
		______TS("Typical case: add an instructor successfully");
		
		gaeSimulation.loginAsInstructor(instructorId);
		
		String newInstructorId = "ICIAAT.newInstructorId";
		String newInstructorName = "New Instructor Name";
		String newInstructorEmail = "newInstructor@email.com";
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, newInstructorName,
				Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail};
		
		Action action = getAction(submissionParams);
		RedirectResult result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED, result.getStatusMessage());
		
		assertEquals(true, instructorsLogic.isInstructorOfCourse(newInstructorId, courseId));
		assertEquals(true, AccountsLogic.inst().isAccountAnInstructor(newInstructorId));
		
		InstructorAttributes instructorAdded = instructorsLogic.getInstructorForGoogleId(courseId, newInstructorId);
		assertEquals(newInstructorName, instructorAdded.name);
		assertEquals(newInstructorEmail, instructorAdded.email);
		
		String expectedLogSegment = "New instructor (<span class=\"bold\"> " + newInstructorId + "</span>)"
				+ " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Error: try to add an existing instructor");
		
		action = getAction(submissionParams);
		result = (RedirectResult) action.executeAndPostProcess();
		
		AssertHelper.assertContains(
				Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE+"?message=An+instructor+with+the+same+ID+already+exists+in+the+course.", 
				result.getDestinationWithParams());
		assertEquals(true, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS, result.getStatusMessage());

		expectedLogSegment = "TEAMMATESLOG|||instructorCourseInstructorAdd|||instructorCourseInstructorAdd"
				+ "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
				+ "|||instr1@course1.com|||Servlet Action Failure : Trying to create a Instructor that exists: ICIAAT.newInstructorId, idOfTypicalCourse1"
				+ "|||/page/instructorCourseInstructorAdd";
		assertEquals(expectedLogSegment, action.getLogMessage());
		
		______TS("Masquerade mode:");
		
		instructorsLogic.deleteInstructor(courseId, newInstructorId);

		gaeSimulation.loginAsAdmin(adminUserId);
		action = getAction(addUserIdToParams(instructorId, submissionParams));
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED, result.getStatusMessage());
		
		assertEquals(true, instructorsLogic.isInstructorOfCourse(newInstructorId, courseId));
		assertEquals(true, AccountsLogic.inst().isAccountAnInstructor(newInstructorId));
		
		instructorAdded = instructorsLogic.getInstructorForGoogleId(courseId, newInstructorId);
		assertEquals(newInstructorName, instructorAdded.name);
		assertEquals(newInstructorEmail, instructorAdded.email);
		
		expectedLogSegment = "New instructor (<span class=\"bold\"> " + newInstructorId + "</span>)"
				+ " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
	}
	
	private InstructorCourseInstructorAddAction getAction(String... parameters) throws Exception {
		return (InstructorCourseInstructorAddAction)gaeSimulation.getActionObject(uri, parameters);
	}

}
