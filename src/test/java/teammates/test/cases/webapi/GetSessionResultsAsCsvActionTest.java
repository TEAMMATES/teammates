package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.CsvResult;
import teammates.ui.webapi.action.GetSessionResultsAsCsvAction;

/**
 * SUT: {@link GetSessionResultsAsCsvAction}.
 */
public class GetSessionResultsAsCsvActionTest extends BaseActionTest<GetSessionResultsAsCsvAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT_CSV;
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

        ______TS("typical: instructor downloads results of his/her course");

        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "true",
        };

        GetSessionResultsAsCsvAction a = getAction(submissionParams);
        CsvResult r = getCsvResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        String output = r.getContent();
        String expectedResults = logic.getFeedbackSessionResultSummaryAsCsv(
                accessibleFeedbackSession.getCourseId(),
                accessibleFeedbackSession.getFeedbackSessionName(),
                instructorAttributes.getEmail(),
                true,
                true,
                null
        );

        assertTrue(expectedResults.equals(output));

        ______TS("fail: instructor downloads results of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "true",
        };

        a = getAction(submissionParams);
        GetSessionResultsAsCsvAction finalA = a;

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(finalA));

    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams;

        ______TS("accessible for authenticated instructor");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }

}
