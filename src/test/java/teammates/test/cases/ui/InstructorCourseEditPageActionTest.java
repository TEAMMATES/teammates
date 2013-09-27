package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEditPageAction;
import teammates.ui.controller.InstructorCourseEditPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseEditPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		______TS("Typical case: open the course edit page");
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		gaeSimulation.loginAsInstructor(instructorId);
		
		String courseId = instructor1OfCourse1.courseId;
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, courseId
		};
		
		InstructorCourseEditPageAction action = getAction(submissionParams);
		ShowPageResult result = getShowPageResult(action);
		assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, result.destination);
		assertEquals(false, result.isError);
		assertEquals("", result.getStatusMessage());
		
		InstructorCourseEditPageData data = (InstructorCourseEditPageData) result.data;
		assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.course.toString());
		verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.instructorList);
		
		String expectedLogSegment = "instructorCourseEdit Page Load<br>"
				+ "Editing information for Course <span class=\"bold\">["
				+ courseId + "]</span>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Masquerade mode");
		
		InstructorAttributes instructor = dataBundle.instructors.get("instructor4");
		instructorId = instructor.googleId;
		
		gaeSimulation.loginAsAdmin("admin.user");
		
		courseId = instructor.courseId;
		submissionParams = new String[]{
				Const.ParamsNames.USER_ID, instructorId,
				Const.ParamsNames.COURSE_ID, courseId
		};
		
		action = getAction(submissionParams);
		result = getShowPageResult(action);
		assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, result.destination);
		assertEquals(false, result.isError);
		assertEquals("", result.getStatusMessage());
		
		data = (InstructorCourseEditPageData) result.data;
		assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.course.toString());
		verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.instructorList);
		
		expectedLogSegment = "instructorCourseEdit Page Load<br>"
				+ "Editing information for Course <span class=\"bold\">["
				+ courseId + "]</span>";
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Failure case: edit a non-existing course");
		
		CoursesLogic.inst().deleteCourseCascade(courseId);
		
		submissionParams = new String[]{
				Const.ParamsNames.USER_ID, instructorId,
				Const.ParamsNames.COURSE_ID, courseId
		};
			
		try {
			action = getAction(submissionParams);
			result = getShowPageResult(action);
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			assertEquals("Course "+courseId+" does not exist", e.getMessage());
		}

	}

	private InstructorCourseEditPageAction getAction(String... params) throws Exception {
		return (InstructorCourseEditPageAction) (gaeSimulation.getActionObject(uri, params));
	}
	
	private void verifySameInstructorList(
			List<InstructorAttributes> list1,
			List<InstructorAttributes> list2) {
		
		assertEquals(list1.size(), list2.size());
		
		for (int i = 0; i < list1.size(); i++) {
			assertEquals(list1.get(i).toString(), list2.get(i).toString());
		}
	}
}
