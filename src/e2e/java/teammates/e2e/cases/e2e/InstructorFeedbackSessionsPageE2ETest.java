package teammates.e2e.cases.e2e;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSIONS_PAGE}.
 */
public class InstructorFeedbackSessionsPageE2ETest extends BaseE2ETestCase {
	private InstructorAttributes instructor;
    private CourseAttributes course;
    private CourseAttributes copiedCourse;
    private StudentAttributes studentToEmail;

    private FeedbackSessionAttributes openSession;
    private FeedbackSessionAttributes closedSession;
    private FeedbackSessionAttributes newSession;

    private String fileName;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle(Const.TestCase.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE_E2E_TEST_JSON);
        studentToEmail = testData.students.get(Const.TestCase.CHARLIE_TMMS_C_FEEDBACK_SESSIONS_E2E_T_CS1101);
        if (!TestProperties.isDevServer()) {
            studentToEmail.email = TestProperties.TEST_STUDENT1_ACCOUNT;
        }
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get(Const.TestCase.INSTRUCTOR_CONTENT);
        course = testData.courses.get(Const.TestCase.COURSE_CONTENT);
        copiedCourse = testData.courses.get(Const.TestCase.COURSE_CONTENT2);

        openSession = testData.feedbackSessions.get(Const.TestCase.OPEN_SESSION);
        closedSession = testData.feedbackSessions.get(Const.TestCase.CLOSED_SESSION);
        newSession = FeedbackSessionAttributes
                .builder(Const.TestCase.NEW_SESSION, course.getId())
                .withCreatorEmail(instructor.getEmail())
                .withStartTime(TimeHelper.parseInstant(Const.TestCase._2035_04_01_9_59_PM_0000))
                .withEndTime(TimeHelper.parseInstant(Const.TestCase._2035_04_30_8_00_PM_0000))
                .withSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                .withGracePeriod(Duration.ZERO)
                .withInstructions(Const.TestCase.P_PLEASE_FILL_IN_THE_NEW_FEEDBACK_SESSION_P)
                .withTimeZone(course.getTimeZone())
                .withIsClosingEmailEnabled(true)
                .withIsPublishedEmailEnabled(true)
                .build();

        fileName = Const.TestCase.FRONT_SLASH + openSession.getCourseId() + Const.TestCase.UNDERSCORE + openSession.getFeedbackSessionName()
                + Const.TestCase._RESULT_CSV;
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);
    }

    @Test
    public void allTests() {
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE).withUserId(instructor.googleId);
        InstructorFeedbackSessionsPage feedbackSessionsPage = loginAdminToPage(url, InstructorFeedbackSessionsPage.class);

        ______TS(Const.TestCase.VERIFY_LOADED_DATA);
        FeedbackSessionAttributes[] loadedSessions = { openSession, closedSession };
        feedbackSessionsPage.sortByCourseId();
        feedbackSessionsPage.verifySessionsTable(loadedSessions);

        ______TS(Const.TestCase.VERIFY_RESPONSE_RATE);
        feedbackSessionsPage.verifyResponseRate(closedSession, getExpectedResponseRate(closedSession));
        feedbackSessionsPage.verifyResponseRate(openSession, getExpectedResponseRate(openSession));

        ______TS(Const.TestCase.ADD_NEW_SESSION);
        FeedbackSessionAttributes[] sessionsForAdded = { closedSession, newSession, openSession };
        feedbackSessionsPage.addFeedbackSession(newSession, true);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_ADDED
                + Const.TestCase.CLICK_THE_ADD_NEW_QUESTION_BUTTON_BELOW_TO_BEGIN_ADDING_QUESTIONS_FOR_THE_FEEDBACK_SESSION);
        feedbackSessionsPage = AppPage.getNewPageInstance(browser, url,
                InstructorFeedbackSessionsPage.class);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForAdded);
        verifyPresentInDatastore(newSession);

        ______TS(Const.TestCase.ADD_NEW_COPIED_SESSION);
        String newName = Const.TestCase.COPIED_NAME;
        FeedbackSessionAttributes copiedSession = openSession.getCopy();
        copiedSession.setCourseId(course.getId());
        copiedSession.setFeedbackSessionName(newName);
        copiedSession.setCreatedTime(Instant.now());
        feedbackSessionsPage.addCopyOfSession(openSession, course, newName);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_COPIED
                        + Const.TestCase.PLEASE_MODIFY_SETTINGS_QUESTIONS_AS_NECESSARY);
        feedbackSessionsPage = AppPage.getNewPageInstance(browser, url,
                InstructorFeedbackSessionsPage.class);
        feedbackSessionsPage.verifySessionDetails(copiedSession);
        verifyPresentInDatastore(copiedSession);

        ______TS(Const.TestCase.COPY_SESSION);
        newName = Const.TestCase.COPIED_NAME_2;
        FeedbackSessionAttributes copiedSession2 = openSession.getCopy();
        copiedSession2.setCourseId(course.getId());
        copiedSession2.setFeedbackSessionName(newName);
        copiedSession2.setCreatedTime(Instant.now());
        feedbackSessionsPage.copySession(openSession, course, newName);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_COPIED
                + Const.TestCase.PLEASE_MODIFY_SETTINGS_QUESTIONS_AS_NECESSARY);
        feedbackSessionsPage = AppPage.getNewPageInstance(browser, url,
                InstructorFeedbackSessionsPage.class);
        feedbackSessionsPage.verifySessionDetails(copiedSession2);
        verifyPresentInDatastore(copiedSession2);

        ______TS(Const.TestCase.PUBLISH_RESULTS);
        openSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW);
        feedbackSessionsPage.publishSessionResults(openSession);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_PUBLISHED
                + Const.TestCase.PLEASE_ALLOW_UP_TO_1_HOUR_FOR_ALL_THE_NOTIFICATION_EMAILS_TO_BE_SENT_OUT);
        feedbackSessionsPage.verifySessionDetails(openSession);
        verifySessionPublishedState(openSession, true);
        verifyEmailSent(studentToEmail.getEmail(), Const.TestCase.TEAMMATES_FEEDBACK_SESSION_RESULTS_PUBLISHED
                + Const.TestCase.LEFT_SQUARE_PAREN_COURSE + copiedCourse.getName() + Const.TestCase.LEFT_AND_RIGHT_SQR_PAREN_FEEDBACK_SESSION
                + openSession.getFeedbackSessionName() + Const.TestCase.RIGHT_SQUARE_PAREN);

        ______TS(Const.TestCase.SEND_REMINDER_EMAIL);
        feedbackSessionsPage.sendReminderEmail(openSession, studentToEmail);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.REMINDER_E_MAILS_HAVE_BEEN_SENT_OUT_TO_THOSE_STUDENTS
                + Const.TestCase.AND_INSTRUCTORS_PLEASE_ALLOW_UP_TO_1_HOUR_FOR_ALL_THE_NOTIFICATION_EMAILS_TO_BE_SENT_OUT);
        verifyEmailSent(studentToEmail.getEmail(), Const.TestCase.TEAMMATES_FEEDBACK_SESSION_REMINDER
                + Const.TestCase.LEFT_SQUARE_PAREN_COURSE + copiedCourse.getName() + Const.TestCase.LEFT_AND_RIGHT_SQR_PAREN_FEEDBACK_SESSION
                + openSession.getFeedbackSessionName() + Const.TestCase.RIGHT_SQUARE_PAREN);

        ______TS(Const.TestCase.RESEND_RESULTS_LINK);
        feedbackSessionsPage.resendResultsLink(openSession, studentToEmail);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.SESSION_PUBLISHED_NOTIFICATION_EMAILS_HAVE_BEEN_RESENT
                + Const.TestCase.TO_THOSE_STUDENTS_AND_INSTRUCTORS_PLEASE_ALLOW_UP_TO_1_HOUR_FOR_ALL_THE_NOTIFICATION_EMAILS_TO_BE
                + Const.TestCase.SENT_OUT);
        verifyEmailSent(studentToEmail.getEmail(), Const.TestCase.TEAMMATES_FEEDBACK_SESSION_RESULTS_PUBLISHED
                + Const.TestCase.LEFT_SQUARE_PAREN_COURSE + copiedCourse.getName() + Const.TestCase.LEFT_AND_RIGHT_SQR_PAREN_FEEDBACK_SESSION
                + openSession.getFeedbackSessionName() + Const.TestCase.RIGHT_SQUARE_PAREN);

        ______TS(Const.TestCase.UNPUBLISH_RESULTS);
        openSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        feedbackSessionsPage.unpublishSessionResults(openSession);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_UNPUBLISHED);
        feedbackSessionsPage.verifySessionDetails(openSession);
        verifySessionPublishedState(openSession, false);
        verifyEmailSent(studentToEmail.getEmail(), Const.TestCase.TEAMMATES_FEEDBACK_SESSION_RESULTS_UNPUBLISHED
                + Const.TestCase.LEFT_SQUARE_PAREN_COURSE + copiedCourse.getName() + Const.TestCase.LEFT_AND_RIGHT_SQR_PAREN_FEEDBACK_SESSION
                + openSession.getFeedbackSessionName() + Const.TestCase.RIGHT_SQUARE_PAREN);

        ______TS(Const.TestCase.DOWNLOAD_RESULTS);
        feedbackSessionsPage.downloadResults(openSession);
        List<String> expectedContent = Arrays.asList(Const.TestCase.COURSE_C_FEEDBACK_SESSIONS_E2E_T_CS1101,
        		Const.TestCase.SESSION_NAME_SECOND_SESSION, Const.TestCase.QUESTION_1_TESTING_QUESTION_TEXT);
        verifyDownloadedFile(fileName, expectedContent);

        ______TS(Const.TestCase.SOFT_DELETE_SESSION);
        closedSession.setDeletedTime(Instant.now());
        FeedbackSessionAttributes[] sessionsForSoftDelete = { copiedSession, copiedSession2, newSession, openSession };
        FeedbackSessionAttributes[] softDeletedSessions = { closedSession };
        feedbackSessionsPage.moveToRecycleBin(closedSession);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_DELETED
                + Const.TestCase.YOU_CAN_RESTORE_IT_FROM_THE_DELETED_SESSIONS_TABLE_BELOW);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForSoftDelete);
        feedbackSessionsPage.verifySoftDeletedSessionsTable(softDeletedSessions);
        assertNotNull(getSoftDeletedSession(closedSession.getFeedbackSessionName(),
                instructor.googleId));

        ______TS(Const.TestCase.RESTORE_SESSION);
        FeedbackSessionAttributes[] sessionsForRestore = { openSession, newSession, closedSession, copiedSession2,
                copiedSession };
        feedbackSessionsPage.restoreSession(closedSession);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_RESTORED);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForRestore);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        assertNull(getSoftDeletedSession(closedSession.getFeedbackSessionName(),
                instructor.googleId));

        ______TS(Const.TestCase.PERMANENTLY_DELETE_SESSION);
        FeedbackSessionAttributes[] sessionsForDelete = { copiedSession, copiedSession2, closedSession,
                openSession };
        feedbackSessionsPage.moveToRecycleBin(newSession);
        feedbackSessionsPage.deleteSession(newSession);

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.THE_FEEDBACK_SESSION_HAS_BEEN_PERMANENTLY_DELETED);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForDelete);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        verifyAbsentInDatastore(newSession);

        ______TS(Const.TestCase.RESTORE_ALL_SESSION);
        FeedbackSessionAttributes[] sessionsForRestoreAll = { openSession, closedSession, copiedSession2,
                copiedSession };
        feedbackSessionsPage.moveToRecycleBin(copiedSession);
        feedbackSessionsPage.moveToRecycleBin(copiedSession2);
        feedbackSessionsPage.restoreAllSessions();

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.ALL_SESSIONS_HAVE_BEEN_RESTORED);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForRestoreAll);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        assertNull(getSoftDeletedSession(copiedSession.getFeedbackSessionName(),
                instructor.googleId));
        assertNull(getSoftDeletedSession(copiedSession2.getFeedbackSessionName(),
                instructor.googleId));

        ______TS(Const.TestCase.DELETE_ALL_SESSION);
        feedbackSessionsPage.moveToRecycleBin(copiedSession);
        feedbackSessionsPage.moveToRecycleBin(copiedSession2);
        FeedbackSessionAttributes[] sessionsForDeleteAll = { closedSession, openSession };
        feedbackSessionsPage.deleteAllSessions();

        feedbackSessionsPage.verifyStatusMessage(Const.TestCase.ALL_SESSIONS_HAVE_BEEN_PERMANENTLY_DELETED);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForDeleteAll);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        verifyAbsentInDatastore(copiedSession);
        verifyAbsentInDatastore(copiedSession2);
    }

    private String getExpectedResponseRate(FeedbackSessionAttributes session) {
        String sessionName = session.getFeedbackSessionName();
        boolean hasQuestion = testData.feedbackQuestions.values()
                .stream()
                .anyMatch(q -> q.feedbackSessionName.equals(sessionName));

        if (!hasQuestion) {
            return Const.TestCase._0_0;
        }

        long numStudents = testData.students.values()
                .stream()
                .filter(s -> s.getCourse().equals(session.getCourseId()))
                .count();

        Set<String> uniqueGivers = new HashSet<>();
        testData.feedbackResponses.values()
                .stream()
                .filter(r -> r.feedbackSessionName.equals(sessionName))
                .forEach(r -> uniqueGivers.add(r.giver));
        int numResponses = uniqueGivers.size();

        return numResponses + Const.TestCase.SPACE_FRONT_SLASH_SPACE + numStudents;
    }

    private void verifySessionPublishedState(FeedbackSessionAttributes feedbackSession, boolean state) {
        int retryLimit = 5;
        FeedbackSessionAttributes actual = getFeedbackSession(feedbackSession.getCourseId(),
                feedbackSession.getFeedbackSessionName());
        while (actual.isPublished() == state && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getFeedbackSession(feedbackSession.getCourseId(),
                    feedbackSession.getFeedbackSessionName());
        }
        assertEquals(actual.isPublished(), state);
    }
}
