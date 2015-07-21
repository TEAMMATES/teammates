package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackEditCopyAction;
import teammates.ui.controller.RedirectResult;


public class InstructorFeedbackEditCopyActionTest extends
        BaseActionTest {
    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorFeedbackEditCopyTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorFeedbackEditCopyTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("teammates.test.instructor2");
        String instructorId = instructor.googleId;
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("openSession");
        CourseAttributes course = dataBundle.courses.get("course");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        String expectedString = "";

        
        ______TS("Failure case: No parameters");
        verifyAssumptionFailure();

        
        ______TS("Failure case: Courses not passed in, instructor home page");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_HOME_PAGE
        };
        
        InstructorFeedbackEditCopyAction a = getAction(params);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, rr.getStatusMessage());
        
        ______TS("Failure case: Courses not passed in, instructor feedbacks page");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACKS_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, rr.getStatusMessage());
        
        ______TS("Failure case: Courses not passed in, instructor feedback copy page");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_COPY
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, rr.getStatusMessage());
        
        ______TS("Failure case: Courses not passed in, instructor feedback edit page");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, rr.getStatusMessage());

        
        ______TS("Failure case: copying from course with insufficient permission");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, "FeedbackEditCopy.CS2107",
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        
        try {
            rr = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch(UnauthorizedAccessException uae) {
            expectedString = "Course [FeedbackEditCopy.CS2107] is not accessible to instructor "
                             + "[tmms.instr@course.tmt] for privilege [canmodifysession]"; 
            assertEquals(expectedString, uae.getMessage());
        }
        
        ______TS("Failure case: copying to course with insufficient permission");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, "FeedbackEditCopy.CS2107",
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        
        try {
            rr = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch(UnauthorizedAccessException uae) {
            expectedString = "Course [FeedbackEditCopy.CS2107] is not accessible to instructor "
                             + "[tmms.instr@course.tmt] for privilege [canmodifysession]"; 
            assertEquals(expectedString, uae.getMessage());
        }
        
        ______TS("Failure case: copying non-existing fs");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non.existing.fs",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        
        try {
            rr = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch(UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         uae.getMessage());
        }
        
        ______TS("Failure case: copying to non-existing course");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, "non.existing.course",
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        
        try {
            rr = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch(UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent instructor entity",
                         uae.getMessage());
        }
        
        ______TS("Failure case: course already has feedback session with same name, instructor home page");
        
        CourseAttributes course6 = dataBundle.courses.get("course6");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_HOME_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104."; 
        assertEquals(expectedString, rr.getStatusMessage());
        
        ______TS("Failure case: course already has feedback session with same name, instructor feedbacks page");
         
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACKS_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104."; 
        assertEquals(expectedString, rr.getStatusMessage());
        
        ______TS("Failure case: course already has feedback session with same name, instructor feedback copy page");
        
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_COPY
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104."; 
        assertEquals(expectedString, rr.getStatusMessage());
        
        ______TS("Failure case: course already has feedback session with same name, instructor feedback edit page");
        
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session";
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104."; 
        assertEquals(expectedString, rr.getStatusMessage());
        
        ______TS("Failure case: empty name, instructor home page");
        
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_HOME_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session"; 
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "\"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                         + "The value of feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, rr.getStatusMessage());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : \"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                + "The value of feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        ______TS("Failure case: empty name, instructor feedbacks page");
        
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACKS_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session"; 
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "\"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                         + "The value of feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, rr.getStatusMessage());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : \"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                + "The value of feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        ______TS("Failure case: empty name, instructor feedback copy page");
        
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_COPY
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session"; 
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "\"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                         + "The value of feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, rr.getStatusMessage());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : \"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                + "The value of feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        ______TS("Failure case: empty name, instructor feedback edit page");
        
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.id,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.CURRENT_PAGE, Const.PageNames.INSTRUCTOR_FEEDBACK_EDIT_PAGE
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                         + "?error=true"
                         + "&user=" + instructor.googleId
                         + "&courseid=" + instructor.courseId
                         + "&fsname=First+Session"; 
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        expectedString = "\"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                         + "The value of feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, rr.getStatusMessage());
        
        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : \"\" is not acceptable to TEAMMATES as feedback session name because it is empty. "
                + "The value of feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        assertEquals(expectedString, a.getLogMessage());
        
        
        ______TS("Successful case");
        
        CourseAttributes course7 = dataBundle.courses.get("course7");
        String copiedCourseName = "Session with valid name";
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.id,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, copiedCourseName,
                Const.ParamsNames.COPIED_COURSES_ID, course6.id,
                Const.ParamsNames.COPIED_COURSES_ID, course7.id
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        expectedString = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                         + "?error=false&user=" + instructor.googleId;
        assertEquals(expectedString, rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED, rr.getStatusMessage());
        
        expectedString = "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||"
                         + "true|||Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||"
                         + "tmms.instr@gmail.tmt|||Copying to multiple feedback sessions.<br>"
                         + "New Feedback Session <span class=\"bold\">(Session with valid name)</span> "
                         + "for Courses: <br>FeedbackEditCopy.CS2103R,FeedbackEditCopy.CS2102<br>"
                         + "<span class=\"bold\">From:</span> Sun Apr 01 23:59:00 UTC 2012<span class=\"bold\"> "
                         + "to</span> Sat Apr 30 23:59:00 UTC 2016<br><span class=\"bold\">Session visible from:</span> "
                         + "Sun Apr 01 23:59:00 UTC 2012<br><span class=\"bold\">Results visible from:</span> "
                         + "Sun May 01 23:59:00 UTC 2016<br><br><span class=\"bold\">Instructions:</span> "
                         + "<Text: Instructions for first session><br>Copied from <span class=\"bold\">(First Session)</span> "
                         + "for Course <span class=\"bold\">[FeedbackEditCopy.CS2104]</span> created.<br>|||"
                         + "/page/instructorFeedbackEditCopy";
        assertEquals(expectedString, a.getLogMessage());
        
    }
    
    private InstructorFeedbackEditCopyAction getAction(String... params) throws Exception {
        return (InstructorFeedbackEditCopyAction) gaeSimulation.getActionObject(uri, params);
    }
}
