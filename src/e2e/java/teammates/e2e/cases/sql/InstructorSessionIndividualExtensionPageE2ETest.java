package teammates.e2e.cases.sql;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionIndividualExtensionPageSql;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.DeadlineExtensionData;
import teammates.ui.output.FeedbackSessionData;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE}.
 */
public class InstructorSessionIndividualExtensionPageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private Course course;
    private FeedbackSession feedbackSession;
    private Collection<Student> students;
    private Collection<Instructor> instructors;
    private String testEmail;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorSessionIndividualExtensionPageE2ETestSql.json"));

        testEmail = TestProperties.TEST_EMAIL;
        Student alice = testData.students.get("alice.tmms@ISesIe.CS2104");
        alice.setEmail(testEmail);

        instructor = testData.instructors.get("ISesIe.instructor1");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("firstSession");
        students = testData.students.values();
        instructors = testData.instructors.values();
    }

    @Test
    @Override
    public void testAll() {
        InstructorSessionIndividualExtensionPageSql individualExtensionPage =
                loginToInstructorSessionIndividualExtensionPage();

        individualExtensionPage.waitForPageToLoad(true);

        ______TS("verify data loaded correctly");

        individualExtensionPage.verifyDeadlineDetails(feedbackSession, students, instructors);

        ______TS("verify extend some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 2); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByTwelveHours(true);

        FeedbackSessionData updatedSessionData =
                getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getName());
        Map<String, Long> updatedStudentDeadlines = updatedSessionData.getStudentDeadlines();
        Map<String, Long> updatedInstructorDeadlines = updatedSessionData.getInstructorDeadlines();
        Instant expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofHours(12));

        verifyUpdatedDeadlinesMap(updatedStudentDeadlines, testEmail, "charlie.tmms@gmail.tmt");
        verifyUpdatedDeadlinesMap(updatedInstructorDeadlines, "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        String expectedSubject = "TEAMMATES: Deadline extension given [Course: "
                + course.getName() + "][Feedback Session: "
                + feedbackSession.getName() + "]";
        verifyEmailSent(testEmail, expectedSubject);

        ______TS("verify updated some deadlines, notifyUsers enabled");

        expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofDays(1));

        // table sorted by students with extension by default
        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByOneDay(true);

        updatedSessionData = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getName());
        updatedStudentDeadlines = updatedSessionData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSessionData.getInstructorDeadlines();

        verifyUpdatedDeadlinesMap(updatedStudentDeadlines, testEmail, "charlie.tmms@gmail.tmt");
        verifyUpdatedDeadlinesMap(updatedInstructorDeadlines, "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        expectedSubject = "TEAMMATES: Deadline extension updated [Course: "
                + course.getName() + "][Feedback Session: "
                + feedbackSession.getName() + "]";
        verifyEmailSent(testEmail, expectedSubject);

        ______TS("verify delete some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.deleteDeadlines(true);

        updatedSessionData = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getName());
        updatedStudentDeadlines = updatedSessionData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSessionData.getInstructorDeadlines();

        assertTrue(updatedStudentDeadlines.isEmpty());
        assertTrue(updatedInstructorDeadlines.isEmpty());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        expectedSubject = "TEAMMATES: Deadline extension revoked [Course: "
                + course.getName() + "][Feedback Session: "
                + feedbackSession.getName() + "]";
        verifyEmailSent(testEmail, expectedSubject);

        ______TS("verify extend all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.extendDeadlineToOneDayAway(feedbackSession, false);

        updatedSessionData = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getName());
        updatedStudentDeadlines = updatedSessionData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSessionData.getInstructorDeadlines();

        assertEquals(5, updatedStudentDeadlines.size());
        assertEquals(2, updatedInstructorDeadlines.size());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        ______TS("verify delete all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.deleteDeadlines(false);

        updatedSessionData = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getName());
        updatedStudentDeadlines = updatedSessionData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSessionData.getInstructorDeadlines();

        assertTrue(updatedStudentDeadlines.isEmpty());
        assertTrue(updatedInstructorDeadlines.isEmpty());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);
    }

    private void verifyUpdatedDeadlinesMap(Map<String, Long> updatedDeadlines, String... emails) {
        assertEquals(emails.length, updatedDeadlines.size());
        for (String email : emails) {
            assertTrue(updatedDeadlines.containsKey(email));
        }
    }

    private void verifyDeadlineExtensionsPresentOrAbsent(Map<String, Long> updatedStudentDeadlines,
                                                         Map<String, Long> updatedInstructorDeadlines,
                                                         Instant extendedDeadline) {
        for (var student : students) {
            String email = student.getEmail();
            if (updatedStudentDeadlines.containsKey(email)) {
                DeadlineExtensionData extension = getDeadlineExtension(
                        course.getId(), feedbackSession.getName(), email, false);
                assertNotNull(extension);
                assertEquals(extendedDeadline, Instant.ofEpochMilli(extension.getEndTime()));
            } else {
                DeadlineExtensionData extension = getDeadlineExtension(
                        course.getId(), feedbackSession.getName(), email, false);
                assertNull(extension);
            }
        }

        for (var instructor : instructors) {
            String email = instructor.getEmail();
            if (updatedInstructorDeadlines.containsKey(email)) {
                DeadlineExtensionData extension = getDeadlineExtension(
                        course.getId(), feedbackSession.getName(), email, true);
                assertNotNull(extension);
                assertEquals(extendedDeadline, Instant.ofEpochMilli(extension.getEndTime()));
            } else {
                DeadlineExtensionData extension = getDeadlineExtension(
                        course.getId(), feedbackSession.getName(), email, true);
                assertNull(extension);
            }
        }
    }

    private InstructorSessionIndividualExtensionPageSql loginToInstructorSessionIndividualExtensionPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getName());

        return loginToPage(url, InstructorSessionIndividualExtensionPageSql.class, instructor.getGoogleId());
    }
}
