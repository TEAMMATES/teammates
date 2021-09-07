package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link UnpublishFeedbackSessionAction}.
 */
public class UnpublishFeedbackSessionActionTest extends BaseActionTest<UnpublishFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_PUBLISH;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes sessionPublishedInCourse1 = typicalBundle.feedbackSessions.get("closedSession");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, typicalCourse1.getId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                sessionPublishedInCourse1.getFeedbackSessionName());

        ______TS("Typical success case");

        assertTrue(sessionPublishedInCourse1.isPublished());
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionPublishedInCourse1.getFeedbackSessionName(),
        };

        UnpublishFeedbackSessionAction a = getAction(params);
        getJsonResult(a);

        // session is unpublished
        assertFalse(logic.getFeedbackSession(sessionPublishedInCourse1.getFeedbackSessionName(),
                typicalCourse1.getId()).isPublished());

        // sent unpublish email task is added
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());

        ______TS("Typical case, session is not published yet");

        assertFalse(session1InCourse1.isPublished());
        params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        a = getAction(params);
        getJsonResult(a);

        // session is still unpublished
        assertFalse(logic.getFeedbackSession(sessionPublishedInCourse1.getFeedbackSessionName(),
                typicalCourse1.getId()).isPublished());

        // sent unpublish email task should not be added
        verifyNoEmailsSent();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("non-existent course");

        String[] nonExistParams = new String[] {
                Const.ParamsNames.COURSE_ID, "abcRandomCourseId",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        verifyEntityNotFoundAcl(nonExistParams);

        ______TS("non-existent feedback session");

        nonExistParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "abcRandomSession",
        };

        verifyEntityNotFoundAcl(nonExistParams);

        ______TS("accessible only for instructor with ModifySessionPrivilege");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }
}
