package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.controller.InstructorCourseStudentDetailsEditSaveAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;



public class InstructorCourseStudentDetailsEditSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
		StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
		
        String instructorId = instructor1OfCourse1.googleId;
        String newStudentEmail = "newemail@gmail.com";
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Invalid parameters");
        
        //no parameters
		verifyAssumptionFailure();
		
		//null student email
		String[] invalidParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
		};
		verifyAssumptionFailure(invalidParams);
        
		//null course id
		invalidParams = new String[]{
				Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
		};
		verifyAssumptionFailure(invalidParams);

        
        ______TS("Typical case, successful edit and save student detail");
        
        String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email,
				Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
				Const.ParamsNames.NEW_STUDENT_EMAIL, newStudentEmail,
				Const.ParamsNames.COMMENTS, student1InCourse1.comments,
				Const.ParamsNames.TEAM_NAME, student1InCourse1.team
		};

        
        InstructorCourseStudentDetailsEditSaveAction a = getAction(submissionParams);
        RedirectResult r = getRedirectResult(a);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE +
        		"?message=" + "The+student+has+been+edited+successfully&" +
        		"error=" + "false&user=idOfInstructor1OfCourse1&" +
        		"courseid=" + "idOfTypicalCourse1",
        		r.getDestinationWithParams());
        
        assertEquals(false, r.isError);
        assertEquals(Const.StatusMessages.STUDENT_EDITED, r.getStatusMessage());
        
        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave" +
        		"|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||" +
        		"Student <span class=\"bold\">" + student1InCourse1.email + 
				"'s</span> details in Course <span class=\"bold\">[idOfTypicalCourse1]</span> edited.<br>"+ 
				"New Email: " + newStudentEmail + 
				"<br>New Team: " + student1InCourse1.team + 
				"<br>Comments: " + student1InCourse1.comments + 
				"|||/page/instructorCourseStudentDetailsEditSave";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Error case, invalid email parameter");
        
        String invalidStudentEmail = "thisisaveryverylonglonglongstudentemailaccountname@gmail.com";
        
        submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
				Const.ParamsNames.STUDENT_EMAIL, newStudentEmail, //Use the new email as the previous email have been changed
				Const.ParamsNames.STUDENT_NAME, student1InCourse1.name,
				Const.ParamsNames.NEW_STUDENT_EMAIL, invalidStudentEmail,
				Const.ParamsNames.COMMENTS, student1InCourse1.comments,
				Const.ParamsNames.TEAM_NAME, student1InCourse1.team
		};
        
        gaeSimulation.loginAsInstructor(instructorId);
        a = getAction(submissionParams);
        ShowPageResult result = getShowPageResult(a);
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT +
        		"?message=" + "%22thisisaveryverylonglonglongstudentemailaccountname%40gmail.com%22+is" +
        		"+not+acceptable+to+TEAMMATES+as+an+email+because+it+is+too+long.+An+email+address+contains" +
        		"+some+text+followed+by+one+%27%40%27+sign+followed+by+some+more+text.+It+cannot+be+longer+" +
        		"than+45+characters.+It+cannot+be+empty+and+it+cannot+have+spaces." +
        		"&error=" + "true" +
        		"&user=idOfInstructor1OfCourse1",
        		result.getDestinationWithParams());
        
        assertEquals(true, result.isError);
        assertEquals(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail, FieldValidator.REASON_TOO_LONG), 
        		result.getStatusMessage());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseStudentDetailsEditSave|||instructorCourseStudentDetailsEditSave" +
        		"|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||" +
        		"Servlet Action Failure : " + 
        		String.format(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail, FieldValidator.REASON_TOO_LONG) + 
				"|||/page/instructorCourseStudentDetailsEditSave";
        
        assertEquals(expectedLogMessage, a.getLogMessage());
        
	}
	
	private InstructorCourseStudentDetailsEditSaveAction getAction(String... params) throws Exception{
        return (InstructorCourseStudentDetailsEditSaveAction) (gaeSimulation.getActionObject(uri, params));
	}
	

}
