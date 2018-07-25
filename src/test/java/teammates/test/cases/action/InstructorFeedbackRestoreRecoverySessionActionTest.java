package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackRestoreRecoverySessionAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackRestoreRecoverySessionAction}.
 */
public class InstructorFeedbackRestoreRecoverySessionActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RECOVERY_SESSION_RESTORE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session2InCourse3");

        ______TS("Typical case, restore 1 session from Recycle Bin, without privilege");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };
        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        gaeSimulation.loginAsInstructor(instructor2OfCourse3.googleId);
        List<FeedbackSessionAttributes> existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        List<FeedbackSessionAttributes> recoveryFsList = fsDb.getRecoveryFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, recoveryFsList.size());

        InstructorFeedbackRestoreRecoverySessionAction restoreAction = getAction(submissionParams);
        try {
            getRedirectResult(restoreAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [Second feedback session] is not accessible to instructor "
                    + "[instructor2@course3.tmt] for privilege [canmodifysession]", e.getMessage());
        }

        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        recoveryFsList = fsDb.getRecoveryFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, recoveryFsList.size());

        ______TS("Typical case, restore 1 session from Recycle Bin, with privilege");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        gaeSimulation.loginAsInstructor(instructor1OfCourse3.googleId);
        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        recoveryFsList = fsDb.getRecoveryFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, recoveryFsList.size());

        restoreAction = getAction(submissionParams);
        RedirectResult r = getRedirectResult(restoreAction);

        assertNotNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE,
                        false,
                        "idOfInstructor1OfCourse3"),
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_RESTORED, r.getStatusMessage());
        assertFalse(r.isError);
        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(2, existingFsList.size());
        recoveryFsList = fsDb.getRecoveryFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(0, recoveryFsList.size());
    }

    @Override
    protected InstructorFeedbackRestoreRecoverySessionAction getAction(String... params) {
        return (InstructorFeedbackRestoreRecoverySessionAction) gaeSimulation.getActionObject(getActionUri(), params);
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
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
    }
}
