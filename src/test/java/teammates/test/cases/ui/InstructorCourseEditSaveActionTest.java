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
import teammates.ui.controller.InstructorCourseEditSaveAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	InstructorsLogic instructorsLogic;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_SAVE;
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
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructor.googleId+"|instr|ins@gmail.com"
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception {
		String instructorList = "";
		String[] submissionParams = null;
		
		______TS("Typical case: add and edit existing instructor");
		
		InstructorAttributes instructorOfCourseWithOneInstructor = dataBundle.instructors.get("instructor4");
		String instructorId = instructorOfCourseWithOneInstructor.googleId;
		String courseId = instructorOfCourseWithOneInstructor.courseId;
		
		gaeSimulation.loginAsInstructor(instructorId);

		InstructorAttributes newInstructor = dataBundle.instructors.get("instructor1OfCourse1");
		instructorList = getInstructorLine(newInstructor);
		
		instructorList += Const.EOL + instructorId + "|New instructor name|" + instructorOfCourseWithOneInstructor.email; 
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructorList
		};
		
		InstructorCourseEditSaveAction action = getAction(submissionParams);
		RedirectResult result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_EDITED, result.getStatusMessage());
		
		instructorsLogic.isInstructorOfCourse(newInstructor.googleId, courseId);
		
		InstructorAttributes editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
		assertEquals("New instructor name", editedInstructor.name);
		
		String expectedLogSegment = "Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
						+ "New Instructor List: <br> - " + instructorList.replace("\n", "<br> - ");
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Failure case: edit failed due to invalid parameters");
		
		String invalidEmail = "wrongEmail.com";
		instructorList = instructorId + "|New instructor name|" + invalidEmail; 
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructorList
		};
		
		action = getAction(submissionParams);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, result.destination);
		assertEquals(true, result.isError);
		String expectedErrorMessage = (new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail);
		assertEquals(expectedErrorMessage, result.getStatusMessage());
		
		AssertHelper.assertContains(expectedErrorMessage, action.getLogMessage());
		
		______TS("Masquerade mode: delete existing instructor");
		
		gaeSimulation.loginAsAdmin("admin.user");
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		courseId = instructor1OfCourse1.courseId;
		
		InstructorAttributes deletedInstructor = dataBundle.instructors.get("instructor2OfCourse1");
		String idOfDeletedInstructor = deletedInstructor.googleId;
		
		instructorList = getInstructorLine(instructor1OfCourse1);
		instructorList += Const.EOL + getInstructorLine(dataBundle.instructors.get("instructor3OfCourse1"));
		
		submissionParams = new String[]{
				Const.ParamsNames.USER_ID, instructorId,
				Const.ParamsNames.COURSE_ID, courseId,
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructorList
		};
		
		action = getAction(submissionParams);
		result = (RedirectResult) action.executeAndPostProcess();
		
		assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, result.destination);
		assertEquals(false, result.isError);
		assertEquals(Const.StatusMessages.COURSE_EDITED, result.getStatusMessage());
		
		assertEquals(false, instructorsLogic.isInstructorOfCourse(idOfDeletedInstructor, courseId));
		
		expectedLogSegment = "Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
						+ "New Instructor List: <br> - " + instructorList.replace("\n", "<br> - ");
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
	}
	
	private InstructorCourseEditSaveAction getAction(String... parameters) throws Exception {
		return (InstructorCourseEditSaveAction) (gaeSimulation.getActionObject(uri, parameters));
	}

	private String getInstructorLine(InstructorAttributes instructor) {
		String id = instructor.googleId;
		String name = instructor.name;
		String email = instructor.email;
		
		return id + "|" + name + "|" + email;
	}
}
