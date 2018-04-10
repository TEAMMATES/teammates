package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackSessionsPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackSessionsPageData;

/**
 * SUT: {@link InstructorFeedbackSessionsPageAction}.
 */
public class InstructorFeedbackSessionsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String instructorId = typicalBundle.instructors.get("instructor1OfCourse1").googleId;
        String adminUserId = "admin.user";
        String[] submissionParams = new String[] {Const.ParamsNames.IS_USING_AJAX, "true"};

        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Typical case, 2 courses");
        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course", "UTC");
        gaeSimulation.loginAsInstructor(instructorId);
        InstructorFeedbackSessionsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, false, "idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorFeedbackSessionsPageData pageData = (InstructorFeedbackSessionsPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.getNewFsForm().getCourses().size());
        assertEquals(6, pageData.getFsList().getExistingFeedbackSessions().size());
        assertEquals("", pageData.getNewFsForm().getFsName());
        assertNull(pageData.getNewFsForm().getCourseId());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackSessionsPage|||instructorFeedbackSessionsPage|||"
                + "true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Number of feedback sessions: 6|||/page/instructorFeedbackSessionsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("0 sessions");

        FeedbackSessionsLogic.inst().deleteFeedbackSessionsForCourseCascade(instructor1ofCourse1.courseId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId,
                Const.ParamsNames.IS_USING_AJAX, "true"
        };
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getShowPageResult(a);

        assertEquals(getPageResultDestination(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, false,
                     "idOfInstructor1OfCourse1"), r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EMPTY, r.getStatusMessage());
        assertFalse(r.isError);

        pageData = (InstructorFeedbackSessionsPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.getNewFsForm().getCourses().size());
        assertEquals(0, pageData.getFsList().getExistingFeedbackSessions().size());
        assertEquals("", pageData.getNewFsForm().getFsName());
        assertEquals(instructor1ofCourse1.courseId, pageData.getNewFsForm().getCourseId());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackSessionsPage|||instructorFeedbackSessionsPage|||"
                + "true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Number of feedback sessions: 0|||/page/instructorFeedbackSessionsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Masquerade mode, 0 courses");

        gaeSimulation.loginAsAdmin(adminUserId);

        CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("new-course");

        submissionParams = new String[] {Const.ParamsNames.IS_USING_AJAX, "true"};
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = getShowPageResult(a);

        assertEquals(getPageResultDestination(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SESSIONS, false,
                     "idOfInstructor1OfCourse1"), r.getDestinationWithParams());
        assertEquals("You have not created any courses yet, or you have no active courses. "
                     + "Go <a href=\"/page/instructorCoursesPage?user=idOfInstructor1OfCourse1\">here</a> "
                     + "to create or unarchive a course.",
                     r.getStatusMessage());
        assertFalse(r.isError);

        pageData = (InstructorFeedbackSessionsPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(0, pageData.getNewFsForm().getCourses().size());
        assertEquals(0, pageData.getFsList().getExistingFeedbackSessions().size());
        assertEquals("", pageData.getNewFsForm().getFsName());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackSessionsPage|||instructorFeedbackSessionsPage|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Number of feedback sessions: 0|||/page/instructorFeedbackSessionsPage";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, a.getLogMessage(), adminUserId);
    }

    @Override
    protected InstructorFeedbackSessionsPageAction getAction(String... params) {
        return (InstructorFeedbackSessionsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID,
                typicalBundle.instructors.get("instructor1OfCourse1").courseId
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
