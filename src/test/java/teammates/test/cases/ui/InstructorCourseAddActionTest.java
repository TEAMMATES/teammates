package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.CoursesLogic;
import teammates.ui.controller.InstructorCourseAddAction;
import teammates.ui.controller.InstructorCoursePageData;
import teammates.ui.controller.ShowPageResult;


public class InstructorCourseAddActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String otherInstructorId;
	String studentId;
	String adminUserId;

	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorCourseAdd";
		sr.registerServlet(URI, InstructorCourseAddAction.class.getName());
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();

		unregUserId = "unreg.user";
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		instructorId = instructor1OfCourse1.googleId;
		
		InstructorAttributes instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
		otherInstructorId = instructor1OfCourse2.googleId;
		
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		studentId = student1InCourse1.googleId;
		
		adminUserId = "admin.user";
		
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, "ticac.tac.id",
				Common.PARAM_COURSE_NAME, "ticac tac name",
				Common.PARAM_COURSE_INSTRUCTOR_LIST, "gid|name|email@email.com"};
		
		logoutUser();
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructorId,submissionParams));
		
		loginUser(unregUserId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructorId,submissionParams));
		
		loginAsStudent(studentId);
		verifyCannotAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(instructorId,submissionParams));
		
		loginAsInstructor(instructorId);
		verifyCanAccess(submissionParams);
		verifyCannotMasquerade(addUserIdToParams(otherInstructorId,submissionParams));
		
		loginAsAdmin(adminUserId);
		//not checking for non-masquerade mode because admin may not be an instructor
		verifyCanMasquerade(addUserIdToParams(instructorId,submissionParams));
	}
	
	@Test
	public void testExecute() throws Exception{
		InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		______TS("Not enough parameters");
		
		loginAsInstructor(instructorId);
		
		verifyAssumptionFailure();
		verifyAssumptionFailure(
				Common.PARAM_COURSE_INSTRUCTOR_LIST, "gid|name|email@email.com");
		verifyAssumptionFailure(
				Common.PARAM_COURSE_NAME, "ticac tac name",
				Common.PARAM_COURSE_INSTRUCTOR_LIST, "gid|name|email@email.com");
		
		______TS("Typical case, 1 existing course");
		
		InstructorCourseAddAction a = getAction(
				Common.PARAM_COURSE_ID, "ticac.tpa1.id",
				Common.PARAM_COURSE_NAME, "ticac tpa1 name",
				Common.PARAM_COURSE_INSTRUCTOR_LIST, instructorId+"|name|email@email.com");
		ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Common.JSP_INSTRUCTOR_COURSE+"?message=The+course+has+been+added.+Click+the+%27Enroll%27+link+in+the+table+below+to+add+students+to+the+course.+If+you+don%27t+see+the+course+in+the+list+below%2C+please+refresh+the+page+after+a+few+moments.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals(Common.MESSAGE_COURSE_ADDED, r.getStatusMessage());
		
		InstructorCoursePageData pageData = (InstructorCoursePageData)r.data;
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
				Common.PARAM_COURSE_ID, "ticac.tpa1.id",
				Common.PARAM_COURSE_NAME, "ticac tpa1 name",
				Common.PARAM_COURSE_INSTRUCTOR_LIST, instructorId+"|name|email@email.com");
		r = (ShowPageResult)a.executeAndPostProcess();
		
		assertEquals(
				Common.JSP_INSTRUCTOR_COURSE+"?message=A+course+by+the+same+ID+already+exists+in+the+system%2C+possibly+created+by+another+user.+Please+choose+a+different+course+ID&error=true&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(true, r.isError);
		assertEquals(Common.MESSAGE_COURSE_EXISTS, r.getStatusMessage());
		
		pageData = (InstructorCoursePageData)r.data;
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
		loginAsAdmin(adminUserId);
		a = getAction(
				Common.PARAM_USER_ID, instructorId,
				Common.PARAM_COURSE_ID, "ticac.tpa2.id",
				Common.PARAM_COURSE_NAME, "ticac tpa2 name",
				Common.PARAM_COURSE_INSTRUCTOR_LIST, instructorId+"|name|email@email.com");
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Common.JSP_INSTRUCTOR_COURSE+"?message=The+course+has+been+added.+Click+the+%27Enroll%27+link+in+the+table+below+to+add+students+to+the+course.+If+you+don%27t+see+the+course+in+the+list+below%2C+please+refresh+the+page+after+a+few+moments.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals(Common.MESSAGE_COURSE_ADDED, r.getStatusMessage());
		
		pageData = (InstructorCoursePageData)r.data;
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
	
	private InstructorCourseAddAction getAction(String... parameters) throws Exception {
		return (InstructorCourseAddAction)getActionObject(parameters);
	}

}
