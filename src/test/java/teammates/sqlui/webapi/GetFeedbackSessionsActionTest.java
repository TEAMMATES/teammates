package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.webapi.GetFeedbackSessionsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest extends BaseActionTest<GetFeedbackSessionsAction> {

    private Student student1;
    private List<FeedbackSession> sessionsInCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        Course course1 = generateCourse1();
        Instructor instructor1 = generateInstructor1InCourse(course1);
        student1 = generateStudent1InCourse(course1);
        sessionsInCourse1 = new ArrayList<>();
        sessionsInCourse1.add(generateSession1InCourse(course1, "feedbacksession-1"));
        sessionsInCourse1.add(generateSession1InCourse(course1, "feedbacksession-2"));

        when(mockLogic.getFeedbackSessionsForCourse(course1.getId())).thenReturn(sessionsInCourse1);
        when(mockLogic.getStudentsByGoogleId(student1.getAccount().getGoogleId())).thenReturn(List.of(student1));
        when(mockLogic.getInstructorByGoogleId(
                instructor1.getAccount().getGoogleId(), course1.getId())).thenReturn(instructor1);
        for (FeedbackSession session : sessionsInCourse1) {
            when(mockLogic.getDeadlineForUser(session, student1)).thenReturn(session.getEndTime());
        }
    }

    @Test
    protected void textExecute() {
        loginAsStudent(student1.getAccount().getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);

        JsonResult r = getJsonResult(a);
        FeedbackSessionsData response = (FeedbackSessionsData) r.getOutput();

        assertEquals(2, response.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(response, sessionsInCourse1);

        logoutUser();
    }

    @Test
    protected void testExecute_studentWithDeadlineExtension_closedSessionShownAsOpen() {
        FeedbackSession closedSession = generateClosedFeedbackSessionInCourse(student1.getCourse(), "closed-session");

        Instant extendedDeadline = Instant.parse("2028-01-01T00:00:00Z");

        when(mockLogic.getFeedbackSessionsForCourse(student1.getCourseId()))
                .thenReturn(List.of(closedSession));
        when(mockLogic.getDeadlineForUser(closedSession, student1))
                .thenReturn(extendedDeadline);

        loginAsStudent(student1.getAccount().getGoogleId());

        String[] submissionParams = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);
        FeedbackSessionsData response = (FeedbackSessionsData) r.getOutput();

        assertEquals(1, response.getFeedbackSessions().size());
        FeedbackSessionData sessionData = response.getFeedbackSessions().get(0);
        assertEquals(FeedbackSessionSubmissionStatus.OPEN, sessionData.getSubmissionStatus());

        logoutUser();
    }

    @Test
    protected void testExecute_instructorWithDeadlineExtension_closedSessionShownAsOpen() {
        Course course = student1.getCourse();
        Instructor instructor = generateInstructor1InCourse(course);
        FeedbackSession closedSession = generateClosedFeedbackSessionInCourse(course, "closed-session");

        Instant extendedDeadline = Instant.parse("2028-01-01T00:00:00Z");

        when(mockLogic.getInstructorsForGoogleId(instructor.getAccount().getGoogleId()))
                .thenReturn(List.of(instructor));
        when(mockLogic.getFeedbackSessionsForInstructors(List.of(instructor)))
                .thenReturn(List.of(closedSession));
        when(mockLogic.getDeadlineForUser(closedSession, instructor))
                .thenReturn(extendedDeadline);

        loginAsInstructor(instructor.getAccount().getGoogleId());

        String[] submissionParams = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);
        FeedbackSessionsData response = (FeedbackSessionsData) r.getOutput();

        assertEquals(1, response.getFeedbackSessions().size());
        FeedbackSessionData sessionData = response.getFeedbackSessions().get(0);
        assertEquals(FeedbackSessionSubmissionStatus.OPEN, sessionData.getSubmissionStatus());

        logoutUser();
    }

    @Test
    protected void testExecute_instructorWithoutDeadlineExtension_closedSessionShownAsClosed() {
        Course course = student1.getCourse();
        Instructor instructor = generateInstructor1InCourse(course);
        FeedbackSession closedSession = generateClosedFeedbackSessionInCourse(course, "closed-session");

        when(mockLogic.getInstructorsForGoogleId(instructor.getAccount().getGoogleId()))
                .thenReturn(List.of(instructor));
        when(mockLogic.getFeedbackSessionsForInstructors(List.of(instructor)))
                .thenReturn(List.of(closedSession));
        when(mockLogic.getDeadlineForUser(closedSession, instructor))
                .thenReturn(closedSession.getEndTime());

        loginAsInstructor(instructor.getAccount().getGoogleId());

        String[] submissionParams = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);
        FeedbackSessionsData response = (FeedbackSessionsData) r.getOutput();

        assertEquals(1, response.getFeedbackSessions().size());
        FeedbackSessionData sessionData = response.getFeedbackSessions().get(0);
        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, sessionData.getSubmissionStatus());

        logoutUser();
    }

    @Test
    protected void testExecute_studentWithoutDeadlineExtension_closedSessionShownAsClosed() {
        FeedbackSession closedSession = generateClosedFeedbackSessionInCourse(student1.getCourse(), "closed-session");

        when(mockLogic.getFeedbackSessionsForCourse(student1.getCourseId()))
                .thenReturn(List.of(closedSession));
        when(mockLogic.getDeadlineForUser(closedSession, student1))
                .thenReturn(closedSession.getEndTime());

        loginAsStudent(student1.getAccount().getGoogleId());

        String[] submissionParams = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);
        FeedbackSessionsData response = (FeedbackSessionsData) r.getOutput();

        assertEquals(1, response.getFeedbackSessions().size());
        FeedbackSessionData sessionData = response.getFeedbackSessions().get(0);
        assertEquals(FeedbackSessionSubmissionStatus.CLOSED, sessionData.getSubmissionStatus());

        logoutUser();
    }

    private void assertAllStudentSessionsMatch(
            FeedbackSessionsData sessionsData, List<FeedbackSession> expectedSessions) {

        assertEquals(sessionsData.getFeedbackSessions().size(), expectedSessions.size());
        for (FeedbackSessionData sessionData : sessionsData.getFeedbackSessions()) {
            List<FeedbackSession> matchedSessions =
                    expectedSessions.stream().filter(session -> session.getName().equals(
                            sessionData.getFeedbackSessionName())
                            && session.getCourseId().equals(sessionData.getCourseId())).collect(Collectors.toList());

            assertEquals(1, matchedSessions.size());
            FeedbackSession matchedSession = matchedSessions.get(0);
            assertPartialInformationMatch(sessionData, matchedSession);
            assertInformationHiddenForStudent(sessionData);
        }
    }

    private void assertPartialInformationMatch(FeedbackSessionData data, FeedbackSession expectedSession) {
        String timeZone = expectedSession.getCourse().getTimeZone();
        assertEquals(expectedSession.getCourseId(), data.getCourseId());
        assertEquals(timeZone, data.getTimeZone());
        assertEquals(expectedSession.getName(), data.getFeedbackSessionName());
        assertEquals(expectedSession.getInstructions(), data.getInstructions());
        assertEquals(expectedSession.getStartTime().toEpochMilli(),
                data.getSubmissionStartTimestamp());
        assertEquals(expectedSession.getEndTime().toEpochMilli(),
                data.getSubmissionEndTimestamp());

        if (!expectedSession.isVisible()) {
            assertEquals(FeedbackSessionSubmissionStatus.NOT_VISIBLE, data.getSubmissionStatus());
        } else if (expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.OPEN, data.getSubmissionStatus());
        } else if (expectedSession.isClosed()) {
            assertEquals(FeedbackSessionSubmissionStatus.CLOSED, data.getSubmissionStatus());
        } else if (expectedSession.isInGracePeriod()) {
            assertEquals(FeedbackSessionSubmissionStatus.GRACE_PERIOD, data.getSubmissionStatus());
        } else if (expectedSession.isVisible() && !expectedSession.isOpened()) {
            assertEquals(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, data.getSubmissionStatus());
        }

        if (expectedSession.getDeletedAt() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedAt().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }

        assertInformationHidden(data);
    }

    private void assertInformationHiddenForStudent(FeedbackSessionData data) {
        assertNull(data.getGracePeriod());
        assertNull(data.getSessionVisibleSetting());
        assertNull(data.getCustomSessionVisibleTimestamp());
        assertNull(data.getResponseVisibleSetting());
        assertNull(data.getCustomResponseVisibleTimestamp());
        assertNull(data.getIsClosingSoonEmailEnabled());
        assertNull(data.getIsPublishedEmailEnabled());
        assertEquals(data.getCreatedAtTimestamp(), 0);
    }

    private void assertInformationHidden(FeedbackSessionData data) {
        assertNull(data.getGracePeriod());
        assertNull(data.getIsClosingSoonEmailEnabled());
        assertNull(data.getIsPublishedEmailEnabled());
        assertEquals(data.getCreatedAtTimestamp(), 0);
    }

    private Course generateCourse1() {
        Course c = new Course("course-1", "Typical Course 1",
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
    }

    private Student generateStudent1InCourse(Course courseStudentIsIn) {
        String email = "student1@gmail.com";
        String name = "student-1";
        String googleId = "student-1";
        Student s = new Student(courseStudentIsIn, name, email, "comment for student-1");
        s.setAccount(new Account(googleId, name, email));
        return s;
    }

    private FeedbackSession generateSession1InCourse(Course course, String name) {
        FeedbackSession fs = new FeedbackSession(name, course,
                "instructor1@gmail.com", "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2027-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2027-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }

    private Instructor generateInstructor1InCourse(Course course) {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null,
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor.setAccount(new Account("instructor-1", instructor.getName(), instructor.getEmail()));
        return instructor;
    }

    private FeedbackSession generateClosedFeedbackSessionInCourse(Course course, String name) {
        FeedbackSession closedSession = new FeedbackSession(
                name, course,
                "instructor1@gmail.com", "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"),
                Instant.parse("2027-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true);
        closedSession.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        closedSession.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return closedSession;
    }
}
