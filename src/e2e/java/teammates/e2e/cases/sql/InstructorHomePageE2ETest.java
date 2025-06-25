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
import teammates.e2e.pageobjects.InstructorHomePageSql;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.ThreadHelper;
import teammates.ui.output.FeedbackSessionData;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private Student studentToEmail;
    private Course course;
    private Course otherCourse;

    private FeedbackSession feedbackSessionAwaiting;
    private FeedbackSession feedbackSessionOpen;
    private FeedbackSession feedbackSessionClosed;
    private FeedbackSession feedbackSessionPublished;
    private FeedbackSession otherCourseSession;

    private String fileName;

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorHomePageE2ETestSql.json");
        studentToEmail = testData.students.get("IHome.charlie.d.tmms@IHome.CS2104");
        studentToEmail.setEmail(TestProperties.TEST_EMAIL);
        testData = removeAndRestoreDataBundle(testData);
        putDocuments(testData);

        instructor = testData.instructors.get("IHome.instr.CS2104");
        course = testData.courses.get("IHome.CS2104");
        otherCourse = testData.courses.get("IHome.CS1101");

        feedbackSessionAwaiting = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSessionOpen = testData.feedbackSessions.get("First Feedback Session");
        feedbackSessionClosed = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSessionPublished = testData.feedbackSessions.get("Fourth Feedback Session");
        otherCourseSession = testData.feedbackSessions.get("CS1101 Session");

        fileName = "/" + feedbackSessionOpen.getCourse().getId() + "_" + feedbackSessionOpen.getName()
                + "_result.csv";
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE);
        InstructorHomePageSql homePage = loginToPage(url, InstructorHomePageSql.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        homePage.sortCoursesById();
        int courseIndex = 1;
        int otherCourseIndex = 0;
        // by default, sessions are sorted by end date in descending order
        FeedbackSession[] courseSessions = { feedbackSessionOpen, feedbackSessionAwaiting,
                feedbackSessionClosed, feedbackSessionPublished };
        FeedbackSession[] otherCourseSessions = { otherCourseSession };
        // use course index instead of searching for course in table to test sorted order of courses
        homePage.verifyCourseTabDetails(otherCourseIndex, otherCourse, otherCourseSessions);
        homePage.verifyCourseTabDetails(courseIndex, course, courseSessions);

        ______TS("notification banner is visible");
        assertTrue(homePage.isBannerVisible());

        ______TS("verify response rate");
        for (int i = 0; i < courseSessions.length; i++) {
            homePage.verifyResponseRate(courseIndex, i, getExpectedResponseRate(courseSessions[i]));
        }

        ______TS("copy session with modified session timings");
        int sessionIndex = 1;
        String newName = "Copied Name";
        FeedbackSession copiedSession = feedbackSessionAwaiting.getCopy();
        copiedSession.setCourse(otherCourse);
        copiedSession.setName(newName);
        copiedSession.setCreatedAt(Instant.now());
        int startHour = ZonedDateTime.ofInstant(copiedSession.getStartTime(),
                        ZoneId.of(copiedSession.getCourse().getTimeZone())).getHour();
        copiedSession.setStartTime(ZonedDateTime.now(ZoneId.of(otherCourse.getTimeZone())).plus(Duration.ofDays(2))
                .withHour(startHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        int endHour = ZonedDateTime.ofInstant(copiedSession.getEndTime(), ZoneId.of(copiedSession.getCourse().getTimeZone()))
                .getHour();
        copiedSession.setEndTime(ZonedDateTime.now(ZoneId.of(otherCourse.getTimeZone())).plus(Duration.ofDays(7))
                .withHour(endHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setSessionVisibleFromTime(ZonedDateTime.now(ZoneId.of(otherCourse.getTimeZone()))
                .minus(Duration.ofDays(28)).withHour(startHour).truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        homePage.copySession(courseIndex, sessionIndex, otherCourse, newName);

        homePage.waitForConfirmationModalAndClickOk();
        homePage = getNewPageInstance(url, InstructorHomePageSql.class);
        homePage.sortCoursesByName();
        // flip index after sorting
        courseIndex = 0;
        otherCourseIndex = 1;
        FeedbackSession[] otherCourseSessionsWithCopy = { copiedSession, otherCourseSession };
        homePage.verifyCourseTabDetails(otherCourseIndex, otherCourse, otherCourseSessionsWithCopy);
        verifyPresentInDatabase(copiedSession);

        ______TS("copy session with same session timings");
        sessionIndex = 0;
        newName = "Copied Name 2";
        FeedbackSession copiedSession2 = copiedSession.getCopy();
        copiedSession2.setName(newName);
        copiedSession2.setCreatedAt(Instant.now());
        homePage.copySession(otherCourseIndex, sessionIndex, otherCourse, newName);

        homePage.verifyStatusMessage("The feedback session has been copied. "
                + "Please modify settings/questions as necessary.");
        homePage = getNewPageInstance(url, InstructorHomePageSql.class);
        homePage.sortCoursesByName();
        FeedbackSession[] otherCourseSessionsWithTwoCopies = { copiedSession, copiedSession2, otherCourseSession };
        homePage.verifyCourseTabDetails(otherCourseIndex, otherCourse, otherCourseSessionsWithTwoCopies);
        verifyPresentInDatabase(copiedSession2);

        ______TS("publish results");
        sessionIndex = 0;
        feedbackSessionOpen.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NOW);
        homePage.publishSessionResults(courseIndex, sessionIndex);

        homePage.verifyStatusMessage("The feedback session has been published. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.");
        homePage.verifySessionDetails(courseIndex, sessionIndex, feedbackSessionOpen);
        verifySessionPublishedState(feedbackSessionOpen, true);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getName() + "]");

        ______TS("send reminder email to selected student");
        homePage.sendReminderEmailToSelectedStudent(courseIndex, sessionIndex, studentToEmail);

        homePage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getName() + "]");

        ______TS("send reminder email to all student non-submitters");
        homePage.sendReminderEmailToNonSubmitters(courseIndex, sessionIndex);

        homePage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getName() + "]");
        ______TS("resend results link");
        homePage.resendResultsLink(courseIndex, sessionIndex, studentToEmail);

        homePage.verifyStatusMessage("Session published notification emails have been resent"
                + " to those students and instructors. Please allow up to 1 hour for all the notification emails to be"
                + " sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getName() + "]");

        ______TS("unpublish results");
        feedbackSessionOpen.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        homePage.unpublishSessionResults(courseIndex, sessionIndex);

        homePage.verifyStatusMessage("The feedback session has been unpublished.");
        homePage.verifySessionDetails(courseIndex, sessionIndex, feedbackSessionOpen);
        verifySessionPublishedState(feedbackSessionOpen, false);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results unpublished"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getName() + "]");

        ______TS("download results");
        homePage.downloadResults(courseIndex, sessionIndex);
        List<String> expectedContent = Arrays.asList("Course,tm.e2e.IHome.CS2104",
                "Session Name,First Feedback Session", "Question 1,Rate 5 other students' products");
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("soft delete session");
        sessionIndex = 1;
        copiedSession.setDeletedAt(Instant.now());
        homePage.deleteSession(otherCourseIndex, sessionIndex);

        homePage.verifyStatusMessage("The feedback session has been deleted. "
                + "You can restore it from the 'Sessions' tab.");
        homePage.sortCoursesByName();
        otherCourseIndex = 1;
        FeedbackSession[] otherCourseSessionsWithCopyTwo = { copiedSession, otherCourseSession };
        homePage.verifyCourseTabDetails(otherCourseIndex, otherCourse, otherCourseSessionsWithCopyTwo);
        assertNotNull(getSoftDeletedSession(copiedSession2.getName(),
                instructor.getGoogleId()));

        ______TS("delete course");
        otherCourseIndex = 1;
        homePage.deleteCourse(otherCourseIndex);

        homePage.verifyStatusMessage("The course " + otherCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        homePage.verifyNumCourses(1);
        assertTrue(BACKDOOR.isCourseInRecycleBin(otherCourse.getId()));
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
                .filter(s -> s.getCourse().getId().equals(session.getCourse().getId()))
                .count();

        Set<String> uniqueGivers = new HashSet<>();
        testData.feedbackResponses.values()
                .stream()
                .filter(r -> r.getFeedbackQuestion().getFeedbackSessionName().equals(sessionName))
                .forEach(r -> uniqueGivers.add(r.getGiver()));
        int numResponses = uniqueGivers.size();

        return numResponses + " / " + numStudents;
    }

    private void verifySessionPublishedState(FeedbackSession feedbackSession, boolean state) {
        int retryLimit = 5;
        FeedbackSessionData actual = getFeedbackSession(feedbackSession.getCourse().getId(),
                feedbackSession.getName());
        while (isFeedbackSessionPublished(actual.getPublishStatus()) != state && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getFeedbackSession(feedbackSession.getCourse().getId(),
                    feedbackSession.getName());
        }
        assertEquals(isFeedbackSessionPublished(actual.getPublishStatus()), state);
    }
}
