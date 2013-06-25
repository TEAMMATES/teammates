package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.EvaluationsLogic;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.common.EvaluationAttributesTest;
import teammates.ui.controller.ControllerServlet;
import teammates.ui.controller.InstructorHomePageAction;
import teammates.ui.controller.InstructorHomePageData;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentHomePageAction;
import teammates.ui.controller.StudentHomePageData;

public class InstructorHomePageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	String unregUserId;
	String instructorId;
	String otherInstructorId;
	String studentId;
	String adminUserId;

	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		URI = "/page/instructorHome";
		sr.registerServlet(URI, ControllerServlet.class.getName());
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
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
		
		String[] submissionParams = new String[]{};
		
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
		
		String[] submissionParams = new String[]{};
		
		______TS("insctructor with no courses");
		
		loginAsInstructor(dataBundle.accounts.get("instructorWithoutCourses").googleId);
		InstructorHomePageAction a = getAction(submissionParams);
		ShowPageResult r = getShowPageResult(a);
		assertContainsRegex("/jsp/instructorHome.jsp?error=false&user=instructorWithoutCourses", r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("",r.getStatusMessage());
		
		InstructorHomePageData data = (InstructorHomePageData)r.data;
		assertEquals(0, data.courses.size());
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorHome|||instructorHome|||true" +
				"|||Instructor|||Instructor Without Courses|||instructorWithoutCourses" +
				"|||iwc@yahoo.com|||instructorHome Page Load<br>Total Courses: 0|||/page/instructorHome" ;
		assertEquals(expectedLogMessage, a.getLogMessage());
//		
//		______TS("instructor with multiple courses");
//		
//		//Note: this can happen only if the course was deleted after the student joined it.
//		// The 'welcome stranger' response is not really appropriate for this situation, but 
//		//   we keep it because the situation is rare and not worth extra coding.
//		
//		//create a student account without courses
//		AccountAttributes studentWithoutCourses = new AccountAttributes();
//		studentWithoutCourses.googleId = "googleId.without.courses";
//		studentWithoutCourses.name = "Student Without Courses";
//		studentWithoutCourses.email = "googleId.without.courses@email.com";
//		studentWithoutCourses.institute = "NUS";
//		studentWithoutCourses.isInstructor = false;
//		AccountsDb accountsDb = new AccountsDb();
//		accountsDb.createAccount(studentWithoutCourses);
//		assertNotNull(accountsDb.getAccount(studentWithoutCourses.googleId));
//		
//		loginUser(studentWithoutCourses.googleId);
//		a = getAction(submissionParams);
//		r = getShowPageResult(a);
//		assertContainsRegex("/jsp/studentHome.jsp?message=Welcome+stranger{*}&error=false&user="+studentWithoutCourses.googleId, r.getDestinationWithParams());
//		assertEquals(false, r.isError);
//		assertContainsRegex("Welcome stranger :-){*}use the new Gmail address. ",r.getStatusMessage());
//		
//		data = (StudentHomePageData)r.data;
//		assertEquals(0, data.courses.size());
//		assertEquals(0, data.evalSubmissionStatusMap.keySet().size());
//		
//		expectedLogMessage = "TEAMMATESLOG|||studentHome|||studentHome|||true" +
//				"|||Student|||Student Without Courses|||googleId.without.courses" +
//				"|||googleId.without.courses@email.com|||Servlet Action Failure :Student with Google ID googleId.without.courses does not exist|||/page/studentHome" ;
//		assertEquals(expectedLogMessage, a.getLogMessage());
//		
//		
		______TS("instructor with multiple courses, masquerade mode");
		
		loginAsAdmin(adminUserId);
		
		//create a CLOSED evaluation
		EvaluationAttributes eval = EvaluationAttributesTest.generateValidEvaluationAttributesObject();
		String IdOfCourse2 = dataBundle.courses.get("typicalCourse2").id;
		eval.courseId = IdOfCourse2;
		eval.name = "Closed eval";
		eval.startTime = Common.getDateOffsetToCurrentTime(-2);
		eval.endTime = Common.getDateOffsetToCurrentTime(-1);
		eval.setDerivedAttributes();
		assertEquals(EvalStatus.CLOSED, eval.getStatus());
		EvaluationsLogic evaluationsLogic = new EvaluationsLogic();
		evaluationsLogic.createEvaluationCascade(eval);
		
		//create a PUBLISHED evaluation
		eval.name = "published eval";
		eval.startTime = Common.getDateOffsetToCurrentTime(-2);
		eval.endTime = Common.getDateOffsetToCurrentTime(-1);
		eval.published = true;
		eval.setDerivedAttributes();
		assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
		evaluationsLogic.createEvaluationCascade(eval);
		
		//access page in masquerade mode
		String instructorWithMultipleCourses = dataBundle.accounts.get("instructor3").googleId;
		a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
		r = getShowPageResult(a);
		
		assertEquals("/jsp/instructorHome.jsp?error=false&user="+instructorWithMultipleCourses, r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("",r.getStatusMessage());
		
		data = (InstructorHomePageData)r.data;
		assertEquals(2, data.courses.size());
		
		
		expectedLogMessage = "TEAMMATESLOG|||instructorHome|||instructorHome|||true" +
				"|||Instructor(M)|||Instructor 3 of Course 1 and 2|||idOfInstructor3" +
				"|||instr3@course1n2.com|||instructorHome Page Load<br>Total Courses: 2|||/page/instructorHome" ;
		assertEquals(expectedLogMessage, a.getLogMessage());
		
	}

	private InstructorHomePageAction getAction(String... params) throws Exception{
			return (InstructorHomePageAction) (super.getActionObject(params));
	}
	
}
