package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetSessionResultsAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.SessionResultsData;
import teammates.ui.webapi.request.Intent;

/**
 * SUT: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionTest extends BaseActionTest<GetSessionResultsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructorAttributes.getGoogleId());

        ______TS("typical: instructor accesses results of his/her course");

        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetSessionResultsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        SessionResultsData output = (SessionResultsData) r.getOutput();
        SessionResultsData expectedResults =
                new SessionResultsData(logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                accessibleFeedbackSession.getFeedbackSessionName(),
                accessibleFeedbackSession.getCourseId(),
                instructorAttributes.getEmail(),
                1,
                Const.FeedbackSessionResults.QUESTION_SORT_TYPE
        ), instructorAttributes);

        assertTrue(expectedResults.equals(output));

        ______TS("fail: instructor accesses results of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        a = getAction(submissionParams);
        GetSessionResultsAction finalA = a;

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(finalA));

    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams;

        ______TS("accessible for admin");
        verifyAccessibleForAdmin();

        ______TS("accessible for authenticated instructor");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        ______TS("inaccessible for authenticated student when unpublished");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        String[] finalParams = submissionParams;
        assertThrows(UnauthorizedAccessException.class, () -> verifyAccessibleForStudentsOfTheSameCourse(finalParams));

        ______TS("accessible for authenticated student when published");
        FeedbackSessionAttributes publishedFeedbackSession = typicalBundle.feedbackSessions.get("closedSession");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, publishedFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);

        ______TS("invalid intent");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(submissionParams);
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(submissionParams);
    }

}
