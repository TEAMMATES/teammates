package teammates.test.cases.logic;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionStats;
import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.CsvChecker;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicTest extends BaseLogicTest {
    private static CoursesLogic coursesLogic = CoursesLogic.inst();
    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
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
                frcLogic.getFeedbackResponseCommentForSession(fsa.getCourseId(), fsa.getFeedbackSessionName()).isEmpty());

        // delete existing feedback session directly
        fsLogic.deleteFeedbackSessionCascade(fsa.getFeedbackSessionName(), fsa.getCourseId());

        assertNull(fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertNull(fsLogic.getFeedbackSessionFromRecycleBin(fsa.getFeedbackSessionName(), fsa.getCourseId()));
        assertTrue(
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertTrue(
                frLogic.getFeedbackResponsesForSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).isEmpty());
        assertTrue(
                frcLogic.getFeedbackResponseCommentForSession(fsa.getCourseId(), fsa.getFeedbackSessionName()).isEmpty());
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
                frcLogic.getFeedbackResponseCommentForSession(fsa.getCourseId(), fsa.getFeedbackSessionName()).isEmpty());
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
                frcLogic.getFeedbackResponseCommentForSession(fsa.getCourseId(), fsa.getFeedbackSessionName()).isEmpty());
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
    public void testDeleteInstructorFromRespondentsList_typicalData_emailShouldBeRemoved() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        fsLogic.addInstructorRespondent("test@email.com",
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());
        FeedbackSessionAttributes session2InCourse1 = dataBundle.feedbackSessions.get("session2InCourse1");
        fsLogic.addInstructorRespondent("test@email.com",
                session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId());
        // they are in the same course
        assertEquals(session1InCourse1.getCourseId(), session2InCourse1.getCourseId());
        assertTrue(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingInstructorList()
                        .contains("test@email.com"));
        assertTrue(
                fsLogic.getFeedbackSession(session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId())
                        .getRespondingInstructorList()
                        .contains("test@email.com"));

        // remove email from all respondents list
        fsLogic.deleteInstructorFromRespondentsList(session1InCourse1.getCourseId(), "test@email.com");

        // the email should not appear
        assertFalse(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingInstructorList()
                        .contains("test@email.com"));
        assertFalse(
                fsLogic.getFeedbackSession(session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId())
                        .getRespondingInstructorList()
                        .contains("test@email.com"));
    }

    @Test
    public void testDeleteStudentFromRespondentsList_typicalData_emailShouldBeRemoved() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        fsLogic.addStudentRespondent("test@email.com",
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());
        FeedbackSessionAttributes session2InCourse1 = dataBundle.feedbackSessions.get("session2InCourse1");
        fsLogic.addStudentRespondent("test@email.com",
                session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId());
        // they are in the same course
        assertEquals(session1InCourse1.getCourseId(), session2InCourse1.getCourseId());
        assertTrue(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingStudentList()
                        .contains("test@email.com"));
        assertTrue(
                fsLogic.getFeedbackSession(session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId())
                        .getRespondingStudentList()
                        .contains("test@email.com"));

        // remove email from all respondents list
        fsLogic.deleteStudentFromRespondentsList(session1InCourse1.getCourseId(), "test@email.com");

        // the email should not appear
        assertFalse(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingStudentList()
                        .contains("test@email.com"));
        assertFalse(
                fsLogic.getFeedbackSession(session2InCourse1.getFeedbackSessionName(), session2InCourse1.getCourseId())
                        .getRespondingStudentList()
                        .contains("test@email.com"));
    }

    @Test
    public void testDeleteInstructorRespondent_typicalData_shouldRemoveFromRespondentList() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        fsLogic.addInstructorRespondent("test@email.com",
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());
        assertTrue(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingInstructorList()
                        .contains("test@email.com"));

        // delete the instructor from the list
        fsLogic.deleteInstructorRespondent("test@email.com",
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());

        assertFalse(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingInstructorList()
                        .contains("test@email.com"));
    }

    @Test
    public void testDeleteStudentRespondent_typicalData_shouldRemoveFromRespondentList() throws Exception {
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        fsLogic.addStudentRespondent("test@email.com",
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());
        assertTrue(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingStudentList()
                        .contains("test@email.com"));

        // delete the student from the list
        fsLogic.deleteStudentFromRespondentList("test@email.com",
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId());

        assertFalse(
                fsLogic.getFeedbackSession(session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId())
                        .getRespondingStudentList()
                        .contains("test@email.com"));
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

        testGetFeedbackSessionsForCourse();
        testGetFeedbackSessionsListForInstructor();
        testGetSoftDeletedFeedbackSessionsListForInstructor();
        testGetSoftDeletedFeedbackSessionsListForInstructors();
        testGetFeedbackSessionDetailsForInstructor();
        testGetFeedbackSessionQuestionsForStudent();
        testGetFeedbackSessionQuestionsForInstructor();
        testGetFeedbackSessionResultsForUser();
        testGetFeedbackSessionResultsSummaryAsCsv();
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
        testRestoreAllFeedbackSessionsFromRecycleBin();
    }

    private void testGetFeedbackSessionsListForInstructor() {
        List<FeedbackSessionAttributes> finalFsa = new ArrayList<>();
        Collection<FeedbackSessionAttributes> allFsa = dataBundle.feedbackSessions.values();

        String courseId = dataBundle.courses.get("typicalCourse1").getId();
        String instructorGoogleId = dataBundle.instructors.get("instructor1OfCourse1").googleId;

        for (FeedbackSessionAttributes fsa : allFsa) {
            if (fsa.getCourseId().equals(courseId)) {
                finalFsa.add(fsa);
            }
        }
        AssertHelper.assertSameContentIgnoreOrder(
                finalFsa, fsLogic.getFeedbackSessionsListForInstructor(instructorGoogleId, false));

    }

    private void testGetSoftDeletedFeedbackSessionsListForInstructor() {
        List<FeedbackSessionAttributes> softDeletedFsa = new ArrayList<>();
        Collection<FeedbackSessionAttributes> allFsa = dataBundle.feedbackSessions.values();

        String courseId = dataBundle.courses.get("typicalCourse3").getId();
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse3");

        for (FeedbackSessionAttributes fsa : allFsa) {
            if (fsa.getCourseId().equals(courseId) && fsa.isSessionDeleted()) {
                softDeletedFsa.add(fsa);
            }
        }
        AssertHelper.assertSameContentIgnoreOrder(
                softDeletedFsa, fsLogic.getSoftDeletedFeedbackSessionsListForInstructor(instructor));

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
                        + "(&lt; &gt; &quot; &#x2f; &#39; &amp;)",
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

    private void testGetFeedbackSessionDetailsForInstructor() throws Exception {

        // This file contains a session with a standard
        // session + a special session with all questions without recipients.
        DataBundle newDataBundle = loadDataBundle("/FeedbackSessionDetailsTest.json");
        removeAndRestoreDataBundle(newDataBundle);

        Map<String, FeedbackSessionDetailsBundle> detailsMap = new HashMap<>();

        String instrGoogleId = newDataBundle.instructors.get("instructor1OfCourse1").googleId;
        List<FeedbackSessionDetailsBundle> detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(instrGoogleId);

        List<String> expectedSessions = new ArrayList<>();
        expectedSessions.add(newDataBundle.feedbackSessions.get("standard.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("no.responses.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("no.recipients.session").toString());

        StringBuilder actualSessionsBuilder = new StringBuilder();
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessionsBuilder.append(details.feedbackSession.toString());
            detailsMap.put(
                    details.feedbackSession.getFeedbackSessionName() + "%" + details.feedbackSession.getCourseId(),
                    details);
        }

        String actualSessions = actualSessionsBuilder.toString();
        ______TS("standard session");

        assertEquals(3, detailsList.size());
        AssertHelper.assertContains(expectedSessions, actualSessions);

        FeedbackSessionStats stats =
                detailsMap.get(newDataBundle.feedbackSessions.get("standard.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("standard.session").getCourseId()).stats;

        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // 1 instructor, 1 student, did not respond => 8-2=6
        assertEquals(6, stats.submittedTotal);

        ______TS("No recipients session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("no.recipients.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("no.recipients.session").getCourseId()).stats;

        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // only 1 student responded
        assertEquals(1, stats.submittedTotal);

        ______TS("No responses session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("no.responses.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("no.responses.session").getCourseId()).stats;

        // 1 instructors, 1 students = 2
        assertEquals(2, stats.expectedTotal);
        // no responses
        assertEquals(0, stats.submittedTotal);

        ______TS("instructor does not exist");

        assertTrue(fsLogic.getFeedbackSessionDetailsForInstructor("non-existent.google.id").isEmpty());

    }

    private void testGetFeedbackSessionsForCourse() throws Exception {

        List<FeedbackSessionAttributes> actualSessions = null;

        ______TS("non-existent course");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.getFeedbackSessionsForUserInCourse("NonExistentCourseId", "randomUserId"));
        assertEquals("Error getting feedback session(s): Course does not exist.", ednee.getMessage());

        ______TS("Student viewing: 2 visible, 1 awaiting, 1 no questions");

        // 2 valid sessions in course 1, 0 in course 2.

        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        // Student can see sessions 1 and 2. Session 3 has no questions. Session 4 is not yet visible for students.
        String expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("session2InCourse1").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("gracePeriodSession").toString() + System.lineSeparator();

        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertEquals(3, actualSessions.size());

        // Course 2 only has an instructor session.
        // The instructor session has questions where responses are visible
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "student1InCourse2@gmail.tmt");
        assertEquals(1, actualSessions.size());

        ______TS("Instructor viewing");

        // 3 valid sessions in course 1, 1 in course 2.

        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "instructor1@course1.tmt");

        // Instructors should be able to see all sessions for the course
        expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("session2InCourse1").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("empty.session").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("awaiting.session").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("closedSession").toString() + System.lineSeparator()
                + dataBundle.feedbackSessions.get("gracePeriodSession").toString() + System.lineSeparator();

        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertEquals(6, actualSessions.size());
        // We should only have one session here as there is only one session in the course.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor2@course2.tmt");

        assertEquals(actualSessions.get(0).toString(),
                dataBundle.feedbackSessions.get("session1InCourse2").toString());
        assertEquals(1, actualSessions.size());

        ______TS("Feedback session without questions for students but with visible responses are visible");
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfArchivedCourse", "student1InCourse1@gmail.tmt");
        AssertHelper.assertContains(dataBundle.feedbackSessions.get("archiveCourse.session1").toString(),
                actualSessions.toString());
    }

    private void testGetFeedbackSessionQuestionsForStudent() throws Exception {

        ______TS("standard test");

        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForStudent(
                        "First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        // We just test this once.
        assertEquals(dataBundle.feedbackSessions.get("session1InCourse1").toString(), actual.feedbackSession.toString());

        // There should be 3 questions for students to do in session 1.
        // Other questions are set for instructors.
        assertEquals(3, actual.questionResponseBundle.size());

        // Question 1
        FeedbackQuestionAttributes expectedQuestion =
                getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S1C1", dataBundle).toString();
        List<String> actualResponses = new ArrayList<>();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Comment on response 1 of question 1
        List<FeedbackResponseCommentAttributes> frcList =
                actual.commentsForResponses.get(actual.questionResponseBundle.get(expectedQuestion).get(0).getId());
        assertEquals(1, frcList.size());

        String expectedCommentString = getCommentFromDatastore("comment1FromT1C1ToR1Q1S1C1", dataBundle).toString();
        assertEquals(expectedCommentString, frcList.get(0).toString());

        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        expectedResponsesString = getResponseFromDatastore("response2ForQ2S1C1", dataBundle).toString();
        actualResponses.clear();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Comment on response 1 of question 2
        frcList = actual.commentsForResponses.get(actual.questionResponseBundle.get(expectedQuestion).get(0).getId());
        assertEquals(0, frcList.size());

        // Question for students to instructors
        expectedQuestion = getQuestionFromDatastore("qn5InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        // Check that instructors (except the one who is not displayed to student) appear as recipients
        Map<String, String> recipients = actual.recipientList.get(expectedQuestion.getId());
        assertTrue(recipients.containsKey("instructor1@course1.tmt"));
        assertTrue(recipients.containsKey("instructor2@course1.tmt"));
        assertTrue(recipients.containsKey("instructor3@course1.tmt"));
        assertTrue(recipients.containsKey("instructorNotYetJoinedCourse1@email.tmt"));
        assertFalse(recipients.containsKey("helper@course1.tmt"));

        ______TS("team feedback test");

        // Check that student3 get team member's (student4) feedback response as well (for team question).
        actual = fsLogic.getFeedbackSessionQuestionsForStudent(
                        "Second feedback session", "idOfTypicalCourse1", "student3InCourse1@gmail.tmt");

        assertEquals(2, actual.questionResponseBundle.size());

        // Question 1
        expectedQuestion = getQuestionFromDatastore("team.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        expectedResponsesString = getResponseFromDatastore(
                "response1ForQ1S2C1", dataBundle).toString();
        actualResponses.clear();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle
                .get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Question 2, no responses from this student yet
        expectedQuestion = getQuestionFromDatastore("team.members.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());

        ______TS("failure: invalid session");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.getFeedbackSessionQuestionsForStudent(
                        "invalid session", "idOfTypicalCourse1", "student3InCourse1@gmail.tmt"));
        assertEquals("Trying to get a non-existent feedback session: idOfTypicalCourse1/invalid session",
                ednee.getMessage());

        ______TS("failure: non-existent student");

        ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.getFeedbackSessionQuestionsForStudent(
                        "Second feedback session", "idOfTypicalCourse1", "randomUserId"));
        assertEquals("Error getting feedback session(s): Student does not exist.", ednee.getMessage());

    }

    private void testGetFeedbackSessionQuestionsForInstructor() throws Exception {
        ______TS("standard test");

        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.tmt");

        // We just test this once.
        assertEquals(dataBundle.feedbackSessions.get("session1InCourse2").toString(),
                actual.feedbackSession.toString());

        // There should be 2 question for students to do in session 1.
        // The final question is set for SELF (creator) only.
        assertEquals(2, actual.questionResponseBundle.size());

        // Question 1
        FeedbackQuestionAttributes expectedQuestion =
                getQuestionFromDatastore("qn1InSession1InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S1C2", dataBundle).toString();
        List<String> actualResponses = new ArrayList<>();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Comment on response 1 of question 1
        List<FeedbackResponseCommentAttributes> frcList =
                actual.commentsForResponses.get(actual.questionResponseBundle.get(expectedQuestion).get(0).getId());
        assertEquals(1, frcList.size());

        String expectedCommentString = getCommentFromDatastore("comment1FromT1C1ToR1Q1S1C2", dataBundle).toString();
        assertEquals(expectedCommentString, frcList.get(0).toString());

        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession1InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());

        ______TS("failure: invalid session");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "invalid session", "idOfTypicalCourse1", "instructor1@course1.tmt"));
        assertEquals("Trying to get a non-existent feedback session: idOfTypicalCourse1/invalid session",
                ednee.getMessage());
    }

    private void testGetFeedbackSessionResultsForUser() throws Exception {

        // This file contains a session with a standard
        // session which needs to have enough qn/response combinations to cover as much
        // of the SUT as possible
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        ______TS("standard session with varied visibilities");

        FeedbackSessionAttributes session =
                responseBundle.feedbackSessions.get("standard.session");

        /*** Test result bundle for student1 ***/
        StudentAttributes student =
                responseBundle.students.get("student1InCourse1");
        FeedbackSessionResultsBundle results =
                fsLogic.getFeedbackSessionResultsForStudent(session.getFeedbackSessionName(),
                        session.getCourseId(), student.email);

        // We just check for correct session once
        assertEquals(session.toString(), results.feedbackSession.toString());

        // Student can see responses: q1r1, q2r1,3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r1-2
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        assertEquals(11, results.responses.size());
        assertEquals(7, results.questions.size());

        // Test the user email-name maps used for display purposes
        String mapString = results.emailNameTable.toString();
        List<String> expectedStrings = new ArrayList<>();

        String student2AnonEmail = getStudentAnonEmail(responseBundle, "student2InCourse1");
        String student2AnonName = getStudentAnonName(responseBundle, "student2InCourse1");
        String student4AnonEmail = getStudentAnonEmail(responseBundle, "student4InCourse1");
        String student4AnonName = getStudentAnonName(responseBundle, "student4InCourse1");
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "FSRTest.student4InCourse1@gmail.tmt=student4 In Course1",
                "Team 1.1</td></div>'\"=Team 1.1</td></div>'\"",
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1",
                "FSRTest.student1InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student2InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student4InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.2",
                student2AnonEmail + "=" + student2AnonName,
                student4AnonEmail + "=" + student4AnonName);
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student4InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "FSRTest.student1InCourse1@gmail.tmt's Team=",
                "FSRTest.student2InCourse1@gmail.tmt's Team=",
                "FSRTest.student4InCourse1@gmail.tmt's Team=",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "Team 1.1</td></div>'\"=",
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=",
                "FSRTest.instr1@course1.tmt=Instructors",
                student2AnonEmail + "=" + student2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student4AnonEmail + "=" + student4AnonName + Const.TEAM_OF_EMAIL_OWNER);
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailTeamNameTable.size());

        // Test 'Append TeamName to Name' for display purposes with Typical Cases
        expectedStrings.clear();
        List<String> actualStrings = new ArrayList<>();
        for (FeedbackResponseAttributes response : results.responses) {
            String giverName = results.getNameForEmail(response.giver);
            String giverTeamName = results.getTeamNameForEmail(response.giver);
            giverName = results.appendTeamNameToName(giverName, giverTeamName);
            String recipientName = results.getNameForEmail(response.recipient);
            String recipientTeamName = results.getTeamNameForEmail(response.recipient);
            recipientName = results.appendTeamNameToName(recipientName, recipientTeamName);
            actualStrings.add(giverName);
            actualStrings.add(recipientName);
        }
        Collections.addAll(expectedStrings,
                getStudentAnonName(responseBundle, "student2InCourse1"),
                getStudentAnonName(responseBundle, "student4InCourse1"),
                "student1 In Course1</td></div>'\" (Team 1.1</td></div>'\")",
                "student2 In Course1 (Team 1.1</td></div>'\")",
                "student4 In Course1 (Team 1.2)",
                "Instructor1 Course1 (Instructors)",
                "Team 1.1</td></div>'\"",
                "Team 1.2",
                "Team 1.3",
                "Team 1.4");
        AssertHelper.assertContains(expectedStrings, actualStrings.toString());

        // Test 'Append TeamName to Name' for display purposes with Special Cases
        expectedStrings.clear();
        actualStrings.clear();

        // case: Unknown User
        String unknownUserName = Const.USER_UNKNOWN_TEXT;
        String someTeamName = "Some Team Name";
        unknownUserName = results.appendTeamNameToName(unknownUserName, someTeamName);
        actualStrings.add(unknownUserName);

        // case: Nobody
        String nobodyUserName = Const.USER_NOBODY_TEXT;
        nobodyUserName = results.appendTeamNameToName(nobodyUserName, someTeamName);
        actualStrings.add(nobodyUserName);

        // case: Anonymous User
        String anonymousUserName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " " + System.currentTimeMillis();
        anonymousUserName = results.appendTeamNameToName(anonymousUserName, someTeamName);
        actualStrings.add(anonymousUserName);
        Collections.addAll(expectedStrings,
                Const.USER_UNKNOWN_TEXT,
                Const.USER_NOBODY_TEXT,
                anonymousUserName);
        assertEquals(expectedStrings.toString(), actualStrings.toString());

        // Test the generated response visibilityTable for userNames.
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn1.resp1", responseBundle) + "={true,true}",
                getResponseId("qn2.resp1", responseBundle) + "={true,true}",
                getResponseId("qn2.resp3", responseBundle) + "={true,true}",
                getResponseId("qn3.resp1", responseBundle) + "={true,true}",
                getResponseId("qn4.resp2", responseBundle) + "={true,true}",
                getResponseId("qn4.resp3", responseBundle) + "={false,true}",
                getResponseId("qn5.resp1", responseBundle) + "={true,false}",
                getResponseId("qn7.resp1", responseBundle) + "={true,true}",
                getResponseId("qn7.resp2", responseBundle) + "={true,true}",
                getResponseId("qn8.resp1", responseBundle) + "={true,true}",
                getResponseId("qn8.resp2", responseBundle) + "={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(11, results.visibilityTable.size());

        /*** Test result bundle for instructor1 within a course ***/
        InstructorAttributes instructor =
                responseBundle.instructors.get("instructor1OfCourse1");
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email);

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        assertEquals(10, results.responses.size());
        //Instructor should still see all questions
        assertEquals(8, results.questions.size());

        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        String student1AnonEmail = getStudentAnonEmail(responseBundle, "student1InCourse1");
        String student1AnonName = getStudentAnonName(responseBundle, "student1InCourse1");
        String student3AnonEmail = getStudentAnonEmail(responseBundle, "student3InCourse1");
        String student3AnonName = getStudentAnonName(responseBundle, "student3InCourse1");
        String student6AnonEmail = getStudentAnonEmail(responseBundle, "student6InCourse1");
        String student6AnonName = getStudentAnonName(responseBundle, "student6InCourse1");
        String instructor1AnonEmail = FeedbackSessionResultsBundle.getAnonEmail(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor1OfCourse1").name);
        String instructor1AnonName = FeedbackSessionResultsBundle.getAnonName(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor1OfCourse1").name);
        String instructor2AnonEmail = FeedbackSessionResultsBundle.getAnonEmail(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor2OfCourse1").name);
        String instructor2AnonName = FeedbackSessionResultsBundle.getAnonName(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor2OfCourse1").name);
        Collections.addAll(expectedStrings,
                "%GENERAL%=%NOBODY%",
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1</td></div>'\"",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "FSRTest.student3InCourse1@gmail.tmt=student3 In Course1",
                "FSRTest.student4InCourse1@gmail.tmt=student4 In Course1",
                "FSRTest.student5InCourse1@gmail.tmt=student5 In Course1",
                "FSRTest.student6InCourse1@gmail.tmt=student6 In Course1",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1",
                "FSRTest.instr2@course1.tmt=Instructor2 Course1",
                student1AnonEmail + "=" + student1AnonName,
                student2AnonEmail + "=" + student2AnonName,
                student3AnonEmail + "=" + student3AnonName,
                student6AnonEmail + "=" + student6AnonName,
                instructor1AnonEmail + "=" + instructor1AnonName,
                instructor2AnonEmail + "=" + instructor2AnonName,
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(18, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "%GENERAL%=",
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "FSRTest.student3InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student4InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student5InCourse1@gmail.tmt=Team 1.3",
                "FSRTest.student6InCourse1@gmail.tmt=Team 1.4",
                "FSRTest.instr2@course1.tmt=Instructors",
                "FSRTest.instr1@course1.tmt=Instructors",
                student1AnonEmail + "=" + student1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student2AnonEmail + "=" + student2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student3AnonEmail + "=" + student3AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student6AnonEmail + "=" + student6AnonName + Const.TEAM_OF_EMAIL_OWNER,
                instructor1AnonEmail + "=" + instructor1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                instructor2AnonEmail + "=" + instructor2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(18, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn2.resp1", responseBundle) + "={false,false}",
                getResponseId("qn2.resp2", responseBundle) + "={false,false}",
                getResponseId("qn2.resp3", responseBundle) + "={false,false}",
                getResponseId("qn3.resp1", responseBundle) + "={true,false}",
                getResponseId("qn3.resp2", responseBundle) + "={false,false}",
                getResponseId("qn4.resp1", responseBundle) + "={true,true}",
                getResponseId("qn4.resp2", responseBundle) + "={true,true}",
                getResponseId("qn4.resp3", responseBundle) + "={true,true}",
                getResponseId("qn5.resp1", responseBundle) + "={false,true}",
                getResponseId("qn6.resp1", responseBundle) + "={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(10, results.visibilityTable.size());

        /*** Test result bundle for instructor1 within a section ***/

        ______TS("standard case to view by Section A with default section detail");

        results = fsLogic.getFeedbackSessionResultsForInstructorInSection(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email, "Section A", SectionDetail.EITHER);

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        // after filtering by section, the number of responses seen by instructor will differ.
        // Responses viewed by instructor after filtering: q2r1-3, q3r1, q4r2-3, q5r1
        assertEquals(7, results.responses.size());
        //Instructor should still see all questions
        assertEquals(8, results.questions.size());

        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                student1AnonEmail + "=" + student1AnonName,
                student2AnonEmail + "=" + student2AnonName,
                student3AnonEmail + "=" + student3AnonName,
                student6AnonEmail + "=" + student6AnonName,
                instructor1AnonEmail + "=" + instructor1AnonName,
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                student1AnonEmail + "=" + student1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student2AnonEmail + "=" + student2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student3AnonEmail + "=" + student3AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student6AnonEmail + "=" + student6AnonName + Const.TEAM_OF_EMAIL_OWNER,
                instructor1AnonEmail + "=" + instructor1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "Team 1.4=",
                "FSRTest.instr1@course1.tmt=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn3.resp1", responseBundle) + "={true,false}",
                getResponseId("qn4.resp3", responseBundle) + "={true,true}",
                getResponseId("qn2.resp3", responseBundle) + "={false,false}",
                getResponseId("qn2.resp1", responseBundle) + "={false,false}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(7, results.visibilityTable.size());
        // TODO: test student2 too.

        ______TS("standard case to view by receiver in Section A");

        results = fsLogic.getFeedbackSessionResultsForInstructorInSection(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email, "Section A", SectionDetail.EVALUEE);

        // Responses viewed by instructor after filtering: q1r1, q2r1, q4r3
        assertEquals(3, results.responses.size());
        assertEquals(8, results.questions.size());

        ______TS("standard case to view by giver in Section A");

        results = fsLogic.getFeedbackSessionResultsForInstructorInSection(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email, "Section A", SectionDetail.GIVER);

        // Responses viewed by instructor after filtering: q2r1-3, q3r1, q4r2-3, q5r1
        assertEquals(7, results.responses.size());
        assertEquals(8, results.questions.size());

        ______TS("standard case to view by both giver and receiver in Section A");

        results = fsLogic.getFeedbackSessionResultsForInstructorInSection(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email, "Section A", SectionDetail.BOTH);

        // Responses viewed by instructor after filtering: q2r1, q2r3, q3r1, q4r3
        assertEquals(4, results.responses.size());
        assertEquals(8, results.questions.size());

        ______TS("failure: no session");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.getFeedbackSessionResultsForInstructor(
                        "invalid session", session.getCourseId(), instructor.email));
        assertEquals("Trying to view a non-existent feedback session: " + session.getCourseId() + "/invalid session",
                ednee.getMessage());
        //TODO: check for cases where a person is both a student and an instructor
    }

    private void testGetFeedbackSessionResultsSummaryAsCsv() throws Exception {

        ______TS("typical case: get all results");

        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");

        String export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsAllResults.csv");

        ______TS("typical case: get all results with unchecked isMissingResponsesShown");

        session = dataBundle.feedbackSessions.get("session1InCourse1");
        instructor = dataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, false, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsHideMissingResponses.csv");

        ______TS("typical case: get results for single question");

        // results for single question with sectionDetail is tested in InstructorFeedbackResultsDownloadActionTest.java
        int questionNum = dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").getQuestionNumber();
        String questionId = fqLogic.getFeedbackQuestion(session.getFeedbackSessionName(),
                session.getCourseId(), questionNum).getId();

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, questionId, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsSingleQuestion.csv");

        ______TS("MCQ results");

        DataBundle newDataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(newDataBundle);
        session = newDataBundle.feedbackSessions.get("mcqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsMcqResults.csv");

        ______TS("MSQ results");

        session = newDataBundle.feedbackSessions.get("msqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsMsqResults.csv");

        ______TS("NUMSCALE results");

        session = newDataBundle.feedbackSessions.get("numscaleSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsNumscaleResults.csv");

        ______TS("CONSTSUM results");

        session = newDataBundle.feedbackSessions.get("constSumSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsConstsumResults.csv");

        ______TS("Instructor without privilege to view responses");

        instructor = newDataBundle.instructors.get("instructor2OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsConstsumResultsInstructorNoPrivilege.csv");

        ______TS("CONTRIB results");

        session = newDataBundle.feedbackSessions.get("contribSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsContribResults.csv");

        ______TS("CONTRIB summary visibility variations");

        // instructor not allowed to see student
        session = newDataBundle.feedbackSessions.get("contribSessionStudentAnonymised");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsContribResultsStudentsAnonymous.csv");

        // instructor not allowed to view student responses in section
        session = newDataBundle.feedbackSessions.get("contribSessionInstructorSectionRestricted");
        instructor = newDataBundle.instructors.get("instructor1OfCourseWithSections");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsContribResultsRestrictedSections.csv");

        ______TS("RUBRIC results");

        session = newDataBundle.feedbackSessions.get("rubricSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsRubricResults.csv");

        ______TS("RANK results");

        session = newDataBundle.feedbackSessions.get("rankSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, true);

        CsvChecker.verifyCsvContent(export, "/feedbackSessionResultsRankResults.csv");

        ______TS("MSQ results without statistics");

        session = newDataBundle.feedbackSessions.get("msqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, true, false);

        assertFalse(export.contains("Summary Statistics"));

        ______TS("Non-existent Course/Session");

        InstructorAttributes finalInstructor = instructor;
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                        "non.existent", "no course", finalInstructor.email, null, true, true));
        assertEquals("Trying to view a non-existent feedback session: no course/non.existent",
                ednee.getMessage());
    }

    private String getStudentAnonEmail(DataBundle dataBundle, String studentKey) {
        return FeedbackSessionResultsBundle.getAnonEmail(FeedbackParticipantType.STUDENTS,
                                                         dataBundle.students.get(studentKey).name);
    }

    private String getStudentAnonName(DataBundle dataBundle, String studentKey) {
        return FeedbackSessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS,
                                                        dataBundle.students.get(studentKey).name);
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
        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
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

    private FeedbackResponseCommentAttributes getCommentFromDatastore(String jsonId, DataBundle bundle) {
        FeedbackResponseCommentAttributes comment = bundle.feedbackResponseComments.get(jsonId);
        return frcLogic.getFeedbackResponseComment(comment.feedbackResponseId, comment.commentGiver, comment.createdAt);
    }

    private void unpublishAllSessions() throws InvalidParametersException, EntityDoesNotExistException {
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            if (fs.isPublished()) {
                fsLogic.unpublishFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
            }
        }
    }

    // Stringifies the visibility table for easy testing/comparison.
    private String tableToString(Map<String, boolean[]> table) {
        return table.entrySet().stream()
                .map(entry -> "{" + entry.getKey() + "={" + entry.getValue()[0] + ',' + entry.getValue()[1] + "}}")
                .collect(Collectors.joining(","));
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
        feedbackSession.resetDeletedTime();

        verifyPresentInDatastore(feedbackSession);
        assertFalse(feedbackSession.isSessionDeleted());
    }

    private void testRestoreAllFeedbackSessionsFromRecycleBin()
            throws InvalidParametersException, EntityDoesNotExistException {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse3");
        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(instructor);

        List<FeedbackSessionAttributes> softDeletedFsa = fsLogic.getSoftDeletedFeedbackSessionsListForInstructor(instructor);
        for (FeedbackSessionAttributes fsa : softDeletedFsa) {
            assertTrue(fsa.isSessionDeleted());
        }

        fsLogic.restoreAllFeedbackSessionsFromRecycleBin(instructors);

        for (FeedbackSessionAttributes fsa : softDeletedFsa) {
            fsa.resetDeletedTime();

            verifyPresentInDatastore(fsa);
            assertFalse(fsa.isSessionDeleted());
        }
    }

}
