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
import teammates.e2e.pageobjects.InstructorCoursesPage;
import teammates.e2e.util.BackDoor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageE2ETest extends BaseE2ETestCase {
	private CourseAttributes[] courses = new CourseAttributes[3];
	private CourseAttributes newCourse;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.INSTRUCTOR_COURSES_PAGE_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		courses[0] = testData.courses.get(Const.TestCase.CS1101);
		courses[1] = testData.courses.get(Const.TestCase.CS2104);
		courses[2] = testData.courses.get(Const.TestCase.CS2105);

		newCourse = CourseAttributes.builder(Const.TestCase.IC_ADD_E2E_TEST_CS4100).withName(Const.TestCase.NEW_COURSE)
				.withTimezone(ZoneId.of(Const.TestCase.ASIA_SINGAPORE)).build();
	}

	@BeforeClass
	public void classSetup() {
		BackDoor.deleteCourse(newCourse.getId());
	}

	@Test
	public void testAll() {
		String instructorId = testData.accounts.get(Const.TestCase.INSTRUCTOR_CONTENT).getGoogleId();
		AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE).withUserId(instructorId);
		InstructorCoursesPage coursesPage = loginAdminToPage(url, InstructorCoursesPage.class);

		______TS(Const.TestCase.VERIFY_LOADED_DATA);
		CourseAttributes[] activeCourses = { courses[0] };
		CourseAttributes[] archivedCourses = { courses[1] };
		CourseAttributes[] deletedCourses = { courses[2] };

		coursesPage.verifyActiveCoursesDetails(activeCourses);
		coursesPage.verifyArchivedCoursesDetails(archivedCourses);
		coursesPage.verifyDeletedCoursesDetails(deletedCourses);

		______TS(Const.TestCase.VERIFY_STATISTICS);
		verifyActiveCourseStatistics(coursesPage, courses[0]);

		______TS(Const.TestCase.VERIFY_CANNOT_MODIFY_WITHOUT_PERMISSIONS);
		coursesPage.verifyNotModifiable(courses[0].getId());

		______TS(Const.TestCase.ADD_NEW_COURSE);
		CourseAttributes[] activeCoursesWithNewCourse = { courses[0], newCourse };
		coursesPage.addCourse(newCourse);

		coursesPage.verifyStatusMessage(Const.TestCase.THE_COURSE_HAS_BEEN_ADDED);
		coursesPage.sortByCourseId();
		coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourse);
		verifyPresentInDatastore(newCourse);

		______TS(Const.TestCase.ARCHIVE_COURSE);
		CourseAttributes[] archivedCoursesWithNewCourse = { newCourse, courses[1] };
		coursesPage.archiveCourse(newCourse.getId());

		coursesPage.verifyStatusMessage(Const.TestCase.THE_COURSE + newCourse.getId() + Const.TestCase.HAS_BEEN_ARCHIVED
				+ Const.TestCase.IT_WILL_NOT_APPEAR_ON_THE_HOME_PAGE_ANYMORE);
		coursesPage.verifyNumActiveCourses(1);
		coursesPage.verifyArchivedCoursesDetails(archivedCoursesWithNewCourse);
		verifyCourseArchivedInDatastore(instructorId, newCourse);

		______TS(Const.TestCase.UNARCHIVE_COURSE);
		CourseAttributes[] activeCoursesWithNewCourseSortedByName = { newCourse, courses[0] };
		coursesPage.unarchiveCourse(newCourse.getId());

		coursesPage.verifyStatusMessage(Const.TestCase.THE_COURSE_HAS_BEEN_UNARCHIVED);
		coursesPage.verifyNumArchivedCourses(1);
		coursesPage.sortByCourseName();
		coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourseSortedByName);
		verifyCourseNotArchivedInDatastore(instructorId, newCourse);

		______TS(Const.TestCase.MOVE_ACTIVE_COURSE_TO_RECYCLE_BIN);
		newCourse.deletedAt = Instant.now();
		CourseAttributes[] deletedCoursesWithNewCourse = { newCourse, courses[2] };
		coursesPage.moveCourseToRecycleBin(newCourse.getId());

		coursesPage.verifyStatusMessage(Const.TestCase.THE_COURSE + newCourse.getId() + Const.TestCase.HAS_BEEN_DELETED
				+ Const.TestCase.YOU_CAN_RESTORE_IT_FROM_THE_RECYCLE_BIN_MANUALLY);
		coursesPage.verifyNumActiveCourses(1);
		coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
		assertTrue(isCourseInRecycleBin(newCourse.getId()));

		______TS(Const.TestCase.RESTORE_ACTIVE_COURSE);
		newCourse.deletedAt = null;
		CourseAttributes[] activeCoursesWithNewCourseSortedByCreationDate = { newCourse, courses[0] };
		coursesPage.restoreCourse(newCourse.getId());

		coursesPage
				.verifyStatusMessage(Const.TestCase.THE_COURSE + newCourse.getId() + Const.TestCase.HAS_BEEN_RESTORED);
		coursesPage.verifyNumDeletedCourses(1);
		// No need to call sortByCreationDate() here because it is the default sort in
		// DESC order
		coursesPage.verifyActiveCoursesDetails(activeCoursesWithNewCourseSortedByCreationDate);
		assertFalse(isCourseInRecycleBin(newCourse.getId()));

		______TS(Const.TestCase.MOVE_ARCHIVED_COURSE_TO_RECYCLE_BIN);
		coursesPage.archiveCourse(newCourse.getId());
		newCourse.deletedAt = Instant.now();
		coursesPage.moveArchivedCourseToRecycleBin(newCourse.getId());

		coursesPage.verifyStatusMessage(Const.TestCase.THE_COURSE + newCourse.getId() + Const.TestCase.HAS_BEEN_DELETED
				+ Const.TestCase.YOU_CAN_RESTORE_IT_FROM_THE_RECYCLE_BIN_MANUALLY);
		coursesPage.verifyNumArchivedCourses(1);
		coursesPage.verifyDeletedCoursesDetails(deletedCoursesWithNewCourse);
		assertTrue(isCourseInRecycleBin(newCourse.getId()));

		______TS(Const.TestCase.RESTORE_ARCHIVED_COURSE);
		newCourse.deletedAt = null;
		coursesPage.restoreCourse(newCourse.getId());

		coursesPage
				.verifyStatusMessage(Const.TestCase.THE_COURSE + newCourse.getId() + Const.TestCase.HAS_BEEN_RESTORED);
		coursesPage.verifyNumDeletedCourses(1);
		coursesPage.verifyArchivedCoursesDetails(archivedCoursesWithNewCourse);
		assertFalse(isCourseInRecycleBin(newCourse.getId()));
		verifyCourseArchivedInDatastore(instructorId, newCourse);

		______TS(Const.TestCase.PERMANENTLY_DELETE_COURSE);
		coursesPage.moveArchivedCourseToRecycleBin(newCourse.getId());
		coursesPage.deleteCourse(newCourse.getId());

		coursesPage.verifyStatusMessage(
				Const.TestCase.THE_COURSE + newCourse.getId() + Const.TestCase.HAS_BEEN_PERMANENTLY_DELETED);
		coursesPage.verifyNumDeletedCourses(1);
		verifyAbsentInDatastore(newCourse);

		______TS(Const.TestCase.RESTORE_ALL);
		coursesPage.moveArchivedCourseToRecycleBin(courses[1].getId());
		CourseAttributes[] activeCoursesWithRestored = { courses[0], courses[2] };
		coursesPage.restoreAllCourses();

		coursesPage.verifyStatusMessage(Const.TestCase.ALL_COURSES_HAVE_BEEN_RESTORED);
		coursesPage.sortByCourseId();
		coursesPage.verifyActiveCoursesDetails(activeCoursesWithRestored);
		coursesPage.verifyArchivedCoursesDetails(archivedCourses);
		coursesPage.verifyNumDeletedCourses(0);
		assertFalse(isCourseInRecycleBin(courses[1].getId()));
		assertFalse(isCourseInRecycleBin(courses[2].getId()));

		______TS(Const.TestCase.PERMANENTLY_DELETE_ALL);
		coursesPage.moveArchivedCourseToRecycleBin(courses[1].getId());
		coursesPage.moveCourseToRecycleBin(courses[2].getId());
		coursesPage.deleteAllCourses();

		coursesPage.verifyStatusMessage(Const.TestCase.ALL_COURSES_HAVE_BEEN_PERMANENTLY_DELETED);
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
