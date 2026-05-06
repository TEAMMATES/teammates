package teammates.e2e.cases;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorSessionIndividualExtensionPage;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.output.DeadlineExtensionsData;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_INDIVIDUAL_EXTENSION_PAGE}.
 */
public class InstructorSessionIndividualExtensionPageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private Course course;
    private FeedbackSession feedbackSession;
    private Map<String, User> users;
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
        users = new HashMap<>();

        for (Student s : students) {
            users.put(s.getEmail(), s);
        }
        for (Instructor i : instructors) {
            users.put(i.getEmail(), i);
        }
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
        Map<UUID, Long> updatedDeadlines = updatedExtensionsData.getUserDeadlines();
        Instant expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofHours(12));

        verifyUpdatedDeadlinesMap(updatedDeadlines, "alice.tmms@gmail.tmt",
                "charlie.tmms@gmail.tmt", "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsUpdated(updatedDeadlines, expectedDeadline);

        ______TS("verify updated some deadlines, notifyUsers enabled");

        expectedDeadline = feedbackSession.getEndTime().plus(Duration.ofDays(1));

        // table sorted by students with extension by default
        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.extendDeadlineByOneDay(true);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedDeadlines = updatedExtensionsData.getUserDeadlines();

        verifyUpdatedDeadlinesMap(updatedDeadlines, "alice.tmms@gmail.tmt",
                "charlie.tmms@gmail.tmt", "instructor1.tmms@gmail.tmt");
        verifyDeadlineExtensionsUpdated(updatedDeadlines, expectedDeadline);

        ______TS("verify delete some deadlines, notifyUsers enabled");

        individualExtensionPage.selectStudents(0, 1); // alice and charlie
        individualExtensionPage.selectInstructor(0); // instructor 1

        individualExtensionPage.deleteDeadlines(true);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedDeadlines = updatedExtensionsData.getUserDeadlines();

        assertTrue(updatedDeadlines.isEmpty());

        ______TS("verify extend all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.extendDeadlineToOneDayAway(feedbackSession, false);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedDeadlines = updatedExtensionsData.getUserDeadlines();

        // 5 students and 2 instructors
        assertEquals(7, updatedDeadlines.size());
        verifyDeadlineExtensionsUpdated(updatedDeadlines, expectedDeadline);

        ______TS("verify delete all deadlines, notifyUsers disabled");

        individualExtensionPage.selectAllStudents();
        individualExtensionPage.selectAllInstructors();

        individualExtensionPage.deleteDeadlines(false);

        updatedExtensionsData =
                getDeadlineExtensions(feedbackSession.getId());
        updatedDeadlines = updatedExtensionsData.getUserDeadlines();

        assertTrue(updatedDeadlines.isEmpty());
    }

    private void verifyUpdatedDeadlinesMap(Map<UUID, Long> updatedDeadlines, String... emails) {
        assertEquals(emails.length, updatedDeadlines.size());
        for (String email : emails) {
            User user = users.get(email);
            assertTrue(updatedDeadlines.containsKey(user.getId()));
        }
    }

    private void verifyDeadlineExtensionsUpdated(Map<UUID, Long> updatedDeadlines, Instant extendedDeadline) {
        for (Map.Entry<UUID, Long> entry : updatedDeadlines.entrySet()) {
            assertEquals(extendedDeadline, Instant.ofEpochMilli(entry.getValue()));
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
