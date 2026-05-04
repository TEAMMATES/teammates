package teammates.e2e.cases;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionIndividualExtensionPage;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.DeadlineExtensionsData;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE}.
 */
public class InstructorSessionIndividualExtensionPageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private Course course;
    private FeedbackSession feedbackSession;
    private Collection<Student> students;
    private Collection<Instructor> instructors;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/InstructorSessionIndividualExtensionPageE2ETest.json"));

        instructor = testData.instructors.get("ISesIe.instructor1");
        course = testData.courses.get("course");
        feedbackSession = testData.feedbackSessions.get("firstSession");
        students = testData.students.values();
        instructors = testData.instructors.values();
    }

    @Test
    @Override
    public void testAll() {
        InstructorSessionIndividualExtensionPage individualExtensionPage =
                loginToInstructorSessionIndividualExtensionPage();

        individualExtensionPage.waitForPageToLoad(true);

        ______TS("verify data loaded correctly");

        individualExtensionPage.verifyDeadlineDetails(feedbackSession, students, instructors);

        ______TS("verify extend some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 2); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByTwelveHours(true);

        DeadlineExtensionsData updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        Map<String, Long> updatedStudentDeadlines = updatedExtensionsData.getStudentDeadlines();
        Map<String, Long> updatedInstructorDeadlines = updatedExtensionsData.getInstructorDeadlines();
        Instant expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofHours(12));

        verifyUpdatedDeadlinesMap(updatedStudentDeadlines, "alice.tmms@gmail.tmt", "charlie.tmms@gmail.tmt");
        verifyUpdatedDeadlinesMap(updatedInstructorDeadlines, "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        ______TS("verify updated some deadlines, notifyUsers enabled");

        expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofDays(1));

        // table sorted by students with extension by default
        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByOneDay(true);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedStudentDeadlines = updatedExtensionsData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedExtensionsData.getInstructorDeadlines();

        verifyUpdatedDeadlinesMap(updatedStudentDeadlines, "alice.tmms@gmail.tmt", "charlie.tmms@gmail.tmt");
        verifyUpdatedDeadlinesMap(updatedInstructorDeadlines, "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        ______TS("verify delete some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.deleteDeadlines(true);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedStudentDeadlines = updatedExtensionsData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedExtensionsData.getInstructorDeadlines();

        assertTrue(updatedStudentDeadlines.isEmpty());
        assertTrue(updatedInstructorDeadlines.isEmpty());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        ______TS("verify extend all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.extendDeadlineToOneDayAway(feedbackSession, false);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedStudentDeadlines = updatedExtensionsData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedExtensionsData.getInstructorDeadlines();

        assertEquals(5, updatedStudentDeadlines.size());
        assertEquals(2, updatedInstructorDeadlines.size());

        verifyDeadlineExtensionsPresentOrAbsent(updatedStudentDeadlines, updatedInstructorDeadlines, expectedDeadline);

        ______TS("verify delete all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.deleteDeadlines(false);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedStudentDeadlines = updatedExtensionsData.getStudentDeadlines();
        updatedInstructorDeadlines = updatedExtensionsData.getInstructorDeadlines();

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
        for (var s : students) {
            String email = s.getEmail();
            if (updatedStudentDeadlines.containsKey(email)) {
                assertTrue(updatedStudentDeadlines.containsKey(email));
                assertEquals(extendedDeadline, Instant.ofEpochMilli(updatedStudentDeadlines.get(email)));
            } else {
                assertFalse(updatedStudentDeadlines.containsKey(email));
            }
        }

        for (var i : instructors) {
            String email = i.getEmail();
            if (updatedInstructorDeadlines.containsKey(email)) {
                assertTrue(updatedInstructorDeadlines.containsKey(email));
                assertEquals(extendedDeadline, Instant.ofEpochMilli(updatedInstructorDeadlines.get(email)));
            } else {
                assertFalse(updatedInstructorDeadlines.containsKey(email));
            }
        }
    }

    private InstructorSessionIndividualExtensionPage loginToInstructorSessionIndividualExtensionPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE)
                .withCourseId(course.getId())
                .withFeedbackSessionId(feedbackSession.getId().toString())
                .withSessionName(feedbackSession.getName());

        return loginToPage(url, InstructorSessionIndividualExtensionPage.class, instructor.getGoogleId());
    }
}
