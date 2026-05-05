package teammates.e2e.cases;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCoursesPage;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageE2ETest extends BaseE2ETestCase {
    private Course[] courses = new Course[4];
    private Course newCourse;
    private Course copyCourse;
    private Course copyCourse2;
    private FeedbackSession copySession;
    private FeedbackSession copySession2;

    @Override
    protected void prepareTestData() {
        deleteCourseIfExists("tm.e2e.ICs.CS4100");
        deleteCourseIfExists("tm.e2e.ICs.CS5000");
        deleteCourseIfExists("tm.e2e.ICs.CS6000");
        testData = loadDataBundle("/InstructorCoursesPageE2ETest.json");
        testData = removeAndRestoreDataBundle(testData);

        courses[0] = testData.courses.get("ICs.CS1101");
        courses[1] = testData.courses.get("ICs.CS1231");
        courses[2] = testData.courses.get("ICs.CS2104");
        courses[3] = testData.courses.get("ICs.CS2105");
        FeedbackSession session = testData.feedbackSessions.get("ICs.session");
        Instructor instructor = testData.instructors.get("ICs.instructor.CS1231");

        newCourse = new Course(
                "tm.e2e.ICs.CS4100",
                "New Course",
                "Asia/Singapore",
                "TEAMMATES Test Institute 1"
        );
        newCourse.setCreatedAt(Instant.now());

        copyCourse = new Course(
                "tm.e2e.ICs.CS5000",
                "Copy Course",
                "Asia/Singapore",
                "TEAMMATES Test Institute 1"
        );
        copyCourse.setCreatedAt(Instant.now());

        copyCourse2 = new Course(
                "tm.e2e.ICs.CS6000",
                "Copy Course 2",
                "Asia/Singapore",
                "TEAMMATES Test Institute 1"
        );
        copyCourse2.setCreatedAt(Instant.now());

        copySession = new FeedbackSession(
                "Second Session",
                instructor.getEmail(),
                session.getInstructions(),
                ZonedDateTime.now(ZoneId.of(copyCourse.getTimeZone()))
                    .plus(Duration.ofDays(2))
                    .truncatedTo(ChronoUnit.HOURS)
                    .toInstant(),
                ZonedDateTime.now(ZoneId.of(copyCourse.getTimeZone()))
                    .plus(Duration.ofDays(7))
                    .truncatedTo(ChronoUnit.HOURS)
                    .toInstant(),
                ZonedDateTime.now(ZoneId.of(copyCourse.getTimeZone()))
                    .minus(Duration.ofDays(28))
                    .truncatedTo(ChronoUnit.HOURS)
                    .toInstant(),
                Const.TIME_REPRESENTS_LATER,
                session.getGracePeriod(),
                session.isClosingSoonEmailEnabled(),
                session.isPublishedEmailEnabled()
        );
        copySession.setId(null);
        copyCourse.addFeedbackSession(copySession);

        copySession2 = new FeedbackSession(
                "Second Session",
                instructor.getEmail(),
                copySession.getInstructions(),
                copySession.getStartTime(),
                copySession.getEndTime(),
                copySession.getSessionVisibleFromTime(),
                copySession.getResultsVisibleFromTime(),
                copySession.getGracePeriod(),
                copySession.isClosingSoonEmailEnabled(),
                copySession.isPublishedEmailEnabled()
        );
        copySession2.setId(null);
        copyCourse2.addFeedbackSession(copySession2);
    }

    @Test
    @Override
    public void testAll() {
        String instructorId = testData.accounts.get("ICs.instructor").getGoogleId();
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE);
        InstructorCoursesPage coursesPage = loginToPage(url, InstructorCoursesPage.class, instructorId);

        ______TS("verify loaded data");
        Course[] activeCourses = { courses[0], courses[1], courses[2] };
        Course[] deletedCourses = { courses[3] };

        coursesPage.verifyActiveCoursesDetails(activeCourses);
        coursesPage.verifyDeletedCoursesDetails(deletedCourses);

        ______TS("verify statistics");
        verifyActiveCourseStatistics(coursesPage, courses[0]);

        ______TS("verify cannot modify without permissions");
        coursesPage.verifyNotModifiable(courses[0].getId());

        ______TS("add new course");
        Course[] activeCoursesWithNewCourse = { courses[0], courses[1], courses[2], newCourse };
        coursesPage.addCourse(newCourse);

        coursesPage.verifyStatusMessage("The course has been added.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
        verifyPresentInDatabase(newCourse);

        ______TS("copy course with session of modified timings");
        Course[] activeCoursesWithCopyCourse = { courses[0], courses[1], courses[2], newCourse, copyCourse };
        coursesPage.copyCourse(courses[1].getId(), copyCourse);

        coursesPage.waitForConfirmationModalAndClickOk();
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithCopyCourse);
        verifyPresentInDatabase(copyCourse);
        verifyPresentInDatabase(copySession);

        ______TS("copy course with session of same timings");
        Course[] activeCoursesWithCopyCourse2 = { courses[0], courses[1], courses[2], newCourse, copyCourse, copyCourse2 };
        coursesPage.copyCourse(copyCourse.getId(), copyCourse2);
        coursesPage.verifyStatusMessage("The course has been added.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithCopyCourse2);
        verifyPresentInDatabase(copyCourse2);
        verifyPresentInDatabase(copySession2);

        ______TS("move active course to recycle bin");
        newCourse.setDeletedAt(Instant.now());
        Course[] deletedCoursesWithNewCourse = { newCourse, courses[3] };
        coursesPage.moveCourseToRecycleBin(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        coursesPage.verifyNumActiveCourses(5);
        coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
        assertTrue(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        ______TS("restore active course");
        newCourse.setDeletedAt(null);
        Course[] activeCoursesWithNewCourseSortedByCreationDate =
                { copyCourse2, copyCourse, newCourse, courses[0], courses[1], courses[2] };
        coursesPage.restoreCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been restored.");
        coursesPage.waitForPageToLoad();
        coursesPage.verifyNumDeletedCourses(1);
        // No need to call sortByCreationDate() here because it is the default sort in DESC order
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourseSortedByCreationDate);
        assertFalse(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        ______TS("permanently delete course");
        coursesPage.moveCourseToRecycleBin(newCourse.getId());
        newCourse.setDeletedAt(Instant.now());
        coursesPage.deleteCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId()
                + " has been permanently deleted.");
        coursesPage.verifyNumDeletedCourses(1);
        verifyAbsentInDatabase(newCourse);

        ______TS("restore all");
        Course[] activeCoursesWithRestored = { courses[0], courses[1], courses[2], courses[3], copyCourse, copyCourse2 };
        coursesPage.restoreAllCourses();

        coursesPage.verifyStatusMessage("All courses have been restored.");
        coursesPage.waitForPageToLoad();
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithRestored);
        coursesPage.verifyNumDeletedCourses(0);
        assertFalse(BACKDOOR.isCourseInRecycleBin(courses[3].getId()));

        ______TS("permanently delete all");
        coursesPage.moveCourseToRecycleBin(courses[1].getId());
        coursesPage.moveCourseToRecycleBin(courses[2].getId());
        coursesPage.deleteAllCourses();

        coursesPage.verifyStatusMessage("All courses have been permanently deleted.");
        coursesPage.verifyNumActiveCourses(4);
        coursesPage.verifyNumDeletedCourses(0);
        verifyAbsentInDatabase(courses[1]);
        verifyAbsentInDatabase(courses[2]);
    }

    private void verifyActiveCourseStatistics(InstructorCoursesPage coursesPage, Course course) {
        int numSections = 0;
        int numTeams = 0;
        int numStudents = 0;
        int numUnregistered = 0;
        Set<String> sections = new HashSet<>();
        Set<String> teams = new HashSet<>();

        for (Student student : testData.students.values()) {
            if (!student.getCourseId().equals(course.getId())) {
                continue;
            }
            if (!sections.contains(student.getSectionName())) {
                sections.add(student.getSectionName());
                numSections++;
            }
            if (!teams.contains(student.getTeamName())) {
                teams.add(student.getTeamName());
                numTeams++;
            }
            if (student.getGoogleId() == null || student.getGoogleId().isEmpty()) {
                numUnregistered++;
            }
            numStudents++;
        }
        coursesPage.verifyActiveCourseStatistics(course, Integer.toString(numSections), Integer.toString(numTeams),
                Integer.toString(numStudents), Integer.toString(numUnregistered));
    }
}
