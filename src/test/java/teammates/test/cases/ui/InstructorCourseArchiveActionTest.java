package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseArchiveAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseArchiveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId,
				Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true"
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception {
		
		String[] submissionParams = new String[]{};
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		String courseId = instructor1OfCourse1.courseId;
		gaeSimulation.loginAsInstructor(instructorId);
		
		______TS("Not enough parameters");
		
		verifyAssumptionFailure();
		verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, courseId);
		verifyAssumptionFailure(Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true");
		
		______TS("Typical case: archive a course, redirect to home page");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
				Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
		};
		
		InstructorCourseArchiveAction action = getAction(submissionParams);
		RedirectResult result = getRedirectResult(action);
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_HOME_PAGE+"?message=The+course+idOfTypicalCourse1+has+been+archived.+It+will+not+appear+in+the+home+page+any+more."
				+"You+can+access+archived+courses+from+the+'Courses'+tab.<br>Click+here+to+undo+the+archiving+and+bring+the+course+back+to+the+home+page."
				+"&error=false&user=idOfInstructor1OfCourse1", 
				result.getDestinationWithParams());
		assertEquals(false, result.isError);
		assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, courseId), result.getStatusMessage());
		
		String expectedLogSegment = "Course archived: " + courseId;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Rare case: archive an already archived course");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
				Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
		};
		
		action = getAction(submissionParams);
		result = getRedirectResult(action);
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_HOME_PAGE+"?message=The+course+idOfTypicalCourse1+has+been+archived.+It+will+not+appear+in+the+home+page+any+more."
				+"You+can+access+archived+courses+from+the+'Courses'+tab.<br>Click+here+to+undo+the+archiving+and+bring+the+course+back+to+the+home+page."
				+"&error=false&user=idOfInstructor1OfCourse1", 
				result.getDestinationWithParams());
		assertEquals(false, result.isError);
		assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, courseId), result.getStatusMessage());
		
		expectedLogSegment = "Course archived: " + courseId;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Typical case: unarchive a course");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
				Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
		};
		
		action = getAction(submissionParams);
		result = getRedirectResult(action);
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_COURSES_PAGE+"?message=The+course+idOfTypicalCourse1+has+been+unarchived.&error=false&user=idOfInstructor1OfCourse1", 
				result.getDestinationWithParams());
		assertEquals(false, result.isError);
		assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId), result.getStatusMessage());
		
		expectedLogSegment = "Course unarchived: " + courseId;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Rare case: unarchive an active course");
		
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
				Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
		};
		
		action = getAction(submissionParams);
		result = getRedirectResult(action);
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_COURSES_PAGE+"?message=The+course+idOfTypicalCourse1+has+been+unarchived.&error=false&user=idOfInstructor1OfCourse1", 
				result.getDestinationWithParams());
		assertEquals(false, result.isError);
		assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId), result.getStatusMessage());
		
		expectedLogSegment = "Course unarchived: " + courseId;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
		______TS("Masquerade mode: archive course, redirect to course page");
		
		gaeSimulation.loginAsAdmin("admin.user");
		submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
				Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
		};
		action = getAction(addUserIdToParams(instructorId, submissionParams));
		result = getRedirectResult(action);
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_COURSES_PAGE+"?message=The+course+idOfTypicalCourse1+has+been+archived.+It+will+not+appear+in+the+home+page+any+more.&error=false&user=idOfInstructor1OfCourse1", 
				result.getDestinationWithParams());
		assertEquals(false, result.isError);
		assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED, courseId), result.getStatusMessage());
		
		expectedLogSegment = "Course archived: " + courseId;
		AssertHelper.assertContains(expectedLogSegment, action.getLogMessage());
		
	}
	
	private InstructorCourseArchiveAction getAction(String... params) throws Exception {
		return (InstructorCourseArchiveAction) (gaeSimulation.getActionObject(uri, params));
	}
	

}

