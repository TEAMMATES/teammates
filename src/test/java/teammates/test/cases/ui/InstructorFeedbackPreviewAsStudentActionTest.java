package teammates.test.cases.ui;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackPreviewAsStudentAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackPreviewAsStudentActionTest extends
        BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        String idOfInstructor = instructor.googleId;
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("typical success case");

        String feedbackSessionName = "First feedback session";
        String courseId = student.course;
        String previewAsEmail = student.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        InstructorFeedbackPreviewAsStudentAction paia = getAction(submissionParams);
        ShowPageResult showPageResult = (ShowPageResult) paia.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT 
                + "?error=false"
                + "&user="+ idOfInstructor
                ,showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        assertEquals("TEAMMATESLOG|||instructorFeedbackPreviewAsStudent|||instructorFeedbackPreviewAsStudent"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                + "Preview feedback session as student (" + student.email + ")<br>"
                + "Session Name: First feedback session<br>Course ID: idOfTypicalCourse1|||"
                + "/page/instructorFeedbackPreviewAsStudent"
                , paia.getLogMessage());

        ______TS("failure: non-existent previewas email");
        previewAsEmail = "non-exIstentEmail@gsail.com";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        try {
            paia = getAction(submissionParams);
            showPageResult = (ShowPageResult) paia.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Student Email "
                            + previewAsEmail + " does not exist in " + courseId
                            + ".", 
                        edne.getMessage());
        }
    }
            
        private InstructorFeedbackPreviewAsStudentAction getAction(String... params) throws Exception{
        return (InstructorFeedbackPreviewAsStudentAction) gaeSimulation.getActionObject(uri, params);
    }
}
