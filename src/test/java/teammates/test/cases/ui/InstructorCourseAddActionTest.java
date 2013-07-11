package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCoursesPageData;
import teammates.ui.controller.ShowPageResult;


public class InstructorCourseAddActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_ADD;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, "ticac.tac.id",
				Const.ParamsNames.COURSE_NAME, "ticac tac name",
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, "gid|name|email@email.com"};
		
		verifyOnlyInstructorsCanAccess(submissionParams);
	}
	
	@Test
	public void testExecute() throws Exception{

		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		String instructorId = instructor1OfCourse1.googleId;
		
		String adminUserId = "admin.user";
		InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		______TS("Not enough parameters");
		
		gaeSimulation.loginAsInstructor(instructorId);
		
		verifyAssumptionFailure();
		verifyAssumptionFailure(
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, "gid|name|email@email.com");
		verifyAssumptionFailure(
				Const.ParamsNames.COURSE_NAME, "ticac tac name",
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, "gid|name|email@email.com");
		
		______TS("Typical case, 1 existing course");
		
		Action a = getAction(
				Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
				Const.ParamsNames.COURSE_NAME, "ticac tpa1 name",
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructorId+"|name|email@email.com");
		ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Const.ViewURIs.INSTRUCTOR_COURSES+"?message=The+course+has+been+added.+Click+the+%27Enroll%27+link+in+the+table+below+to+add+students+to+the+course.+If+you+don%27t+see+the+course+in+the+list+below%2C+please+refresh+the+page+after+a+few+moments.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals(Const.StatusMessages.COURSE_ADDED, r.getStatusMessage());
		
		InstructorCoursesPageData pageData = (InstructorCoursesPageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(2, pageData.currentCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		assertEquals("idOfInstructor1OfCourse1|Instructor 1 of Course 1|instr1@course1.com", pageData.instructorListToShow);
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd" +
				"|||instructorCourseAdd|||true|||Instructor|||Instructor 1 of Course 1" +
				"|||idOfInstructor1OfCourse1|||instr1@course1.com" +
				"|||Course added : ticac.tpa1.id<br>Total courses: 2|||/page/instructorCourseAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Error: try to add the same course again");
		
		a = getAction(
				Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
				Const.ParamsNames.COURSE_NAME, "ticac tpa1 name",
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructorId+"|name|email@email.com");
		r = (ShowPageResult)a.executeAndPostProcess();
		
		assertEquals(
				Const.ViewURIs.INSTRUCTOR_COURSES+"?message=A+course+by+the+same+ID+already+exists+in+the+system%2C+possibly+created+by+another+user.+Please+choose+a+different+course+ID&error=true&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(true, r.isError);
		assertEquals(Const.StatusMessages.COURSE_EXISTS, r.getStatusMessage());
		
		pageData = (InstructorCoursesPageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(2, pageData.currentCourses.size());
		assertEquals("ticac.tpa1.id", pageData.courseIdToShow);
		assertEquals("ticac tpa1 name", pageData.courseNameToShow);
		assertEquals(instructorId+"|name|email@email.com", pageData.instructorListToShow);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd" +
				"|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||A course by the same ID already exists in the system, possibly created by another user. Please choose a different course ID<br>Total courses: 2" +
				"|||/page/instructorCourseAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode, 0 courses");
		
		CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
		CoursesLogic.inst().deleteCourseCascade("ticac.tpa1.id");
		gaeSimulation.loginAsAdmin(adminUserId);
		a = getAction(
				Const.ParamsNames.USER_ID, instructorId,
				Const.ParamsNames.COURSE_ID, "ticac.tpa2.id",
				Const.ParamsNames.COURSE_NAME, "ticac tpa2 name",
				Const.ParamsNames.COURSE_INSTRUCTOR_LIST, instructorId+"|name|email@email.com");
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Const.ViewURIs.INSTRUCTOR_COURSES+"?message=The+course+has+been+added.+Click+the+%27Enroll%27+link+in+the+table+below+to+add+students+to+the+course.+If+you+don%27t+see+the+course+in+the+list+below%2C+please+refresh+the+page+after+a+few+moments.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals(Const.StatusMessages.COURSE_ADDED, r.getStatusMessage());
		
		pageData = (InstructorCoursesPageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(1, pageData.currentCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		assertEquals(instructorId+"|Instructor 1 of Course 1|instr1@course1.com", pageData.instructorListToShow);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1" +
				"|||idOfInstructor1OfCourse1|||instr1@course1.com|||Course added : ticac.tpa2.id<br>Total courses: 1" +
				"|||/page/instructorCourseAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
	}
	
	private Action getAction(String... parameters) throws Exception {
		return (Action)gaeSimulation.getActionObject(uri, parameters);
	}

}
