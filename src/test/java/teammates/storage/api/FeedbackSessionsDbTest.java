package teammates.storage.api;

import static teammates.common.util.FieldValidator.SESSION_END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.SESSION_NAME;
import static teammates.common.util.FieldValidator.SESSION_START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_BEFORE_ERROR_MESSAGE;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.JsonUtils;
import teammates.common.util.TimeHelperExtension;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;
import teammates.test.ThreadHelper;

/**
 * SUT: {@link FeedbackSessionsDb}.
 */
public class FeedbackSessionsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();
    private DataBundle dataBundle = getTypicalDataBundle();

    @BeforeMethod
    public void addSessionsToDb() throws Exception {
        Set<String> keys = dataBundle.feedbackSessions.keySet();
        for (String i : keys) {
            fsDb.createEntity(dataBundle.feedbackSessions.get(i));
        }
    }

    @AfterMethod
    public void deleteSessionsFromDb() {
        Set<String> keys = dataBundle.feedbackSessions.keySet();
        for (String i : keys) {
            FeedbackSessionAttributes sessionToDelete = dataBundle.feedbackSessions.get(i);
            fsDb.deleteFeedbackSession(sessionToDelete.getFeedbackSessionName(), sessionToDelete.getCourseId());
        }
        FeedbackSessionAttributes sessionToDelete = getNewFeedbackSession();
        fsDb.deleteFeedbackSession(sessionToDelete.getFeedbackSessionName(), sessionToDelete.getCourseId());
    }

    @Test
    public void testGetAllOngoingSessions_typicalCase_shouldQuerySuccessfullyWithoutDuplication() {
        Instant rangeStart = Instant.parse("2000-12-03T10:15:30.00Z");
        Instant rangeEnd = Instant.parse("2050-04-30T21:59:00Z");
        List<FeedbackSessionAttributes> actualAttributesList = fsDb.getAllOngoingSessions(rangeStart, rangeEnd);
        assertEquals("should not return more than 14 sessions as there are only 14 distinct sessions in the range",
                14, actualAttributesList.size());
    }

    @Test
    public void testDeleteFeedbackSession() throws Exception {
        FeedbackSessionAttributes fsa = getNewFeedbackSession();
        fsDb.createEntity(fsa);
        fsa = fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertNotNull(fsa);

        ______TS("non-existent course ID");

        fsDb.deleteFeedbackSession(fsa.getFeedbackSessionName(), "not_exist");

        assertNotNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));

        ______TS("non-existent session name");

        fsDb.deleteFeedbackSession("not_exist", fsa.getCourseId());

        assertNotNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));

        ______TS("non-existent course ID and session name");

        fsDb.deleteFeedbackSession("not_exist", "not_exist");

        assertNotNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));

        ______TS("standard success case");

        fsDb.deleteFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId());

        assertNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));
    }

    @Test
    public void testDeleteFeedbackSessions_byCourseId() throws Exception {
        FeedbackSessionAttributes fsa = getNewFeedbackSession();
        fsDb.createEntity(fsa);
        fsa = fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName());
        assertNotNull(fsa);

        FeedbackSessionAttributes anotherFas = getNewFeedbackSession();
        anotherFas.setCourseId("courseId");
        fsDb.createEntity(anotherFas);
        anotherFas = fsDb.getFeedbackSession(anotherFas.getCourseId(), anotherFas.getFeedbackSessionName());
        assertNotNull(anotherFas);

        ______TS("non-existent course ID");

        fsDb.deleteFeedbackSessions(
                AttributesDeletionQuery.builder()
                        .withCourseId("non_exist")
                        .build());

        assertNotNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        assertNotNull(fsDb.getFeedbackSession(anotherFas.getCourseId(), anotherFas.getFeedbackSessionName()));

        ______TS("standard success case");

        fsDb.deleteFeedbackSessions(
                AttributesDeletionQuery.builder()
                        .withCourseId(fsa.getCourseId())
                        .build());

        assertNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        assertNotNull(fsDb.getFeedbackSession(anotherFas.getCourseId(), anotherFas.getFeedbackSessionName()));

        fsDb.deleteFeedbackSessions(
                AttributesDeletionQuery.builder()
                        .withCourseId(anotherFas.getCourseId())
                        .build());

        assertNull(fsDb.getFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()));
        assertNull(fsDb.getFeedbackSession(anotherFas.getCourseId(), anotherFas.getFeedbackSessionName()));
    }

    @Test
    public void testCreateDeleteFeedbackSession()
            throws Exception {

        ______TS("standard success case");

        FeedbackSessionAttributes fsa = getNewFeedbackSession();
        fsDb.createEntity(fsa);
        verifyPresentInDatabase(fsa);

        ______TS("duplicate");
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class, () -> fsDb.createEntity(fsa));
        assertEquals(
                String.format(FeedbackSessionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, fsa.toString()), eaee.getMessage());

        fsDb.deleteFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId());
        verifyAbsentInDatabase(fsa);

        ______TS("null params");

        assertThrows(AssertionError.class, () -> fsDb.createEntity(null));

        ______TS("invalid params");

        // wait for very briefly so that the start timestamp is guaranteed to change
        ThreadHelper.waitFor(5);

        fsa.setStartTime(Instant.now());
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class, () -> fsDb.createEntity(fsa));
        AssertHelper.assertContains("start time", ipe.getLocalizedMessage());

    }

    @Test
    public void testGetSoftDeletedFeedbackSession_typicalCase_shouldGetDeletedSession() {
        assertNotNull(fsDb.getSoftDeletedFeedbackSession("idOfTypicalCourse4",
                "First feedback session"));
    }

    @Test
    public void testGetSoftDeletedFeedbackSession_sessionIsNotDeleted_shouldReturnNull() {
        assertNotNull(fsDb.getFeedbackSession("idOfTypicalCourse2", "Instructor feedback session"));
        assertNull(fsDb.getSoftDeletedFeedbackSession("idOfTypicalCourse2", "Instructor feedback session"));
    }

    @Test
    public void testAllGetFeedbackSessions() {

        testGetFeedbackSessions();
        testGetFeedbackSessionsForCourse();
        testGetSoftDeletedFeedbackSessionsForCourse();
    }

    private void testGetFeedbackSessions() {

        ______TS("standard success case");

        FeedbackSessionAttributes expected =
                dataBundle.feedbackSessions.get("session1InCourse2");
        FeedbackSessionAttributes actual =
                fsDb.getFeedbackSession("idOfTypicalCourse2", "Instructor feedback session");

        assertEquals(expected.toString(), actual.toString());

        ______TS("non-existant session");

        assertNull(fsDb.getFeedbackSession("non-course", "Non-existant feedback session"));

        ______TS("soft-deleted session");

        assertNotNull(fsDb.getSoftDeletedFeedbackSession("idOfTypicalCourse4", "First feedback session"));
        assertNull(fsDb.getFeedbackSession("idOfTypicalCourse4", "First feedback session"));

        ______TS("null fsName");

        assertThrows(AssertionError.class,
                () -> fsDb.getFeedbackSession("idOfTypicalCourse1", null));

        ______TS("null courseId");

        assertThrows(AssertionError.class,
                () -> fsDb.getFeedbackSession(null, "First feedback session"));

    }

    private void testGetFeedbackSessionsForCourse() {

        ______TS("standard success case");

        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsForCourse("idOfTypicalCourse1");

        String expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("session2InCourse1").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("empty.session").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("awaiting.session").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("closedSession").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("gracePeriodSession").toString() + System.lineSeparator();

        for (FeedbackSessionAttributes session : sessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertEquals(6, sessions.size());

        ______TS("null params");

        assertThrows(AssertionError.class, () -> fsDb.getFeedbackSessionsForCourse(null));

        ______TS("non-existant course");

        assertTrue(fsDb.getFeedbackSessionsForCourse("non-existant course").isEmpty());

        ______TS("no sessions in course");

        assertTrue(fsDb.getFeedbackSessionsForCourse("idOfCourseNoEvals").isEmpty());
    }

    private void testGetSoftDeletedFeedbackSessionsForCourse() {

        ______TS("standard success case");

        List<FeedbackSessionAttributes> softDeletedSessions = fsDb
                .getSoftDeletedFeedbackSessionsForCourse("idOfTypicalCourse3");

        String expected =
                dataBundle.feedbackSessions.get("session2InCourse3").toString() + System.lineSeparator();

        for (FeedbackSessionAttributes session : softDeletedSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertEquals(1, softDeletedSessions.size());

        ______TS("null params");

        assertThrows(AssertionError.class, () -> fsDb.getSoftDeletedFeedbackSessionsForCourse(null));

        ______TS("non-existant course");

        assertTrue(fsDb.getSoftDeletedFeedbackSessionsForCourse("non-existant course").isEmpty());

        ______TS("no sessions in course");

        assertTrue(fsDb.getSoftDeletedFeedbackSessionsForCourse("idOfCourseNoEvals").isEmpty());
    }

    @Test
    public void testSoftDeleteFeedbackSession() throws Exception {
        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fsDb.createEntity(fs);

        ______TS("Success: soft delete an existing feedback session");
        fsDb.softDeleteFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());

        assertNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertNotNull(fsDb.getSoftDeletedFeedbackSession(fs.getCourseId(),
                fs.getFeedbackSessionName()));

        ______TS("Success: restore soft deleted course");
        fsDb.restoreDeletedFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());

        assertNull(fsDb.getSoftDeletedFeedbackSession(fs.getCourseId(),
                fs.getFeedbackSessionName()));
        assertNotNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
        assertFalse(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()).isSessionDeleted());

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> fsDb.softDeleteFeedbackSession(null, null));

    }

    @Test
    public void testGetFeedbackSessionsPossiblyNeedingOpeningSoonEmail() throws Exception {
        ______TS("standard success case");

        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsPossiblyNeedingOpeningSoonEmail();

        assertEquals(1, fsaList.size());
        for (FeedbackSessionAttributes fsa : fsaList) {
            assertFalse(fsa.isSentOpeningSoonEmail());
            assertFalse(fsa.isSessionDeleted());
        }

        ______TS("soft-deleted session should not appear");

        // soft delete a feedback session now
        FeedbackSessionAttributes feedbackSession = fsaList.get(0);
        fsDb.softDeleteFeedbackSession(feedbackSession.getFeedbackSessionName(), feedbackSession.getCourseId());

        fsaList = fsDb.getFeedbackSessionsPossiblyNeedingOpeningSoonEmail();
        assertEquals(0, fsaList.size());
    }

    @Test
    public void testGetFeedbackSessionsPossiblyNeedingOpenEmail() throws Exception {

        ______TS("standard success case");

        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsPossiblyNeedingOpenEmail();

        assertEquals(1, fsaList.size());
        for (FeedbackSessionAttributes fsa : fsaList) {
            assertFalse(fsa.isSentOpenEmail());
            assertFalse(fsa.isSessionDeleted());
        }

        ______TS("soft-deleted session should not appear");

        // soft delete a feedback session now
        FeedbackSessionAttributes feedbackSession = fsaList.get(0);
        fsDb.softDeleteFeedbackSession(feedbackSession.getFeedbackSessionName(), feedbackSession.getCourseId());

        fsaList = fsDb.getFeedbackSessionsPossiblyNeedingOpenEmail();
        assertEquals(0, fsaList.size());
    }

    @Test
    public void testGetFeedbackSessionsPossiblyNeedingClosingEmail() throws Exception {

        ______TS("standard success case");

        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsPossiblyNeedingClosingEmail();

        assertEquals(9, fsaList.size());
        for (FeedbackSessionAttributes fsa : fsaList) {
            assertFalse(fsa.isSentClosingEmail());
            assertTrue(fsa.isClosingEmailEnabled());
            assertFalse(fsa.isSessionDeleted());
        }

        ______TS("soft-deleted session should not appear");

        // soft delete a feedback session now
        FeedbackSessionAttributes feedbackSession = fsaList.get(0);
        fsDb.softDeleteFeedbackSession(feedbackSession.getFeedbackSessionName(), feedbackSession.getCourseId());

        fsaList = fsDb.getFeedbackSessionsPossiblyNeedingClosingEmail();
        assertEquals(8, fsaList.size());
    }

    @Test
    public void testGetFeedbackSessionsPossiblyNeedingClosedEmail() throws Exception {

        ______TS("standard success case");

        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsPossiblyNeedingClosedEmail();

        assertEquals(9, fsaList.size());
        for (FeedbackSessionAttributes fsa : fsaList) {
            assertFalse(fsa.isSentClosedEmail());
            assertTrue(fsa.isClosingEmailEnabled());
            assertFalse(fsa.isSessionDeleted());
        }

        ______TS("soft-deleted session should not appear");

        // soft delete a feedback session now
        FeedbackSessionAttributes feedbackSession = fsaList.get(0);
        fsDb.softDeleteFeedbackSession(feedbackSession.getFeedbackSessionName(), feedbackSession.getCourseId());

        fsaList = fsDb.getFeedbackSessionsPossiblyNeedingClosedEmail();
        assertEquals(8, fsaList.size());
    }

    @Test
    public void testGetFeedbackSessionsPossiblyNeedingPublishedEmail() throws Exception {

        ______TS("standard success case");

        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsPossiblyNeedingPublishedEmail();

        assertEquals(8, fsaList.size());
        for (FeedbackSessionAttributes fsa : fsaList) {
            assertFalse(fsa.isSentPublishedEmail());
            assertTrue(fsa.isPublishedEmailEnabled());
            assertFalse(fsa.isSessionDeleted());
        }

        ______TS("soft-deleted session should not appear");

        // soft delete a feedback session now
        FeedbackSessionAttributes feedbackSession = fsaList.get(0);
        fsDb.softDeleteFeedbackSession(feedbackSession.getFeedbackSessionName(), feedbackSession.getCourseId());

        fsaList = fsDb.getFeedbackSessionsPossiblyNeedingPublishedEmail();
        assertEquals(7, fsaList.size());
    }

    @Test
    public void testUpdateFeedbackSession_noChangeToSession_shouldNotIssueSaveRequest() throws Exception {
        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fs = fsDb.putEntity(fs);

        FeedbackSessionAttributes updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(fs.getFeedbackSessionName(), fs.getCourseId())
                        .build());

        assertEquals(JsonUtils.toJson(fs), JsonUtils.toJson(updatedFs));

        // please verify the log message manually to ensure that saving request is not issued

        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(fs.getFeedbackSessionName(), fs.getCourseId())
                        .withInstructions(fs.getInstructions())
                        .withStartTime(fs.getStartTime())
                        .withEndTime(fs.getEndTime())
                        .withSessionVisibleFromTime(fs.getSessionVisibleFromTime())
                        .withResultsVisibleFromTime(fs.getResultsVisibleFromTime())
                        .withTimeZone(fs.getTimeZone())
                        .withGracePeriod(Duration.ofMinutes(fs.getGracePeriodMinutes()))
                        .withSentOpenEmail(fs.isSentOpenEmail())
                        .withSentClosingEmail(fs.isSentClosingEmail())
                        .withSentClosedEmail(fs.isSentClosedEmail())
                        .withSentPublishedEmail(fs.isSentPublishedEmail())
                        .withIsClosingEmailEnabled(fs.isClosingEmailEnabled())
                        .withIsPublishedEmailEnabled(fs.isPublishedEmailEnabled())
                        .withStudentDeadlines(fs.getStudentDeadlines())
                        .withInstructorDeadlines(fs.getInstructorDeadlines())
                        .build());

        assertEquals(JsonUtils.toJson(fs), JsonUtils.toJson(updatedFs));

        // please verify the log message manually to ensure that saving request is not issued
    }

    @Test
    public void testUpdateFeedbackSession() throws Exception {

        ______TS("null params");
        assertThrows(AssertionError.class, () -> fsDb.updateFeedbackSession(null));

        ______TS("invalid feedback session attributes");
        FeedbackSessionAttributes invalidFs = getNewFeedbackSession();
        fsDb.deleteFeedbackSession(invalidFs.getFeedbackSessionName(), invalidFs.getCourseId());
        fsDb.createEntity(invalidFs);
        Instant afterEndTime = invalidFs.getEndTime().plus(Duration.ofDays(30));
        invalidFs.setStartTime(afterEndTime);
        invalidFs.setResultsVisibleFromTime(afterEndTime);
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> fsDb.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(invalidFs.getFeedbackSessionName(), invalidFs.getCourseId())
                                .withStartTime(invalidFs.getStartTime())
                                .withResultsVisibleFromTime(invalidFs.getResultsVisibleFromTime())
                                .build()));
        assertEquals(
                String.format(TIME_BEFORE_ERROR_MESSAGE, SESSION_END_TIME_FIELD_NAME, SESSION_NAME,
                        SESSION_START_TIME_FIELD_NAME),
                ipe.getLocalizedMessage());

        ______TS("feedback session does not exist");
        FeedbackSessionAttributes nonexistantFs = getNewFeedbackSession();
        nonexistantFs.setFeedbackSessionName("non existant fs");
        nonexistantFs.setCourseId("non.existant.course");
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsDb.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(nonexistantFs.getFeedbackSessionName(), nonexistantFs.getCourseId())
                                .withInstructions("test")
                                .build()));
        AssertHelper.assertContains(FeedbackSessionsDb.ERROR_UPDATE_NON_EXISTENT, ednee.getLocalizedMessage());

        ______TS("standard success case");
        FeedbackSessionAttributes modifiedSession = getNewFeedbackSession();
        fsDb.deleteFeedbackSession(modifiedSession.getFeedbackSessionName(), modifiedSession.getCourseId());
        fsDb.createEntity(modifiedSession);
        verifyPresentInDatabase(modifiedSession);
        modifiedSession.setInstructions("new instructions");
        modifiedSession.setGracePeriodMinutes(0);
        modifiedSession.setSentOpenEmail(false);
        FeedbackSessionAttributes updatedSession = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(modifiedSession.getFeedbackSessionName(), modifiedSession.getCourseId())
                        .withInstructions(modifiedSession.getInstructions())
                        .withGracePeriod(Duration.ofMinutes(modifiedSession.getGracePeriodMinutes()))
                        .withSentOpenEmail(modifiedSession.isSentOpenEmail())
                        .build());
        FeedbackSessionAttributes actualFs =
                fsDb.getFeedbackSession(modifiedSession.getCourseId(), modifiedSession.getFeedbackSessionName());
        assertEquals(JsonUtils.toJson(modifiedSession), JsonUtils.toJson(actualFs));
        assertEquals(JsonUtils.toJson(modifiedSession), JsonUtils.toJson(updatedSession));
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateFeedbackSession_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        FeedbackSessionAttributes typicalFs = getNewFeedbackSession();
        typicalFs = fsDb.putEntity(typicalFs);

        assertNotEquals("new instructions", typicalFs.getInstructions());
        FeedbackSessionAttributes updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withInstructions("new instructions")
                        .build());
        FeedbackSessionAttributes actualFs =
                fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals("new instructions", updatedFs.getInstructions());
        assertEquals("new instructions", actualFs.getInstructions());

        Instant startTime = typicalFs.getStartTime().plus(Duration.ofHours(1));
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withStartTime(startTime)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(startTime, updatedFs.getStartTime());
        assertEquals(startTime, actualFs.getStartTime());

        Instant endTime = typicalFs.getEndTime().plus(Duration.ofHours(1));
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withEndTime(endTime)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(endTime, updatedFs.getEndTime());
        assertEquals(endTime, actualFs.getEndTime());

        Instant sessionVisibleTime = typicalFs.getSessionVisibleFromTime().plus(Duration.ofHours(1));
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withSessionVisibleFromTime(sessionVisibleTime)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(sessionVisibleTime, updatedFs.getSessionVisibleFromTime());
        assertEquals(sessionVisibleTime, actualFs.getSessionVisibleFromTime());

        Instant resultVisibleTime = typicalFs.getResultsVisibleFromTime().plus(Duration.ofHours(1));
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withResultsVisibleFromTime(resultVisibleTime)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(resultVisibleTime, updatedFs.getResultsVisibleFromTime());
        assertEquals(resultVisibleTime, actualFs.getResultsVisibleFromTime());

        assertNotEquals("Asia/Singapore", actualFs.getTimeZone());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withTimeZone("Asia/Singapore")
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals("Asia/Singapore", updatedFs.getTimeZone());
        assertEquals("Asia/Singapore", actualFs.getTimeZone());

        assertNotEquals(10, actualFs.getGracePeriodMinutes());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withGracePeriod(Duration.ofMinutes(10))
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(10, updatedFs.getGracePeriodMinutes());
        assertEquals(10, actualFs.getGracePeriodMinutes());

        assertFalse(actualFs.isSentOpeningSoonEmail());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withSentOpeningSoonEmail(true)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertTrue(updatedFs.isSentOpeningSoonEmail());
        assertTrue(actualFs.isSentOpeningSoonEmail());

        assertFalse(actualFs.isSentOpenEmail());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withSentOpenEmail(true)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertTrue(updatedFs.isSentOpenEmail());
        assertTrue(actualFs.isSentOpenEmail());

        assertFalse(actualFs.isSentClosingEmail());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withSentClosingEmail(true)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertTrue(updatedFs.isSentClosingEmail());
        assertTrue(actualFs.isSentClosingEmail());

        assertFalse(actualFs.isSentClosedEmail());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withSentClosedEmail(true)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertTrue(updatedFs.isSentClosedEmail());
        assertTrue(actualFs.isSentClosedEmail());

        assertFalse(actualFs.isSentPublishedEmail());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withSentPublishedEmail(true)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertTrue(updatedFs.isSentPublishedEmail());
        assertTrue(actualFs.isSentPublishedEmail());

        assertTrue(actualFs.isClosingEmailEnabled());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withIsClosingEmailEnabled(false)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertFalse(updatedFs.isClosingEmailEnabled());
        assertFalse(actualFs.isClosingEmailEnabled());

        assertTrue(actualFs.isPublishedEmailEnabled());
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withIsPublishedEmailEnabled(false)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertFalse(updatedFs.isPublishedEmailEnabled());
        assertFalse(actualFs.isPublishedEmailEnabled());

        assertEquals(new HashMap<>(), actualFs.getStudentDeadlines());
        Map<String, Instant> newStudentDeadlines = new HashMap<>();
        newStudentDeadlines.put("student@school.edu", updatedFs.getEndTime().plus(Duration.ofHours(1)));
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withStudentDeadlines(newStudentDeadlines)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(newStudentDeadlines, updatedFs.getStudentDeadlines());
        assertEquals(newStudentDeadlines, actualFs.getStudentDeadlines());

        assertEquals(new HashMap<>(), actualFs.getInstructorDeadlines());
        Map<String, Instant> newInstructorDeadlines = new HashMap<>();
        newInstructorDeadlines.put("instructor@school.edu", updatedFs.getEndTime().plus(Duration.ofHours(1)));
        updatedFs = fsDb.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(typicalFs.getFeedbackSessionName(), typicalFs.getCourseId())
                        .withInstructorDeadlines(newInstructorDeadlines)
                        .build());
        actualFs = fsDb.getFeedbackSession(typicalFs.getCourseId(), typicalFs.getFeedbackSessionName());
        assertEquals(newInstructorDeadlines, updatedFs.getInstructorDeadlines());
        assertEquals(newInstructorDeadlines, actualFs.getInstructorDeadlines());
    }

    private FeedbackSessionAttributes getNewFeedbackSession() {
        return FeedbackSessionAttributes.builder("fsTest1", "testCourse")
                .withCreatorEmail("valid@email.com")
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(2))
                .withStartTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(2))
                .withEndTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(7))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(7))
                .withGracePeriod(Duration.ofMinutes(5))
                .withInstructions("Give feedback.")
                .build();
    }

}
