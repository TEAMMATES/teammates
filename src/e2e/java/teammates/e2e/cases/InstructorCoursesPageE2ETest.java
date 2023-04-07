package teammates.e2e.cases;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    private CourseAttributes copyCourse2;
    private FeedbackSessionAttributes copySession;
    private FeedbackSessionAttributes copySession2;

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

        copyCourse2 = CourseAttributes.builder("tm.e2e.ICs.CS6000")
                .withName("Copy Course 2")
                .withTimezone("Asia/Singapore")
                .withInstitute("TEAMMATES Test Institute 1")
                .build();

        copySession = FeedbackSessionAttributes
                .builder("Second Session", copyCourse.getId())
                .withCreatorEmail(instructor.getEmail())
                .withStartTime(ZonedDateTime.now(ZoneId.of(copyCourse.getTimeZone())).plus(Duration.ofDays(2))
                        .truncatedTo(ChronoUnit.HOURS).toInstant())
                .withEndTime(ZonedDateTime.now(ZoneId.of(copyCourse.getTimeZone())).plus(Duration.ofDays(7))
                        .truncatedTo(ChronoUnit.HOURS).toInstant())
                .withSessionVisibleFromTime(ZonedDateTime.now(ZoneId.of(copyCourse.getTimeZone())).minus(Duration.ofDays(28))
                        .truncatedTo(ChronoUnit.HOURS).toInstant())
                .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                .withGracePeriod(Duration.ofMinutes(session.getGracePeriodMinutes()))
                .withInstructions(session.getInstructions())
                .withTimeZone(copyCourse.getTimeZone())
                .withIsClosingEmailEnabled(session.isClosingEmailEnabled())
                .withIsPublishedEmailEnabled(session.isPublishedEmailEnabled())
                .build();

        copySession2 = FeedbackSessionAttributes
                .builder("Second Session", copyCourse2.getId())
                .withCreatorEmail(instructor.getEmail())
                .withStartTime(copySession.getStartTime())
                .withEndTime(copySession.getEndTime())
                .withSessionVisibleFromTime(copySession.getSessionVisibleFromTime())
                .withResultsVisibleFromTime(copySession.getResultsVisibleFromTime())
                .withGracePeriod(Duration.ofMinutes(copySession.getGracePeriodMinutes()))
                .withInstructions(copySession.getInstructions())
                .withTimeZone(copyCourse2.getTimeZone())
                .withIsClosingEmailEnabled(copySession.isClosingEmailEnabled())
                .withIsPublishedEmailEnabled(copySession.isPublishedEmailEnabled())
                .build();
    }

    @BeforeClass
    public void classSetup() {
        BACKDOOR.deleteCourse(newCourse.getId());
        BACKDOOR.deleteCourse(copyCourse.getId());
        BACKDOOR.deleteCourse(copyCourse2.getId());
    }

    @Test
    @Override
    public void testAll() {
        String instructorId = testData.accounts.get("instructor").getGoogleId();
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE);
        InstructorCoursesPage coursesPage = loginToPage(url, InstructorCoursesPage.class, instructorId);

        ______TS("verify loaded data");
        CourseAttributes[] activeCourses = { courses[0], courses[3] };
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
        CourseAttributes[] activeCoursesWithNewCourse = { courses[0], courses[3], newCourse };
        coursesPage.addCourse(newCourse);

        coursesPage.verifyStatusMessage("The course has been added.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
        verifyPresentInDatabase(newCourse);

        ______TS("copy course with session of modified timings");
        CourseAttributes[] activeCoursesWithCopyCourse = { courses[0], courses[3], newCourse, copyCourse };
        coursesPage.copyCourse(courses[3].getId(), copyCourse);

        coursesPage.waitForConfirmationModalAndClickOk();
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithCopyCourse);
        verifyPresentInDatabase(copyCourse);
        verifyPresentInDatabase(copySession);

        ______TS("copy course with session of same timings");
        CourseAttributes[] activeCoursesWithCopyCourse2 = { courses[0], courses[3], newCourse, copyCourse, copyCourse2 };
        coursesPage.copyCourse(copyCourse.getId(), copyCourse2);
        coursesPage.verifyStatusMessage("The course has been added.");
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithCopyCourse2);
        verifyPresentInDatabase(copyCourse2);
        verifyPresentInDatabase(copySession2);

        ______TS("archive course");
        CourseAttributes[] archivedCoursesWithNewCourse = { newCourse, courses[1] };
        coursesPage.archiveCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been archived. "
                + "It will not appear on the home page anymore.");
        coursesPage.verifyNumActiveCourses(4);
        coursesPage.verifyArchivedCoursesDetails(archivedCoursesWithNewCourse);
        verifyCourseArchivedInDatabase(instructorId, newCourse);

        ______TS("unarchive course");
        CourseAttributes[] activeCoursesWithNewCourseSortedByName = { copyCourse, copyCourse2, courses[3], newCourse,
                courses[0] };
        coursesPage.unarchiveCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course has been unarchived.");
        coursesPage.verifyNumArchivedCourses(1);
        coursesPage.sortByCourseName();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourseSortedByName);
        verifyCourseNotArchivedInDatabase(instructorId, newCourse);

        ______TS("move active course to recycle bin");
        newCourse.setDeletedAt(Instant.now());
        CourseAttributes[] deletedCoursesWithNewCourse = { newCourse, courses[2] };
        coursesPage.moveCourseToRecycleBin(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        coursesPage.verifyNumActiveCourses(4);
        coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
        assertTrue(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        ______TS("restore active course");
        newCourse.setDeletedAt(null);
        CourseAttributes[] activeCoursesWithNewCourseSortedByCreationDate =
                { copyCourse2, copyCourse, newCourse, courses[0], courses[3] };
        coursesPage.restoreCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been restored.");
        coursesPage.waitForPageToLoad();
        coursesPage.verifyNumDeletedCourses(1);
        // No need to call sortByCreationDate() here because it is the default sort in DESC order
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourseSortedByCreationDate);
        assertFalse(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        ______TS("move archived course to recycle bin");
        coursesPage.archiveCourse(newCourse.getId());
        newCourse.setDeletedAt(Instant.now());
        coursesPage.moveArchivedCourseToRecycleBin(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        coursesPage.verifyNumArchivedCourses(1);
        coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
        assertTrue(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));

        ______TS("restore archived course");
        newCourse.setDeletedAt(null);
        coursesPage.restoreCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId() + " has been restored.");
        coursesPage.waitForPageToLoad();
        coursesPage.verifyNumDeletedCourses(1);
        coursesPage.verifyArchivedCoursesDetails(archivedCoursesWithNewCourse);
        assertFalse(BACKDOOR.isCourseInRecycleBin(newCourse.getId()));
        verifyCourseArchivedInDatabase(instructorId, newCourse);

        ______TS("permanently delete course");
        coursesPage.moveArchivedCourseToRecycleBin(newCourse.getId());
        coursesPage.deleteCourse(newCourse.getId());

        coursesPage.verifyStatusMessage("The course " + newCourse.getId()
                + " has been permanently deleted.");
        coursesPage.verifyNumDeletedCourses(1);
        verifyAbsentInDatabase(newCourse);

        ______TS("restore all");
        coursesPage.moveArchivedCourseToRecycleBin(courses[1].getId());
        CourseAttributes[] activeCoursesWithRestored = { courses[0], courses[3], courses[2], copyCourse, copyCourse2 };
        coursesPage.restoreAllCourses();

        coursesPage.verifyStatusMessage("All courses have been restored.");
        coursesPage.waitForPageToLoad();
        coursesPage.sortByCourseId();
        coursesPage.verifyActiveCoursesDetails(activeCoursesWithRestored);
        coursesPage.verifyArchivedCoursesDetails(archivedCourses);
        coursesPage.verifyNumDeletedCourses(0);
        assertFalse(BACKDOOR.isCourseInRecycleBin(courses[1].getId()));
        assertFalse(BACKDOOR.isCourseInRecycleBin(courses[2].getId()));

        ______TS("permanently delete all");
        coursesPage.moveArchivedCourseToRecycleBin(courses[1].getId());
        coursesPage.moveCourseToRecycleBin(courses[2].getId());
        coursesPage.deleteAllCourses();

        coursesPage.verifyStatusMessage("All courses have been permanently deleted.");
        coursesPage.verifyNumActiveCourses(4);
        coursesPage.verifyNumArchivedCourses(0);
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

    private void verifyCourseNotArchivedInDatabase(String instructorId, CourseAttributes course) {
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
