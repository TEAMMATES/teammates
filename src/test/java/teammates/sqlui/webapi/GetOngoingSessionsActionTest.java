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

/**
 * SUT: {@link GetOngoingSessionsAction}.
 */
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
        Instant instantNow = Instant.now();
        Instant end = instantNow.plus(Duration.ofDays(1L));
        long endTime = end.toEpochMilli();
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_noEndTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant instantNow = Instant.now();
        Instant start = instantNow.minus(Duration.ofDays(1L));
        long startTime = start.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nonLongStartTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant instantNow = Instant.now();
        Instant end = instantNow.plus(Duration.ofDays(1L));
        long endTime = end.toEpochMilli();
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "not_a_long",
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nonLongEndTimeParameter_shouldThrowInvalidHttpParameterException() {
        Instant instantNow = Instant.now();
        Instant start = instantNow.minus(Duration.ofDays(1L));
        long startTime = start.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "not_a_long",
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_startTimeParameterBelowMinimum_shouldThrowInvalidHttpParameterException() {
        long minStartTime = Long.MIN_VALUE + 30L * 24L * 60L * 60L * 1000L;
        long belowMinStartTime = minStartTime - 1L;
        String belowMinStartTimeString = String.valueOf(belowMinStartTime);
        Instant instantNow = Instant.now();
        Instant end = instantNow.plus(Duration.ofDays(1L));
        long endTime = end.toEpochMilli();
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, belowMinStartTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_endTimeParameterAboveMaximum_shouldThrowInvalidHttpParameterException() {
        Instant instantNow = Instant.now();
        Instant start = instantNow.minus(Duration.ofDays(1L));
        long startTime = start.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        long maxEndTime = Long.MAX_VALUE - 30L * 24L * 60L * 60L * 1000L;
        long aboveMaxEndTime = maxEndTime + 1L;
        String aboveMaxEndTimeString = String.valueOf(aboveMaxEndTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, aboveMaxEndTimeString,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_endTimeBeforeStartTime_shouldThrowInvalidHttpParameterException() {
        Instant instantNow = Instant.now();
        Instant start = instantNow.minus(Duration.ofDays(1L));
        long startTime = start.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        long endTime = startTime - 1;
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_typicalCase_shouldGetOngoingSessionsDataCorrectly() {
        // The Instant input parameters into the mock methods have a precision up to the nanoseconds, but the time
        // input parameters into the Action only have a precision up to the milliseconds. We must truncate to
        // milliseconds so that the mock methods can mock the exact time that the Action would parse, instead of
        // mocking a time that is off by an amount of time less than a millisecond.
        Instant instantNow = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant start = instantNow.minus(Duration.ofDays(1L));
        Instant end = instantNow.plus(Duration.ofDays(1L));
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        when(mockLogic.getCourse(course1.getId())).thenReturn(course1);
        Course course2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        when(mockLogic.getCourse(course2.getId())).thenReturn(course2);
        Course course3 = new Course("test-id3", "test-name3", "UTC", "UCL");
        when(mockLogic.getCourse(course3.getId())).thenReturn(course3);
        Account instructor2Account = new Account("instructor2", "instructor2", "test2@test.com");
        Instructor instructor2 = new Instructor(course1, "instructor2", "test2@test.com", false, "instructor2",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor2.setAccount(instructor2Account);
        when(mockLogic.getInstructorsByCourse(course1.getId())).thenReturn(Collections.singletonList(instructor2));
        Account instructor3Account = new Account("instructor3", "instructor3", "test3@test.com");
        Instructor instructor3 = new Instructor(course2, "instructor3", "test3@test.com", false, "instructor3",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor3.setAccount(instructor3Account);
        when(mockLogic.getInstructorsByCourse(course2.getId())).thenReturn(Collections.singletonList(instructor3));
        Account instructor4Account = new Account("instructor4", "instructor4", "test4@test.com");
        Instructor instructor4 = new Instructor(course3, "instructor4", "test4@test.com", false, "instructor4",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor4.setAccount(instructor4Account);
        when(mockLogic.getInstructorsByCourse(course3.getId())).thenReturn(Collections.singletonList(instructor4));
        FeedbackSession c1Fs2 = new FeedbackSession("name1-2", course1, "test2@test.com", "test-instruction",
                instantNow.plus(Duration.ofHours(12L)), instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        FeedbackSession c2Fs1 = new FeedbackSession("name2-1", course2, "test3@test.com", "test-instruction",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        FeedbackSession c3Fs1 = new FeedbackSession("name3-1", course3, "test4@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow.minus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        List<FeedbackSession> ongoingSqlSessions = new ArrayList<>();
        ongoingSqlSessions.add(c1Fs2);
        ongoingSqlSessions.add(c2Fs1);
        ongoingSqlSessions.add(c3Fs1);
        when(mockLogic.getOngoingSessions(start, end)).thenReturn(ongoingSqlSessions);
        when(mockDatastoreLogic.getAllOngoingSessions(start, end)).thenReturn(Collections.emptyList());

        long startTime = start.toEpochMilli();
        long endTime = end.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };

        GetOngoingSessionsAction getOngoingSessionsAction = getAction(params);
        JsonResult r = getJsonResult(getOngoingSessionsAction);
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(3, response.getTotalOngoingSessions());
        assertEquals(1, response.getTotalOpenSessions());
        assertEquals(1, response.getTotalClosedSessions());
        assertEquals(1, response.getTotalAwaitingSessions());
        assertEquals(3L, response.getTotalInstitutes());
        Map<String, List<OngoingSession>> expectedSessions = new HashMap<>();
        OngoingSession expectedOngoingC1Fs2 = new OngoingSession(c1Fs2, instructor2.getGoogleId());
        expectedSessions.put("NUS", Collections.singletonList(expectedOngoingC1Fs2));
        OngoingSession expectedOngoingC2Fs1 = new OngoingSession(c2Fs1, instructor3.getGoogleId());
        expectedSessions.put("MIT", Collections.singletonList(expectedOngoingC2Fs1));
        OngoingSession expectedOngoingC3Fs1 = new OngoingSession(c3Fs1, instructor4.getGoogleId());
        expectedSessions.put("UCL", Collections.singletonList(expectedOngoingC3Fs1));
        Map<String, List<OngoingSession>> actualSessions = response.getSessions();
        assertEqualSessions(expectedSessions, actualSessions);
    }

    @Test
    void testExecute_ongoingSessionsInBothDatastoreAndSql_shouldGetOngoingSessionsDataCorrectly() {
        // The Instant input parameters into the mock methods have a precision up to the nanoseconds, but the time
        // input parameters into the Action only have a precision up to the milliseconds. We must truncate to
        // milliseconds so that the mock methods can mock the exact time that the Action would parse, instead of
        // mocking a time that is off by an amount of time less than a millisecond.
        Instant instantNow = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant start = instantNow.minus(Duration.ofDays(1L));
        Instant end = instantNow.plus(Duration.ofDays(1L));
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        when(mockLogic.getCourse(course1.getId())).thenReturn(course1);
        Course course2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        when(mockLogic.getCourse(course2.getId())).thenReturn(course2);
        Account instructor2Account = new Account("instructor2", "instructor2", "test2@test.com");
        Instructor instructor2 = new Instructor(course1, "instructor2", "test2@test.com", false, "instructor2",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor2.setAccount(instructor2Account);
        when(mockLogic.getInstructorsByCourse(course1.getId())).thenReturn(Collections.singletonList(instructor2));
        Account instructor3Account = new Account("instructor3", "instructor3", "test3@test.com");
        Instructor instructor3 = new Instructor(course2, "instructor3", "test3@test.com", false, "instructor3",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        instructor3.setAccount(instructor3Account);
        when(mockLogic.getInstructorsByCourse(course2.getId())).thenReturn(Collections.singletonList(instructor3));
        FeedbackSession sqlC1Fs2 = new FeedbackSession("name1-2", course1, "test2@test.com", "test-instruction",
                instantNow.plus(Duration.ofHours(12L)), instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        FeedbackSession sqlC2Fs1 = new FeedbackSession("name2-1", course2, "test3@test.com", "test-instruction",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        List<FeedbackSession> ongoingSqlSessions = new ArrayList<>();
        ongoingSqlSessions.add(sqlC1Fs2);
        ongoingSqlSessions.add(sqlC2Fs1);
        when(mockLogic.getOngoingSessions(start, end)).thenReturn(ongoingSqlSessions);
        when(mockDatastoreLogic.getCourseInstitute("test-id3")).thenReturn("UCL");
        InstructorAttributes instructor4 = InstructorAttributes.builder("test-id3", "test4@test.com")
                .withGoogleId("instructor4")
                .build();
        when(mockDatastoreLogic.getInstructorsForCourse("test-id3")).thenReturn(Collections.singletonList(instructor4));
        FeedbackSessionAttributes c2Fs1 = FeedbackSessionAttributes.builder("name2-1", "test-id2")
                .withCreatorEmail("test3@test.com")
                .withStartTime(instantNow.minus(Duration.ofHours(12L)))
                .withEndTime(instantNow.plus(Duration.ofHours(12L)))
                .withSessionVisibleFromTime(instantNow.minus(Duration.ofDays(7L)))
                .withResultsVisibleFromTime(instantNow.plus(Duration.ofDays(7L)))
                .build();
        FeedbackSessionAttributes c3Fs1 = FeedbackSessionAttributes.builder("name3-1", "test-id3")
                .withCreatorEmail("test4@test.com")
                .withStartTime(instantNow.minus(Duration.ofDays(7L)))
                .withEndTime(instantNow.minus(Duration.ofHours(12L)))
                .withSessionVisibleFromTime(instantNow.minus(Duration.ofDays(7L)))
                .withResultsVisibleFromTime(instantNow.plus(Duration.ofDays(7L)))
                .build();
        List<FeedbackSessionAttributes> allOngoingSessions = new ArrayList<>();
        allOngoingSessions.add(c2Fs1);
        allOngoingSessions.add(c3Fs1);
        when(mockDatastoreLogic.getAllOngoingSessions(start, end)).thenReturn(allOngoingSessions);

        long startTime = start.toEpochMilli();
        long endTime = end.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };

        GetOngoingSessionsAction getOngoingSessionsAction = getAction(params);
        JsonResult r = getJsonResult(getOngoingSessionsAction);
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(3, response.getTotalOngoingSessions());
        assertEquals(1, response.getTotalOpenSessions());
        assertEquals(1, response.getTotalClosedSessions());
        assertEquals(1, response.getTotalAwaitingSessions());
        assertEquals(3L, response.getTotalInstitutes());
        Map<String, List<OngoingSession>> expectedSessions = new HashMap<>();
        OngoingSession expectedOngoingC1Fs2 = new OngoingSession(sqlC1Fs2, instructor2.getGoogleId());
        expectedSessions.put("NUS", Collections.singletonList(expectedOngoingC1Fs2));
        OngoingSession expectedOngoingC2Fs1 = new OngoingSession(sqlC2Fs1, instructor3.getGoogleId());
        expectedSessions.put("MIT", Collections.singletonList(expectedOngoingC2Fs1));
        OngoingSession expectedOngoingC3Fs1 = new OngoingSession(c3Fs1, instructor4.getGoogleId());
        expectedSessions.put("UCL", Collections.singletonList(expectedOngoingC3Fs1));
        Map<String, List<OngoingSession>> actualSessions = response.getSessions();
        assertEqualSessions(expectedSessions, actualSessions);
    }

    @Test
    void testExecute_courseMigratedButAccountNotMigrated_shouldGetOngoingSessionsDataCorrectly() {
        // The Instant input parameters into the mock methods have a precision up to the nanoseconds, but the time
        // input parameters into the Action only have a precision up to the milliseconds. We must truncate to
        // milliseconds so that the mock methods can mock the exact time that the Action would parse, instead of
        // mocking a time that is off by an amount of time less than a millisecond.
        Instant instantNow = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant start = instantNow.minus(Duration.ofDays(1L));
        Instant end = instantNow.plus(Duration.ofDays(1L));
        Course sqlCourse2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        when(mockLogic.getCourse(sqlCourse2.getId())).thenReturn(sqlCourse2);
        Instructor sqlInstructor3 = new Instructor(sqlCourse2, "instructor3", "test3@test.com", false, "instructor3",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new InstructorPrivileges(InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
        when(mockLogic.getInstructorsByCourse(sqlCourse2.getId())).thenReturn(Collections.singletonList(sqlInstructor3));
        FeedbackSession sqlC2Fs1 = new FeedbackSession("name2-1", sqlCourse2, "test3@test.com", "test-instruction",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        List<FeedbackSession> ongoingSqlSessions = Collections.singletonList(sqlC2Fs1);
        when(mockLogic.getOngoingSessions(start, end)).thenReturn(ongoingSqlSessions);
        CourseAttributes course2 = CourseAttributes.builder("test-id2")
                .build();
        when(mockDatastoreLogic.getCourse("test-id2")).thenReturn(course2);
        InstructorAttributes instructor3 = InstructorAttributes.builder("test-id2", "test3@test.com")
                .withGoogleId("instructor3")
                .build();
        when(mockDatastoreLogic.getInstructorsForCourse("test-id2")).thenReturn(Collections.singletonList(instructor3));
        FeedbackSessionAttributes c2Fs1 = FeedbackSessionAttributes.builder("name2-1", "test-id2")
                .withCreatorEmail("test3@test.com")
                .withStartTime(instantNow.minus(Duration.ofHours(12L)))
                .withEndTime(instantNow.plus(Duration.ofHours(12L)))
                .withSessionVisibleFromTime(instantNow.minus(Duration.ofDays(7L)))
                .withResultsVisibleFromTime(instantNow.plus(Duration.ofDays(7L)))
                .build();
        List<FeedbackSessionAttributes> allOngoingSessions = Collections.singletonList(c2Fs1);
        when(mockDatastoreLogic.getAllOngoingSessions(start, end)).thenReturn(allOngoingSessions);

        long startTime = start.toEpochMilli();
        long endTime = end.toEpochMilli();
        String startTimeString = String.valueOf(startTime);
        String endTimeString = String.valueOf(endTime);
        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, startTimeString,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, endTimeString,
        };

        GetOngoingSessionsAction getOngoingSessionsAction = getAction(params);
        JsonResult r = getJsonResult(getOngoingSessionsAction);
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(1, response.getTotalOngoingSessions());
        assertEquals(1, response.getTotalOpenSessions());
        assertEquals(0, response.getTotalClosedSessions());
        assertEquals(0, response.getTotalAwaitingSessions());
        assertEquals(1L, response.getTotalInstitutes());
        Map<String, List<OngoingSession>> expectedSessions = new HashMap<>();
        OngoingSession expectedOngoingC2Fs1 = new OngoingSession(sqlC2Fs1, instructor3.getGoogleId());
        expectedSessions.put("MIT", Collections.singletonList(expectedOngoingC2Fs1));
        Map<String, List<OngoingSession>> actualSessions = response.getSessions();
        assertEqualSessions(expectedSessions, actualSessions);
    }

    private void assertEqualSessions(
            Map<String, List<OngoingSession>> expectedSessions, Map<String, List<OngoingSession>> actualSessions) {
        assertEquals(expectedSessions.keySet(), actualSessions.keySet());
        for (Map.Entry<String, List<OngoingSession>> expectedInstituteSessionList : expectedSessions.entrySet()) {
            String institute = expectedInstituteSessionList.getKey();
            List<OngoingSession> expectedInstituteSessions = expectedInstituteSessionList.getValue();
            List<OngoingSession> actualInstituteSessions = actualSessions.get(institute);
            assertEqualInstituteSessions(expectedInstituteSessions, actualInstituteSessions);
        }
    }

    private void assertEqualInstituteSessions(
            List<OngoingSession> expectedInstituteSessions, List<OngoingSession> actualInstituteSessions) {
        int expectedSize = expectedInstituteSessions.size();
        assertEquals(expectedSize, actualInstituteSessions.size());
        for (int i = 0; i < expectedSize; i++) {
            OngoingSession expectedOngoingSession = expectedInstituteSessions.get(i);
            OngoingSession actualOngoingSession = actualInstituteSessions.get(i);
            assertEqualOngoingSessions(expectedOngoingSession, actualOngoingSession);
        }
    }

    private void assertEqualOngoingSessions(OngoingSession expectedOngoingSession,
            OngoingSession actualOngoingSession) {
        assertEquals(expectedOngoingSession.getSessionStatus(), actualOngoingSession.getSessionStatus());
        assertEquals(expectedOngoingSession.getInstructorHomePageLink(),
                actualOngoingSession.getInstructorHomePageLink());
        assertEquals(expectedOngoingSession.getStartTime(), actualOngoingSession.getStartTime());
        assertEquals(expectedOngoingSession.getEndTime(), actualOngoingSession.getEndTime());
        assertEquals(expectedOngoingSession.getCreatorEmail(), actualOngoingSession.getCreatorEmail());
        assertEquals(expectedOngoingSession.getCourseId(), actualOngoingSession.getCourseId());
        assertEquals(expectedOngoingSession.getFeedbackSessionName(), actualOngoingSession.getFeedbackSessionName());
    }
}
