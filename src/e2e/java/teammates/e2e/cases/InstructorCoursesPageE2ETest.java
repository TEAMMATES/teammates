package teammates.e2e.cases;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCoursesPage;
import teammates.test.ThreadHelper;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageE2ETest extends BaseE2ETestCase {
    private CourseAttributes[] courses = new CourseAttributes[4];
    private CourseAttributes newCourse;
    private CourseAttributes copyCourse;
    private FeedbackSessionAttributes copySession;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCoursesPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        courses[0] = testData.courses.get("CS1101");
        courses[1] = testData.courses.get("CS2104");
        courses[2] = testData.courses.get("CS2105");
        courses[3] = testData.courses.get("CS1231");
        FeedbackSessionAttributes session = testData.feedbackSessions.get("session");
        InstructorAttributes instructor = testData.instructors.get("instructorCS1231");

        newCourse = CourseAttributes.builder("tm.e2e.ICs.CS4100")
                .withName("New Course")
                .withTimezone("Asia/Singapore")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        copyCourse = CourseAttributes.builder("tm.e2e.ICs.CS5000")
                .withName("Copy Course")
                .withTimezone("Asia/Singapore")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        copySession = FeedbackSessionAttributes
                .builder("Second Session", copyCourse.getId())
                .withCreatorEmail(instructor.getEmail())
                .withStartTime(session.getStartTime())
                .withEndTime(session.getEndTime())
                .withSessionVisibleFromTime(session.getSessionVisibleFromTime())
                .withResultsVisibleFromTime(session.getResultsVisibleFromTime())
                .withGracePeriod(Duration.ofMinutes(session.getGracePeriodMinutes()))
                .withInstructions(session.getInstructions())
                .withTimeZone(copyCourse.getTimeZone())
                .withIsClosingEmailEnabled(session.isClosingEmailEnabled())
                .withIsPublishedEmailEnabled(session.isPublishedEmailEnabled())
                .build();
    }

    @BeforeClass
    public void classSetup() {
        BACKDOOR.deleteCourse(newCourse.getId());
        BACKDOOR.deleteCourse(copyCourse.getId());
    }

    @Test
    @Override
    public void testAll() {
        String instructorId = testData.accounts.get("instructor").getGoogleId();
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE);
        InstructorCoursesPage coursesPage = loginToPage(url, InstructorCoursesPage.class, instructorId);

        ______TS("verify loaded data");
        CourseAttributes[] activeCourses = { courses[0], courses[3] };
        CourseAttributes[] deletedCourses = { courses[2] };

        coursesPage.verifyActiveCoursesDetails(activeCourses);
        coursesPage.verifyDeletedCoursesDetails(deletedCourses);

        ______TS("verify statistics");
        verifyActiveCourseStatistics(coursesPage, courses[0]);

        ______TS("verify cannot modify without permissions");
        coursesPage.verifyNotModifiable(courses[0].getId());

        ______TS("add new course");
        CourseAttributes[] activeCoursesWithNewCourse = { courses[0], courses[3], newCourse };
        coursesPage.addCourse(newCourse);

        coursesPage.verifyStatusMessage("The course has been added.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
        verifyPresentInDatabase(newCourse);

        ______TS("copy course");
        CourseAttributes[] activeCoursesWithCopyCourse = { courses[0], courses[3], newCourse, copyCourse };
        coursesPage.copyCourse(courses[3].getId(), copyCourse);

        coursesPage.verifyStatusMessage("The course has been added.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithCopyCourse);
        verifyPresentInDatabase(copyCourse);
        verifyPresentInDatabase(copySession);

        ______TS("move active course to recycle bin");
        newCourse.setDeletedAt(Instant.now());
        CourseAttributes[] deletedCoursesWithNewCourse = { newCourse, courses[2] };
        coursesPage.moveCourseToRecycleBin(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        coursesPage.verifyNumActiveCourses(3);
        coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
        assertTrue(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        ______TS("restore active course");
        newCourse.setDeletedAt(null);
        CourseAttributes[] activeCoursesWithNewCourseSortedByCreationDate =
                { copyCourse, newCourse, courses[0], courses[3] };
        coursesPage.restoreCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been restored.");
        coursesPage.waitForPageToLoad();
        coursesPage.verifyNumDeletedCourses(1);
        // No need to call sortByCreationDate() here because it is the default sort in DESC order
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourseSortedByCreationDate);
        assertFalse(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        coursesPage.verifyStatusMessage("The course " + newCourse.getId()
                + " has been permanently deleted.");
        coursesPage.verifyNumDeletedCourses(1);
        verifyAbsentInDatabase(newCourse);

        ______TS("permanently delete all");
        coursesPage.moveCourseToRecycleBin(courses[2].getId());
        coursesPage.deleteAllCourses();

        coursesPage.verifyStatusMessage("All courses have been permanently deleted.");
        coursesPage.verifyNumActiveCourses(3);
        coursesPage.verifyNumDeletedCourses(0);
        verifyAbsentInDatabase(courses[1]);
        verifyAbsentInDatabase(courses[2]);
    }

    private void verifyActiveCourseStatistics(InstructorCoursesPage coursesPage, CourseAttributes course) {
        int numSections = 0;
        int numTeams = 0;
        int numStudents = 0;
        int numUnregistered = 0;
        Set<String> sections = new HashSet<>();
        Set<String> teams = new HashSet<>();

        for (StudentAttributes student : testData.students.values()) {
            if (!student.getCourse().equals(course.getId())) {
                continue;
            }
            if (!sections.contains(student.getSection())) {
                sections.add(student.getSection());
                numSections++;
            }
            if (!teams.contains(student.getTeam())) {
                teams.add(student.getTeam());
                numTeams++;
            }
            if (student.getGoogleId().isEmpty()) {
                numUnregistered++;
            }
            numStudents++;
        }
        coursesPage.verifyActiveCourseStatistics(course, Integer.toString(numSections), Integer.toString(numTeams),
                Integer.toString(numStudents), Integer.toString(numUnregistered));
    }
}
