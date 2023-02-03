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
import teammates.e2e.pageobjects.InstructorHomePage;
import teammates.e2e.util.TestProperties;
import teammates.test.ThreadHelper;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_HOME_PAGE}.
 */
public class InstructorHomePageE2ETest extends BaseE2ETestCase {
    private InstructorAttributes instructor;
    private StudentAttributes studentToEmail;
    private CourseAttributes course;
    private CourseAttributes otherCourse;

    private FeedbackSessionAttributes feedbackSessionAwaiting;
    private FeedbackSessionAttributes feedbackSessionOpen;
    private FeedbackSessionAttributes feedbackSessionClosed;
    private FeedbackSessionAttributes feedbackSessionPublished;
    private FeedbackSessionAttributes otherCourseSession;

    private String fileName;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorHomePageE2ETest.json");
        studentToEmail = testData.students.get("IHome.charlie.d.tmms@IHome.CS2104");
        studentToEmail.setEmail(TestProperties.TEST_EMAIL);
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);

        instructor = testData.instructors.get("IHome.instr.CS2104");
        course = testData.courses.get("IHome.CS2104");
        otherCourse = testData.courses.get("IHome.CS1101");

        feedbackSessionAwaiting = testData.feedbackSessions.get("Second Feedback Session");
        feedbackSessionOpen = testData.feedbackSessions.get("First Feedback Session");
        feedbackSessionClosed = testData.feedbackSessions.get("Third Feedback Session");
        feedbackSessionPublished = testData.feedbackSessions.get("Fourth Feedback Session");
        otherCourseSession = testData.feedbackSessions.get("CS1101 Session");

        fileName = "/" + feedbackSessionOpen.getCourseId() + "_" + feedbackSessionOpen.getFeedbackSessionName()
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
        InstructorHomePage homePage = loginToPage(url, InstructorHomePage.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        homePage.sortCoursesById();
        int courseIndex = 1;
        int otherCourseIndex = 0;
        // by default, sessions are sorted by end date in descending order
        FeedbackSessionAttributes[] courseSessions = { feedbackSessionOpen, feedbackSessionAwaiting,
                feedbackSessionClosed, feedbackSessionPublished };
        FeedbackSessionAttributes[] otherCourseSessions = { otherCourseSession };
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
        FeedbackSessionAttributes copiedSession = feedbackSessionAwaiting.getCopy();
        copiedSession.setCourseId(otherCourse.getId());
        copiedSession.setFeedbackSessionName(newName);
        copiedSession.setCreatedTime(Instant.now());
        copiedSession.setStartTime(ZonedDateTime.now(ZoneId.of(otherCourse.getTimeZone())).plus(Duration.ofDays(2))
                .truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setEndTime(ZonedDateTime.now(ZoneId.of(otherCourse.getTimeZone())).plus(Duration.ofDays(7))
                .truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setSessionVisibleFromTime(ZonedDateTime.now(ZoneId.of(otherCourse.getTimeZone()))
                .minus(Duration.ofDays(28)).truncatedTo(ChronoUnit.HOURS).toInstant());
        copiedSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        copiedSession.setTimeZone(otherCourse.getTimeZone());
        homePage.copySession(courseIndex, sessionIndex, otherCourse, newName);

        homePage.waitForConfirmationModalAndClickOk();
        homePage = getNewPageInstance(url, InstructorHomePage.class);
        homePage.sortCoursesByName();
        // flip index after sorting
        courseIndex = 0;
        otherCourseIndex = 1;
        FeedbackSessionAttributes[] otherCourseSessionsWithCopy = { copiedSession, otherCourseSession };
        homePage.verifyCourseTabDetails(otherCourseIndex, otherCourse, otherCourseSessionsWithCopy);
        verifyPresentInDatabase(copiedSession);

        ______TS("copy session with same session timings");
        sessionIndex = 0;
        newName = "Copied Name 2";
        FeedbackSessionAttributes copiedSession2 = copiedSession.getCopy();
        copiedSession2.setFeedbackSessionName(newName);
        copiedSession2.setCreatedTime(Instant.now());
        homePage.copySession(otherCourseIndex, sessionIndex, otherCourse, newName);

        homePage.verifyStatusMessage("The feedback session has been copied. "
                + "Please modify settings/questions as necessary.");
        homePage = getNewPageInstance(url, InstructorHomePage.class);
        homePage.sortCoursesByName();
        FeedbackSessionAttributes[] otherCourseSessionsWithTwoCopies = { copiedSession2, copiedSession, otherCourseSession };
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
                + feedbackSessionOpen.getFeedbackSessionName() + "]");

        ______TS("send reminder email to selected student");
        homePage.sendReminderEmailToSelectedStudent(courseIndex, sessionIndex, studentToEmail);

        homePage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getFeedbackSessionName() + "]");

        ______TS("send reminder email to all student non-submitters");
        homePage.sendReminderEmailToNonSubmitters(courseIndex, sessionIndex);

        homePage.verifyStatusMessage("Reminder e-mails have been sent out to those students"
                + " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.");

        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getFeedbackSessionName() + "]");

        ______TS("resend results link");
        homePage.resendResultsLink(courseIndex, sessionIndex, studentToEmail);

        homePage.verifyStatusMessage("Session published notification emails have been resent"
                + " to those students and instructors. Please allow up to 1 hour for all the notification emails to be"
                + " sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getFeedbackSessionName() + "]");

        ______TS("unpublish results");
        feedbackSessionOpen.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        homePage.unpublishSessionResults(courseIndex, sessionIndex);

        homePage.verifyStatusMessage("The feedback session has been unpublished.");
        homePage.verifySessionDetails(courseIndex, sessionIndex, feedbackSessionOpen);
        verifySessionPublishedState(feedbackSessionOpen, false);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results unpublished"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSessionOpen.getFeedbackSessionName() + "]");

        ______TS("download results");
        homePage.downloadResults(courseIndex, sessionIndex);
        List<String> expectedContent = Arrays.asList("Course,tm.e2e.IHome.CS2104",
                "Session Name,First Feedback Session", "Question 1,Rate 5 other students' products");
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("soft delete session");
        sessionIndex = 1;
        copiedSession.setDeletedTime(Instant.now());
        homePage.deleteSession(otherCourseIndex, sessionIndex);

        homePage.verifyStatusMessage("The feedback session has been deleted. "
                + "You can restore it from the 'Sessions' tab.");
        homePage.sortCoursesByCreationDate();
        courseIndex = 1;
        otherCourseIndex = 0;
        FeedbackSessionAttributes[] otherCourseSessionsWithCopy2 = { copiedSession2, otherCourseSession };
        homePage.verifyCourseTabDetails(otherCourseIndex, otherCourse, otherCourseSessionsWithCopy2);
        assertNotNull(getSoftDeletedSession(copiedSession.getFeedbackSessionName(),
                instructor.getGoogleId()));

        ______TS("archive course");
        homePage.archiveCourse(courseIndex);

        homePage.verifyStatusMessage("The course " + course.getId() + " has been archived. "
                + "You can retrieve it from the Courses page.");
        homePage.verifyNumCourses(1);
        verifyCourseArchivedInDatabase(instructor.getGoogleId(), course);

        ______TS("delete course");
        otherCourseIndex = 0;
        homePage.deleteCourse(otherCourseIndex);

        homePage.verifyStatusMessage("The course " + otherCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        homePage.verifyNumCourses(0);
        assertTrue(BACKDOOR.isCourseInRecycleBin(otherCourse.getId()));
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

    private void verifyCourseArchivedInDatabase(String instructorId, CourseAttributes course) {
        int retryLimit = 5;
        CourseAttributes actual = getArchivedCourse(instructorId, course.getId());
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getArchivedCourse(instructorId, course.getId());
        }
        assertEquals(actual, course);
    }
}
