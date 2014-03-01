package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.controller.InstructorCoursesPageAction;
import teammates.ui.controller.InstructorCoursesPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCoursesPageActionTest extends BaseActionTest {

	/* Explanation: The parent class has method for @BeforeTest and @AfterTest
	 */
	
	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		
		/* Explanation: This is just to display the test class name in the console */
		printTestClassHeader();
		
		/* Explanation: we set the Action URI once as a static variable, to avoid passing
		 * it as a parameter multiple times. This is for convenience. Any other
		 * test code can pick up the URI from this variable.
		 */
		uri = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		
		/* Explanation: Before every test, we put a standard set of test data into the
		 * simulated GAE datastore.
		 */
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		/* Explanation: In this case, we use an empty array because this action does not 
		 * require any parameters. When the action does need parameters, we
		 * can put them in this array as pairs of strings (parameter name, value).
		 * e.g., new String[]{Const.ParamsNames.COURSE_ID, "course101"}
		 */
		String[] submissionParams = new String[]{};
		
		/* Explanation: Here, we use one of the access control test methods available in the
		 * parent class. 
		 */
		verifyOnlyInstructorsCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		
		String[] submissionParams = new String[]{};
		
		InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		/* Explanation: If the action is supposed to verify parameters, 
		 * we should check here the correctness of parameter verification.
		 * e.g.
		 
	         ______TS("Invalid parameters");
	        //both parameters missing. 
			verifyAssumptionFailure(new String[]{});
			
			//null student email, only course ID is set
			String[] invalidParams = new String[]{
					Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
			};
			verifyAssumptionFailure(invalidParams);
		 
		 * In this action, there is no parameter verification.
		 */
		
		______TS("Typical case, 2 courses");
		if (CoursesLogic.inst().isCoursePresent("new-course")){
			CoursesLogic.inst().deleteCourseCascade("new-course");
		}
		
		CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course");
		gaeSimulation.loginAsInstructor(instructorId);
		InstructorCoursesPageAction a = getAction(submissionParams);
		ShowPageResult r = getShowPageResult(a);
		
		assertEquals(Const.ViewURIs.INSTRUCTOR_COURSES+"?error=false&user=idOfInstructor1OfCourse1", r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("", r.getStatusMessage());
		
		InstructorCoursesPageData pageData = (InstructorCoursesPageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(2, pageData.allCourses.size());
		assertEquals(0, pageData.archivedCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage" +
				"|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||instructorCourse Page Load<br>Total courses: 2" +
				"|||/page/instructorCoursesPage";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode, 0 courses");
		
		CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
		CoursesLogic.inst().deleteCourseCascade("new-course");
		gaeSimulation.loginAsAdmin("admin.user");
		a = getAction(addUserIdToParams(instructorId, submissionParams));
		r = getShowPageResult(a);
		
		assertEquals(
				Const.ViewURIs.INSTRUCTOR_COURSES+"?message=You+have+not+created+any+courses+yet.+Use+the+form+above+to+create+a+course.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals("You have not created any courses yet. Use the form above to create a course.", r.getStatusMessage());
		assertEquals(false, r.isError);
		
		pageData = (InstructorCoursesPageData) r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(0, pageData.allCourses.size());
		assertEquals(0, pageData.archivedCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||instructorCourse Page Load<br>Total courses: 0" +
				"|||/page/instructorCoursesPage";
		assertEquals(expectedLogMessage, a.getLogMessage());
	}
	
	
	private InstructorCoursesPageAction getAction(String... params) throws Exception{
			return (InstructorCoursesPageAction) (gaeSimulation.getActionObject(uri, params));
	}
	
}
