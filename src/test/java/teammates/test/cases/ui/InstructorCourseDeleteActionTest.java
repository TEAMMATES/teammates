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
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.InstructorCourseDeleteAction;
import teammates.ui.controller.InstructorCoursePageAction;
import teammates.ui.controller.InstructorCoursePageData;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseDeleteActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String otherInstructorId;
	String studentId;
	String adminUserId;

	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorCourseDelete";
		sr.registerServlet(URI, ControllerServlet.class.getName());
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
				Common.PARAM_COURSE_ID, "icdtc.tac.id1"
		};
		
		CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdtc.tac.id1", "New course");
		
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
	public void testExecuteAndPostProcess() throws Exception{
		
		InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		
		String[] submissionParams = new String[]{
				Common.PARAM_COURSE_ID, instructor1ofCourse1.courseId
		};
		
		
		______TS("Typical case, 2 courses");
		
		CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdct.tpa.id1", "New course");
		loginAsInstructor(instructorId);
		InstructorCoursePageAction a = getAction(submissionParams);
		ShowPageResult r = (ShowPageResult)a.executeAndPostProcess();
		
		assertEquals(
				Common.JSP_INSTRUCTOR_COURSE+"?message=The+course+has+been+deleted.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("The course has been deleted.", r.getStatusMessage());
		
		InstructorCoursePageData pageData = (InstructorCoursePageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(1, pageData.currentCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		assertEquals("idOfInstructor1OfCourse1|Instructor 1 of Course 1|instr1@course1.com", pageData.instructorListToShow);
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorCourseDelete" +
				"|||instructorCourseDelete|||true|||Instructor|||Instructor 1 of Course 1" +
				"|||idOfInstructor1OfCourse1|||instr1@course1.com" +
				"|||Course deleted: idOfTypicalCourse1|||/page/instructorCourseDelete";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode, delete last courses");
		
		loginAsAdmin(adminUserId);
		submissionParams = new String[]{
				Common.PARAM_COURSE_ID, "icdct.tpa.id1"
		};
		a = getAction(addUserIdToParams(instructorId, submissionParams));
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Common.JSP_INSTRUCTOR_COURSE+"?message=The+course+has+been+deleted.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("The course has been deleted.", r.getStatusMessage());
		
		pageData = (InstructorCoursePageData) r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(0, pageData.currentCourses.size());
		assertEquals("", pageData.courseIdToShow);
		assertEquals("", pageData.courseNameToShow);
		assertEquals("idOfInstructor1OfCourse1|Instructor 1 of Course 1|instr1@course1.com", pageData.instructorListToShow);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorCourseDelete|||instructorCourseDelete" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||Course deleted: icdct.tpa.id1|||/page/instructorCourseDelete";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Still masquerade mode, redirect after delete");
		
		CoursesLogic.inst().createCourseAndInstructor(instructorId, "icdct.tpa.id2", "New course2");
		submissionParams = new String[]{
				Common.PARAM_COURSE_ID, "icdct.tpa.id2",
				Common.PARAM_NEXT_URL, Common.PAGE_INSTRUCTOR_HOME
		};
		a = getAction(addUserIdToParams(instructorId, submissionParams));
		RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(
				Common.PAGE_INSTRUCTOR_HOME+"?message=The+course+has+been+deleted.&error=false&user=idOfInstructor1OfCourse1", 
				rr.getDestinationWithParams());
		
		expectedLogMessage = "TEAMMATESLOG|||instructorCourseDelete|||instructorCourseDelete" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||Course deleted: icdct.tpa.id2|||/page/instructorCourseDelete";
		assertEquals(expectedLogMessage, a.getLogMessage());
	}
	
	
	private InstructorCourseDeleteAction getAction(String... params) throws Exception{
			return (InstructorCourseDeleteAction) (super.getActionObject(params));
	}
	

}
