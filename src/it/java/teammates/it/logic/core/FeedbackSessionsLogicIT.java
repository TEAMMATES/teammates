package teammates.it.logic.core;

import org.junit.jupiter.api.Assertions;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelper;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
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

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGiverSetThatAnsweredFeedbackQuestion_hasGivers_findsGivers() throws EntityDoesNotExistException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Set<String> expectedGivers = new HashSet<>();

        expectedGivers.add(typicalDataBundle.students.get("student1InCourse1").getEmail());
        expectedGivers.add(typicalDataBundle.students.get("student2InCourse1").getEmail());
        expectedGivers.add(typicalDataBundle.students.get("student3InCourse1").getEmail());

        Set<String> givers = fsLogic.getGiverSetThatAnsweredFeedbackSession(fs.getId());
        Assertions.assertEquals(expectedGivers.size(), givers.size());
        Assertions.assertEquals(expectedGivers, givers);
    }

    @Test
    public void testPublishFeedbackSession()
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        FeedbackSession unpublishedFs = typicalDataBundle.feedbackSessions.get("unpublishedSession1InTypicalCourse");

        FeedbackSession publishedFs1 = fsLogic.publishFeedbackSession(unpublishedFs.getId());

        Assertions.assertEquals(publishedFs1.getId(), unpublishedFs.getId());
        Assertions.assertTrue(publishedFs1.isPublished());
        Assertions.assertFalse(publishedFs1.isPublishedEmailSent());

        Assertions.assertThrows(InvalidFeedbackSessionStateException.class, () -> fsLogic.publishFeedbackSession(
                publishedFs1.getId()));
        Assertions.assertThrows(EntityDoesNotExistException.class, () -> fsLogic.publishFeedbackSession(
                UUID.fromString("2da92144-63f3-4da5-9148-dbcbdef6dc2c")));
    }

    @Test
    public void testUnpublishFeedbackSession()
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        FeedbackSession publishedFs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession unpublishedFs1 = fsLogic.unpublishFeedbackSession(
                publishedFs.getId());

        Assertions.assertEquals(unpublishedFs1.getId(), publishedFs.getId());
        Assertions.assertFalse(unpublishedFs1.isPublished());

        Assertions.assertThrows(InvalidFeedbackSessionStateException.class, () -> fsLogic.unpublishFeedbackSession(
                unpublishedFs1.getId()));
        Assertions.assertThrows(EntityDoesNotExistException.class, () -> fsLogic.unpublishFeedbackSession(
                UUID.fromString("2da92144-63f3-4da5-9148-dbcbdef6dc2c")));
    }

    @Test
    public void testGetFeedbackSessionsForInstructors() {
        Instructor instructor = typicalDataBundle.instructors.get("instructor1OfCourse1");
        Course course = instructor.getCourse();
        List<FeedbackSession> expectedFsList = fsLogic.getFeedbackSessionsForCourse(course.getId());
        List<FeedbackSession> actualFsList = fsLogic.getFeedbackSessionsForInstructors(List.of(instructor));

        Assertions.assertEquals(expectedFsList.size(), actualFsList.size());
        for (int i = 0; i < expectedFsList.size(); i++) {
            verifyEquals(expectedFsList.get(i), actualFsList.get(i));
        }
    }

    @Test
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
        List<FeedbackSession> actualOngoingSessions = fsLogic.getOngoingSessions(rangeStart, rangeEnd);
        Set<FeedbackSession> actualUniqueOngoingSessions = new HashSet<>();
        actualUniqueOngoingSessions.addAll(actualOngoingSessions);
        Assertions.assertEquals(expectedUniqueOngoingSessions, actualUniqueOngoingSessions);
    }

    @Test
    public void testGetSoftDeletedFeedbackSessionsForInstructors() {
        Instructor instructor = typicalDataBundle.instructors.get("instructor1OfCourse1");
        Course course = instructor.getCourse();
        List<FeedbackSession> expectedFsList = fsLogic.getFeedbackSessionsForCourse(course.getId());
        for (FeedbackSession fs : expectedFsList) {
            fs.setDeletedAt(Instant.now());
        }
        List<FeedbackSession> actualFsList = fsLogic.getSoftDeletedFeedbackSessionsForInstructors(List.of(instructor));

        Assertions.assertEquals(expectedFsList.size(), actualFsList.size());
        for (int i = 0; i < expectedFsList.size(); i++) {
            verifyEquals(expectedFsList.get(i), actualFsList.get(i));
        }
    }

    @Test
    public void testDeleteFeedbackSessionCascade_deleteSessionNotInRecycleBin_shouldDoCascadeDeletion() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession retrievedFs = fsLogic.getFeedbackSession(fs.getName(), fs.getCourseId());

        Assertions.assertNotNull(retrievedFs);
        Assertions.assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fs.getName(), fs.getCourseId()));
        Assertions.assertFalse(retrievedFs.getFeedbackQuestions().isEmpty());
        Assertions.assertFalse(fqLogic.getFeedbackQuestionsForSession(retrievedFs).isEmpty());

        // delete existing feedback session directly
        fsLogic.deleteFeedbackSessionCascade(fs.getId());

        // check deletion is cascaded
        Assertions.assertNull(fsLogic.getFeedbackSession(fs.getId()));
        Assertions.assertTrue(fqLogic.getFeedbackQuestionsForSession(retrievedFs).isEmpty());
    }

    @Test
    public void testUpdateFeedbackSession_validUpdate_success() throws Exception {
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

        fs = fsLogic.updateFeedbackSession(fs.getId(), updateRequest);

        Assertions.assertEquals(updateRequest.getInstructions(), fs.getInstructions());
        Assertions.assertEquals(updateRequest.getGracePeriod(), fs.getGracePeriod());
        Assertions.assertEquals(updateRequest.getSessionVisibleFromTime(), fs.getSessionVisibleFromTime());
        Assertions.assertEquals(updateRequest.getResultsVisibleFromTime(), fs.getResultsVisibleFromTime());
        Assertions.assertEquals(updateRequest.getSubmissionStartTime(), fs.getStartTime());
        Assertions.assertEquals(updateRequest.getSubmissionEndTime(), fs.getEndTime());
        Assertions.assertEquals(updateRequest.isClosingSoonEmailEnabled(), fs.isClosingSoonEmailEnabled());
        Assertions.assertEquals(updateRequest.isPublishedEmailEnabled(), fs.isPublishedEmailEnabled());
    }
}
