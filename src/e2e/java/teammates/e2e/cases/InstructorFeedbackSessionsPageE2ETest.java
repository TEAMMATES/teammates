package teammates.e2e.cases;

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

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPage;
import teammates.e2e.util.TestProperties;
import teammates.test.ThreadHelper;

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
        testData = loadDataBundle("/InstructorFeedbackSessionsPageE2ETest.json");
        studentToEmail = testData.students.get("charlie.tmms@IFSess.CS1101");
        studentToEmail.setEmail(TestProperties.TEST_EMAIL);
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("instructor");
        course = testData.courses.get("course");
        copiedCourse = testData.courses.get("course2");

        openSession = testData.feedbackSessions.get("openSession");
        // To ensure the openSession is always open
        openSession.setEndTime(ZonedDateTime.now(ZoneId.of(copiedCourse.getTimeZone())).plus(Duration.ofDays(182))
                .truncatedTo(ChronoUnit.DAYS).toInstant());
        closedSession = testData.feedbackSessions.get("closedSession");
        newSession = FeedbackSessionAttributes
                .builder("New Session", course.getId())
                .withCreatorEmail(instructor.getEmail())
                .withStartTime(ZonedDateTime.now(ZoneId.of(course.getTimeZone())).plus(Duration.ofDays(2))
                        .truncatedTo(ChronoUnit.DAYS).toInstant())
                .withEndTime(ZonedDateTime.now(ZoneId.of(course.getTimeZone())).plus(Duration.ofDays(7))
                        .truncatedTo(ChronoUnit.DAYS).toInstant())
                .withSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                .withGracePeriod(Duration.ZERO)
                .withInstructions("<p>Please fill in the new feedback session.</p>")
                .withTimeZone(course.getTimeZone())
                .withIsClosingEmailEnabled(true)
                .withIsPublishedEmailEnabled(true)
                .build();

        fileName = "/" + openSession.getCourseId() + "_" + openSession.getFeedbackSessionName()
                + "_result.csv";
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE);
        InstructorFeedbackSessionsPage feedbackSessionsPage =
                loginToPage(url, InstructorFeedbackSessionsPage.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        FeedbackSessionAttributes[] loadedSessions = { openSession, closedSession };
        feedbackSessionsPage.sortByCourseId();
        feedbackSessionsPage.verifySessionsTable(loadedSessions);

        ______TS("verify response rate");
        feedbackSessionsPage.verifyResponseRate(closedSession, getExpectedResponseRate(closedSession));
        feedbackSessionsPage.verifyResponseRate(openSession, getExpectedResponseRate(openSession));

        ______TS("add new session");
        FeedbackSessionAttributes[] sessionsForAdded = { closedSession, newSession, openSession };
        feedbackSessionsPage.addFeedbackSession(newSession, true);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been added."
                + "Click the \"Add New Question\" button below to begin adding questions for the feedback session.");
        feedbackSessionsPage = getNewPageInstance(url,
                InstructorFeedbackSessionsPage.class);
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForAdded);
        verifyPresentInDatabase(newSession);

        ______TS("add new copied session");
        String newName = "Copied Name";
        FeedbackSessionAttributes copiedSession = openSession.getCopy();
        copiedSession.setCourseId(course.getId());
        copiedSession.setFeedbackSessionName(newName);
        copiedSession.setCreatedTime(Instant.now());
        int startHour = ZonedDateTime.ofInstant(copiedSession.getStartTime(), ZoneId.of(copiedSession.getTimeZone()))
                .getHour();
        copiedSession.setStartTime(ZonedDateTime.now(ZoneId.of(copiedSession.getTimeZone())).plus(Duration.ofDays(2))
                .withHour(startHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setEndTime(ZonedDateTime.now(ZoneId.of(copiedSession.getTimeZone())).plus(Duration.ofDays(180))
                .truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setSessionVisibleFromTime(ZonedDateTime.now(ZoneId.of(copiedSession.getTimeZone()))
                .minus(Duration.ofDays(28)).withHour(startHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        feedbackSessionsPage.addCopyOfSession(openSession, course, newName);

        feedbackSessionsPage = getNewPageInstance(url,
                InstructorFeedbackSessionsPage.class);
        feedbackSessionsPage.verifySessionDetails(copiedSession);
        verifyPresentInDatabase(copiedSession);

        ______TS("copy session");
        newName = "Copied Name 2";
        FeedbackSessionAttributes copiedSession2 = copiedSession.getCopy();
        copiedSession2.setFeedbackSessionName(newName);
        copiedSession2.setCreatedTime(Instant.now());
        feedbackSessionsPage.copySession(copiedSession, course, newName);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been copied. "
                + "Please modify settings/questions as necessary.");
        feedbackSessionsPage = getNewPageInstance(url,
                InstructorFeedbackSessionsPage.class);
        feedbackSessionsPage.verifySessionDetails(copiedSession2);
        verifyPresentInDatabase(copiedSession2);

        ______TS("publish results");
        openSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW);
        feedbackSessionsPage.publishSessionResults(openSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been published. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.");
        feedbackSessionsPage.verifySessionDetails(openSession);
        verifySessionPublishedState(openSession, true);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getFeedbackSessionName() + "]");

        ______TS("send reminder email to selected student");
        feedbackSessionsPage.sendReminderEmailToSelectedStudent(openSession, studentToEmail);

        feedbackSessionsPage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getFeedbackSessionName() + "]");

        ______TS("send reminder email to all student non-submitters");
        feedbackSessionsPage.sendReminderEmailToNonSubmitters(openSession);

        feedbackSessionsPage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                        + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");

        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getFeedbackSessionName() + "]");

        ______TS("resend results link");
        feedbackSessionsPage.resendResultsLink(openSession, studentToEmail);

        feedbackSessionsPage.verifyStatusMessage("Session published notification emails have been resent"
                + " to those students and instructors. Please allow up to 1 hour for all the notification emails to be"
                + " sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getFeedbackSessionName() + "]");

        ______TS("unpublish results");
        openSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        feedbackSessionsPage.unpublishSessionResults(openSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been unpublished.");
        feedbackSessionsPage.verifySessionDetails(openSession);
        verifySessionPublishedState(openSession, false);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results unpublished"
                + " [Course: " + copiedCourse.getName() + "][Feedback Session: "
                + openSession.getFeedbackSessionName() + "]");

        ______TS("download results");
        feedbackSessionsPage.downloadResults(openSession);
        List<String> expectedContent = Arrays.asList("Course,tm.e2e.IFSess.CS1101",
                "Session Name,Second Session", "Question 1,Testing question text");
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("soft delete session");
        closedSession.setDeletedTime(Instant.now());
        FeedbackSessionAttributes[] sessionsForSoftDelete = { copiedSession, copiedSession2, newSession, openSession };
        FeedbackSessionAttributes[] softDeletedSessions = { closedSession };
        feedbackSessionsPage.moveToRecycleBin(closedSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been deleted. "
                + "You can restore it from the deleted sessions table below.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForSoftDelete);
        feedbackSessionsPage.verifySoftDeletedSessionsTable(softDeletedSessions);
        assertNotNull(getSoftDeletedSession(closedSession.getFeedbackSessionName(),
                instructor.getGoogleId()));

        ______TS("restore session");
        FeedbackSessionAttributes[] sessionsForRestore = { openSession, newSession, closedSession, copiedSession2,
                copiedSession };
        feedbackSessionsPage.restoreSession(closedSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been restored.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForRestore);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        assertNull(getSoftDeletedSession(closedSession.getFeedbackSessionName(),
                instructor.getGoogleId()));

        ______TS("permanently delete session");
        FeedbackSessionAttributes[] sessionsForDelete = { copiedSession, copiedSession2, closedSession,
                openSession };
        feedbackSessionsPage.moveToRecycleBin(newSession);
        feedbackSessionsPage.deleteSession(newSession);

        feedbackSessionsPage.verifyStatusMessage("The feedback session has been permanently deleted.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForDelete);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        verifyAbsentInDatabase(newSession);

        ______TS("restore all session");
        FeedbackSessionAttributes[] sessionsForRestoreAll = { openSession, closedSession, copiedSession2,
                copiedSession };
        feedbackSessionsPage.moveToRecycleBin(copiedSession);
        feedbackSessionsPage.moveToRecycleBin(copiedSession2);
        feedbackSessionsPage.restoreAllSessions();

        feedbackSessionsPage.verifyStatusMessage("All sessions have been restored.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForRestoreAll);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        assertNull(getSoftDeletedSession(copiedSession.getFeedbackSessionName(),
                instructor.getGoogleId()));
        assertNull(getSoftDeletedSession(copiedSession2.getFeedbackSessionName(),
                instructor.getGoogleId()));

        ______TS("delete all session");
        feedbackSessionsPage.moveToRecycleBin(copiedSession);
        feedbackSessionsPage.moveToRecycleBin(copiedSession2);
        FeedbackSessionAttributes[] sessionsForDeleteAll = { closedSession, openSession };
        feedbackSessionsPage.deleteAllSessions();

        feedbackSessionsPage.verifyStatusMessage("All sessions have been permanently deleted.");
        feedbackSessionsPage.sortBySessionsName();
        feedbackSessionsPage.verifySessionsTable(sessionsForDeleteAll);
        feedbackSessionsPage.verifyNumSoftDeleted(0);
        verifyAbsentInDatabase(copiedSession);
        verifyAbsentInDatabase(copiedSession2);
    }

    private String getExpectedResponseRate(FeedbackSessionAttributes session) {
        String sessionName = session.getFeedbackSessionName();
        boolean hasQuestion = testData.feedbackQuestions.values()
                .stream()
                .anyMatch(q -> q.getFeedbackSessionName().equals(sessionName));

        if (!hasQuestion) {
            return "0 / 0";
        }

        long numStudents = testData.students.values()
                .stream()
                .filter(s -> s.getCourse().equals(session.getCourseId()))
                .count();

        Set<String> uniqueGivers = new HashSet<>();
        testData.feedbackResponses.values()
                .stream()
                .filter(r -> r.getFeedbackSessionName().equals(sessionName))
                .forEach(r -> uniqueGivers.add(r.getGiver()));
        int numResponses = uniqueGivers.size();

        return numResponses + " / " + numStudents;
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
