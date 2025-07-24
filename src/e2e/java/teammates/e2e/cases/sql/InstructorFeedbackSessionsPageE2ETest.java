package teammates.e2e.cases.sql;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPageSql;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.ThreadHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSIONS_PAGE}.
 */
public class InstructorFeedbackSessionsPageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private Course course;
    private Course copiedCourse;
    private Student studentToEmail;

    private FeedbackSession openSession;
    private FeedbackSession closedSession;
    private FeedbackSession newSession;

    private String fileName;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorFeedbackSessionsPageE2ETestSql.json"));

        // TODO Use real test email
        studentToEmail = testData.students.get("IFSessionPage.charlie");

        instructor = testData.instructors.get("IFSessionPage.instr1");

        course = testData.courses.get("IFSessionPage.CS2104");
        copiedCourse = testData.courses.get("IFSessionPage.CS1101");

        openSession = testData.feedbackSessions.get("openSession");
        // To ensure the openSession is always open
        openSession.setEndTime(ZonedDateTime.now(ZoneId.of(copiedCourse.getTimeZone())).plus(Duration.ofDays(182))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        closedSession = testData.feedbackSessions.get("closedSession");
        newSession = getTypicalFeedbackSessionForCourse(course);
        newSession.setStartTime(ZonedDateTime.now(ZoneId.of(course.getTimeZone())).plus(Duration.ofDays(2))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        newSession.setEndTime(ZonedDateTime.now(ZoneId.of(course.getTimeZone())).plus(Duration.ofDays(7))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
        newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        newSession.setGracePeriod(Duration.ZERO);

        fileName = "/" + closedSession.getCourseId() + "_" + closedSession.getName() + "_result.csv";
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE);
        InstructorFeedbackSessionsPageSql feedbackSessionsPage =
                loginToPage(url, InstructorFeedbackSessionsPageSql.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        FeedbackSession[] loadedSessions = { closedSession, openSession };
        feedbackSessionsPage.verifySessionsTable(loadedSessions);

        ______TS("verify response rate");
        feedbackSessionsPage.verifyResponseRate(closedSession, getExpectedResponseRate(closedSession));
        feedbackSessionsPage.verifyResponseRate(openSession, getExpectedResponseRate(openSession));

        ______TS("add new session");
        FeedbackSession[] sessionsForAdded = { openSession, closedSession, newSession };
        feedbackSessionsPage.addFeedbackSession(newSession, true);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been added."
                + "Click the \"Add New Question\" button below to begin adding questions for the feedback session.");
        feedbackSessionsPage = getNewPageInstance(url,
                InstructorFeedbackSessionsPageSql.class);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForAdded);
        verifyPresentInDatabase(newSession);

        ______TS("add new copied session");
        String newName = "Copied Name";
        FeedbackSession copiedSession = openSession.getCopy();
        copiedSession.setCourse(course);
        copiedSession.setName(newName);
        copiedSession.setCreatedAt(Instant.now());
        int startHour = ZonedDateTime.ofInstant(copiedSession.getStartTime(), ZoneId.of(copiedSession.getCourse()
                        .getTimeZone())).getHour();
        copiedSession.setStartTime(ZonedDateTime.now(ZoneId.of(copiedSession.getCourse().getTimeZone()))
                .plus(Duration.ofDays(2)).withHour(startHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setEndTime(ZonedDateTime.now(ZoneId.of(copiedSession.getCourse().getTimeZone()))
                .plus(Duration.ofDays(180)).truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setSessionVisibleFromTime(ZonedDateTime.now(ZoneId.of(copiedSession.getCourse().getTimeZone()))
                .minus(Duration.ofDays(28)).withHour(startHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        feedbackSessionsPage.addCopyOfSession(openSession, course, newName);

        feedbackSessionsPage = getNewPageInstance(url,
                InstructorFeedbackSessionsPageSql.class);
        feedbackSessionsPage.verifySessionDetails(copiedSession);
        verifyPresentInDatabase(copiedSession);

        ______TS("copy session");
        newName = "Copied Name 2";
        FeedbackSession copiedSession2 = copiedSession.getCopy();
        copiedSession2.setName(newName);
        copiedSession2.setCreatedAt(Instant.now());
        feedbackSessionsPage.copySession(copiedSession, course, newName);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been copied. "
                + "Please modify settings/questions as necessary.");
        feedbackSessionsPage = getNewPageInstance(url,
                InstructorFeedbackSessionsPageSql.class);
        feedbackSessionsPage.verifySessionDetails(copiedSession2);
        verifyPresentInDatabase(copiedSession2);

        ______TS("publish results");
        openSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW);
        feedbackSessionsPage.publishSessionResults(openSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been published. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.");
        feedbackSessionsPage.verifySessionDetails(openSession);
        verifySessionPublishedState(openSession, FeedbackSessionPublishStatus.PUBLISHED);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getName() + "]");

        ______TS("send reminder email to selected student");
        feedbackSessionsPage.sendReminderEmailToSelectedStudent(openSession, studentToEmail);

        feedbackSessionsPage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getName() + "]");

        ______TS("send reminder email to all student non-submitters");
        feedbackSessionsPage.sendReminderEmailToNonSubmitters(openSession);

        feedbackSessionsPage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");

        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getName() + "]");

        ______TS("resend results link");
        feedbackSessionsPage.resendResultsLink(openSession, studentToEmail);

        feedbackSessionsPage.verifyStatusMessage("Session published notification emails have been resent"
                + " to those students and instructors. Please allow up to 1 hour for all the notification emails to be"
                + " sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getName() + "]");

        ______TS("unpublish results");
        openSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        feedbackSessionsPage.unpublishSessionResults(openSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been unpublished.");
        feedbackSessionsPage.verifySessionDetails(openSession);
        verifySessionPublishedState(openSession, FeedbackSessionPublishStatus.NOT_PUBLISHED);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results unpublished"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getName() + "]");

        ______TS("download results");
        feedbackSessionsPage.downloadResults(closedSession);
        List<String> expectedContent = Arrays.asList("Course,tm.e2e.sql.CS1101",
                "Session Name,Second Session", "Question 1,What did this instructor do well?");
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("soft delete session");
        closedSession.setDeletedAt(Instant.now());
        FeedbackSession[] sessionsForSoftDelete = {copiedSession, copiedSession2, openSession, newSession};
        FeedbackSession[] softDeletedSessions = {closedSession};
        feedbackSessionsPage.moveToRecycleBin(closedSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been deleted. "
                + "You can restore it from the deleted sessions table below.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForSoftDelete);
        feedbackSessionsPage.verifySoftDeletedSessionsTable(softDeletedSessions);
        assertNotNull(getSoftDeletedSession(closedSession.getName(),
                instructor.getGoogleId()));

        ______TS("restore session");
        FeedbackSession[] sessionsForRestore = { newSession, closedSession, openSession, copiedSession2,
                copiedSession };
        feedbackSessionsPage.restoreSession(closedSession);
        feedbackSessionsPage.verifyStatusMessage("The feedback session has been restored.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForRestore);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        assertNull(getSoftDeletedSession(closedSession.getName(),
                instructor.getGoogleId()));

        ______TS("permanently delete session");
        FeedbackSession[] sessionsForDelete = { copiedSession, copiedSession2, openSession, closedSession};
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.moveToRecycleBin(newSession);
        feedbackSessionsPage.deleteSession(newSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been permanently deleted.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForDelete);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        verifyAbsentInDatabase(newSession);

        ______TS("restore all session");
        FeedbackSession[] sessionsForRestoreAll = { copiedSession, copiedSession2, openSession, closedSession };
        feedbackSessionsPage.moveToRecycleBin(copiedSession);
        feedbackSessionsPage.moveToRecycleBin(copiedSession2);
        feedbackSessionsPage.restoreAllSessions();

        feedbackSessionsPage.verifyStatusMessage("All sessions have been restored.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForRestoreAll);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        assertNull(getSoftDeletedSession(copiedSession.getName(),
                instructor.getGoogleId()));
        assertNull(getSoftDeletedSession(copiedSession2.getName(),
                instructor.getGoogleId()));

        ______TS("delete all session");
        feedbackSessionsPage.moveToRecycleBin(copiedSession);
        feedbackSessionsPage.moveToRecycleBin(copiedSession2);
        FeedbackSession[] sessionsForDeleteAll = { openSession, closedSession };
        feedbackSessionsPage.deleteAllSessions();

        feedbackSessionsPage.verifyStatusMessage("All sessions have been permanently deleted.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForDeleteAll);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        verifyAbsentInDatabase(copiedSession);
        verifyAbsentInDatabase(copiedSession2);
    }

    private String getExpectedResponseRate(FeedbackSession session) {
        String sessionName = session.getName();
        boolean hasQuestion = testData.feedbackQuestions.values()
                .stream()
                .anyMatch(q -> q.getFeedbackSessionName().equals(sessionName));

        if (!hasQuestion) {
            return "0 / 0";
        }

        long numStudents = testData.students.values()
                .stream()
                .filter(s -> s.getCourseId().equals(session.getCourseId()))
                .count();

        Set<String> uniqueGivers = new HashSet<>();
        testData.feedbackResponses.values()
                .stream()
                .filter(r -> r.getFeedbackQuestion().getFeedbackSessionName().equals(sessionName))
                .forEach(r -> uniqueGivers.add(r.getGiver()));
        int numResponses = uniqueGivers.size();

        return numResponses + " / " + numStudents;
    }

    private void verifySessionPublishedState(FeedbackSession feedbackSession, FeedbackSessionPublishStatus publishStatus) {
        int retryLimit = 5;
        FeedbackSessionData actual = getFeedbackSession(feedbackSession.getCourseId(),
                feedbackSession.getName());
        while (actual.getPublishStatus() == publishStatus && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getFeedbackSession(feedbackSession.getCourseId(),
                    feedbackSession.getName());
        }
        assertEquals(actual.getPublishStatus(), publishStatus);
    }
}
