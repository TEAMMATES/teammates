package teammates.test.cases.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackSessionsAction;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.output.FeedbackSessionsData;

/**
 * SUT: {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest extends BaseActionTest<GetFeedbackSessionsAction> {

    private List<FeedbackSessionAttributes> sessions;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        sessions = new ArrayList<>();
        sessions.add(typicalBundle.feedbackSessions.get("session2InCourse1"));
        sessions.add(typicalBundle.feedbackSessions.get("gracePeriodSession"));
        sessions.add(typicalBundle.feedbackSessions.get("closedSession"));
        sessions.add(typicalBundle.feedbackSessions.get("empty.session"));
        sessions.add(typicalBundle.feedbackSessions.get("awaiting.session"));

        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1InCourse1.setDeletedTime(Instant.now());
        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    protected void testExecute() throws Exception {
        // see individual tests
    }

    @Test
    protected void testExecute_asInstructorWithCourseId_shouldReturnAllSessionsForCourse() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(instructor1OfCourse1.getCourseId(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessions.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagTrue_shouldReturnAllSoftDeletedSessionsForInstructor() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "true",
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(1, fsData.getFeedbackSessions().size());
        FeedbackSessionData fs = fsData.getFeedbackSessions().get(0);
        assertNotNull(fs.getDeletedAtTimestamp());
        assertEquals(session1InCourse1.getCourseId(), fs.getCourseId());
        assertEquals(session1InCourse1.getFeedbackSessionName(), fs.getFeedbackSessionName());
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagFalse_shouldReturnAllSoftDeletedSessionsForInstructor() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(instructor1OfCourse1.getCourseId(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessions.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_asInstructorNotEnoughParameters_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_asStudentWithCourseId_shouldReturnAllSessionsForCourse() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(student1InCourse1.getCourse(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessions.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_asStudent_shouldReturnAllSessionsForAccount() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        GetFeedbackSessionsAction a = getAction();
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(student1InCourse1.getCourse(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessions.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        loginAsStudent(student1InCourse1.googleId);
        ______TS("student can access");
        verifyCanAccess();

        ______TS("student of the same course can access");
        loginAsStudent(student1InCourse1.googleId);
        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        verifyCanAccess(submissionParam);

        ______TS("Student of another course cannot access");
        loginAsStudent(student1InCourse2.googleId);
        verifyCannotAccess(submissionParam);

        ______TS("instructor can access");
        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess();

        ______TS("instructor of the same course can access");
        verifyCanAccess(submissionParam);

        ______TS("instructor of another course cannot access");
        loginAsInstructor(instructor1OfCourse2.googleId);
        verifyCannotAccess(submissionParam);

        verifyInaccessibleForAdmin();
        verifyInaccessibleForUnregisteredUsers();
        verifyInaccessibleWithoutLogin();
    }

}
