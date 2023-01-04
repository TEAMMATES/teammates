package teammates.logic.core;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.test.AssertHelper;
import teammates.test.ThreadHelper;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicTest extends BaseLogicTest {
    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();
    private final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();

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
    public void testDeleteFeedbackSessionCascade_deleteSessionNotInRecycleBin_shouldDoCascadeDeletion() {
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
    public void testDeleteFeedbackSessionCascade_deleteSessionInRecycleBin_shouldDoCascadeDeletion() throws Exception {
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
        testGetFeedbackSessionsOpeningWithinTimeLimit();
    }

    @Test
    public void testAll() throws Exception {

        testGetSoftDeletedFeedbackSessionsListForInstructors();
        testIsFeedbackSessionViewableToUserType();

        testCreateAndDeleteFeedbackSession();

        testIsFeedbackSessionForUserTypeToAnswer();

        testUpdateFeedbackSession();
        testPublishUnpublishFeedbackSession();

        testIsFeedbackSessionAttemptedByStudent();
        testIsFeedbackSessionAttemptedByInstructor();

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

    private void testGetFeedbackSessionsClosingWithinTimeLimit() throws Exception {

        ______TS("init : 0 standard sessions closing within time-limit");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsClosingWithinTimeLimit();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session in undeleted course closing within time-limit");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone("UTC");
        session.setSessionVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        session.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        fsLogic.createFeedbackSession(session);
        coursesLogic.createCourse(
                CourseAttributes.builder(session.getCourseId())
                        .withName("Test Course")
                        .withTimezone("UTC")
                        .withInstitute("Test institute")
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
        // removeAndRestoreTypicalDataInDatabase() wont do it
        coursesLogic.restoreCourseFromRecycleBin(session.getCourseId());
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(), session.getCourseId());
    }

    private void testGetFeedbackSessionsClosedWithinThePastHour() throws Exception {

        ______TS("init : 0 standard sessions closed within the past hour");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsClosedWithinThePastHour();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 closed session in undeleted course within the past hour");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone("UTC");
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
        // removeAndRestoreTypicalDataInDatabase() wont do it
        coursesLogic.restoreCourseFromRecycleBin(session.getCourseId());
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(), session.getCourseId());
    }

    private void testGetFeedbackSessionsOpeningWithinTimeLimit() throws Exception {
        ______TS("init : 0 standard sessions opening within time-limit");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsOpeningWithinTimeLimit();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 closed session in undeleted course opening within time-limit");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone("UTC");
        session.setSessionVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        session.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        session.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(5));
        session.setResultsVisibleFromTime(TimeHelper.getInstantDaysOffsetFromNow(5));
        fsLogic.createFeedbackSession(session);

        // wait for very briefly so that the above session will be within the time limit
        ThreadHelper.waitFor(5);

        sessionList = fsLogic.getFeedbackSessionsOpeningWithinTimeLimit();

        assertEquals(1, sessionList.size());
        assertEquals(session.getFeedbackSessionName(), sessionList.get(0).getFeedbackSessionName());

        ______TS("case : 1 closed session in deleted course opening within time-limit");
        session.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(1));
        coursesLogic.moveCourseToRecycleBin(session.getCourseId());

        // wait for very briefly so that the above session will be within the time limit
        ThreadHelper.waitFor(5);

        sessionList = fsLogic.getFeedbackSessionsOpeningWithinTimeLimit();

        assertEquals(0, sessionList.size());

        // restore the new course from Recycle Bin, and delete the newly added session as
        // removeAndRestoreTypicalDataInDatabase() wont do it
        coursesLogic.restoreCourseFromRecycleBin(session.getCourseId());
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(), session.getCourseId());
    }

    private void testGetFeedbackSessionsWhichNeedOpenMailsToBeSent() throws Exception {

        ______TS("init : 0 open sessions");
        List<FeedbackSessionAttributes> sessionList = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session in undeleted course with mail unsent");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone("UTC");
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
        // removeAndRestoreTypicalDataInDatabase() wont do it
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
        session.setTimeZone("UTC");
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

    private void testCreateAndDeleteFeedbackSession() throws Exception {
        ______TS("test create");

        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fsLogic.createFeedbackSession(fs);
        verifyPresentInDatabase(fs);

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
        verifyAbsentInDatabase(fs);
        verifyAbsentInDatabase(fq);
    }

    private void testIsFeedbackSessionViewableToUserType() {
        ______TS("Session with questions for students/instructors to answer");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        assertTrue(fsLogic.isFeedbackSessionViewableToUserType(session, false));
        assertTrue(fsLogic.isFeedbackSessionViewableToUserType(session, true));

        ______TS("Session without questions for students, but with visible responses");
        session = dataBundle.feedbackSessions.get("archiveCourse.session1");
        assertTrue(fsLogic.isFeedbackSessionViewableToUserType(session, false));

        session = dataBundle.feedbackSessions.get("session1InCourse2");
        assertTrue(fsLogic.isFeedbackSessionViewableToUserType(session, false));

        ______TS("Session without questions for instructors, but with visible responses");
        session = dataBundle.feedbackSessions.get("session2InCourse1");
        assertTrue(fsLogic.isFeedbackSessionViewableToUserType(session, true));

        ______TS("empty session");
        session = dataBundle.feedbackSessions.get("empty.session");
        assertFalse(fsLogic.isFeedbackSessionViewableToUserType(session, false));
        assertFalse(fsLogic.isFeedbackSessionViewableToUserType(session, true));
    }

    private void testIsFeedbackSessionForUserTypeToAnswer() {
        ______TS("Non-visible session should not be for any types of user to answer");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("awaiting.session");
        assertFalse(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false));
        assertFalse(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true));

        ______TS("Empty session should not be for any types of user to answer");
        session = dataBundle.feedbackSessions.get("empty.session");
        assertFalse(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false));
        assertFalse(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true));

        ______TS("Session without student question should not be for students to answer");
        session = dataBundle.feedbackSessions.get("archiveCourse.session1");
        assertFalse(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false));
        assertTrue(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true));

        ______TS("Session without instructor question should not be for instructors to answer");
        session = dataBundle.feedbackSessions.get("session2InCourse1");
        assertFalse(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, true));
        assertTrue(fsLogic.isFeedbackSessionForUserTypeToAnswer(session, false));
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

        ______TS("open email sent, whether the updated session is open determines the "
                + "open/opening soon email sending status");

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
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentOpeningSoonEmail());

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withSentOpenEmail(true)
                        .build());

        fsLogic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(
                        typicalSession.getFeedbackSessionName(), typicalSession.getCourseId())
                        .withStartTime(TimeHelperExtension.getInstantHoursOffsetFromNow(20))
                        .withEndTime(TimeHelper.getInstantDaysOffsetFromNow(2))
                        .build());
        // updated session not open, status set to false
        assertFalse(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentOpenEmail());
        // updated session opening soon, opening soon email shouldn't be sent anymore
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentOpeningSoonEmail());

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
        // opening soon email shouldn't be sent anymore
        assertTrue(fsLogic.getFeedbackSession(
                typicalSession.getFeedbackSessionName(), typicalSession.getCourseId()).isSentOpeningSoonEmail());

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

    @Test
    public void testUpdateFeedbackSessionsInstructorDeadlinesWithNewEmail() {
        InstructorAttributes instructorToBeUpdated = dataBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructorToBeUpdated.getCourseId();
        String oldEmailAddress = instructorToBeUpdated.getEmail();
        String newEmailAddress = "new@email.tmt";

        ______TS("Update email; transfers deadlines to new email.");

        Map<Instant, Integer> oldDeadlineCounts = fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .map(FeedbackSessionAttributes::getInstructorDeadlines)
                .filter(instructorDeadlines -> instructorDeadlines.containsKey(oldEmailAddress))
                .map(instructorDeadlines -> instructorDeadlines.get(oldEmailAddress))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(deadline -> 1)));
        assertEquals(2, oldDeadlineCounts.values()
                .stream()
                .reduce(0, Integer::sum)
                .intValue());

        fsLogic.updateFeedbackSessionsInstructorDeadlinesWithNewEmail(courseId, oldEmailAddress, newEmailAddress);

        assertTrue(fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .noneMatch(feedbackSessionAttributes -> feedbackSessionAttributes.getInstructorDeadlines()
                        .containsKey(oldEmailAddress)));
        Map<Instant, Integer> newDeadlineCounts = fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .map(FeedbackSessionAttributes::getInstructorDeadlines)
                .filter(instructorDeadlines -> instructorDeadlines.containsKey(newEmailAddress))
                .map(instructorDeadlines -> instructorDeadlines.get(newEmailAddress))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(deadline -> 1)));
        assertEquals(oldDeadlineCounts, newDeadlineCounts);
    }

    @Test
    public void testUpdateFeedbackSessionsStudentDeadlinesWithNewEmail() {
        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        String courseId = student4InCourse1.getCourse();
        String oldEmailAddress = student4InCourse1.getEmail();
        String newEmailAddress = "new@email.tmt";

        ______TS("Update email; transfers deadlines to new email.");

        Map<Instant, Integer> oldDeadlineCounts = fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .map(FeedbackSessionAttributes::getStudentDeadlines)
                .filter(studentDeadlines -> studentDeadlines.containsKey(oldEmailAddress))
                .map(studentDeadlines -> studentDeadlines.get(oldEmailAddress))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(deadline -> 1)));
        assertEquals(2, oldDeadlineCounts.values()
                .stream()
                .reduce(0, Integer::sum)
                .intValue());

        fsLogic.updateFeedbackSessionsStudentDeadlinesWithNewEmail(courseId, oldEmailAddress, newEmailAddress);

        assertTrue(fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .noneMatch(feedbackSessionAttributes -> feedbackSessionAttributes.getStudentDeadlines()
                        .containsKey(oldEmailAddress)));
        Map<Instant, Integer> newDeadlineCounts = fsLogic.getFeedbackSessionsForCourse(courseId)
                .stream()
                .map(FeedbackSessionAttributes::getStudentDeadlines)
                .filter(studentDeadlines -> studentDeadlines.containsKey(newEmailAddress))
                .map(studentDeadlines -> studentDeadlines.get(newEmailAddress))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(deadline -> 1)));
        assertEquals(oldDeadlineCounts, newDeadlineCounts);
    }

    @Test
    public void testDeleteFeedbackSessionsDeadlinesForInstructor() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        verifyPresentInDatabase(instructor1OfCourse1);

        String courseId = instructor1OfCourse1.getCourseId();
        String emailAddress = instructor1OfCourse1.getEmail();

        ______TS("Delete user; deadlines associated with the email are removed.");

        // The instructor should have selective deadlines.
        Set<FeedbackSessionAttributes> oldSessionsWithInstructor1Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getInstructorDeadlines()
                        .containsKey(emailAddress))
                .collect(Collectors.toSet());
        Map<FeedbackSessionAttributes, Integer> oldSessionsDeadlineCounts = oldSessionsWithInstructor1Deadlines
                .stream()
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getInstructorDeadlines().size()));
        assertEquals(2, oldSessionsWithInstructor1Deadlines.size());

        fsLogic.deleteFeedbackSessionsDeadlinesForInstructor(courseId, emailAddress);

        // The instructor should have no more selective deadlines.
        Set<FeedbackSessionAttributes> newSessionsWithInstructor1Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getInstructorDeadlines()
                        .containsKey(emailAddress))
                .collect(Collectors.toSet());
        assertTrue(newSessionsWithInstructor1Deadlines.isEmpty());
        Map<FeedbackSessionAttributes, Integer> expectedSessionsDeadlineCounts = oldSessionsDeadlineCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() - 1));
        Map<FeedbackSessionAttributes, Integer> newSessionsDeadlineCounts = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(oldSessionsWithInstructor1Deadlines::contains)
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getInstructorDeadlines().size()));
        assertEquals(expectedSessionsDeadlineCounts, newSessionsDeadlineCounts);
    }

    @Test
    public void testDeleteFeedbackSessionsDeadlinesForStudent() {
        StudentAttributes student4InCourse1 = dataBundle.students.get("student4InCourse1");
        verifyPresentInDatabase(student4InCourse1);

        String courseId = student4InCourse1.getCourse();
        String emailAddress = student4InCourse1.getEmail();

        ______TS("Delete user; deadlines associated with the email are removed.");

        // The student should have selective deadlines.
        Set<FeedbackSessionAttributes> oldSessionsWithStudent4Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getStudentDeadlines()
                        .containsKey(emailAddress))
                .collect(Collectors.toSet());
        Map<FeedbackSessionAttributes, Integer> oldSessionsDeadlineCounts = oldSessionsWithStudent4Deadlines
                .stream()
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getStudentDeadlines().size()));
        assertEquals(2, oldSessionsWithStudent4Deadlines.size());

        fsLogic.deleteFeedbackSessionsDeadlinesForStudent(courseId, emailAddress);

        // The student should have no more selective deadlines.
        Set<FeedbackSessionAttributes> newSessionsWithStudent4Deadlines = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(feedbackSessionAttributes -> feedbackSessionAttributes.getStudentDeadlines()
                        .containsKey(emailAddress))
                .collect(Collectors.toSet());
        assertTrue(newSessionsWithStudent4Deadlines.isEmpty());
        Map<FeedbackSessionAttributes, Integer> expectedSessionsDeadlineCounts = oldSessionsDeadlineCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() - 1));
        Map<FeedbackSessionAttributes, Integer> newSessionsDeadlineCounts = fsLogic
                .getFeedbackSessionsForCourse(courseId)
                .stream()
                .filter(oldSessionsWithStudent4Deadlines::contains)
                .collect(Collectors.toMap(fsa -> fsa, fsa -> fsa.getStudentDeadlines().size()));
        assertEquals(expectedSessionsDeadlineCounts, newSessionsDeadlineCounts);
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

    private void testIsFeedbackSessionAttemptedByInstructor() {

        ______TS("success: empty session");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("empty.session");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");

        assertTrue(fsLogic.isFeedbackSessionAttemptedByInstructor(fs, instructor.getEmail()));
    }

    private void testIsFeedbackSessionAttemptedByStudent() {

        ______TS("success: empty session");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("empty.session");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");

        assertTrue(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        ______TS("success: grace period session (all team questions)");

        fs = dataBundle.feedbackSessions.get("gracePeriodSession");
        // student who answered team question
        student = dataBundle.students.get("student4InCourse1");
        assertTrue(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        // student whose teammate answered team question
        student = dataBundle.students.get("student1InCourse1");
        assertTrue(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        // student whose team has not answered team question
        student = dataBundle.students.get("student5InCourse1");
        assertFalse(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        ______TS("success: second feedback session (both team and individual questions)");

        fs = dataBundle.feedbackSessions.get("session2InCourse1");
        // student who did not answer any question
        student = dataBundle.students.get("student5InCourse1");
        assertFalse(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        // student who answered only team question
        student = dataBundle.students.get("student2InCourse1");
        assertFalse(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        // student who answered only individual question
        student = dataBundle.students.get("student6InCourse1");
        assertTrue(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));

        // student who answered both team and individual question
        student = dataBundle.students.get("student4InCourse1");
        assertTrue(fsLogic.isFeedbackSessionAttemptedByStudent(fs, student.getEmail(), student.getTeam()));
    }

    private FeedbackSessionAttributes getNewFeedbackSession() {
        return FeedbackSessionAttributes.builder("fsTest1", "testCourse")
                .withCreatorEmail("valid@email.tmt")
                .withSessionVisibleFromTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(2))
                .withStartTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(2))
                .withEndTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(7))
                .withResultsVisibleFromTime(TimeHelperExtension.getInstantTruncatedDaysOffsetFromNow(7))
                .withGracePeriod(Duration.ofMinutes(5))
                .withInstructions("Give feedback.")
                .build();
    }

    private void unpublishAllSessions() throws Exception {
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            if (fs.isPublished()) {
                fsLogic.unpublishFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
            }
        }
    }

    private void testMoveFeedbackSessionToRecycleBin() throws Exception {
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

    private void testRestoreFeedbackSessionFromRecycleBin() throws Exception {
        FeedbackSessionAttributes feedbackSession = dataBundle.feedbackSessions.get("session2InCourse3");
        String feedbackSessionName = dataBundle.feedbackSessions.get("session2InCourse3").getFeedbackSessionName();
        String courseId = dataBundle.courses.get("typicalCourse3").getId();

        assertTrue(feedbackSession.isSessionDeleted());

        fsLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        feedbackSession.setDeletedTime(null);

        verifyPresentInDatabase(feedbackSession);
        assertFalse(feedbackSession.isSessionDeleted());
    }

}
