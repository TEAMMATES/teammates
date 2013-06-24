package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.logic.CoursesLogic;
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.InstructorCoursePageAction;
import teammates.ui.controller.InstructorCoursePageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCoursePageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = Common.PAGE_INSTRUCTOR_COURSE;
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{};
		verifyOnlyInstructorsCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		
		String[] submissionParams = new String[]{};
		
		InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		______TS("Typical case, 2 courses");
		
		CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course");
		loginAsInstructor(instructorId);
		InstructorCoursePageAction a = getAction(submissionParams);
		ShowPageResult r = getShowPageResult(a);
		
		assertEquals(Common.JSP_INSTRUCTOR_COURSE+"?error=false&user=idOfInstructor1OfCourse1", r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("", r.getStatusMessage());
		
		InstructorCoursePageData pageData = (InstructorCoursePageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(2, pageData.currentCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		assertEquals("idOfInstructor1OfCourse1|Instructor 1 of Course 1|instr1@course1.com", pageData.instructorListToShow);
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorCourse|||instructorCourse" +
				"|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||instructorCourse Page Load<br>Total courses: 2" +
				"|||/page/instructorCourse";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode, 0 courses");
		
		CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
		CoursesLogic.inst().deleteCourseCascade("new-course");
		loginAsAdmin("admin.user");
		a = getAction(addUserIdToParams(instructorId, submissionParams));
		r = getShowPageResult(a);
		
		assertEquals(
				Common.JSP_INSTRUCTOR_COURSE+"?message=You+have+not+created+any+courses+yet.+Use+the+form+above+to+create+a+course.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals("You have not created any courses yet. Use the form above to create a course.", r.getStatusMessage());
		assertEquals(false, r.isError);
		
		pageData = (InstructorCoursePageData) r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(0, pageData.currentCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		assertEquals("idOfInstructor1OfCourse1|Instructor 1 of Course 1|instr1@course1.com", pageData.instructorListToShow);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorCourse|||instructorCourse" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||instructorCourse Page Load<br>Total courses: 0" +
				"|||/page/instructorCourse";
		assertEquals(expectedLogMessage, a.getLogMessage());
	}
	
	
	private InstructorCoursePageAction getAction(String... params) throws Exception{
			return (InstructorCoursePageAction) (super.getActionObject(params));
	}
	
}
