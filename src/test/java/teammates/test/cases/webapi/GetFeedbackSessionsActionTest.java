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

    private List<FeedbackSessionAttributes> sessionsInCourse1;
    private List<FeedbackSessionAttributes> sessionsInCourse2;

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
        sessionsInCourse1 = new ArrayList<>();
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("session2InCourse1"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("gracePeriodSession"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("closedSession"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("empty.session"));
        sessionsInCourse1.add(typicalBundle.feedbackSessions.get("awaiting.session"));

        sessionsInCourse2 = new ArrayList<>();
        sessionsInCourse2.add(typicalBundle.feedbackSessions.get("session1InCourse2"));
        sessionsInCourse2.add(typicalBundle.feedbackSessions.get("session2InCourse2"));

        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        session1InCourse1.setDeletedTime(Instant.now());

        // Make student2InCourse2 and instructor1OfCourse1 belong to the same account.
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student2InCourse2.googleId = instructor1OfCourse1.getGoogleId();

        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    protected void testExecute() throws Exception {
        // see individual tests
    }

    @Test
    protected void testExecute_asInstructorWithCourseId_shouldReturnAllSessionsForCourse() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.googleId);

        // The presence of IS_IN_RECYCLE_BIN flag is just used to indicate perform this request as instructor.
        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, instructor2OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(instructor2OfCourse1.getCourseId(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessionsInCourse1.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagTrue_shouldReturnAllSoftDeletedSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor2OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "true",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
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
    protected void testExecute_asInstructorWithRecycleBinFlagFalse_shouldReturnAllSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(instructor2OfCourse1.getCourseId(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessionsInCourse1.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_instructorAsStudent_shouldReturnAllSessionsForStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        loginAsStudentInstructor(instructor1OfCourse1.googleId);
        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(2, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(student2InCourse2.getCourse(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessionsInCourse2.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_instructorAsStudentWithCourseId_shouldReturnAllSessionsForCourseOfStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        loginAsStudentInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(2, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(student2InCourse2.getCourse(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessionsInCourse2.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_instructorAsStudentWithInvalidCourseId_shouldReturnEmptyList() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(0, fsData.getFeedbackSessions().size());
    }

    @Test
    protected void testExecute_asStudentWithCourseId_shouldReturnAllSessionsForCourse() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(student1InCourse1.getCourse(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessionsInCourse1.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_asStudent_shouldReturnAllSessionsForAccount() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        for (FeedbackSessionData sessionData : fsData.getFeedbackSessions()) {
            assertEquals(student1InCourse1.getCourse(), sessionData.getCourseId());
            assertNull(sessionData.getDeletedAtTimestamp());
            assertTrue(sessionsInCourse1.stream().anyMatch(session
                    -> session.getFeedbackSessionName().equals(sessionData.getFeedbackSessionName())));
        }
    }

    @Test
    protected void testExecute_noEntityType_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.googleId);

        verifyHttpParameterFailure();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsStudent(student1InCourse1.googleId);

        ______TS("student can access");
        String[] studentEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(studentEntityParam);

        ______TS("student of the same course can access");
        loginAsStudent(student1InCourse2.googleId);
        String[] courseParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(courseParam);

        ______TS("Student of another course cannot access");
        loginAsStudent(student1InCourse1.googleId);
        verifyCannotAccess(courseParam);

        ______TS("instructor can access");
        loginAsInstructor(instructor1OfCourse2.googleId);

        String[] instructorParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(instructorParam);

        ______TS("instructor of the same course can access");
        String[] instructorAndCourseIdParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyCanAccess(instructorAndCourseIdParam);

        ______TS("instructor of another course cannot access");
        loginAsInstructor(instructor2OfCourse1.googleId);
        verifyCannotAccess(instructorAndCourseIdParam);

        ______TS("instructor as student can access");
        loginAsStudentInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(studentEntityParam);

        ______TS("instructor as student can access for course");
        loginAsStudentInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(courseParam);

        String[] adminEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyInaccessibleForAdmin(adminEntityParam);
        verifyInaccessibleForUnregisteredUsers(studentEntityParam);
        verifyInaccessibleWithoutLogin();
    }
}
