package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackDeleteSoftDeletedSessionAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackDeleteSoftDeletedSessionAction}.
 */
public class InstructorFeedbackDeleteSoftDeletedSessionActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_DELETE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session2InCourse3");

        ______TS("Typical case, delete 1 session from Recycle Bin, without privilege");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };
        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        gaeSimulation.loginAsInstructor(instructor2OfCourse3.googleId);
        List<FeedbackSessionAttributes> existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        List<FeedbackSessionAttributes> softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, softDeletedFsList.size());

        InstructorFeedbackDeleteSoftDeletedSessionAction deleteAction = getAction(submissionParams);
        try {
            getRedirectResult(deleteAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [Second feedback session] is not accessible to instructor "
                    + "[instructor2@course3.tmt] for privilege [canmodifysession]", e.getMessage());
        }

        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, softDeletedFsList.size());

        ______TS("Typical case, delete 1 session from Recycle Bin, with privilege");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        gaeSimulation.loginAsInstructor(instructor1OfCourse3.googleId);
        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, softDeletedFsList.size());

        deleteAction = getAction(submissionParams);
        RedirectResult r = getRedirectResult(deleteAction);

        assertNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE,
                        false,
                        "idOfInstructor1OfCourse3"),
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_DELETED, r.getStatusMessage());
        assertFalse(r.isError);
        existingFsList = fsDb.getFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(1, existingFsList.size());
        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(fs.getCourseId());
        assertEquals(0, softDeletedFsList.size());
    }

    @Override
    protected InstructorFeedbackDeleteSoftDeletedSessionAction getAction(String... params) {
        return (InstructorFeedbackDeleteSoftDeletedSessionAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse3");

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
