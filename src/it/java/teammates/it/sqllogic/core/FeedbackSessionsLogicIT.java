package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.FeedbackQuestionsLogic;
import teammates.sqllogic.core.FeedbackSessionsLogic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGiverSetThatAnsweredFeedbackQuestion_hasGivers_findsGivers() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Set<String> expectedGivers = new HashSet<>();

        expectedGivers.add(typicalDataBundle.students.get("student1InCourse1").getEmail());
        expectedGivers.add(typicalDataBundle.students.get("student2InCourse1").getEmail());
        expectedGivers.add(typicalDataBundle.students.get("student3InCourse1").getEmail());

        Set<String> givers = fsLogic.getGiverSetThatAnsweredFeedbackSession(fs.getName(), fs.getCourse().getId());
        assertEquals(expectedGivers.size(), givers.size());
        assertEquals(expectedGivers, givers);
    }

    @Test
    public void testPublishFeedbackSession()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession unpublishedFs = typicalDataBundle.feedbackSessions.get("unpublishedSession1InTypicalCourse");

        FeedbackSession publishedFs1 = fsLogic.publishFeedbackSession(
                unpublishedFs.getName(), unpublishedFs.getCourse().getId());

        assertEquals(publishedFs1.getName(), unpublishedFs.getName());
        assertTrue(publishedFs1.isPublished());

        assertThrows(InvalidParametersException.class, () -> fsLogic.publishFeedbackSession(
                publishedFs1.getName(), publishedFs1.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.publishFeedbackSession(
                "non-existent name", unpublishedFs.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.publishFeedbackSession(
                unpublishedFs.getName(), "random-course-id"));
    }

    @Test
    public void testUnpublishFeedbackSession()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession publishedFs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession unpublishedFs1 = fsLogic.unpublishFeedbackSession(
                publishedFs.getName(), publishedFs.getCourse().getId());

        assertEquals(unpublishedFs1.getName(), publishedFs.getName());
        assertFalse(unpublishedFs1.isPublished());

        assertThrows(InvalidParametersException.class, () -> fsLogic.unpublishFeedbackSession(
                unpublishedFs1.getName(), unpublishedFs1.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.unpublishFeedbackSession(
                "non-existent name", publishedFs.getCourse().getId()));
        assertThrows(EntityDoesNotExistException.class, () -> fsLogic.unpublishFeedbackSession(
                publishedFs.getName(), "random-course-id"));
    }

    @Test
    public void testGetFeedbackSessionsForInstructors() {
        Instructor instructor = typicalDataBundle.instructors.get("instructor1OfCourse1");
        Course course = instructor.getCourse();
        List<FeedbackSession> expectedFsList = fsLogic.getFeedbackSessionsForCourse(course.getId());
        List<FeedbackSession> actualFsList = fsLogic.getFeedbackSessionsForInstructors(List.of(instructor));

        assertEquals(expectedFsList.size(), actualFsList.size());
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
        assertEquals(expectedUniqueOngoingSessions, actualUniqueOngoingSessions);
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

        assertEquals(expectedFsList.size(), actualFsList.size());
        for (int i = 0; i < expectedFsList.size(); i++) {
            verifyEquals(expectedFsList.get(i), actualFsList.get(i));
        }
    }

    @Test
    public void testDeleteFeedbackSessionCascade_deleteSessionNotInRecycleBin_shouldDoCascadeDeletion() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackSession retrievedFs = fsLogic.getFeedbackSession(fs.getName(), fs.getCourse().getId());

        assertNotNull(retrievedFs);
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fs.getName(), fs.getCourse().getId()));
        assertFalse(retrievedFs.getFeedbackQuestions().isEmpty());
        assertFalse(fqLogic.getFeedbackQuestionsForSession(retrievedFs).isEmpty());

        // delete existing feedback session directly
        fsLogic.deleteFeedbackSessionCascade(fs.getName(), fs.getCourse().getId());

        // check deletion is cascaded
        assertNull(fsLogic.getFeedbackSession(fs.getName(), fs.getCourse().getId()));
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fs.getName(), fs.getCourse().getId()));
        assertTrue(fqLogic.getFeedbackQuestionsForSession(retrievedFs).isEmpty());
    }
}
