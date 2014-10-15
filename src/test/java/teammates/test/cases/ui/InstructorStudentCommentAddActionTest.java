package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorStudentCommentAddAction;
import teammates.ui.controller.RedirectResult;

public class InstructorStudentCommentAddActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = dataBundle.students.get("student3InCourse1");
        String instructorId = instructor.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Unsuccessful case: test empty course id parameter");
        String[] submissionParams = new String[]{
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added"
        };
        
        InstructorStudentCommentAddAction a;
        RedirectResult r;
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
       
        ______TS("Unsuccessful case: test empty student email parameter");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added"
        };

        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.STUDENT_EMAIL), e.getMessage());
        }
        
        ______TS("Unsuccessful case: test empty comment text parameter");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
  
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.COMMENT_TEXT), e.getMessage());
        }  

        ______TS("Typical case, add comment successful");
        
        submissionParams = new String[] {
                Const.ParamsNames.COMMENT_TEXT, "A typical comment to be added",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.RECIPIENT_TYPE, "PERSON",
                Const.ParamsNames.RECIPIENTS, student.email
        };

        a = getAction(submissionParams);
        r = getRedirectResult(a);

        assertEquals(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE
                + "?courseid=idOfTypicalCourse1&"
                + "studentemail=student3InCourse1%40gmail.tmt&"
                + "user=idOfInstructor3&"
                + "error=false",
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("New comment has been added", r.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorStudentCommentAdd|||instructorStudentCommentAdd"+
                "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"+
                "|||instr3@course1n2.tmt|||" +
                "Created Comment for Student:<span class=\"bold\">([" + student.email + "])</span> " +
                "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>" +
                "<span class=\"bold\">Comment:</span> "  + "<Text: A typical comment to be added>" +
                "|||/page/instructorStudentCommentAdd";
        
        assertEquals(expectedLogMessage, a.getLogMessage());
    }
    
    private InstructorStudentCommentAddAction getAction(String... params) throws Exception {
        return (InstructorStudentCommentAddAction) (gaeSimulation.getActionObject(uri, params));
    }
}
