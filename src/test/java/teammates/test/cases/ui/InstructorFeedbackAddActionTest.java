package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackAddAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackAddActionTest extends BaseActionTest {
	
	DataBundle dataBundle;	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		InstructorAttributes instructor1ofCourse1 = 
				dataBundle.instructors.get("instructor1OfCourse1");
		
		String[] params = 
				createParamsForTypicalFeedbackSession(
						instructor1ofCourse1.courseId, "ifaat tca fs");
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		InstructorAttributes instructor1ofCourse1 =
				dataBundle.instructors.get("instructor1OfCourse1");
		
		______TS("Not enough parameters");
		
		gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
		verifyAssumptionFailure();
		//TODO make sure IFAA does assertNotNull for required parameters then uncomment
		//verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId);
		
		______TS("Typical case");
		
		String[] params =
				createParamsForTypicalFeedbackSession(
						instructor1ofCourse1.courseId, "ifaat tca fs");
		
		InstructorFeedbackAddAction a = getAction(params);
		RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=ifaat+tca+fs"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+feedback+session+has+been+added.+Click+the+%22Add+New+Question%22+button+below+to+begin+adding+questions+for+the+feedback+session."
						+ "&error=false",
				rr.getDestinationWithParams());
		
		String expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
				"Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
				"instr1@course1.com|||New Feedback Session <span class=\"bold\">(ifaat tca fs)</span>" +
				" for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
				"<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
				"<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
				"<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>" +
				"<span class=\"bold\">Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br>" +
				"<br><span class=\"bold\">Instructions:</span> <Text: instructions>|||/page/instructorFeedbackAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Error: try to add the same session again");
		
		a = getAction(params);
		ShowPageResult pr = (ShowPageResult) a.executeAndPostProcess();
		assertEquals(
				Const.ViewURIs.INSTRUCTOR_FEEDBACKS+"?message=A+feedback+session+by+this+name+already+exists+under+this+course&error=true&user=idOfInstructor1OfCourse1", 
				pr.getDestinationWithParams());
		assertEquals(true, pr.isError);
		assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EXISTS, pr.getStatusMessage());
		

		______TS("Add course with trailing space");
		
		params =
				createParamsForTypicalFeedbackSession(
						instructor1ofCourse1.courseId, "Course with trailing space ");
		
		a = getAction(params);
		rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=Course+with+trailing+space"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+feedback+session+has+been+added.+Click+the+%22Add+New+Question%22+button+below+to+begin+adding+questions+for+the+feedback+session."
						+ "&error=false",
				rr.getDestinationWithParams());
		
		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
				"Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
				"instr1@course1.com|||New Feedback Session <span class=\"bold\">(Course with trailing space)</span>" +
				" for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
				"<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
				"<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
				"<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>" +
				"<span class=\"bold\">Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br>" +
				"<br><span class=\"bold\">Instructions:</span> <Text: instructions>|||/page/instructorFeedbackAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("imezone with minute offset");
		
		params = createParamsForTypicalFeedbackSession(
						instructor1ofCourse1.courseId, "Course with minute offset timezone");
		params[25] = "5.5";
		
		a = getAction(params);
		rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=Course+with+minute+offset+timezone"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+feedback+session+has+been+added.+Click+the+%22Add+New+Question%22+button+below+to+begin+adding+questions+for+the+feedback+session."
						+ "&error=false",
				rr.getDestinationWithParams());
		
		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
				"Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
				"instr1@course1.com|||New Feedback Session <span class=\"bold\">(Course with minute offset timezone)</span>" +
				" for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
				"<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
				"<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
				"<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>" +
				"<span class=\"bold\">Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br>" +
				"<br><span class=\"bold\">Instructions:</span> <Text: instructions>|||/page/instructorFeedbackAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("Masquerade mode");
		
		gaeSimulation.loginAsAdmin("admin.user");

		params = createParamsForTypicalFeedbackSession(
						instructor1ofCourse1.courseId, "masquerade session");
		params = addUserIdToParams(instructor1ofCourse1.googleId, params);
		
		a = getAction(params);
		rr = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(
				Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
						+ "?courseid="
						+ instructor1ofCourse1.courseId
						+ "&fsname=masquerade+session"
						+ "&user="
						+ instructor1ofCourse1.googleId
						+ "&message=The+feedback+session+has+been+added.+Click+the+%22Add+New+Question%22+button+below+to+begin+adding+questions+for+the+feedback+session."
						+ "&error=false",
				rr.getDestinationWithParams());
		
		expectedLogMessage =
				"TEAMMATESLOG|||instructorFeedbackAdd|||instructorFeedbackAdd|||true|||" +
				"Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
				"instr1@course1.com|||New Feedback Session <span class=\"bold\">(masquerade session)</span>" +
				" for Course <span class=\"bold\">[idOfTypicalCourse1]</span> created." +
				"<br><span class=\"bold\">From:</span> Wed Feb 01 00:00:00 UTC 2012" +
				"<span class=\"bold\"> to</span> Thu Jan 01 00:00:00 UTC 2015<br>" +
				"<span class=\"bold\">Session visible from:</span> Sun Jan 01 00:00:00 UTC 2012<br>" +
				"<span class=\"bold\">Results visible from:</span> Mon Jun 22 00:00:00 UTC 1970<br>" +
				"<br><span class=\"bold\">Instructions:</span> <Text: instructions>|||/page/instructorFeedbackAdd";
		assertEquals(expectedLogMessage, a.getLogMessage());
	}
	
	private InstructorFeedbackAddAction getAction (String... params) throws Exception {
		return (InstructorFeedbackAddAction) gaeSimulation.getActionObject(uri, params);
	}
}
