package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.SubmittedGiverSetBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionUpdateRequest;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private DataBundle typicalDataBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetSubmittedGiverSet_hasGivers_findsGivers() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Set<UUID> expectedStudentGivers = new HashSet<>();
        Set<UUID> expectedStudentNonGivers;
        Set<UUID> expectedInstructorNonGivers = typicalDataBundle.instructors.values()
                .stream()
                .filter(instructor -> instructor.getCourseId().equals(fs.getCourseId()))
                .map(Instructor::getId)
                .collect(Collectors.toSet());

        expectedStudentGivers.add(typicalDataBundle.students.get("student1InCourse1").getId());
        expectedStudentGivers.add(typicalDataBundle.students.get("student2InCourse1").getId());
        expectedStudentGivers.add(typicalDataBundle.students.get("student3InCourse1").getId());
        expectedStudentNonGivers = typicalDataBundle.students.values()
                .stream()
                .filter(student -> student.getCourseId().equals(fs.getCourseId()))
                .map(Student::getId)
                .filter(studentId -> !expectedStudentGivers.contains(studentId))
                .collect(Collectors.toSet());

        SubmittedGiverSetBundle givers = inTransaction(() -> fsLogic.getSubmittedGiverSet(fs.getId()));
        assertEquals(expectedStudentGivers, new HashSet<>(givers.studentGiverIds()));
        assertTrue(givers.instructorGiverIds().isEmpty());
        assertEquals(expectedStudentNonGivers, new HashSet<>(givers.studentNonGiverIds()));
        assertEquals(expectedInstructorNonGivers, new HashSet<>(givers.instructorNonGiverIds()));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetSubmittedGiverSet_studentQuestionsOnly_excludesInstructorNonGivers() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session2InTypicalCourse");

        Set<UUID> expectedStudentGivers = Set.of(typicalDataBundle.students.get("student1InCourse1").getId());
        Set<UUID> expectedStudentNonGivers = typicalDataBundle.students.values()
                .stream()
                .filter(student -> student.getCourseId().equals(fs.getCourseId()))
                .map(Student::getId)
                .filter(studentId -> !expectedStudentGivers.contains(studentId))
                .collect(Collectors.toSet());

        SubmittedGiverSetBundle givers = inTransaction(() -> fsLogic.getSubmittedGiverSet(fs.getId()));

        assertEquals(expectedStudentGivers, new HashSet<>(givers.studentGiverIds()));
        assertTrue(givers.instructorGiverIds().isEmpty());
        assertEquals(expectedStudentNonGivers, new HashSet<>(givers.studentNonGiverIds()));
        assertTrue(givers.instructorNonGiverIds().isEmpty());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testPublishFeedbackSession() {
        FeedbackSession unpublishedFs = typicalDataBundle.feedbackSessions.get("unpublishedSession1InTypicalCourse");

        FeedbackSession publishedFs1 = inTransaction(() -> fsLogic.publishFeedbackSession(unpublishedFs.getId()));

        assertEquals(publishedFs1.getId(), unpublishedFs.getId());
        assertTrue(publishedFs1.isPublished());
        assertFalse(publishedFs1.isPublishedEmailSent());

        assertThrowsInTransaction(InvalidFeedbackSessionStateException.class, () -> fsLogic.publishFeedbackSession(
                publishedFs1.getId()));
        assertThrowsInTransaction(EntityDoesNotExistException.class, () -> fsLogic.publishFeedbackSession(
                UUID.fromString("2da92144-63f3-4da5-9148-dbcbdef6dc2c")));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testUnpublishFeedbackSession() {
        FeedbackSession publishedFs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession unpublishedFs1 = inTransaction(() -> fsLogic.unpublishFeedbackSession(
                publishedFs.getId()));

        assertEquals(unpublishedFs1.getId(), publishedFs.getId());
        assertFalse(unpublishedFs1.isPublished());

        assertThrowsInTransaction(InvalidFeedbackSessionStateException.class, () -> fsLogic.unpublishFeedbackSession(
                unpublishedFs1.getId()));
        assertThrowsInTransaction(EntityDoesNotExistException.class, () -> fsLogic.unpublishFeedbackSession(
                UUID.fromString("2da92144-63f3-4da5-9148-dbcbdef6dc2c")));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetFeedbackSessionsForInstructors() {
        Instructor instructor = typicalDataBundle.instructors.get("instructor1OfCourse1");
        Course course = instructor.getCourse();
        List<FeedbackSession> expectedFsList = inTransaction(() -> fsLogic.getFeedbackSessionsForCourse(course.getId()));
        List<FeedbackSession> actualFsList =
                inTransaction(() -> fsLogic.getFeedbackSessionsForInstructors(List.of(instructor)));

        assertEquals(expectedFsList.size(), actualFsList.size());
        for (int i = 0; i < expectedFsList.size(); i++) {
            assertEquals(expectedFsList.get(i), actualFsList.get(i));
        }
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetOngoingSessions_typicalCase_shouldGetOnlyOngoingSessionsWithinRange() {
        FeedbackSession c1Fs2 = typicalDataBundle.feedbackSessions.get("ongoingSession2InCourse1");
        FeedbackSession c1Fs3 = typicalDataBundle.feedbackSessions.get("ongoingSession3InCourse1");
        FeedbackSession c3Fs2 = typicalDataBundle.feedbackSessions.get("ongoingSession2InCourse3");
        Set<FeedbackSession> expectedUniqueOngoingSessions = new HashSet<>();
        expectedUniqueOngoingSessions.add(c1Fs2);
        expectedUniqueOngoingSessions.add(c1Fs3);
        expectedUniqueOngoingSessions.add(c3Fs2);
        Instant rangeStart = Instant.parse("2012-01-25T22:00:00Z");
        Instant rangeEnd = Instant.parse("2012-01-27T22:00:00Z");
        List<FeedbackSession> actualOngoingSessions = inTransaction(() -> fsLogic.getOngoingSessions(
                rangeStart, rangeEnd));
        Set<FeedbackSession> actualUniqueOngoingSessions = new HashSet<>();
        actualUniqueOngoingSessions.addAll(actualOngoingSessions);
        assertEquals(expectedUniqueOngoingSessions, actualUniqueOngoingSessions);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetSoftDeletedFeedbackSessionsForInstructors() {
        Instructor instructor = typicalDataBundle.instructors.get("instructor1OfCourse1");
        Course course = instructor.getCourse();
        List<FeedbackSession> expectedFsList = inTransaction(() -> {
            List<FeedbackSession> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(course.getId());
            for (FeedbackSession fs : feedbackSessions) {
                fs.setDeletedAt(Instant.now());
            }
            return feedbackSessions;
        });
        List<FeedbackSession> actualFsList =
                inTransaction(() -> fsLogic.getSoftDeletedFeedbackSessionsForInstructors(List.of(instructor)));

        assertEquals(expectedFsList.size(), actualFsList.size());
        for (int i = 0; i < expectedFsList.size(); i++) {
            assertEquals(expectedFsList.get(i), actualFsList.get(i));
        }
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testDeleteFeedbackSessionCascade_deleteSessionNotInRecycleBin_shouldDoCascadeDeletion() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession retrievedFs = inTransaction(() -> fsLogic.getFeedbackSession(fs.getId()));

        assertNotNull(retrievedFs);
        assertFalse(inTransaction(() -> fsLogic.getFeedbackSession(fs.getId()).getFeedbackQuestions().isEmpty()));
        assertFalse(inTransaction(() -> fqLogic.getFeedbackQuestionsForSession(retrievedFs)).isEmpty());

        // delete existing feedback session directly
        inTransaction(() -> fsLogic.deleteFeedbackSessionCascade(fs.getId()));

        // check deletion is cascaded
        assertNull(inTransaction(() -> fsLogic.getFeedbackSession(fs.getId())));
        assertTrue(inTransaction(() -> fqLogic.getFeedbackQuestionsForSession(retrievedFs)).isEmpty());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testUpdateFeedbackSession_validUpdate_success() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        Instant newStartTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(1)
        );
        Instant newEndTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(24)
        );
        Instant newSessionVisibleFromTime = newStartTime;
        Instant newResultsVisibleFromTime = TimeHelper.getInstantNearestHourBefore(
                TimeHelper.getInstantHoursOffsetFromNow(48)
        );

        FeedbackSessionUpdateRequest updateRequest = new FeedbackSessionUpdateRequest();
        updateRequest.setInstructions("new instructions");
        updateRequest.setGracePeriod(60);
        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        updateRequest.setSubmissionStartTimestamp(newStartTime.toEpochMilli());
        updateRequest.setSubmissionEndTimestamp(newEndTime.toEpochMilli());
        updateRequest.setCustomSessionVisibleTimestamp(newSessionVisibleFromTime.toEpochMilli());
        updateRequest.setCustomResponseVisibleTimestamp(newResultsVisibleFromTime.toEpochMilli());
        updateRequest.setClosingSoonEmailEnabled(false);
        updateRequest.setPublishedEmailEnabled(false);

        FeedbackSession updatedFs = inTransaction(() -> fsLogic.updateFeedbackSession(fs.getId(), updateRequest));

        assertEquals(updateRequest.getInstructions(), updatedFs.getInstructions());
        assertEquals(updateRequest.getGracePeriod(), updatedFs.getGracePeriod());
        assertEquals(updateRequest.getSessionVisibleFromTime(), updatedFs.getSessionVisibleFromTime());
        assertEquals(updateRequest.getResultsVisibleFromTime(), updatedFs.getResultsVisibleFromTime());
        assertEquals(updateRequest.getSubmissionStartTime(), updatedFs.getStartTime());
        assertEquals(updateRequest.getSubmissionEndTime(), updatedFs.getEndTime());
        assertEquals(updateRequest.isClosingSoonEmailEnabled(), updatedFs.isClosingSoonEmailEnabled());
        assertEquals(updateRequest.isPublishedEmailEnabled(), updatedFs.isPublishedEmailEnabled());
    }
}
