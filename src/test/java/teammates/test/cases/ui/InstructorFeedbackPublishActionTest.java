package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackPublishAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackPublishActionTest extends BaseActionTest {
    private static final boolean PUBLISHED = true;
    private static final boolean UNPUBLISHED = false;
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        String[] paramsNormal = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session.feedbackSessionName
        };
        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session.feedbackSessionName
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID,
                session.courseId
        };
        
        ______TS("Typical successful case: session publishable");
        
        makeFeedbackSessionUnpublished(session);
        
        InstructorFeedbackPublishAction publishAction = getAction(paramsNormal);
        RedirectResult result = (RedirectResult) publishAction.executeAndPostProcess();
        
        String expectedDestination = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                                     + "?error=false"
                                     + "&user=idOfInstructor1OfCourse1";
        
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_PUBLISHED, result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Unsuccessful case 1: params with null course id");
        
        String errorMessage = "";
        publishAction = getAction(paramsWithNullCourseId);
        
        try {
            publishAction.executeAndPostProcess();
        } catch (Throwable e) {
            assertTrue(e instanceof AssertionError);
            errorMessage = e.getMessage();
        }
        
        assertEquals(Const.StatusCodes.NULL_PARAMETER, errorMessage);
        
        ______TS("Unsuccessful case 2: params with null feedback session name");
        
        errorMessage = "";
        publishAction = getAction(paramsWithNullFeedbackSessionName);
        
        try {
            publishAction.executeAndPostProcess();
        } catch (Throwable e) {
            assertTrue(e instanceof AssertionError);
            errorMessage = e.getMessage();
        }
        
        assertEquals(Const.StatusCodes.NULL_PARAMETER, errorMessage);
        
        ______TS("Unsuccessful case 3: trying to publish a session not currently unpublished");
        
        makeFeedbackSessionPublished(session);
        
        publishAction = getAction(paramsNormal);
        result = (RedirectResult) publishAction.executeAndPostProcess();
        
        expectedDestination = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + "?error=true"
                              + "&user=idOfInstructor1OfCourse1";
        
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertEquals("Session is already published.", result.getStatusMessage());
        assertTrue(result.isError);
        
        makeFeedbackSessionUnpublished(session);
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
        
        session.sentPublishedEmail = false;
        
        new FeedbackSessionsDb().updateFeedbackSession(session);
    }
    
    private void makeFeedbackSessionUnpublished(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPublishState(session, UNPUBLISHED);
    }
    
    private void makeFeedbackSessionPublished(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPublishState(session, PUBLISHED);
    }
    
    private InstructorFeedbackPublishAction getAction(String[] params) {
        return (InstructorFeedbackPublishAction) gaeSimulation.getActionObject(uri, params);
    }
}
