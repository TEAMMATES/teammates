package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackRestoreAllRecoverySessionsAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackRestoreAllRecoverySessionsAction}.
 */
public class InstructorFeedbackRestoreAllRecoverySessionsActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RECOVERY_SESSION_RESTORE_ALL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session2InCourse3");

        ______TS("Typical case, restore all sessions from Recycle Bin, without privilege");
        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        gaeSimulation.loginAsInstructor(instructor2OfCourse3.googleId);

        assertNotNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertTrue(fs.isSessionDeleted());

        InstructorFeedbackRestoreAllRecoverySessionsAction a = getAction();
        try {
            getRedirectResult(a);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [Second feedback session] is not accessible to instructor "
                    + "[instructor2@course3.tmt] for privilege [canmodifysession]", e.getMessage());
        }

        List<FeedbackSessionAttributes> existingFsList = FeedbackSessionsLogic.inst()
                .getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        List<FeedbackSessionAttributes> recoveryFsList = fsDb.getRecoveryFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, recoveryFsList.size());

        ______TS("Typical case, restore all sessions from Recycle Bin, with privilege");
        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        gaeSimulation.loginAsInstructor(instructor1OfCourse3.googleId);

        assertNotNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertTrue(fs.isSessionDeleted());

        a = getAction();
        RedirectResult r = getRedirectResult(a);

        assertNotNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE,
                        false,
                        "idOfInstructor1OfCourse3"),
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ALL_RESTORED, r.getStatusMessage());
        assertFalse(r.isError);

        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(2, existingFsList.size());
        recoveryFsList = fsDb.getRecoveryFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(0, recoveryFsList.size());
    }

    @Override
    protected InstructorFeedbackRestoreAllRecoverySessionsAction getAction(String... params) {
        return (InstructorFeedbackRestoreAllRecoverySessionsAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session2InCourse3");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
    }
}
