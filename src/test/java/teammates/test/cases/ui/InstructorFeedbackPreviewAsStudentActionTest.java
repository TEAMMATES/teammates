package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackPreviewAsStudentAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackPreviewAsStudentActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructorHelper = dataBundle.instructors.get("helperOfCourse1");
        String idOfInstructor = instructor.googleId;
        String idOfInstructorHelper = instructorHelper.googleId;
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("typical success case");

        String feedbackSessionName = "First feedback session";
        String courseId = student.course;
        String previewAsEmail = student.email;

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        InstructorFeedbackPreviewAsStudentAction paia = getAction(submissionParams);
        ShowPageResult showPageResult = (ShowPageResult) paia.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT
                     + "?error=false"
                     + "&user=" + idOfInstructor,
                     showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorFeedbackPreviewAsStudent|||instructorFeedbackPreviewAsStudent"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Preview feedback session as student (" + student.email + ")<br>"
                + "Session Name: First feedback session<br>Course ID: idOfTypicalCourse1|||"
                + "/page/instructorFeedbackPreviewAsStudent",
                paia.getLogMessage());
        
        gaeSimulation.loginAsInstructor(idOfInstructorHelper);
        
        ______TS("failure: not enough privilege");
        
        feedbackSessionName = "First feedback session";
        courseId = "idOfTypicalCourse1";
        previewAsEmail = student.email;
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };
        
        try {
            paia = getAction(submissionParams);
            showPageResult = (ShowPageResult) paia.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor ["
                         + instructorHelper.email + "] for privilege [canmodifysession]", e.getMessage());
        }
        
        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("failure: non-existent previewas email");
        
        previewAsEmail = "non-exIstentEmail@gsail.tmt";

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };

        try {
            paia = getAction(submissionParams);
            showPageResult = (ShowPageResult) paia.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (EntityNotFoundException enfe) {
            assertEquals("Student Email " + previewAsEmail + " does not exist in " + courseId + ".",
                         enfe.getMessage());
        }
    }
            
    private InstructorFeedbackPreviewAsStudentAction getAction(String... params) {
        return (InstructorFeedbackPreviewAsStudentAction) gaeSimulation.getActionObject(uri, params);
    }
}
