package teammates.it.storage.sqlapi;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link FeedbackSessionsDb}.
 */
public class FeedbackSessionsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();

    @Test
    public void testGetFeedbackSessionByFeedbackSessionNameAndCourseId()
            throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: get feedback session that exists");
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        FeedbackSession fs2 = new FeedbackSession("name2", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        fsDb.createFeedbackSession(fs1);
        fsDb.createFeedbackSession(fs2);

        FeedbackSession actualFs = fsDb.getFeedbackSession(fs2.getName(), fs2.getCourse().getId());

        verifyEquals(fs2, actualFs);
    }

    @Test
    public void testGetOngoingSessions_typicalCase_shouldGetOnlyOngoingSessionsWithinRange()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Instant instantNow = Instant.now();
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession c1Fs1 = new FeedbackSession("name1-1", course1, "test1@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow.minus(Duration.ofDays(1L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c1Fs1);
        FeedbackSession c1Fs2 = new FeedbackSession("name1-2", course1, "test2@test.com", "test-instruction",
                instantNow, instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c1Fs2);
        Course course2 = new Course("test-id2", "test-name2", "UTC", "MIT");
        coursesDb.createCourse(course2);
        FeedbackSession c2Fs1 = new FeedbackSession("name2-1", course2, "test3@test.com", "test-instruction",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c2Fs1);
        FeedbackSession c2Fs2 = new FeedbackSession("name2-2", course2, "test3@test.com", "test-instruction",
                instantNow.plus(Duration.ofDays(1L)), instantNow.plus(Duration.ofDays(7L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c2Fs2);
        Course course3 = new Course("test-id3", "test-name3", "UTC", "UCL");
        coursesDb.createCourse(course3);
        FeedbackSession c3Fs1 = new FeedbackSession("name3-1", course3, "test4@test.com", "test-instruction",
                instantNow.minus(Duration.ofDays(7L)), instantNow,
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
        fsDb.createFeedbackSession(c3Fs1);
        Set<FeedbackSession> expectedUniqueOngoingSessions = new HashSet<>();
        expectedUniqueOngoingSessions.add(c1Fs2);
        expectedUniqueOngoingSessions.add(c2Fs1);
        expectedUniqueOngoingSessions.add(c3Fs1);
        List<FeedbackSession> actualOngoingSessions =
                fsDb.getOngoingSessions(instantNow.minus(Duration.ofDays(1L)), instantNow.plus(Duration.ofDays(1L)));
        Set<FeedbackSession> actualUniqueOngoingSessions = new HashSet<>();
        actualUniqueOngoingSessions.addAll(actualOngoingSessions);
        assertEquals(expectedUniqueOngoingSessions, actualUniqueOngoingSessions);
    }

    @Test
    public void testSoftDeleteFeedbackSession()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        fsDb.createFeedbackSession(fs1);
        fsDb.softDeleteFeedbackSession(fs1.getName(), course1.getId());

        FeedbackSession softDeletedFs = fsDb.getSoftDeletedFeedbackSession(fs1.getName(), course1.getId());
        verifyEquals(fs1, softDeletedFs);
    }

    @Test
    public void testRestoreFeedbackSession()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course1 = new Course("test-id1", "test-name1", "UTC", "NUS");
        coursesDb.createCourse(course1);
        FeedbackSession fs1 = new FeedbackSession("name1", course1, "test1@test.com", "test-instruction",
                Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)), Instant.now(),
                Instant.now().plus(Duration.ofDays(7)), Duration.ofMinutes(10), true, true, true);
        fs1.setDeletedAt(Instant.now());
        fsDb.createFeedbackSession(fs1);
        FeedbackSession softDeletedFs = fsDb.getSoftDeletedFeedbackSession(fs1.getName(), course1.getId());

        verifyEquals(fs1, softDeletedFs);

        fsDb.restoreDeletedFeedbackSession(fs1.getName(), course1.getId());
        FeedbackSession restoredFs = fsDb.getFeedbackSession(fs1.getName(), course1.getId());

        verifyEquals(fs1, restoredFs);
    }

    // It is not possible to use quotation marks in a FeedbackSession name. It is not known if it is possible to do SQL
    // injection here. We keep this here to show it was not missed out.
    // @Test
    // public void testCreateFeedbackSession_sqlInjectionAttemptIntoName_notKnownToBePossible()
    //         throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {}

    @Test
    public void testCreateFeedbackSession_sqlInjectionAttemptIntoCreatorEmail_shouldNotThrowException()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        // It is not possible to use a semicolon in an email address. Instead, we simply check if it throws an error for
        // invalid SQL syntax.
        String sqlInjectionCreatorEmail = "instructor'@gmail.com"; // Unbalanced single quotation mark.
        fs.setCreatorEmail(sqlInjectionCreatorEmail);
        fsDb.createFeedbackSession(fs);
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs);
        assertEquals(sqlInjectionCreatorEmail, createdFs.getCreatorEmail());
    }

    @Test
    public void testCreateFeedbackSession_sqlInjectionAttemptIntoInstructions_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        // insert into feedback_sessions (course_id, created_at, creator_email, deleted_at, end_time, grace_period,
        // instructions, is_closed_email_sent, is_closing_email_enabled, is_closing_soon_email_sent, is_open_email_sent,
        // is_opening_email_enabled, is_opening_soon_email_sent, is_published_email_enabled, is_published_email_sent,
        // name, results_visible_from_time, session_visible_from_time,
        // start_time, updated_at, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        String sqlInjectionInstructions = "instructions', FALSE, TRUE, FALSE, FALSE, "
                + "TRUE, FALSE, TRUE, TRUE, "
                + "'fs-name', '2024-03-05 12:00:00'::timestamp, '2024-02-20 12:00:00'::timestamp, "
                + "'2024-02-27 12:00:00'::timestamp, '2024-02-27 12:00:00'::timestamp, uuid_generate_v4()); "
                + "DROP TABLE feedback_sessions;--";
        fs.setInstructions(sqlInjectionInstructions);
        fsDb.createFeedbackSession(fs);
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs);
        assertEquals(SanitizationHelper.sanitizeForRichText(sqlInjectionInstructions), createdFs.getInstructions());
    }

    @Test
    public void testGetFeedbackSession_sqlInjectionAttemptIntoName_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs);
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionName = "fs-name' OR 1 = 1;--";
        FeedbackSession nonExistentFs = fsDb.getFeedbackSession(sqlInjectionName, "course-id");
        assertNull(nonExistentFs);
    }

    @Test
    public void testGetFeedbackSession_sqlInjectionAttemptIntoCourseId_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs);
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionCourseId = "course-id' OR 1 = 1;--";
        FeedbackSession nonExistentFs = fsDb.getFeedbackSession("fs-name", sqlInjectionCourseId);
        assertNull(nonExistentFs);
    }

    @Test
    public void testGetSoftDeletedFeedbackSession_sqlInjectionAttemptIntoName_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        fsDb.softDeleteFeedbackSession("fs-name", "course-id");
        FeedbackSession createdFs = fsDb.getSoftDeletedFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs);
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionName = "fs-name' OR 1 = 1;--";
        FeedbackSession nonExistentFs = fsDb.getSoftDeletedFeedbackSession(sqlInjectionName, "course-id");
        assertNull(nonExistentFs);
    }

    @Test
    public void testGetSoftDeletedFeedbackSession_sqlInjectionAttemptIntoCourseId_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        fsDb.softDeleteFeedbackSession("fs-name", "course-id");
        FeedbackSession createdFs = fsDb.getSoftDeletedFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs);
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionCourseId = "course-id' OR 1 = 1;--";
        FeedbackSession nonExistentFs = fsDb.getSoftDeletedFeedbackSession("fs-name", sqlInjectionCourseId);
        assertNull(nonExistentFs);
    }

    @Test
    public void testGetSoftDeletedFeedbackSessionsForCourse_sqlInjectionAttempt_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        fsDb.softDeleteFeedbackSession("fs-name", "course-id");
        List<FeedbackSession> sessions = fsDb.getSoftDeletedFeedbackSessionsForCourse("course-id");
        assertEquals(1, sessions.size());
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.deleted_at is not null and
        // f1_0.course_id=?
        String sqlInjectionCourseId = "course-id' OR 1 = 1;--";
        List<FeedbackSession> nonExistentSessions = fsDb.getSoftDeletedFeedbackSessionsForCourse(sqlInjectionCourseId);
        assertEquals(0, nonExistentSessions.size());
    }

    @Test
    public void testGetFeedbackSessionEntitiesForCourse_sqlInjectionAttempt_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionEntitiesForCourse("course-id");
        assertEquals(1, sessions.size());
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.course_id=?
        String sqlInjectionCourseId = "course-id' OR 1 = 1;--";
        List<FeedbackSession> nonExistentSessions = fsDb.getFeedbackSessionEntitiesForCourse(sqlInjectionCourseId);
        assertEquals(0, nonExistentSessions.size());
    }

    @Test
    public void testGetFeedbackSessionEntitiesForCourseStartingAfter_sqlInjectionAttempt_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        Instant beforeStart = Instant.now().minus(Duration.ofDays(7L));
        List<FeedbackSession> sessions =
                fsDb.getFeedbackSessionEntitiesForCourseStartingAfter("course-id", beforeStart);
        assertEquals(1, sessions.size());
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.start_time>=? and
        // f1_0.course_id=?
        String sqlInjectionCourseId = "course-id' OR 1 = 1;--";
        List<FeedbackSession> nonExistentSessions =
                fsDb.getFeedbackSessionEntitiesForCourseStartingAfter(sqlInjectionCourseId, beforeStart);
        assertEquals(0, nonExistentSessions.size());
    }

    // It is not possible to use quotation marks in a FeedbackSession name. It is not known if it is possible to do SQL
    // injection here. We keep this here to show it was not missed out.
    // @Test
    // public void testUpdateFeedbackSession_sqlInjectionAttemptIntoName_notKnownToBePossible()
    //         throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {}

    @Test
    public void testUpdateFeedbackSession_sqlInjectionAttemptIntoCreatorEmail_shouldNotThrowException()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        fs.setId(createdFs.getId());
        // It is not possible to use a semicolon in an email address. Instead, we simply check if it throws an error for
        // invalid SQL syntax.
        String sqlInjectionCreatorEmail = "instructor'@gmail.com"; // Unbalanced single quotation mark.
        fs.setCreatorEmail(sqlInjectionCreatorEmail);
        fsDb.updateFeedbackSession(fs);
        FeedbackSession updatedFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertEquals(sqlInjectionCreatorEmail, updatedFs.getCreatorEmail());
    }

    @Test
    public void testUpdateFeedbackSession_sqlInjectionAttemptIntoInstructions_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        fs.setId(createdFs.getId());
        // update feedback_sessions set course_id=?, creator_email=?, deleted_at=?, end_time=?, grace_period=?,
        // instructions=?, is_closed_email_sent=?, is_closing_email_enabled=?, is_closing_soon_email_sent=?,
        // is_open_email_sent=?, is_opening_email_enabled=?, is_opening_soon_email_sent=?, is_published_email_enabled=?,
        // is_published_email_sent=?, name=?, results_visible_from_time=?, session_visible_from_time=?, start_time=?,
        // updated_at=? where id=?
        String sqlInjectionInstructions = "new-instructions'; DROP TABLE feedback_sessions;--";
        fs.setInstructions(sqlInjectionInstructions);
        fsDb.updateFeedbackSession(fs);
        FeedbackSession updatedFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertEquals(SanitizationHelper.sanitizeForRichText(sqlInjectionInstructions), updatedFs.getInstructions());
    }

    @Test
    public void testRestoreDeletedFeedbackSession_sqlInjectionAttemptIntoName_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        fsDb.softDeleteFeedbackSession("fs-name", "course-id");
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionName = "fs-name'; DROP TABLE feedback_sessions;--";
        assertThrows(EntityDoesNotExistException.class,
                () -> fsDb.restoreDeletedFeedbackSession(sqlInjectionName, "course-id"));
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs.getDeletedAt());
    }

    @Test
    public void testRestoreDeletedFeedbackSession_sqlInjectionAttemptIntoCourseId_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        fsDb.softDeleteFeedbackSession("fs-name", "course-id");
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionCourseId = "course-id'; DROP TABLE feedback_sessions;--";
        assertThrows(EntityDoesNotExistException.class,
                () -> fsDb.restoreDeletedFeedbackSession("fs-name", sqlInjectionCourseId));
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNotNull(createdFs.getDeletedAt());
    }

    @Test
    public void testSoftDeleteFeedbackSession_sqlInjectionAttemptIntoName_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionName = "fs-name'; DROP TABLE feedback_sessions;--";
        assertThrows(EntityDoesNotExistException.class,
                () -> fsDb.softDeleteFeedbackSession(sqlInjectionName, "course-id"));
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNull(createdFs.getDeletedAt());
    }

    @Test
    public void testSoftDeleteFeedbackSession_sqlInjectionAttemptIntoCourseId_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Course course = createTypicalCourse();
        coursesDb.createCourse(course);
        FeedbackSession fs = createTypicalFeedbackSession(course);
        fsDb.createFeedbackSession(fs);
        // select f1_0.id,f1_0.course_id,f1_0.created_at,f1_0.creator_email,f1_0.deleted_at,f1_0.end_time,
        // f1_0.grace_period,f1_0.instructions,f1_0.is_closed_email_sent,f1_0.is_closing_email_enabled,
        // f1_0.is_closing_soon_email_sent,f1_0.is_open_email_sent,f1_0.is_opening_email_enabled,
        // f1_0.is_opening_soon_email_sent,f1_0.is_published_email_enabled,f1_0.is_published_email_sent,f1_0.name,
        // f1_0.results_visible_from_time,f1_0.session_visible_from_time,f1_0.start_time,f1_0.updated_at from
        // feedback_sessions f1_0 join courses c1_0 on c1_0.id=f1_0.course_id where f1_0.name=? and f1_0.course_id=?
        String sqlInjectionCourseId = "course-id'; DROP TABLE feedback_sessions;--";
        assertThrows(EntityDoesNotExistException.class,
                () -> fsDb.softDeleteFeedbackSession("fs-name", sqlInjectionCourseId));
        FeedbackSession createdFs = fsDb.getFeedbackSession("fs-name", "course-id");
        assertNull(createdFs.getDeletedAt());
    }

    // The SQL query is
    // delete from feedback_sessions where id=?
    // It may not be possible to do injection with an ID that was not obtained from user input. We keep this here to
    // show it was not missed out.
    // @Test
    // public void testDeleteFeedbackSession_sqlInjectionAttempt_notKnownToBePossible()
    //         throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {}

    private Course createTypicalCourse() {
        return new Course("course-id", "course-name", "UTC", "NUS");
    }

    private FeedbackSession createTypicalFeedbackSession(Course course) {
        Instant instantNow = Instant.now();
        return new FeedbackSession("fs-name", course, "instructor@example.com", "instructions",
                instantNow.minus(Duration.ofHours(12L)), instantNow.plus(Duration.ofHours(12L)),
                instantNow.minus(Duration.ofDays(7L)), instantNow.plus(Duration.ofDays(7L)), Duration.ofMinutes(10L),
                true, true, true);
    }
}
