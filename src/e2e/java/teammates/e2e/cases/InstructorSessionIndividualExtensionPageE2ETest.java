package teammates.e2e.cases;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionIndividualExtensionPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE}.
 */
public class InstructorSessionIndividualExtensionPageE2ETest extends BaseE2ETestCase {
    private InstructorAttributes instructor;
    private CourseAttributes course;
    private FeedbackSessionAttributes feedbackSession;
    private Collection<StudentAttributes> students;
    private Collection<InstructorAttributes> instructors;
    private String testEmail;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSessionIndividualExtensionPageE2ETest.json");
        testEmail = TestProperties.TEST_EMAIL;
        testData.students.get("alice.tmms@ISesIe.CS2104").setEmail(testEmail);
        instructor = testData.instructors.get("ISesIe.instructor1");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("firstSession");
        students = testData.students.values();
        instructors = testData.instructors.values();

        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorSessionIndividualExtensionPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        InstructorSessionIndividualExtensionPage individualExtensionPage = loginToInstructorSessionIndividualExtensionPage();

        individualExtensionPage.waitForPageToLoad(true);

        ______TS("verify data loaded correctly");

        individualExtensionPage.verifyDeadlineDetails(feedbackSession, students, instructors);

        ______TS("verify extend some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 2); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByTwelveHours(true);

        FeedbackSessionAttributes updatedSession =
                getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName());
        Map<String, Instant> updatedStudentDeadlines = updatedSession.getStudentDeadlines();
        Map<String, Instant> updatedInstructorDeadlines = updatedSession.getInstructorDeadlines();
        Instant expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofHours(12));

        verifyUpdatedDeadlinesMap(updatedStudentDeadlines, TestProperties.TEST_EMAIL, "charlie.tmms@gmail.tmt");
        verifyUpdatedDeadlinesMap(updatedInstructorDeadlines, "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        String expectedSubject = "TEAMMATES: Deadline extension given [Course: "
                + course.getName() + "][Feedback Session: "
                + feedbackSession.getFeedbackSessionName() + "]";
        verifyEmailSent(testEmail, expectedSubject);

        ______TS("verify updated some deadlines, notifyUsers enabled");

        expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofDays(1));

        // table sorted by students with extension by default
        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByOneDay(true);

        updatedSession = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName());
        updatedStudentDeadlines = updatedSession.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSession.getInstructorDeadlines();

        verifyUpdatedDeadlinesMap(updatedStudentDeadlines, TestProperties.TEST_EMAIL, "charlie.tmms@gmail.tmt");
        verifyUpdatedDeadlinesMap(updatedInstructorDeadlines, "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        expectedSubject = "TEAMMATES: Deadline extension updated [Course: "
                + course.getName() + "][Feedback Session: "
                + feedbackSession.getFeedbackSessionName() + "]";
        verifyEmailSent(testEmail, expectedSubject);

        ______TS("verify delete some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.deleteDeadlines(true);

        updatedSession = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName());
        updatedStudentDeadlines = updatedSession.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSession.getInstructorDeadlines();

        assertTrue(updatedStudentDeadlines.isEmpty());
        assertTrue(updatedInstructorDeadlines.isEmpty());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        expectedSubject = "TEAMMATES: Deadline extension revoked [Course: "
                + course.getName() + "][Feedback Session: "
                + feedbackSession.getFeedbackSessionName() + "]";
        verifyEmailSent(testEmail, expectedSubject);

        ______TS("verify extend all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.extendDeadlineToOneDayAway(feedbackSession, false);

        updatedSession = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName());
        updatedStudentDeadlines = updatedSession.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSession.getInstructorDeadlines();

        assertEquals(5, updatedStudentDeadlines.size());
        assertEquals(2, updatedInstructorDeadlines.size());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        ______TS("verify delete all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.deleteDeadlines(false);

        updatedSession = getFeedbackSession(feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName());
        updatedStudentDeadlines = updatedSession.getStudentDeadlines();
        updatedInstructorDeadlines = updatedSession.getInstructorDeadlines();

        assertTrue(updatedStudentDeadlines.isEmpty());
        assertTrue(updatedInstructorDeadlines.isEmpty());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);
    }

    private void verifyUpdatedDeadlinesMap(Map<String, Instant> updatedDeadlines, String... emails) {
        assertEquals(emails.length, updatedDeadlines.size());
        for (String email : emails) {
            assertTrue(updatedDeadlines.containsKey(email));
        }
    }

    private void verifyDeadlineExtensionsPresentOrAbsent(Map<String, Instant> updatedStudentDeadlines,
            Map<String, Instant> updatedInstructorDeadlines, Instant extendedDeadline) {
        for (var student : students) {
            String email = student.getEmail();
            var extension = DeadlineExtensionAttributes
                    .builder(course.getId(), feedbackSession.getFeedbackSessionName(), email, false)
                    .build();
            if (updatedStudentDeadlines.containsKey(email)) {
                extension = getDeadlineExtension(extension);
                assertEquals(updatedStudentDeadlines.get(email), extension.getEndTime());
                assertEquals(extendedDeadline, extension.getEndTime());
            } else {
                verifyAbsentInDatabase(extension);
            }
        }

        for (var instructor : instructors) {
            String email = instructor.getEmail();
            var extension = DeadlineExtensionAttributes
                    .builder(course.getId(), feedbackSession.getFeedbackSessionName(), email, true)
                    .build();
            if (updatedInstructorDeadlines.containsKey(email)) {
                extension = getDeadlineExtension(extension);
                assertEquals(updatedInstructorDeadlines.get(email), extension.getEndTime());
                assertEquals(extendedDeadline, extension.getEndTime());
            } else {
                verifyAbsentInDatabase(extension);
            }
        }
    }

    private InstructorSessionIndividualExtensionPage loginToInstructorSessionIndividualExtensionPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());

        return loginToPage(url, InstructorSessionIndividualExtensionPage.class, instructor.getGoogleId());
    }
}
