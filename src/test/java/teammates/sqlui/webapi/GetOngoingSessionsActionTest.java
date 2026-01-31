package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissionRoleNames;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.OngoingSession;
import teammates.ui.output.OngoingSessionsData;
import teammates.ui.webapi.GetOngoingSessionsAction;
import teammates.ui.webapi.JsonResult;

public class GetOngoingSessionsActionTest extends BaseActionTest<GetOngoingSessionsAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.SESSIONS_ONGOING;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_noParameters_shouldThrowInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_noStartTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant end = Instant.now().plus(Duration.ofDays(1L));
        String[] params = { Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(end.toEpochMilli()) };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_noEndTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant start = Instant.now().minus(Duration.ofDays(1L));
        String[] params = { Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(start.toEpochMilli()) };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nonLongStartTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant end = Instant.now().plus(Duration.ofDays(1L));
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "not_a_long",
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(end.toEpochMilli())
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nonLongEndTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant start = Instant.now().minus(Duration.ofDays(1L));
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(start.toEpochMilli()),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "not_a_long"
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_startTimeParameterBelowMinimum_shouldThrowInvalidHttpParameterException() {
        long minStartTime = Long.MIN_VALUE + 30L * 24L * 60L * 60L * 1000L;
        long belowMinStartTime = minStartTime - 1L;
        Instant end = Instant.now().plus(Duration.ofDays(1L));
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(belowMinStartTime),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(end.toEpochMilli())
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_endTimeParameterAboveMaximum_shouldThrowInvalidHttpParameterException() {
        Instant start = Instant.now().minus(Duration.ofDays(1L));
        long maxEndTime = Long.MAX_VALUE - 30L * 24L * 60L * 60L * 1000L;
        long aboveMaxEndTime = maxEndTime + 1L;
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(start.toEpochMilli()),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(aboveMaxEndTime)
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_endTimeBeforeStartTime_shouldThrowInvalidHttpParameterException() {
        Instant start = Instant.now().minus(Duration.ofDays(1L));
        long endTime = start.toEpochMilli() - 1;
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(start.toEpochMilli()),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(endTime)
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_typicalCase_shouldGetOngoingSessionsDataCorrectly() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant start = now.minus(Duration.ofDays(1L));
        Instant end = now.plus(Duration.ofDays(1L));

        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        Course course2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        Course course3 = new Course("test-id3", "test-name3", "UTC", "UCL");

        when(mockLogic.getCourse(course1.getId())).thenReturn(course1);
        when(mockLogic.getCourse(course2.getId())).thenReturn(course2);
        when(mockLogic.getCourse(course3.getId())).thenReturn(course3);

        Account instructor2Account = new Account("instructor2", "instructor2", "test2@test.com");
        Instructor instructor2 = new Instructor(course1, "instructor2", "test2@test.com", false, "instructor2",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor2.setAccount(instructor2Account);

        Account instructor3Account = new Account("instructor3", "instructor3", "test3@test.com");
        Instructor instructor3 = new Instructor(course2, "instructor3", "test3@test.com", false, "instructor3",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor3.setAccount(instructor3Account);

        Account instructor4Account = new Account("instructor4", "instructor4", "test4@test.com");
        Instructor instructor4 = new Instructor(course3, "instructor4", "test4@test.com", false, "instructor4",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor4.setAccount(instructor4Account);

        when(mockLogic.getInstructorsByCourse(course1.getId())).thenReturn(Collections.singletonList(instructor2));
        when(mockLogic.getInstructorsByCourse(course2.getId())).thenReturn(Collections.singletonList(instructor3));
        when(mockLogic.getInstructorsByCourse(course3.getId())).thenReturn(Collections.singletonList(instructor4));

        FeedbackSession c1Fs2 = new FeedbackSession("name1-2", course1, "test2@test.com", "test-instruction",
                now.plus(Duration.ofHours(12L)), now.plus(Duration.ofDays(7L)),
                now.minus(Duration.ofDays(7L)), now.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);

        FeedbackSession c2Fs1 = new FeedbackSession("name2-1", course2, "test3@test.com", "test-instruction",
                now.minus(Duration.ofHours(12L)), now.plus(Duration.ofHours(12L)),
                now.minus(Duration.ofDays(7L)), now.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);

        FeedbackSession c3Fs1 = new FeedbackSession("name3-1", course3, "test4@test.com", "test-instruction",
                now.minus(Duration.ofDays(7L)), now.minus(Duration.ofHours(12L)),
                now.minus(Duration.ofDays(7L)), now.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);

        List<FeedbackSession> ongoingSqlSessions = List.of(c1Fs2, c2Fs1, c3Fs1);
        when(mockLogic.getOngoingSessions(start, end)).thenReturn(ongoingSqlSessions);
        when(mockDatastoreLogic.getAllOngoingSessions(start, end)).thenReturn(Collections.emptyList());

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(start.toEpochMilli()),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(end.toEpochMilli())
        };

        GetOngoingSessionsAction action = getAction(params);
        JsonResult r = getJsonResult(action);
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(3, response.getTotalOngoingSessions());
        assertEquals(1, response.getTotalOpenSessions());
        assertEquals(1, response.getTotalClosedSessions());
        assertEquals(1, response.getTotalAwaitingSessions());
        assertEquals(3L, response.getTotalInstitutes());

        Map<String, List<OngoingSession>> expectedSessions = new HashMap<>();
        expectedSessions.put("NUS", List.of(new OngoingSession(c1Fs2, instructor2.getGoogleId())));
        expectedSessions.put("MIT", List.of(new OngoingSession(c2Fs1, instructor3.getGoogleId())));
        expectedSessions.put("UCL", List.of(new OngoingSession(c3Fs1, instructor4.getGoogleId())));

        assertEqualSessions(expectedSessions, response.getSessions());
    }

    // Other tests (merged datastore + SQL sessions, course migrated cases) can follow same refactored pattern

    private void assertEqualSessions(Map<String, List<OngoingSession>> expectedSessions,
                                     Map<String, List<OngoingSession>> actualSessions) {
        assertEquals(expectedSessions.keySet(), actualSessions.keySet());
        for (String institute : expectedSessions.keySet()) {
            List<OngoingSession> expectedList = expectedSessions.get(institute);
            List<OngoingSession> actualList = actualSessions.get(institute);
            assertEquals(expectedList.size(), actualList.size());
            for (int i = 0; i < expectedList.size(); i++) {
                OngoingSession expected = expectedList.get(i);
                OngoingSession actual = actualList.get(i);
                assertEquals(expected.getFeedbackSessionName(), actual.getFeedbackSessionName());
                assertEquals(expected.getCourseId(), actual.getCourseId());
                assertEquals(expected.getCreatorEmail(), actual.getCreatorEmail());
                assertEquals(expected.getStartTime(), actual.getStartTime());
                assertEquals(expected.getEndTime(), actual.getEndTime());
                assertEquals(expected.getInstructorHomePageLink(), actual.getInstructorHomePageLink());
                assertEquals(expected.getSessionStatus(), actual.getSessionStatus());
            }
        }
    }
}
