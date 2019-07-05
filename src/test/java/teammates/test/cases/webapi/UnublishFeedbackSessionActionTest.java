package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.UnpublishFeedbackSessionAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link UnpublishFeedbackSessionAction}.
 */
public class UnublishFeedbackSessionActionTest extends BaseActionTest<UnpublishFeedbackSessionAction> {

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
    protected void testExecute() throws Exception {
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
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        // session is unpublished
        assertFalse(logic.getFeedbackSession(sessionPublishedInCourse1.getFeedbackSessionName(),
                typicalCourse1.getId()).isPublished());

        // sent unpublish email task is added
        assertEquals(1, a.getTaskQueuer().getTasksAdded().size());

        ______TS("Failed case, session is not published yet");

        assertFalse(session1InCourse1.isPublished());
        String[] failedParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        a = getAction(failedParams);
        r = getJsonResult(a);
        MessageOutput out = (MessageOutput) r.getOutput();

        assertEquals(out.getMessage(),
                "Error unpublishing feedback session: Session has already been unpublished.");
        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("non-existent course");

        String[] nonExistParams = new String[] {
                Const.ParamsNames.COURSE_ID, "abcRandomCourseId",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        verifyCannotAccess(nonExistParams);

        ______TS("non-existent feedback session");

        nonExistParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "abcRandomSession",
        };

        verifyCannotAccess(nonExistParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session1InCourse1.getFeedbackSessionName(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(params);

        ______TS("only instructors of the same course can access");

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
    }

}
