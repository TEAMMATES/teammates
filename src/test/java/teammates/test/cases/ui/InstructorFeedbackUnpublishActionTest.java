package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
    private static final boolean PRIVATE = true;
    private static final boolean PUBLIC = false;
    DataBundle dataBundle;
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        makeFeedbackSessionPublished(session); //we have to revert to the closed state
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        makeFeedbackSessionPublished(session); //we have to revert to the closed state
        
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        String[] unpublishParamsForTypicalCases = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        
        ______TS("Success Case 1: unpublish published feedback session");
        
        makeFeedbackSessionPublished(session);
        
        InstructorFeedbackUnpublishAction unpublishAction = getAction(unpublishParamsForTypicalCases);
        RedirectResult result = (RedirectResult) unpublishAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE + 
                "?message=The+feedback+session+has+been+unpublished.&error=false&user=idOfInstructor1OfCourse1"
                , result.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED, result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Failed Case 1: unpublish unpublished feedback session");
        
        makeFeedbackSessionUnpublished(session);
        
        String errorMessage = "";
        unpublishAction = getAction(unpublishParamsForTypicalCases);
        try{
            unpublishAction.executeAndPostProcess();
        } catch(Throwable e){
            assertTrue(e instanceof AssertionError);
            errorMessage = e.getMessage();
        }
        assertEquals(errorMessage, "InvalidParametersException not expected at this point");
        
        makeFeedbackSessionPublished(session);
        
        ______TS("Failed Case 2: unpublish private feedback session");

        makeFeedbackSessionPrivate(session);
        
        errorMessage = "";
        unpublishAction = getAction(unpublishParamsForTypicalCases);
        try{
            unpublishAction.executeAndPostProcess();
        } catch(Throwable e){
            assertTrue(e instanceof AssertionError);
            errorMessage = e.getMessage();
        }
        assertEquals(errorMessage, "InvalidParametersException not expected at this point");
        
        makeFeedbackSessionPublic(session);
    }
    
    private void modifyFeedbackSessionPublishState(FeedbackSessionAttributes session, boolean isPublished) throws Exception {
        session.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        session.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        if(isPublished){
            session.resultsVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-1);
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
    
    private void modifyFeedbackSessionPrivateState(FeedbackSessionAttributes session, boolean isPrivate) throws Exception {
        if(isPrivate){
            session.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            assertTrue(session.isPrivateSession());
        } else {
            session.sessionVisibleFromTime = TimeHelper.getDateOffsetToCurrentTime(-3);
            assertFalse(session.isPrivateSession());
        }
        new FeedbackSessionsDb().updateFeedbackSession(session);
    }
    
    private void makeFeedbackSessionPublic(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPrivateState(session, PUBLIC);
    }
    
    private void makeFeedbackSessionPrivate(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPrivateState(session, PRIVATE);
    }
    
    private InstructorFeedbackUnpublishAction getAction(String[] params){
        return (InstructorFeedbackUnpublishAction) gaeSimulation.getActionObject(uri, params);
    }
}
