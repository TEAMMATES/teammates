package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackUnpublishAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackUnpublishActionTest extends BaseActionTest {
    private static final boolean PUBLISHED = true;
    private static final boolean UNPUBLISHED = false;
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");

        String[] paramsNormal = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID, session.courseId
        };

        ______TS("Typical successful case: session unpublishable");

        makeFeedbackSessionPublished(session);

        InstructorFeedbackUnpublishAction unpublishAction = getAction(paramsNormal);
        RedirectResult result = (RedirectResult) unpublishAction.executeAndPostProcess();

        String expectedDestination = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + "?error=false"
                                     + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED, result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Unsuccessful case 1: params with null course id");

        String errorMessage = "";
        unpublishAction = getAction(paramsWithNullCourseId);

        try {
            unpublishAction.executeAndPostProcess();
        } catch (Throwable e) {
            assertTrue(e instanceof AssertionError);
            errorMessage = e.getMessage();
        }

        assertEquals(Const.StatusCodes.NULL_PARAMETER, errorMessage);

        ______TS("Unsuccessful case 2: params with null feedback session name");

        errorMessage = "";
        unpublishAction = getAction(paramsWithNullFeedbackSessionName);

        try {
            unpublishAction.executeAndPostProcess();
        } catch (Throwable e) {
            assertTrue(e instanceof AssertionError);
            errorMessage = e.getMessage();
        }

        assertEquals(Const.StatusCodes.NULL_PARAMETER, errorMessage);

        ______TS("Unsuccessful case 3: trying to unpublish a session not currently published");

        makeFeedbackSessionUnpublished(session);

        unpublishAction = getAction(paramsNormal);
        result = (RedirectResult) unpublishAction.executeAndPostProcess();

        expectedDestination = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + "?error=true"
                              + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertEquals("Session is already unpublished.", result.getStatusMessage());
        assertTrue(result.isError);

        makeFeedbackSessionPublished(session);
    }

    private void modifyFeedbackSessionPublishState(FeedbackSessionAttributes session, boolean isPublished) throws Exception {
        // startTime < endTime <= resultsVisibleFromTime
        Date startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        Date endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        Date resultsVisibleFromTimeForPublishedSession = TimeHelper.getDateOffsetToCurrentTime(-1);

        session.startTime = startTime;
        session.endTime = endTime;

        if (isPublished) {
            session.resultsVisibleFromTime = resultsVisibleFromTimeForPublishedSession;
            assertTrue(session.isPublished());
        } else {
            session.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
            assertFalse(session.isPublished());
        }

        session.sentPublishedEmail = true;

        new FeedbackSessionsDb().updateFeedbackSession(session);
    }

    private void makeFeedbackSessionPublished(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPublishState(session, PUBLISHED);
    }

    private void makeFeedbackSessionUnpublished(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPublishState(session, UNPUBLISHED);
    }

    private InstructorFeedbackUnpublishAction getAction(String[] params) {
        return (InstructorFeedbackUnpublishAction) gaeSimulation.getActionObject(uri, params);
    }
}
