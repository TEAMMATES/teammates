package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackEditCopyAction;
import teammates.ui.pagedata.InstructorFeedbackEditCopyData;

/**
 * SUT: {@link InstructorFeedbackEditCopyAction}.
 */
public class InstructorFeedbackEditCopyActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        dataBundle = loadDataBundle("/InstructorFeedbackEditCopyTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name"
        };

        InstructorFeedbackEditCopyAction a = getAction(params);
        AjaxResult ajaxResult = getAjaxResult(a);
        InstructorFeedbackEditCopyData editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, editCopyData.errorMessage);

        ______TS("Failure case: Courses not passed in, instructor feedbacks page");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name"
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);

        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, editCopyData.errorMessage);

        ______TS("Failure case: Courses not passed in, instructor feedback copy page");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name"
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, editCopyData.errorMessage);

        ______TS("Failure case: Courses not passed in, instructor feedback edit page");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name"
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, editCopyData.errorMessage);

        ______TS("Failure case: copying non-existing fs");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "non.existing.fs",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId()
        };

        a = getAction(params);

        try {
            ajaxResult = getAjaxResult(a);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                         uae.getMessage());
        }

        ______TS("Failure case: copying to non-existing course");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, "non.existing.course"
        };

        a = getAction(params);

        try {
            ajaxResult = getAjaxResult(a);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent instructor entity",
                         uae.getMessage());
        }

        ______TS("Failure case: course already has feedback session with same name, instructor home page");

        CourseAttributes course6 = dataBundle.courses.get("course6");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104.";
        assertEquals(expectedString, editCopyData.errorMessage);

        ______TS("Failure case: course already has feedback session with same name, instructor feedbacks page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104.";
        assertEquals(expectedString, editCopyData.errorMessage);

        ______TS("Failure case: course already has feedback session with same name, instructor feedback copy page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104.";
        assertEquals(expectedString, editCopyData.errorMessage);

        ______TS("Failure case: course already has feedback session with same name, instructor feedback edit page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "A feedback session with the name \"First Session\" already exists in "
                         + "the following course(s): FeedbackEditCopy.CS2104.";
        assertEquals(expectedString, editCopyData.errorMessage);

        ______TS("Failure case: empty name, instructor home page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "The field 'feedback session name' is empty. "
                         + "The value of a/an feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, editCopyData.errorMessage);

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : The field 'feedback session name' is empty. The value of "
                + "a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Failure case: empty name, instructor feedbacks page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "The field 'feedback session name' is empty. "
                         + "The value of a/an feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, editCopyData.errorMessage);

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : The field 'feedback session name' is empty. The value of "
                + "a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Failure case: empty name, instructor feedback copy page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "The field 'feedback session name' is empty. "
                         + "The value of a/an feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, editCopyData.errorMessage);

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : The field 'feedback session name' is empty. The value of "
                + "a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Failure case: empty name, instructor feedback edit page");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId()
        };

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        assertEquals("", editCopyData.redirectUrl);

        expectedString = "The field 'feedback session name' is empty. "
                         + "The value of a/an feedback session name should be no longer than 38 characters. "
                         + "It should not be empty.";
        assertEquals(expectedString, editCopyData.errorMessage);

        expectedString =
                "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||true|||"
                + "Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||tmms.instr@gmail.tmt|||"
                + "Servlet Action Failure : The field 'feedback session name' is empty. The value of "
                + "a/an feedback session name should be no longer than 38 characters. "
                + "It should not be empty.|||/page/instructorFeedbackEditCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        ______TS("Successful case");

        CourseAttributes course7 = dataBundle.courses.get("course7");
        String feedbackSessionName = "First Session";
        String newFeedbackSessionName = "Session with valid name";
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, newFeedbackSessionName,
                Const.ParamsNames.COPIED_COURSES_ID, course6.getId(),
                Const.ParamsNames.COPIED_COURSES_ID, course7.getId()
        };

        assertNotEquals(course6.getTimeZone().getId(), course.getTimeZone().getId());
        assertNotEquals(course7.getTimeZone().getId(), course.getTimeZone().getId());
        assertNotEquals(course6.getTimeZone().getId(), course7.getTimeZone().getId());

        String sessionTimeZone =
                FeedbackSessionsLogic.inst().getFeedbackSession(feedbackSessionName, course.getId()).getTimeZone().getId();
        assertEquals(course.getTimeZone().getId(), sessionTimeZone);

        a = getAction(params);
        ajaxResult = getAjaxResult(a);
        editCopyData = (InstructorFeedbackEditCopyData) ajaxResult.data;

        expectedString = getPageResultDestination(
                                 Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE, false, instructor.googleId);
        assertEquals(expectedString, editCopyData.redirectUrl);

        expectedString = "TEAMMATESLOG|||instructorFeedbackEditCopy|||instructorFeedbackEditCopy|||"
                         + "true|||Instructor|||Instructor 2|||FeedbackEditCopyinstructor2|||"
                         + "tmms.instr@gmail.tmt|||Copying to multiple feedback sessions.<br>"
                         + "New Feedback Session <span class=\"bold\">(Session with valid name)</span> "
                         + "for Courses: <br>FeedbackEditCopy.CS2103R,FeedbackEditCopy.CS2102<br>"
                         + "<span class=\"bold\">From:</span> 2012-04-01T21:59:00Z<span class=\"bold\"> "
                         + "to</span> 2026-04-30T21:59:00Z<br><span class=\"bold\">Session visible from:</span> "
                         + "2012-04-01T21:59:00Z<br><span class=\"bold\">Results visible from:</span> "
                         + "2026-05-01T21:59:00Z<br><br><span class=\"bold\">Instructions:</span> "
                         + "<Text: Instructions for first session><br>Copied from "
                         + "<span class=\"bold\">(First Session)</span> "
                         + "for Course <span class=\"bold\">[FeedbackEditCopy.CS2104]</span> created.<br>|||"
                         + "/page/instructorFeedbackEditCopy";
        AssertHelper.assertLogMessageEquals(expectedString, a.getLogMessage());

        sessionTimeZone = FeedbackSessionsLogic.inst().getFeedbackSession(newFeedbackSessionName,
                course6.getId()).getTimeZone().getId();
        assertEquals(course6.getTimeZone().getId(), sessionTimeZone);

        sessionTimeZone = FeedbackSessionsLogic.inst().getFeedbackSession(newFeedbackSessionName,
                course7.getId()).getTimeZone().getId();
        assertEquals(course7.getTimeZone().getId(), sessionTimeZone);
    }

    @Override
    protected InstructorFeedbackEditCopyAction getAction(String... params) {
        return (InstructorFeedbackEditCopyAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session",
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "Session with valid name",
                Const.ParamsNames.COPIED_COURSES_ID, "idOfTypicalCourse2",
                Const.ParamsNames.COPIED_COURSES_ID, "idOfSampleCourse-demo"
        };

        verifyUnaccessibleWithoutViewSessionInSectionsPrivilege(params);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("openSession");
        CourseAttributes course = dataBundle.courses.get("course");

        ______TS("Failure case: copying from course with insufficient permission");
        InstructorAttributes instructor = dataBundle.instructors.get("teammates.test.instructor3");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, course.getId()
        };
        InstructorFeedbackEditCopyAction a = getAction(params);
        try {
            a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            String expectedString = "Course [FeedbackEditCopy.CS2104] is not accessible to instructor "
                             + "[tmms.instr.cust@course.tmt] for privilege [canviewsessioninsection]";
            assertEquals(expectedString, uae.getMessage());
        }

        gaeSimulation.logoutUser();

        ______TS("Failure case: copying to course with insufficient permission");
        instructor = dataBundle.instructors.get("teammates.test.instructor2");
        instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, "FeedbackEditCopy.CS2107"
        };

        a = getAction(params);

        try {
            a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException uae) {
            String expectedString = "Course [FeedbackEditCopy.CS2107] is not accessible to instructor "
                             + "[tmms.instr@course.tmt] for privilege [canmodifysession]";
            assertEquals(expectedString, uae.getMessage());
        }
    }
}
