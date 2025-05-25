package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.FeedbackSessionSubmissionStatus;
import teammates.ui.output.FeedbackSessionsData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;

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
        student2InCourse2.setGoogleId(instructor1OfCourse1.getGoogleId());

        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    protected void testExecute() {
        // see individual tests
    }

    @Test
    protected void testExecute_asInstructorWithCourseId_shouldReturnAllSessionsForCourse() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, instructor2OfCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllInstructorSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagTrue_shouldReturnAllSoftDeletedSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "true",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(1, fsData.getFeedbackSessions().size());
        FeedbackSessionData fs = fsData.getFeedbackSessions().get(0);
        assertAllInformationMatch(fs, session1InCourse1);
    }

    @Test
    protected void testExecute_asInstructorWithRecycleBinFlagFalse_shouldReturnAllSessionsForInstructor() {
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.IS_IN_RECYCLE_BIN, "false",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(5, fsData.getFeedbackSessions().size());
        assertAllInstructorSessionsMatch(fsData, sessionsInCourse1);
    }

    @Test
    protected void testExecute_instructorAsStudent_shouldReturnAllSessionsForStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(2, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse2, instructor1OfCourse1.getEmail());
    }

    @Test
    protected void testExecute_instructorAsStudentWithCourseId_shouldReturnAllSessionsForCourseOfStudent() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student2InCourse2 = typicalBundle.students.get("student2InCourse2");

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student2InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();
        assertAllStudentSessionsMatch(fsData, sessionsInCourse2, student2InCourse2.getEmail());
    }

    @Test
    protected void testExecute_instructorAsStudentWithInvalidCourseId_shouldReturnEmptyList() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());

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
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction action = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(action).getOutput();

        assertEquals(4, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse1.subList(0, 4), student1InCourse1.getEmail());

    }

    @Test
    protected void testExecute_asStudent_shouldReturnAllSessionsForAccount() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertEquals(4, fsData.getFeedbackSessions().size());
        assertAllStudentSessionsMatch(fsData, sessionsInCourse1.subList(0, 4), student1InCourse1.getEmail());
    }

    @Test
    protected void testExecute_asStudentWithDeadlines_shouldHaveCorrectSubmissionStatus() throws Exception {
        StudentAttributes student4InCourse1 = typicalBundle.students.get("student4InCourse1");
        String emailAddress = student4InCourse1.getEmail();
        FeedbackSessionAttributes session2InCourse1 = typicalBundle.feedbackSessions.get("session2InCourse1");
        loginAsStudent(student4InCourse1.getGoogleId());

        Instant newEndTime = Instant.now().plus(Duration.ofHours(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(
                session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId())
                .withEndTime(newEndTime)
                .build());
        List<FeedbackSessionAttributes> expectedSessions = sessionsInCourse1.stream()
                .map(session -> session.getCopyForStudent(emailAddress))
                .collect(Collectors.toList());
        int expectedSession2InCourse1Index = expectedSessions.indexOf(session2InCourse1);
        FeedbackSessionAttributes expectedSession2InCourse1 = expectedSessions.get(expectedSession2InCourse1Index);
        expectedSession2InCourse1.setEndTime(newEndTime);

        String[] submissionParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        ______TS("Before deadline; should indicate open.");

        GetFeedbackSessionsAction a = getAction(submissionParam);
        FeedbackSessionsData fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertAllStudentSessionsMatch(fsData, expectedSessions.subList(0, 4), emailAddress);

        ______TS("After deadline but within grace period; should indicate in grace period.");

        Map<String, Instant> studentDeadlines = expectedSession2InCourse1.getStudentDeadlines();
        studentDeadlines.put(emailAddress, Instant.now().plusSeconds(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(
                expectedSession2InCourse1.getFeedbackSessionName(), expectedSession2InCourse1.getCourseId())
                .withStudentDeadlines(studentDeadlines)
                .build());

        a = getAction(submissionParam);
        fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertAllStudentSessionsMatch(fsData, expectedSessions.subList(0, 4), emailAddress);

        ______TS("After deadline and beyond grace period; should indicate closed.");

        studentDeadlines.put(emailAddress, Instant.now().plus(Duration.ofHours(-1)));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(
                expectedSession2InCourse1.getFeedbackSessionName(), expectedSession2InCourse1.getCourseId())
                .withStudentDeadlines(studentDeadlines)
                .build());

        a = getAction(submissionParam);
        fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertAllStudentSessionsMatch(fsData, expectedSessions.subList(0, 4), emailAddress);

        ______TS("Before deadline with course ID; should indicate open.");

        submissionParam = new String[] {
                Const.ParamsNames.COURSE_ID, student4InCourse1.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        studentDeadlines.put(emailAddress, Instant.now().plus(Duration.ofHours(1)));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(
                expectedSession2InCourse1.getFeedbackSessionName(), expectedSession2InCourse1.getCourseId())
                .withStudentDeadlines(studentDeadlines)
                .build());

        a = getAction(submissionParam);
        fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertAllStudentSessionsMatch(fsData, expectedSessions.subList(0, 4), emailAddress);

        ______TS("After deadline but within grace period with course ID; should indicate in grace period.");

        studentDeadlines.put(emailAddress, Instant.now().plusSeconds(-1));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(
                expectedSession2InCourse1.getFeedbackSessionName(), expectedSession2InCourse1.getCourseId())
                .withStudentDeadlines(studentDeadlines)
                .build());

        a = getAction(submissionParam);
        fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertAllStudentSessionsMatch(fsData, expectedSessions.subList(0, 4), emailAddress);

        ______TS("After deadline and beyond grace period with course ID; should indicate closed.");

        studentDeadlines.put(emailAddress, Instant.now().plus(Duration.ofHours(-1)));
        logic.updateFeedbackSession(FeedbackSessionAttributes.updateOptionsBuilder(
                expectedSession2InCourse1.getFeedbackSessionName(), expectedSession2InCourse1.getCourseId())
                .withStudentDeadlines(studentDeadlines)
                .build());

        a = getAction(submissionParam);
        fsData = (FeedbackSessionsData) getJsonResult(a).getOutput();

        assertAllStudentSessionsMatch(fsData, expectedSessions.subList(0, 4), emailAddress);
    }

    @Test
    protected void testExecute_unknownEntityType_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        verifyHttpParameterFailure();
    }

    @Test
    @Override
    protected void testAccessControl() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        StudentAttributes student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsStudent(student1InCourse1.getGoogleId());

        ______TS("student can access");
        String[] studentEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(studentEntityParam);

        ______TS("student of the same course can access");
        loginAsStudent(student1InCourse2.getGoogleId());
        String[] courseParam = {
                Const.ParamsNames.COURSE_ID, student1InCourse2.getCourse(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyCanAccess(courseParam);

        ______TS("Student of another course cannot access");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCannotAccess(courseParam);

        ______TS("instructor can access");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());

        String[] instructorParam = {
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
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCannotAccess(instructorAndCourseIdParam);

        ______TS("instructor as student can access");
        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(studentEntityParam);

        ______TS("instructor as student can access for course");
        loginAsStudentInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(courseParam);

        String[] adminEntityParam = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyAccessibleForAdmin(adminEntityParam);
        verifyInaccessibleForUnregisteredUsers(studentEntityParam);
        verifyInaccessibleWithoutLogin();
    }

    private void assertDeadlinesFilteredForStudent(FeedbackSessionData sessionData,
            FeedbackSessionAttributes expectedSession, String emailAddress) {
        boolean hasDeadline = expectedSession.getStudentDeadlines().containsKey(emailAddress);
        boolean returnsDeadline = sessionData.getStudentDeadlines().containsKey(emailAddress);
        boolean returnsDeadlineForStudentIfExists = !hasDeadline || returnsDeadline;
        boolean returnsOtherDeadlines = sessionData.getStudentDeadlines().size() > (hasDeadline ? 1 : 0);
        boolean returnsOnlyDeadlineForStudentIfExists = !returnsOtherDeadlines && returnsDeadlineForStudentIfExists;
        assertTrue(returnsOnlyDeadlineForStudentIfExists);
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

    private void assertPartialInformationMatch(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
        String timeZone = expectedSession.getTimeZone();
        assertEquals(expectedSession.getCourseId(), data.getCourseId());
        assertEquals(timeZone, data.getTimeZone());
        assertEquals(expectedSession.getFeedbackSessionName(), data.getFeedbackSessionName());
        assertEquals(expectedSession.getInstructions(), data.getInstructions());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(expectedSession.getStartTime(),
                timeZone, true).toEpochMilli(),
                data.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(expectedSession.getEndTime(),
                timeZone, true).toEpochMilli(),
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

        if (expectedSession.getDeletedTime() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedTime().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }

        assertInformationHidden(data);
    }

    private void assertAllInformationMatch(FeedbackSessionData data, FeedbackSessionAttributes expectedSession) {
        String timeZone = expectedSession.getTimeZone();
        assertEquals(expectedSession.getCourseId(), data.getCourseId());
        assertEquals(timeZone, data.getTimeZone());
        assertEquals(expectedSession.getFeedbackSessionName(), data.getFeedbackSessionName());
        assertEquals(expectedSession.getInstructions(), data.getInstructions());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(expectedSession.getStartTime(),
                timeZone, true).toEpochMilli(),
                data.getSubmissionStartTimestamp());
        assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(expectedSession.getEndTime(),
                timeZone, true).toEpochMilli(),
                data.getSubmissionEndTimestamp());
        assertEquals(expectedSession.getGracePeriodMinutes(), data.getGracePeriod().longValue());

        Instant sessionVisibleTime = expectedSession.getSessionVisibleFromTime();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            assertEquals(data.getSessionVisibleSetting(), SessionVisibleSetting.AT_OPEN);
        } else {
            assertEquals(data.getSessionVisibleSetting(), SessionVisibleSetting.CUSTOM);
            assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(sessionVisibleTime,
                    timeZone, true).toEpochMilli(),
                    data.getCustomSessionVisibleTimestamp().longValue());
        }

        Instant responseVisibleTime = expectedSession.getResultsVisibleFromTime();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            assertEquals(ResponseVisibleSetting.AT_VISIBLE, data.getResponseVisibleSetting());
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertEquals(ResponseVisibleSetting.LATER, data.getResponseVisibleSetting());
        } else {
            assertEquals(ResponseVisibleSetting.CUSTOM, data.getResponseVisibleSetting());
            assertEquals(TimeHelper.getMidnightAdjustedInstantBasedOnZone(responseVisibleTime,
                    timeZone, true).toEpochMilli(),
                    data.getCustomResponseVisibleTimestamp().longValue());
        }

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

        if (expectedSession.isPublished()) {
            assertEquals(FeedbackSessionPublishStatus.PUBLISHED, data.getPublishStatus());
        } else {
            assertEquals(FeedbackSessionPublishStatus.NOT_PUBLISHED, data.getPublishStatus());
        }

        assertEquals(expectedSession.isClosingSoonEmailEnabled(), data.getIsClosingSoonEmailEnabled());
        assertEquals(expectedSession.isPublishedEmailEnabled(), data.getIsPublishedEmailEnabled());

        assertEquals(expectedSession.getCreatedTime().toEpochMilli(), data.getCreatedAtTimestamp());
        if (expectedSession.getDeletedTime() == null) {
            assertNull(data.getDeletedAtTimestamp());
        } else {
            assertEquals(expectedSession.getDeletedTime().toEpochMilli(), data.getDeletedAtTimestamp().longValue());
        }
    }

    private void assertAllInstructorSessionsMatch(FeedbackSessionsData sessionsData,
                                                  List<FeedbackSessionAttributes> expectedSessions) {

        assertEquals(sessionsData.getFeedbackSessions().size(), expectedSessions.size());
        for (FeedbackSessionData sessionData : sessionsData.getFeedbackSessions()) {
            List<FeedbackSessionAttributes> matchedSessions =
                    expectedSessions.stream().filter(session -> session.getFeedbackSessionName().equals(
                            sessionData.getFeedbackSessionName())
                            && session.getCourseId().equals(sessionData.getCourseId())).collect(Collectors.toList());

            assertEquals(1, matchedSessions.size());
            assertAllInformationMatch(sessionData, matchedSessions.get(0));
        }
    }

    private void assertAllStudentSessionsMatch(FeedbackSessionsData sessionsData,
            List<FeedbackSessionAttributes> expectedSessions, String emailAddress) {

        assertEquals(sessionsData.getFeedbackSessions().size(), expectedSessions.size());
        for (FeedbackSessionData sessionData : sessionsData.getFeedbackSessions()) {
            List<FeedbackSessionAttributes> matchedSessions =
                    expectedSessions.stream().filter(session -> session.getFeedbackSessionName().equals(
                            sessionData.getFeedbackSessionName())
                            && session.getCourseId().equals(sessionData.getCourseId())).collect(Collectors.toList());

            assertEquals(1, matchedSessions.size());
            FeedbackSessionAttributes matchedSession = matchedSessions.get(0);
            assertPartialInformationMatch(sessionData, matchedSession);
            assertInformationHiddenForStudent(sessionData);
            assertDeadlinesFilteredForStudent(sessionData, matchedSession, emailAddress);
        }
    }
}
