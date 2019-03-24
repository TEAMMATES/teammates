package teammates.test.cases.storage;

import static teammates.common.util.FieldValidator.SESSION_END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.SESSION_START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
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
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link FeedbackSessionsDb}.
 */
public class FeedbackSessionsDbTest extends BaseComponentTestCase {

    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
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
        assertEquals("should not return more than 13 sessions as there are only 13 distinct sessions in the range",
                13, actualAttributesList.size());
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
        verifyPresentInDatastore(fsa);

        ______TS("duplicate");
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class, () -> fsDb.createEntity(fsa));
        assertEquals(
                String.format(FeedbackSessionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, fsa.toString()), eaee.getMessage());

        fsDb.deleteFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId());
        verifyAbsentInDatastore(fsa);

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class, () -> fsDb.createEntity(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("invalid params");

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> fsDb.getFeedbackSession("idOfTypicalCourse1", null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("null courseId");

        ae = assertThrows(AssertionError.class,
                () -> fsDb.getFeedbackSession(null, "First feedback session"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class, () -> fsDb.getFeedbackSessionsForCourse(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class, () -> fsDb.getSoftDeletedFeedbackSessionsForCourse(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class, () -> fsDb.softDeleteFeedbackSession(null, null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

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

        assertEquals(11, fsaList.size());
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
        assertEquals(10, fsaList.size());
    }

    @Test
    public void testUpdateFeedbackSession() throws Exception {

        ______TS("null params");
        AssertionError ae = assertThrows(AssertionError.class, () -> fsDb.updateFeedbackSession(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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
                String.format(TIME_FRAME_ERROR_MESSAGE, SESSION_END_TIME_FIELD_NAME, SESSION_START_TIME_FIELD_NAME),
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
        verifyPresentInDatastore(modifiedSession);
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

    private FeedbackSessionAttributes getNewFeedbackSession() {
        return FeedbackSessionAttributes.builder("fsTest1", "testCourse")
                .withCreatorEmail("valid@email.com")
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-62))
                .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1))
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(0))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(1))
                .withGracePeriod(Duration.ofMinutes(5))
                .withInstructions("Give feedback.")
                .build();
    }

}
