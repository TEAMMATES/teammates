package teammates.test.cases.action;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.InstructorFeedbackRestoreAllSoftDeletedSessionsAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackRestoreAllSoftDeletedSessionsAction}.
 */
public class InstructorFeedbackRestoreAllSoftDeletedSessionsActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_SOFT_DELETED_SESSION_RESTORE_ALL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes session2InCourse3 = typicalBundle.feedbackSessions.get("session2InCourse3");

        ______TS("Typical case, restore all sessions from Recycle Bin, without privilege");

        InstructorAttributes instructor2OfCourse3 = typicalBundle.instructors.get("instructor2OfCourse3");
        gaeSimulation.loginAsInstructor(instructor2OfCourse3.googleId);
        List<FeedbackSessionAttributes> existingFsList = fsDb
                .getFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        assertEquals(1, existingFsList.size());
        List<FeedbackSessionAttributes> softDeletedFsList = fsDb
                .getSoftDeletedFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        assertEquals(1, softDeletedFsList.size());

        InstructorFeedbackRestoreAllSoftDeletedSessionsAction restoreAllAction = getAction();
        try {
            getRedirectResult(restoreAllAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [Second feedback session] is not accessible to instructor "
                    + "[instructor2@course3.tmt] for privilege [canmodifysession]", e.getMessage());
        }

        existingFsList = fsDb.getFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        assertEquals(1, existingFsList.size());
        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        assertEquals(1, softDeletedFsList.size());

        ______TS("Typical case, restore all sessions from Recycle Bin, with privilege for only some sessions");

        InstructorAttributes instructor1OfCourse3 = typicalBundle.instructors.get("instructor1OfCourse3");
        FeedbackSessionAttributes session1InCourse4 = typicalBundle.feedbackSessions.get("session1InCourse4");
        gaeSimulation.loginAsInstructor(instructor1OfCourse3.googleId);
        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        softDeletedFsList.addAll(fsDb.getSoftDeletedFeedbackSessionsForCourse(session1InCourse4.getCourseId()));
        assertEquals(2, softDeletedFsList.size());

        try {
            restoreAllAction = getAction();
            getRedirectResult(restoreAllAction);
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor "
                    + "[instructor1@course3.tmt] for privilege [canmodifysession]", e.getMessage());
        }

        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        softDeletedFsList.addAll(fsDb.getSoftDeletedFeedbackSessionsForCourse(session1InCourse4.getCourseId()));
        assertEquals(2, softDeletedFsList.size());

        ______TS("Typical case, restore all sessions from Recycle Bin, with privilege");

        gaeSimulation.loginAsInstructor(instructor1OfCourse3.googleId);
        CourseAttributes typicalCourse3 = typicalBundle.courses.get("typicalCourse3");
        CoursesLogic.inst().restoreCourseFromRecycleBin(typicalCourse3.getId());
        InstructorAttributes instructor1OfCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");
        instructor1OfCourse4.privileges.updatePrivilege("canmodifysession", true);
        InstructorsLogic.inst().updateInstructorByGoogleId(instructor1OfCourse4.googleId, instructor1OfCourse4);

        restoreAllAction = getAction();
        RedirectResult r = getRedirectResult(restoreAllAction);

        assertNotNull(fsDb.getFeedbackSession(session2InCourse3.getCourseId(), session2InCourse3.getFeedbackSessionName()));
        assertNotNull(fsDb.getFeedbackSession(session1InCourse4.getCourseId(), session1InCourse4.getFeedbackSessionName()));
        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE,
                        false,
                        "idOfInstructor1OfCourse3"),
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_ALL_RESTORED, r.getStatusMessage());
        assertFalse(r.isError);
        existingFsList = fsDb.getFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        existingFsList.addAll(fsDb.getFeedbackSessionsForCourse(session1InCourse4.getCourseId()));
        assertEquals(3, existingFsList.size());
        softDeletedFsList = fsDb.getSoftDeletedFeedbackSessionsForCourse(session2InCourse3.getCourseId());
        existingFsList.addAll(fsDb.getFeedbackSessionsForCourse(session1InCourse4.getCourseId()));
        assertEquals(0, softDeletedFsList.size());
    }

    @Override
    protected InstructorFeedbackRestoreAllSoftDeletedSessionsAction getAction(String... params) {
        return (InstructorFeedbackRestoreAllSoftDeletedSessionsAction) gaeSimulation.getActionObject(getActionUri(), params);
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
