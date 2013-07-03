package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Config;
import teammates.logic.CoursesLogic;
import teammates.logic.EvaluationsLogic;
import teammates.ui.controller.InstructorEvalPageAction;
import teammates.ui.controller.InstructorEvalPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorEvalPageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Config.PAGE_INSTRUCTOR_EVAL;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
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
		gaeSimulation.loginAsInstructor(instructorId);
		InstructorEvalPageAction a = getAction(submissionParams);
		ShowPageResult r = (ShowPageResult)a.executeAndPostProcess();
		
		assertEquals(Config.JSP_INSTRUCTOR_EVAL+"?error=false&user=idOfInstructor1OfCourse1", r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("", r.getStatusMessage());
		
		InstructorEvalPageData pageData = (InstructorEvalPageData)r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(2, pageData.courses.size());
		assertEquals(2, pageData.evaluations.size());
		assertEquals(null, pageData.newEvaluationToBeCreated);
		assertEquals(null, pageData.courseIdForNewEvaluation);
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorEval|||instructorEval" +
				"|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||Number of evaluations :2|||/page/instructorEval";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode, 0 evaluations");
		
		EvaluationsLogic.inst().deleteEvaluationsForCourse(instructor1ofCourse1.courseId);
		
		gaeSimulation.loginAsAdmin("admin.user");
		submissionParams = new String[]{Config.PARAM_COURSE_ID, instructor1ofCourse1.courseId};
		a = getAction(addUserIdToParams(instructorId, submissionParams));
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Config.JSP_INSTRUCTOR_EVAL+"?message=You+have+not+created+any+evaluations+yet." +
						"+Use+the+form+above+to+create+a+new+evaluation.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals("You have not created any evaluations yet. Use the form above to create a new evaluation.", 
				r.getStatusMessage());
		assertEquals(false, r.isError);
		
		pageData = (InstructorEvalPageData) r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(2, pageData.courses.size());
		assertEquals(0, pageData.evaluations.size());
		assertEquals(null, pageData.newEvaluationToBeCreated);
		assertEquals(instructor1ofCourse1.courseId, pageData.courseIdForNewEvaluation);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorEval|||instructorEval" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||Number of evaluations :0|||/page/instructorEval";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode, 0 courses");
		
		CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
		CoursesLogic.inst().deleteCourseCascade("new-course");
		
		submissionParams = new String[]{};
		a = getAction(addUserIdToParams(instructorId, submissionParams));
		r = (ShowPageResult) a.executeAndPostProcess();
		
		assertEquals(
				Config.JSP_INSTRUCTOR_EVAL+"?message=You+have+not+created+any+courses+yet." +
						"+Go+%3Ca+href%3D%22%2Fpage%2FinstructorCourse%3Fuser%3DidOfInstructor1OfCourse1%22%3Ehere%3C%2Fa%3E+to+create+one.&error=false&user=idOfInstructor1OfCourse1", 
				r.getDestinationWithParams());
		assertEquals("You have not created any courses yet. Go <a href=\"/page/instructorCourse?user=idOfInstructor1OfCourse1\">here</a> to create one.", r.getStatusMessage());
		assertEquals(false, r.isError);
		
		pageData = (InstructorEvalPageData) r.data;
		assertEquals(instructorId, pageData.account.googleId);
		assertEquals(0, pageData.courses.size());
		assertEquals(0, pageData.evaluations.size());
		assertEquals(null, pageData.newEvaluationToBeCreated);
		assertEquals(null, pageData.courseIdForNewEvaluation);
		
		expectedLogMessage = "TEAMMATESLOG|||instructorEval|||instructorEval" +
				"|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1" +
				"|||instr1@course1.com|||Number of evaluations :0|||/page/instructorEval";
		assertEquals(expectedLogMessage, a.getLogMessage());
	}
	
	
	private InstructorEvalPageAction getAction(String... params) throws Exception{
			return (InstructorEvalPageAction) (gaeSimulation.getActionObject(uri, params));
	}

}
