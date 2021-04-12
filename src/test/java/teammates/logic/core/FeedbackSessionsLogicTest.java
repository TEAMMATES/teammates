package teammates.logic.core;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.test.AssertHelper;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicTest extends BaseLogicTest {
    private static CoursesLogic coursesLogic = CoursesLogic.inst();
    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();

    @Override
    protected void prepareTestData() {
        // see beforeMethod()
    }

    @BeforeMethod
    public void beforeMethod() {
        dataBundle = loadDataBundle("/FeedbackSessionsLogicTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Test
    public void testDeleteFeedbackSessionCascade_deleteSessionNotInRecycleBin_shouldDoCascadeDeletion()
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        assertNotNull(fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertFalse(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertFalse(
                frLogic.getFeedbackResponsesForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertFalse(
                frcLogic.getFeedbackResponseCommentForSessionInSection(fsa.getCourseId(), fsa.getFeedbackSessionName(), null)
                        .isEmpty());

        // delete existing feedback session directly
        fsLogic.deleteFeedbackSessionCascade(fsa.getFeedbackSessionName(), fsa.getCourseId());

        assertNull(fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertTrue(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertTrue(
                frcLogic.getFeedbackResponseCommentForSessionInSection(fsa.getCourseId(), fsa.getFeedbackSessionName(), null)
                        .isEmpty());
    }

    @Test
    public void testDeleteFeedbackSessionCascade_deleteSessionInRecycleBin_shouldDoCascadeDeletion()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        assertFalse(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertFalse(
                frLogic.getFeedbackResponsesForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertFalse(
                frcLogic.getFeedbackResponseCommentForSessionInSection(fsa.getCourseId(), fsa.getFeedbackSessionName(), null)
                        .isEmpty());
        fsLogic.moveFeedbackSessionToRecycleBin(fsa.getFeedbackSessionName(), fsa.getCourseId());
        assertNull(fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertNotNull(fsLogic.getFeedbackSessionFromRecycleBin(fsa.getFeedbackSessionName(), fsa.getCourseId()));

        // delete feedback session in recycle bin
        fsLogic.deleteFeedbackSessionCascade(fsa.getFeedbackSessionName(), fsa.getCourseId());

        assertNull(fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertTrue(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertTrue(
                frcLogic.getFeedbackResponseCommentForSessionInSection(fsa.getCourseId(), fsa.getFeedbackSessionName(), null)
                        .isEmpty());
    }

    @Test
    public void testDeleteFeedbackSessions_byCourseId_shouldDeleteAllSessionsUnderCourse() {
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        assertNotNull(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId()));
        FeedbackSessionAttributes session2InCourse1 = dataBundle.feedbackSessions.get("session2InCourse1");
        assertNotNull(
                fsLogic.getFeedbackSession(session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId()));
        // they are in the same course
        assertEquals(session1InCourse1.getCourseId(), session2InCourse1.getCourseId());
        FeedbackSessionAttributes session1InCourse2 = dataBundle.feedbackSessions.get("session1InCourse2");
        assertNotNull(
                fsLogic.getFeedbackSession(session1InCourse2.getFeedbackSessionName(), session1InCourse2.getCourseId()));

        // delete all session under the course
        fsLogic.deleteFeedbackSessions(
                AttributesDeletionQuery.builder()
                        .withCourseId(session1InCourse1.getCourseId())
                        .build());

        // they should gone
        assertNull(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId()));
        assertNull(
                fsLogic.getFeedbackSession(session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId()));
        // sessions in different courses should not be affected
        assertNotNull(
                fsLogic.getFeedbackSession(session1InCourse2.getFeedbackSessionName(), session1InCourse2.getCourseId()));
    }

    @Test
    public void testFeedbackSessionNotification() throws Exception {
        testGetFeedbackSessionsClosingWithinTimeLimit();
        testGetFeedbackSessionsClosedWithinThePastHour();
        testGetFeedbackSessionsWhichNeedOpenMailsToBeSent();
        testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent();
    }

    @Test
    public void testAll() throws Exception {

        testGetSoftDeletedFeedbackSessionsListForInstructors();
        testIsFeedbackSessionViewableToStudents();

        testCreateAndDeleteFeedbackSession();

        testUpdateFeedbackSession();
        testPublishUnpublishFeedbackSession();

        testIsFeedbackSessionHasQuestionForStudents();
        testIsFeedbackSessionCompletedByStudent();
        testIsFeedbackSessionCompletedByInstructor();
        testIsFeedbackSessionFullyCompletedByStudent();

        testMoveFeedbackSessionToRecycleBin();
        testRestoreFeedbackSessionFromRecycleBin();
    }

    private void testGetSoftDeletedFeedbackSessionsListForInstructors() {
        List<FeedbackSessionAttributes> softDeletedFsa = new ArrayList<>();
        Collection<FeedbackSessionAttributes> allFsa = dataBundle.feedbackSessions.values();

        String courseId = dataBundle.courses.get("typicalCourse3").getId();
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse3");

        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(instructor);

        for (FeedbackSessionAttributes fsa : allFsa) {
            if (fsa.getCourseId().equals(courseId) && fsa.isSessionDeleted()) {
                softDeletedFsa.add(fsa);
            }
        }
        AssertHelper.assertSameContentIgnoreOrder(
                softDeletedFsa, fsLogic.getSoftDeletedFeedbackSessionsListForInstructors(instructors));

    }

    private void testIsFeedbackSessionHasQuestionForStudents() throws Exception {
        // no need to removeAndRestoreTypicalDataInDatastore() as the previous test does not change the db

        FeedbackSessionAttributes sessionWithStudents = dataBundle.feedbackSessions.get("gracePeriodSession");
        FeedbackSessionAttributes sessionWithoutStudents = dataBundle.feedbackSessions.get("closedSession");

        ______TS("non-existent session/courseId");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.isFeedbackSessionHasQuestionForStudents("nOnEXistEnT session", "someCourse"));
        assertEquals("Trying to check a non-existent feedback session: someCourse/nOnEXistEnT session",
                ednee.getMessage());

        ______TS("session contains students");

        assertTrue(fsLogic.isFeedbackSessionHasQuestionForStudents(sessionWithStudents.getFeedbackSessionName(),
                                                                   sessionWithStudents.getCourseId()));

        ______TS("session does not contain students");

        assertFalse(fsLogic.isFeedbackSessionHasQuestionForStudents(sessionWithoutStudents.getFeedbackSessionName(),
                                                                    sessionWithoutStudents.getCourseId()));
    }

    private void testGetFeedbackSessionsClosingWithinTimeLimit() throws Exception {

        ______TS("init : 0 standard sessions closing within time-limit");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsClosingWithinTimeLimit();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session in undeleted course closing within time-limit");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone(ZoneId.of("UTC"));
        session.setSessionVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        fsLogic.createFeedbackSession(session);
        coursesLogic.createCourse(
                CourseAttributes.builder(session.getCourseId())
                        .withName("Test Course")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());

        // wait for very briefly so that the above session will be within the time limit
        ThreadHelper.waitFor(5);

        sessionList = fsLogic.getFeedbackSessionsClosingWithinTimeLimit();

        assertEquals(1, sessionList.size());
        assertEquals(session.getFeedbackSessionName(), sessionList.get(0).getFeedbackSessionName());

        ______TS("case : 1 open session in deleted course closing within time-limit");
        coursesLogic.moveCourseToRecycleBin(session.getCourseId());
        sessionList = fsLogic.getFeedbackSessionsClosingWithinTimeLimit();

        assertEquals(0, sessionList.size());

        // restore the new course from Recycle Bin, and delete the newly added session as
        // removeAndRestoreTypicalDataInDatastore() wont do it
        coursesLogic.restoreCourseFromRecycleBin(session.getCourseId());
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(), session.getCourseId());
    }

    private void testGetFeedbackSessionsClosedWithinThePastHour() throws Exception {

        ______TS("init : 0 standard sessions closed within the past hour");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsClosedWithinThePastHour();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 closed session in undeleted course within the past hour");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone(ZoneId.of("UTC"));
        session.setSessionVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setEndTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-59));
        fsLogic.createFeedbackSession(session);

        sessionList = fsLogic.getFeedbackSessionsClosedWithinThePastHour();

        assertEquals(1, sessionList.size());
        assertEquals(session.getFeedbackSessionName(), sessionList.get(0).getFeedbackSessionName());

        ______TS("case : 1 closed session in deleted course within the past hour");
        coursesLogic.moveCourseToRecycleBin(session.getCourseId());
        sessionList = fsLogic.getFeedbackSessionsClosedWithinThePastHour();

        assertEquals(0, sessionList.size());

        // restore the new course from Recycle Bin, and delete the newly added session as
        // removeAndRestoreTypicalDataInDatastore() wont do it
        coursesLogic.restoreCourseFromRecycleBin(session.getCourseId());
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(), session.getCourseId());
    }

    private void testGetFeedbackSessionsWhichNeedOpenMailsToBeSent() throws Exception {

        ______TS("init : 0 open sessions");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session in undeleted course with mail unsent");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone(ZoneId.of("UTC"));
        session.setSessionVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session.setStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-23));
        session.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        session.setSentOpenEmail(false);
        fsLogic.createFeedbackSession(session);

        sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).getFeedbackSessionName(), session.getFeedbackSessionName());

        ______TS("case : 1 open session in undeleted course with mail sent");
        session.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentOpenEmail(session.isSentOpenEmail())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 closed session in undeleted course with mail unsent");
        session.setSentOpenEmail(false);
        session.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentOpenEmail(session.isSentOpenEmail())
                        .withEndTime(session.getEndTime())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session in deleted course with mail unsent");
        coursesLogic.moveCourseToRecycleBin(session.getCourseId());
        session.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(1));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withEndTime(session.getEndTime())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session in deleted course with mail sent");
        session.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentOpenEmail(session.isSentOpenEmail())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 closed session in deleted course with mail unsent");
        session.setSentOpenEmail(false);
        session.setEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1));
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentOpenEmail(session.isSentOpenEmail())
                        .withEndTime(session.getEndTime())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        // restore the new course from Recycle Bin, and delete the newly added session as
        // removeAndRestoreTypicalDataInDatastore() wont do it
        coursesLogic.restoreCourseFromRecycleBin(session.getCourseId());
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(), session.getCourseId());
    }

    private void testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent() throws Exception {

        ______TS("init : no published sessions");
        unpublishAllSessions();
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 published session in undeleted course with mail unsent");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        session.setTimeZone(ZoneId.of("UTC"));
        session.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2));
        session.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setSentPublishedEmail(false);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withTimeZone(session.getTimeZone())
                        .withStartTime(session.getStartTime())
                        .withEndTime(session.getEndTime())
                        .withResultsVisibleFromTime(session.getResultsVisibleFromTime())
                        .withSentPublishedEmail(session.isSentPublishedEmail())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).getFeedbackSessionName(), session.getFeedbackSessionName());

        ______TS("case : 1 published session in undeleted course with mail sent");
        session.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentPublishedEmail(session.isSentPublishedEmail())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 published session in deleted course with mail unsent");
        coursesLogic.moveCourseToRecycleBin(session.getCourseId());
        session.setSentPublishedEmail(false);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentPublishedEmail(session.isSentPublishedEmail())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 published session in deleted course with mail sent");
        session.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentPublishedEmail(session.isSentPublishedEmail())
                        .build());

        sessionList = fsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        assertEquals(0, sessionList.size());
    }

    private void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("test create");

        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fsLogic.createFeedbackSession(fs);
        verifyPresentInDatastore(fs);

        FeedbackSessionAttributes finalFs = fs;
        ______TS("test create with invalid session name");
        fs.setFeedbackSessionName("test & test");
        Exception e = assertThrows(Exception.class, () -> fsLogic.createFeedbackSession(finalFs));
        assertEquals(
                "The provided feedback session name is not acceptable to TEAMMATES "
                        + "as it cannot contain the following special html characters in brackets: "
                        + "(< > \" / ' &)",
                e.getMessage());

        fs.setFeedbackSessionName("test %| test");
        e = assertThrows(Exception.class, () -> fsLogic.createFeedbackSession(finalFs));
        assertEquals(
                "\"test %| test\" is not acceptable to TEAMMATES as a/an feedback session name "
                        + "because it contains invalid characters. A/An feedback session name "
                        + "must start with an alphanumeric character, and cannot contain "
                        + "any vertical bar (|) or percent sign (%).",
                e.getMessage());

        ______TS("test delete");
        fs = getNewFeedbackSession();
        // Create a question under the session to test for cascading during delete.
        FeedbackQuestionAttributes fq = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName(fs.getFeedbackSessionName())
                .withCourseId(fs.getCourseId())
                .withQuestionNumber(1)
                .withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.TEAMS)
                .withQuestionDetails(new FeedbackTextQuestionDetails("question to be deleted through cascade"))
                .withShowResponsesTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .build();

        fqLogic.createFeedbackQuestion(fq);

        fsLogic.deleteFeedbackSessionCascade(fs.getFeedbackSessionName(), fs.getCourseId());
        verifyAbsentInDatastore(fs);
        verifyAbsentInDatastore(fq);
    }

    private void testIsFeedbackSessionViewableToStudents() {
        ______TS("Session with questions for students to answer");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));

        ______TS("Session without questions for students, but with visible responses");
        session = dataBundle.feedbackSessions.get("archiveCourse.session1");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));

        session = dataBundle.feedbackSessions.get("session1InCourse2");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));

        ______TS("empty session");
        session = dataBundle.feedbackSessions.get("empty.session");
        assertFalse(fsLogic.isFeedbackSessionViewableToStudents(session));
    }

    private void testUpdateFeedbackSession() throws Exception {

        ______TS("failure: non-existent session name");
        FeedbackSessionAttributes.UpdateOptions updateOptions =
                FeedbackSessionAttributes.updateOptionsBuilder("asdf_randomName1423", "idOfTypicalCourse1")
                        .withInstructions("test")
                        .build();
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.updateFeedbackSession(updateOptions));
        assertEquals(
                "Trying to update a non-existent feedback session: "
                        + updateOptions.getCourseId() + "/" + updateOptions.getFeedbackSessionName(),
                ednee.getMessage());

        ______TS("success 1: typical case");
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        fsa.setInstructions("test");

        FeedbackSessionAttributes updatedFeedbackSession = fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(fsa.getFeedbackSessionName(), fsa.getCourseId())
                        .withInstructions(fsa.getInstructions())
                        .build());

        assertEquals(fsa.toString(), fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).toString());
        assertEquals(fsa.toString(), updatedFeedbackSession.toString());
    }

    @Test
    public void testUpdateFeedbackSession_shouldAdjustEmailSendingStatusAccordingly() throws Exception {
        FeedbackSessionAttributes typicalSession = dataBundle.feedbackSessions.get("session1InCourse1");

        ______TS("open email sent, whether the updated session is open determines the open email sending status");

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentOpenEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1))
                        .build());
        // updated session not open, status set to false
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentOpenEmail());

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentOpenEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(1))
                        .build());
        // updated session open, status set to true
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentOpenEmail());

        ______TS("closed email sent, whether the updated session is closed determines the "
                + "closed/closing email sending status");

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentClosedEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withStartTime(TimeHelper.getInstantDaysOffsetFromNow(-2))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1))
                        .build());
        // updated session closed, status set to true
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentClosedEmail());
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentClosingEmail());

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentClosedEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(2))
                        .build());
        //  updated session not closed, status set to false
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentClosedEmail());
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentClosingEmail());

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentClosedEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withStartTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-10))
                        .withEndTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(10))
                        .build());
        // updated session not closed, status set to false
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentClosedEmail());
        // closed in 10 minutes, should not send closing email anymore
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentClosingEmail());

        ______TS("published email sent, whether the updated session is published determines the "
                + "publish email sending status");

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentPublishedEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW)
                        .build());
        // updated session published, status set to true
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentPublishedEmail());

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentPublishedEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                        .build());
        // updated session not published, status set to false
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentPublishedEmail());
    }

    private void testPublishUnpublishFeedbackSession() throws Exception {

        ______TS("success: publish");
        FeedbackSessionAttributes sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse1");

        // set as manual publish

        sessionUnderTest.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId())
                        .withResultsVisibleFromTime(sessionUnderTest.getResultsVisibleFromTime())
                        .build());

        fsLogic.publishFeedbackSession(sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId());

        // Set real time of publishing
        FeedbackSessionAttributes sessionPublished =
                fsLogic.getFeedbackSession(sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId());
        sessionUnderTest.setResultsVisibleFromTime(sessionPublished.getResultsVisibleFromTime());

        assertEquals(sessionUnderTest.toString(), sessionPublished.toString());

        ______TS("failure: already published");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> fsLogic.publishFeedbackSession(
                        sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId()));
        assertEquals("Error publishing feedback session: Session has already been published.", ipe.getMessage());

        ______TS("success: unpublish");

        fsLogic.unpublishFeedbackSession(sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId());

        sessionUnderTest.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);

        assertEquals(
                sessionUnderTest.toString(),
                fsLogic.getFeedbackSession(
                        sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId()).toString());

        ______TS("failure: not published");

        ipe = assertThrows(InvalidParametersException.class,
                () -> fsLogic.unpublishFeedbackSession(
                        sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId()));
        assertEquals("Error unpublishing feedback session: Session has already been unpublished.", ipe.getMessage());

        ______TS("failure: publish/unpublish non-existent session");

        assertNull(fsLogic.getFeedbackSession("randomName", "randomId"));
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.publishFeedbackSession("randomName", "randomId"));
        assertEquals("Trying to update a non-existent feedback session: randomId/randomName", ednee.getMessage());

        ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.unpublishFeedbackSession("randomName", "randomId"));
        assertEquals("Trying to update a non-existent feedback session: randomId/randomName", ednee.getMessage());

    }

    private void testIsFeedbackSessionCompletedByInstructor() throws Exception {

        ______TS("success: empty session");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("empty.session");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");

        assertTrue(fsLogic.isFeedbackSessionCompletedByInstructor(fs, instructor.email));
    }

    private void testIsFeedbackSessionCompletedByStudent() {

        ______TS("success: empty session");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("empty.session");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");

        assertTrue(fsLogic.isFeedbackSessionCompletedByStudent(fs, student.email));
    }

    private void testIsFeedbackSessionFullyCompletedByStudent() throws Exception {

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1OfCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student3OfCourse1 = dataBundle.students.get("student3InCourse1");

        ______TS("failure: non-existent feedback session for student");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.isFeedbackSessionFullyCompletedByStudent(
                        "nonExistentFSName", fs.getCourseId(), "random.student@email"));
        assertEquals("Trying to check a non-existent feedback session: " + fs.getCourseId() + "/nonExistentFSName",
                ednee.getMessage());

        ______TS("success case: fully done by student 1");
        assertTrue(fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.getFeedbackSessionName(), fs.getCourseId(),
                                                                    student1OfCourse1.email));

        ______TS("success case: partially done by student 3");
        assertFalse(fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.getFeedbackSessionName(), fs.getCourseId(),
                                                                     student3OfCourse1.email));
    }

    private FeedbackSessionAttributes getNewFeedbackSession() {
        return FeedbackSessionAttributes.builder("fsTest1", "testCourse")
                .withCreatorEmail("valid@email.tmt")
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-62))
                .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(-1))
                .withEndTime(TimeHelperExtension.getInstantHoursOffsetFromNow(0))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(1))
                .withGracePeriod(Duration.ofMinutes(5))
                .withInstructions("Give feedback.")
                .build();
    }

    private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
        FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(
                questionToGet.feedbackSessionName,
                questionToGet.courseId,
                questionToGet.questionNumber);

        return questionToGet;
    }

    // Extract response id from datastore based on json key.
    private String getResponseId(String jsonId, DataBundle bundle) {
        return getResponseFromDatastore(jsonId, bundle).getId();
    }

    private FeedbackResponseAttributes getResponseFromDatastore(String jsonId, DataBundle bundle) {
        FeedbackResponseAttributes response = bundle.feedbackResponses.get(jsonId);

        String questionId = null;
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);
            questionId = fqLogic.getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            questionId = response.feedbackQuestionId;
        }

        return frLogic.getFeedbackResponse(questionId,
                response.giver, response.recipient);
    }

    private void unpublishAllSessions() throws InvalidParametersException, EntityDoesNotExistException {
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            if (fs.isPublished()) {
                fsLogic.unpublishFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
            }
        }
    }

    private void testMoveFeedbackSessionToRecycleBin() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = dataBundle.feedbackSessions.get("session2InCourse3");
        String feedbackSessionName = dataBundle.feedbackSessions.get("session2InCourse3").getFeedbackSessionName();
        String courseId = dataBundle.courses.get("typicalCourse3").getId();

        assertFalse(feedbackSession.isSessionDeleted());

        Instant deletedTime = fsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
        feedbackSession.setDeletedTime(deletedTime);

        FeedbackSessionAttributes actualFs = fsLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        assertEquals(JsonUtils.toJson(feedbackSession), JsonUtils.toJson(actualFs));
        assertTrue(feedbackSession.isSessionDeleted());
    }

    private void testRestoreFeedbackSessionFromRecycleBin() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = dataBundle.feedbackSessions.get("session2InCourse3");
        String feedbackSessionName = dataBundle.feedbackSessions.get("session2InCourse3").getFeedbackSessionName();
        String courseId = dataBundle.courses.get("typicalCourse3").getId();

        assertTrue(feedbackSession.isSessionDeleted());

        fsLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        feedbackSession.setDeletedTime(null);

        verifyPresentInDatastore(feedbackSession);
        assertFalse(feedbackSession.isSessionDeleted());
    }

    @Test
    public void testGetSessionResultsForUser_studentSpecificQuestionAndSection_shouldThrowOperationNotSupported() {
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(loadDataBundle("/SpecialCharacterTest.json"));

        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(
                "First Session", "FQLogicPCT.CS2104", 1);

        assertThrows(UnsupportedOperationException.class, () -> {
            fsLogic.getSessionResultsForUser(
                    "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.alice.b@gmail.tmt",
                    UserRole.STUDENT, question.getId(), Const.DEFAULT_SECTION);
        });
    }

    @Test
    public void testGetSessionResultsForUser_studentSpecificQuestionNoSection_shouldHaveCorrectResponsesFiltered() {
        // extra test data used on top of typical data bundle
        removeAndRestoreDataBundle(loadDataBundle("/SpecialCharacterTest.json"));

        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(
                "First Session", "FQLogicPCT.CS2104", 1);

        // Alice will see 4 responses
        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.alice.b@gmail.tmt",
                UserRole.STUDENT, question.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(4, responseForQuestion.size());

        // Benny will see 4 responses
        bundle = fsLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.benny.c@gmail.tmt",
                UserRole.STUDENT, question.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(4, responseForQuestion.size());

        // Charlie will see 3 responses
        bundle = fsLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.charlie.d@gmail.tmt",
                UserRole.STUDENT, question.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(3, responseForQuestion.size());

        // Danny will see 3 responses
        bundle = fsLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.danny.e@gmail.tmt",
                UserRole.STUDENT, question.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(3, responseForQuestion.size());

        // Emily will see 1 response
        bundle = fsLogic.getSessionResultsForUser(
                "First Session", "FQLogicPCT.CS2104", "FQLogicPCT.emily.f@gmail.tmt",
                UserRole.STUDENT, question.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(1, responseForQuestion.size());
    }

    @Test
    public void testGetSessionResultsForUser_studentSpecificQuestion_shouldHaveCorrectResponsesFiltered() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);

        // no section specific

        // no response visible
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        FeedbackQuestionAttributes question = getQuestionFromDatastore("qn2InSession1InCourse1");
        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                question.getFeedbackSessionName(), question.getCourseId(), student.getEmail(),
                UserRole.STUDENT, question.getId(), null);
        // there won't be question generated for student
        assertEquals(0, bundle.getQuestionsMap().size());
        assertEquals(0, bundle.getQuestionResponseMap().size());
        assertEquals(0, bundle.getQuestionMissingResponseMap().size());

        // one response visible
        question = getQuestionFromDatastore("qn3InSession1InCourse1");
        bundle = fsLogic.getSessionResultsForUser(
                question.getFeedbackSessionName(), question.getCourseId(), student.getEmail(),
                UserRole.STUDENT, question.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(1, responseForQuestion.size());
        assertEquals(1, bundle.getQuestionMissingResponseMap().size());
        assertEquals(0, bundle.getQuestionMissingResponseMap().entrySet().iterator().next().getValue().size());
    }

    @Test
    public void testGetSessionResultsForUser_studentAllQuestions_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        // Test result bundle for student1
        StudentAttributes student = responseBundle.students.get("student1InCourse1");
        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                session.getFeedbackSessionName(), session.getCourseId(), student.getEmail(),
                UserRole.STUDENT, null, null);

        // We just check for correct session once
        assertEquals(session.toString(), bundle.getFeedbackSession().toString());

        // Student can see responses: q1r1, q2r1,3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r1-2
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(11, totalResponse);
        // student should not see missing responses
        assertEquals(0, totalMissingResponse);
        // student cannot see q6 because there is no viewable response
        assertEquals(7, bundle.getQuestionsMap().size());
        assertEquals(7, bundle.getQuestionResponseMap().size());
        assertEquals(7, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn1.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn7.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn7.resp2", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn8.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn8.resp2", responseBundle)));
        assertEquals(totalResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn1.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn7.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn7.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn8.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn8.resp2", responseBundle)));
        assertEquals(totalResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    @Test
    public void testGetSessionResultsForUser_instructorSpecificQuestion_shouldHaveCorrectResponsesFiltered() {
        FeedbackQuestionAttributes fq = getQuestionFromDatastore("qn3InSession1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");

        // no section specified
        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                fq.getFeedbackSessionName(), fq.getCourseId(), instructor.getEmail(),
                UserRole.INSTRUCTOR, fq.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(1, responseForQuestion.size());

        // section specified
        fq = getQuestionFromDatastore("qn2InSession1InCourse1");
        bundle = fsLogic.getSessionResultsForUser(
                fq.getFeedbackSessionName(), fq.getCourseId(), instructor.getEmail(),
                UserRole.INSTRUCTOR, fq.getId(), "Section 1");
        assertEquals(1, bundle.getQuestionResponseMap().size());
        responseForQuestion = bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(3, responseForQuestion.size());
    }

    @Test
    public void testGetSessionResultsForUser_instructorAllQuestions_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        InstructorAttributes instructor = responseBundle.instructors.get("instructor1OfCourse1");
        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                UserRole.INSTRUCTOR, null, null);

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(10, totalResponse);
        assertEquals(19, totalMissingResponse);
        // Instructor should still see all questions
        assertEquals(8, bundle.getQuestionsMap().size());
        assertEquals(8, bundle.getQuestionResponseMap().size());
        assertEquals(8, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp2", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn3.resp2", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn6.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp2", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp2", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn5.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn6.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    @Test
    public void testGetSessionResultsForUser_instructorAllQuestionsSpecificSection_shouldGenerateCorrectBundle() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("standard.session");

        InstructorAttributes instructor = responseBundle.instructors.get("instructor1OfCourse1");
        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.getEmail(),
                UserRole.INSTRUCTOR, null, "Section A");

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        // after filtering by section, the number of responses seen by instructor will differ.
        // Responses viewed by instructor after filtering: q2r1-3, q3r1, q4r2-3, q5r1
        int totalResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionResponseMap().entrySet()) {
            totalResponse += entry.getValue().size();
        }
        int totalMissingResponse = 0;
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry
                : bundle.getQuestionMissingResponseMap().entrySet()) {
            totalMissingResponse += entry.getValue().size();
        }
        assertEquals(7, totalResponse);
        assertEquals(13, totalMissingResponse);
        // Instructor should still see all questions
        assertEquals(8, bundle.getQuestionsMap().size());
        assertEquals(8, bundle.getQuestionResponseMap().size());
        assertEquals(8, bundle.getQuestionMissingResponseMap().size());

        // Test the generated response visibilityTable for userNames.
        Map<String, Boolean> responseGiverVisibilityTable = bundle.getResponseGiverVisibilityTable();
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseGiverVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertFalse(responseGiverVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseGiverVisibilityTable.size());

        Map<String, Boolean> responseRecipientVisibilityTable = bundle.getResponseRecipientVisibilityTable();
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn3.resp1", responseBundle)));
        assertTrue(responseRecipientVisibilityTable.get(getResponseId("qn4.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp3", responseBundle)));
        assertFalse(responseRecipientVisibilityTable.get(getResponseId("qn2.resp1", responseBundle)));
        assertEquals(totalResponse + totalMissingResponse, responseGiverVisibilityTable.size());
        assertEquals(totalResponse + totalMissingResponse, responseRecipientVisibilityTable.size());

        // no entry in comment visibility table
        Map<Long, Boolean> commentGiverVisibilityTable = bundle.getCommentGiverVisibilityTable();
        assertEquals(0, commentGiverVisibilityTable.size());
    }

    // TODO: testGetSessionResultsForUser_studentAllQuestionsSpecificSection_shouldGenerateCorrectBundle

    // TODO: check for cases where a person is both a student and an instructor

    @Test
    public void testGetSessionResultsForUser_orphanResponseInDB_shouldStillHandleCorrectly()
            throws InvalidParametersException, EntityAlreadyExistsException {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);

        FeedbackQuestionAttributes fq = getQuestionFromDatastore("qn2InSession1InCourse1");
        FeedbackResponseAttributes existingResponse = getResponseFromDatastore("response1ForQ2S1C1", dataBundle);
        // create a "null" response to simulate trying to get a null student's response
        FeedbackResponseAttributes newResponse =
                FeedbackResponseAttributes.builder(
                        existingResponse.getFeedbackQuestionId(), existingResponse.getGiver(), "nullRecipient@gmail.tmt")
                        .withFeedbackSessionName(existingResponse.getFeedbackSessionName())
                        .withCourseId("nullCourse")
                        .withGiverSection("Section 1")
                        .withRecipientSection("Section 1")
                        .withResponseDetails(existingResponse.getResponseDetails())
                        .build();
        frLogic.createFeedbackResponse(newResponse);
        StudentAttributes student = dataBundle.students.get("student2InCourse1");

        SessionResultsBundle bundle = fsLogic.getSessionResultsForUser(
                fq.getFeedbackSessionName(), fq.getCourseId(), student.getEmail(),
                UserRole.STUDENT, fq.getId(), null);
        assertEquals(1, bundle.getQuestionResponseMap().size());
        List<FeedbackResponseAttributes> responseForQuestion =
                bundle.getQuestionResponseMap().entrySet().iterator().next().getValue();
        assertEquals(4, responseForQuestion.size());
    }
}
