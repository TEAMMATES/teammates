package teammates.e2e.cases.e2e;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorCoursesPage;
import teammates.e2e.pageobjects.InstructorHomePage;
import teammates.e2e.util.BackDoor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageE2ETest extends BaseE2ETestCase {
    private CourseAttributes[] courses = new CourseAttributes[3];
    private CourseAttributes newCourse;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCoursesPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        courses[0] = testData.courses.get("CS1101");
        courses[1] = testData.courses.get("CS2104");
        courses[2] = testData.courses.get("CS2105");

        newCourse = CourseAttributes.builder("ICAddE2ETest.CS4100")
                .withName("New Course")
                .withTimezone(ZoneId.of("Asia/Singapore"))
                .build();
    }

    @BeforeClass
    public void classSetup() {
        BackDoor.deleteCourse(newCourse.getId());
    }

    @Test
    public void testAll() {
        String instructorId = testData.accounts.get("instructor").getGoogleId();
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE)
                .withUserId(instructorId);
        loginAdminToPage(url, InstructorHomePage.class);
        InstructorCoursesPage coursesPage = AppPage.getNewPageInstance(browser, url, InstructorCoursesPage.class);
        coursesPage.waitForPageToLoad();

        ______TS("verify loaded data");
        CourseAttributes[] activeCourses = { courses[0] };
        CourseAttributes[] archivedCourses = { courses[1] };
        CourseAttributes[] deletedCourses = { courses[2] };

        coursesPage.verifyActiveCoursesDetails(activeCourses);
        coursesPage.verifyArchivedCoursesDetails(archivedCourses);
        coursesPage.verifyDeletedCoursesDetails(deletedCourses);

        ______TS("verify statistics");
        verifyActiveCourseStatistics(coursesPage, courses[0]);

        ______TS("verify cannot modify without permissions");
        coursesPage.verifyNotModifiable(courses[0].getId());

        ______TS("add new course");
        CourseAttributes[] activeCoursesWithNewCourse = { newCourse, courses[0] };
        String[] expectedLinks = { Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE + "?courseid=" + newCourse.getId(),
                Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE + "?courseid=" + newCourse.getId() };
        coursesPage.addCourse(newCourse);

        coursesPage.verifyStatusMessageWithLinks("The course has been added. "
                + "Click here to add students to the course or click here to add other instructors.\n"
                + "If you don't see the course in the list below, please refresh the page after a few moments.",
                expectedLinks);
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
        verifyPresentInDatastore(newCourse);

        ______TS("archive course");
        CourseAttributes[] archivedCoursesWithNewCourse = { newCourse, courses[1] };
        coursesPage.archiveCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been archived. "
                + "It will not appear on the home page anymore.");
        coursesPage.verifyNumActiveCourses(1);
        coursesPage.verifyArchivedCoursesDetails(archivedCoursesWithNewCourse);
        verifyCourseArchivedInDatastore(instructorId, newCourse);

        ______TS("unarchive course");
        coursesPage.unarchiveCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course has been unarchived.");
        coursesPage.verifyNumArchivedCourses(1);
        coursesPage.sortByCourseName();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
        verifyCourseNotArchivedInDatastore(instructorId, newCourse);

        ______TS("move active course to recycle bin");
        newCourse.deletedAt = Instant.now();
        CourseAttributes[] deletedCoursesWithNewCourse = { newCourse, courses[2] };
        coursesPage.moveCourseToRecycleBin(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        coursesPage.verifyNumActiveCourses(1);
        coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
        assertTrue(isCourseInRecycleBin(newCourse.getId()));

        ______TS("restore active course");
        newCourse.deletedAt = null;
        coursesPage.restoreCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been restored.");
        coursesPage.verifyNumDeletedCourses(1);
        coursesPage.sortByCreationDate();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
        assertFalse(isCourseInRecycleBin(newCourse.getId()));

        ______TS("move archived course to recycle bin");
        coursesPage.archiveCourse(newCourse.getId());
        newCourse.deletedAt = Instant.now();
        coursesPage.moveArchivedCourseToRecycleBin(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        coursesPage.verifyNumArchivedCourses(1);
        coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
        assertTrue(isCourseInRecycleBin(newCourse.getId()));

        ______TS("restore archived course");
        newCourse.deletedAt = null;
        coursesPage.restoreCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been restored.");
        coursesPage.verifyNumDeletedCourses(1);
        coursesPage.verifyArchivedCoursesDetails(archivedCoursesWithNewCourse);
        assertFalse(isCourseInRecycleBin(newCourse.getId()));
        verifyCourseArchivedInDatastore(instructorId, newCourse);

        ______TS("permanently delete course");
        coursesPage.moveArchivedCourseToRecycleBin(newCourse.getId());
        coursesPage.deleteCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId()
                + " has been permanently deleted.");
        coursesPage.verifyNumDeletedCourses(1);
        verifyAbsentInDatastore(newCourse);

        ______TS("restore all");
        coursesPage.moveArchivedCourseToRecycleBin(courses[1].getId());
        CourseAttributes[] activeCoursesWithRestored = { courses[2], courses[0] };
        coursesPage.restoreAllCourses();

        coursesPage.verifyStatusMessage("All courses have been restored.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithRestored);
        coursesPage.verifyArchivedCoursesDetails(archivedCourses);
        coursesPage.verifyNumDeletedCourses(0);
        assertFalse(isCourseInRecycleBin(courses[1].getId()));
        assertFalse(isCourseInRecycleBin(courses[2].getId()));

        ______TS("permanently delete all");
        coursesPage.moveArchivedCourseToRecycleBin(courses[1].getId());
        coursesPage.moveCourseToRecycleBin(courses[2].getId());
        coursesPage.deleteAllCourses();

        coursesPage.verifyStatusMessage("All courses have been permanently deleted.");
        coursesPage.verifyNumActiveCourses(1);
        coursesPage.verifyNumArchivedCourses(0);
        coursesPage.verifyNumDeletedCourses(0);
        verifyAbsentInDatastore(courses[1]);
        verifyAbsentInDatastore(courses[2]);
    }

    private void verifyActiveCourseStatistics(InstructorCoursesPage coursesPage, CourseAttributes course) {
        int numSections = 0;
        int numTeams = 0;
        int numStudents = 0;
        int numUnregistered = 0;
        Set<String> sections = new HashSet<>();
        Set<String> teams = new HashSet<>();

        for (StudentAttributes student : testData.students.values()) {
            if (!student.course.equals(course.getId())) {
                continue;
            }
            if (!sections.contains(student.section)) {
                sections.add(student.section);
                numSections++;
            }
            if (!teams.contains(student.team)) {
                teams.add(student.team);
                numTeams++;
            }
            if (student.googleId.isEmpty()) {
                numUnregistered++;
            }
            numStudents++;
        }
        coursesPage.verifyActiveCourseStatistics(course, Integer.toString(numSections), Integer.toString(numTeams),
                Integer.toString(numStudents), Integer.toString(numUnregistered));
    }

    private void verifyCourseArchivedInDatastore(String instructorId, CourseAttributes course) {
        int retryLimit = 5;
        CourseAttributes actual = getArchivedCourse(instructorId, course.getId());
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getArchivedCourse(instructorId, course.getId());
        }
        assertEquals(actual, course);
    }

    private void verifyCourseNotArchivedInDatastore(String instructorId, CourseAttributes course) {
        int retryLimit = 5;
        CourseAttributes actual = getArchivedCourse(instructorId, course.getId());
        while (actual != null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getArchivedCourse(instructorId, course.getId());
        }
        assertNull(actual);
    }
}
