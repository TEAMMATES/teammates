package teammates.test.cases.logic;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link CoursesLogic}.
 */
public class CoursesLogicTest extends BaseLogicTest {

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final CoursesDb coursesDb = new CoursesDb();
    private static final AccountsDb accountsDb = new AccountsDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();

    @Test
    public void testAll() throws Exception {
        testGetCourse();
        testGetCoursesForInstructor();
        testIsSampleCourse();
        testIsCoursePresent();
        testVerifyCourseIsPresent();
        testGetCourseSummary();
        testGetCourseSummaryWithoutStats();
        testGetCourseDetails();
        testGetTeamsForCourse();
        testGetCoursesForStudentAccount();
        testGetCourseDetailsListForStudent();
        testGetCourseSummariesForInstructor();
        testGetCoursesSummaryWithoutStatsForInstructor();
        testGetCourseStudentListAsCsv();
        testHasIndicatedSections();
        testCreateCourse();
        testCreateCourseAndInstructor();
        testDeleteCourse();
    }

    private void testGetCourse() throws Exception {

        ______TS("failure: course doesn't exist");

        assertNull(coursesLogic.getCourse("nonexistant-course"));

        ______TS("success: typical case");

        CourseAttributes c = CourseAttributes
                .builder("Computing101-getthis", "Basic Computing Getting", "UTC")
                .build();
        coursesDb.createEntity(c);

        assertEquals(c.getId(), coursesLogic.getCourse(c.getId()).getId());
        assertEquals(c.getName(), coursesLogic.getCourse(c.getId()).getName());

        coursesDb.deleteEntity(c);
        ______TS("Null parameter");

        try {
            coursesLogic.getCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCoursesForInstructor() throws Exception {

        ______TS("success: instructor with present courses");

        String instructorId = dataBundle.accounts.get("instructor3").googleId;

        List<CourseAttributes> courses = coursesLogic.getCoursesForInstructor(instructorId);

        assertEquals(2, courses.size());

        ______TS("omit archived courses");

        InstructorsLogic.inst().setArchiveStatusOfInstructor(instructorId, courses.get(0).getId(), true);
        courses = coursesLogic.getCoursesForInstructor(instructorId, true);
        assertEquals(1, courses.size());
        InstructorsLogic.inst().setArchiveStatusOfInstructor(instructorId, courses.get(0).getId(), false);

        ______TS("boundary: instructor without any courses");

        instructorId = dataBundle.accounts.get("instructorWithoutCourses").googleId;

        courses = coursesLogic.getCoursesForInstructor(instructorId);

        assertEquals(0, courses.size());

        ______TS("Null parameter");

        try {
            coursesLogic.getCoursesForInstructor((String) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }

        try {
            coursesLogic.getCoursesForInstructor((List<InstructorAttributes>) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null", e.getMessage());
        }
    }

    private void testIsSampleCourse() {

        ______TS("typical case: not a sample course");

        CourseAttributes notSampleCourse = CourseAttributes
                .builder("course.id", "not sample course", "UTC")
                .build();

        assertFalse(coursesLogic.isSampleCourse(notSampleCourse.getId()));

        ______TS("typical case: is a sample course");

        CourseAttributes sampleCourse = CourseAttributes
                .builder("course.id-demo3", "sample course", "UTC")
                .build();
        assertTrue(coursesLogic.isSampleCourse(sampleCourse.getId()));

        ______TS("typical case: is a sample course with '-demo' in the middle of its id");

        CourseAttributes sampleCourse2 = CourseAttributes
                .builder("course.id-demo3-demo33", "sample course with additional -demo", "UTC")
                .build();
        assertTrue(coursesLogic.isSampleCourse(sampleCourse2.getId()));

        ______TS("Null parameter");

        try {
            coursesLogic.isSampleCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Course ID is null", e.getMessage());
        }
    }

    private void testIsCoursePresent() {

        ______TS("typical case: not an existent course");

        CourseAttributes nonExistentCourse = CourseAttributes
                .builder("non-existent-course", "non existent course", "UTC")
                .build();

        assertFalse(coursesLogic.isCoursePresent(nonExistentCourse.getId()));

        ______TS("typical case: an existent course");

        CourseAttributes existingCourse = CourseAttributes
                .builder("idOfTypicalCourse1", "existing course", "UTC")
                .build();

        assertTrue(coursesLogic.isCoursePresent(existingCourse.getId()));

        ______TS("Null parameter");

        try {
            coursesLogic.isCoursePresent(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testVerifyCourseIsPresent() throws Exception {

        ______TS("typical case: verify a non-existent course");

        CourseAttributes nonExistentCourse = CourseAttributes
                .builder("non-existent-course", "non existent course", "UTC")
                .build();

        try {
            coursesLogic.verifyCourseIsPresent(nonExistentCourse.getId());
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Course does not exist: ", e.getMessage());
        }

        ______TS("typical case: verify an existent course");

        CourseAttributes existingCourse = CourseAttributes
                .builder("idOfTypicalCourse1", "existing course", "UTC")
                .build();
        coursesLogic.verifyCourseIsPresent(existingCourse.getId());

        ______TS("Null parameter");

        try {
            coursesLogic.verifyCourseIsPresent(null);
            signalFailureToDetectException();
        } catch (AssertionError | EntityDoesNotExistException e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCourseSummary() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseDetailsBundle courseSummary = coursesLogic.getCourseSummary(course.getId());
        assertEquals(course.getId(), courseSummary.course.getId());
        assertEquals(course.getName(), courseSummary.course.getName());

        assertEquals(2, courseSummary.stats.teamsTotal);
        assertEquals(5, courseSummary.stats.studentsTotal);
        assertEquals(0, courseSummary.stats.unregisteredTotal);

        assertEquals(1, courseSummary.sections.get(0).teams.size());
        assertEquals("Team 1.1</td></div>'\"", courseSummary.sections.get(0).teams.get(0).name);

        ______TS("course without students");

        StudentProfileAttributes spa = StudentProfileAttributes.builder().build();
        spa.googleId = "instructor1";
        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true,
                "instructor@email.tmt", "TEAMMATES Test Institute 1", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1", "Asia/Singapore");
        courseSummary = coursesLogic.getCourseSummary("course1");
        assertEquals("course1", courseSummary.course.getId());
        assertEquals("course 1", courseSummary.course.getName());
        assertEquals("Asia/Singapore", courseSummary.course.getTimeZone());

        assertEquals(0, courseSummary.stats.teamsTotal);
        assertEquals(0, courseSummary.stats.studentsTotal);
        assertEquals(0, courseSummary.stats.unregisteredTotal);

        assertEquals(0, courseSummary.sections.size());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummary("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }

        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummary((CourseAttributes) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }

        try {
            coursesLogic.getCourseSummary((String) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCourseSummaryWithoutStats() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseSummaryBundle courseSummary = coursesLogic.getCourseSummaryWithoutStats(course.getId());
        assertEquals(course.getId(), courseSummary.course.getId());
        assertEquals(course.getName(), courseSummary.course.getName());

        ______TS("course without students");

        StudentProfileAttributes spa = StudentProfileAttributes.builder().build();
        spa.googleId = "instructor1";

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true,
                "instructor@email.tmt", "TEAMMATES Test Institute 1", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1", "America/Los_Angeles");
        courseSummary = coursesLogic.getCourseSummaryWithoutStats("course1");
        assertEquals("course1", courseSummary.course.getId());
        assertEquals("course 1", courseSummary.course.getName());
        assertEquals("America/Los_Angeles", courseSummary.course.getTimeZone());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummaryWithoutStats("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }

        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummaryWithoutStats((CourseAttributes) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }

        try {
            coursesLogic.getCourseSummaryWithoutStats((String) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCourseDetails() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseDetailsBundle courseDetails = coursesLogic.getCourseSummary(course.getId());

        assertEquals(course.getId(), courseDetails.course.getId());
        assertEquals(course.getName(), courseDetails.course.getName());
        assertEquals(course.getTimeZone(), courseDetails.course.getTimeZone());

        assertEquals(2, courseDetails.stats.teamsTotal);
        assertEquals(5, courseDetails.stats.studentsTotal);
        assertEquals(0, courseDetails.stats.unregisteredTotal);

        assertEquals(1, courseDetails.sections.get(0).teams.size());
        assertEquals("Team 1.1</td></div>'\"", courseDetails.sections.get(0).teams.get(0).name);

        ______TS("course without students");

        StudentProfileAttributes spa = StudentProfileAttributes.builder().build();
        spa.googleId = "instructor1";

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true,
                "instructor@email.tmt", "TEAMMATES Test Institute 1", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1", "Australia/Adelaide");
        courseDetails = coursesLogic.getCourseSummary("course1");
        assertEquals("course1", courseDetails.course.getId());
        assertEquals("course 1", courseDetails.course.getName());
        assertEquals("Australia/Adelaide", courseDetails.course.getTimeZone());

        assertEquals(0, courseDetails.stats.teamsTotal);
        assertEquals(0, courseDetails.stats.studentsTotal);
        assertEquals(0, courseDetails.stats.unregisteredTotal);

        assertEquals(0, courseDetails.sections.size());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummary("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }

        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummary((String) null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetTeamsForCourse() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        List<TeamDetailsBundle> teams = coursesLogic.getTeamsForCourse(course.getId());

        assertEquals(2, teams.size());
        assertEquals("Team 1.1</td></div>'\"", teams.get(0).name);
        assertEquals("Team 1.2", teams.get(1).name);

        ______TS("course without students");

        StudentProfileAttributes spa = StudentProfileAttributes.builder().build();
        spa.googleId = "instructor1";

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true,
                "instructor@email.tmt", "TEAMMATES Test Institute 1", spa));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1", "UTC");
        teams = coursesLogic.getTeamsForCourse("course1");

        assertEquals(0, teams.size());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        try {
            coursesLogic.getTeamsForCourse("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }

        ______TS("null parameter");

        try {
            coursesLogic.getTeamsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCoursesForStudentAccount() throws Exception {

        ______TS("student having two courses");

        StudentAttributes studentInTwoCourses = dataBundle.students
                .get("student2InCourse1");
        List<CourseAttributes> courseList = coursesLogic
                .getCoursesForStudentAccount(studentInTwoCourses.googleId);
        CourseAttributes.sortById(courseList);
        assertEquals(2, courseList.size());

        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");

        CourseAttributes course2 = dataBundle.courses.get("typicalCourse2");

        List<CourseAttributes> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);
        CourseAttributes.sortById(courses);

        assertEquals(courses.get(0).getId(), courseList.get(0).getId());
        assertEquals(courses.get(0).getName(), courseList.get(0).getName());

        assertEquals(courses.get(1).getId(), courseList.get(1).getId());
        assertEquals(courses.get(1).getName(), courseList.get(1).getName());

        ______TS("student having one course");

        StudentAttributes studentInOneCourse = dataBundle.students
                .get("student1InCourse1");
        courseList = coursesLogic.getCoursesForStudentAccount(studentInOneCourse.googleId);
        assertEquals(1, courseList.size());
        course1 = dataBundle.courses.get("typicalCourse1");
        assertEquals(course1.getId(), courseList.get(0).getId());
        assertEquals(course1.getName(), courseList.get(0).getName());

        // Student having zero courses is not applicable

        ______TS("non-existent student");

        try {
            coursesLogic.getCoursesForStudentAccount("non-existent-student");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("null parameter");

        try {
            coursesLogic.getCoursesForStudentAccount(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCourseDetailsListForStudent() throws Exception {

        ______TS("student having multiple evaluations in multiple courses");

        CourseAttributes expectedCourse1 = dataBundle.courses.get("typicalCourse1");

        // This student is in both course 1 and 2
        StudentAttributes studentInBothCourses = dataBundle.students
                .get("student2InCourse1");

        // Get course details for student
        List<CourseDetailsBundle> courseList = coursesLogic
                .getCourseDetailsListForStudent(studentInBothCourses.googleId);

        // Verify number of courses received
        assertEquals(2, courseList.size());

        CourseDetailsBundle actualCourse1 = courseList.get(0);
        assertEquals(expectedCourse1.getId(), actualCourse1.course.getId());
        assertEquals(expectedCourse1.getName(), actualCourse1.course.getName());

        // student with no courses is not applicable
        ______TS("non-existent student");

        try {
            coursesLogic.getCourseDetailsListForStudent("non-existent-student");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("null parameter");

        try {
            coursesLogic.getCourseDetailsListForStudent(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCourseSummariesForInstructor() throws Exception {

        ______TS("Instructor with 2 courses");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        Map<String, CourseDetailsBundle> courseList =
                coursesLogic.getCourseSummariesForInstructor(instructor.googleId, false);
        assertEquals(2, courseList.size());
        for (CourseDetailsBundle cdd : courseList.values()) {
            // check if course belongs to this instructor
            assertTrue(InstructorsLogic.inst().isGoogleIdOfInstructorOfCourse(instructor.googleId, cdd.course.getId()));
        }

        ______TS("Instructor with 1 archived, 1 unarchived course");

        InstructorsLogic.inst().setArchiveStatusOfInstructor(instructor.googleId, "idOfTypicalCourse1", true);
        courseList = coursesLogic.getCourseSummariesForInstructor(instructor.googleId, true);
        assertEquals(1, courseList.size());
        InstructorsLogic.inst().setArchiveStatusOfInstructor(instructor.googleId, "idOfTypicalCourse1", false);

        ______TS("Instructor with 0 courses");
        courseList = coursesLogic.getCourseSummariesForInstructor("instructorWithoutCourses", false);
        assertEquals(0, courseList.size());

        ______TS("Non-existent instructor");

        try {
            coursesLogic.getCourseSummariesForInstructor("non-existent-instructor", false);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("Null parameter");

        try {
            coursesLogic.getCourseSummariesForInstructor(null, false);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }

    }

    private void testGetCoursesSummaryWithoutStatsForInstructor() throws Exception {

        ______TS("Typical case");

        Map<String, CourseSummaryBundle> courseListForInstructor = coursesLogic
                .getCoursesSummaryWithoutStatsForInstructor("idOfInstructor3", false);
        assertEquals(2, courseListForInstructor.size());

        ______TS("Instructor has an archived course");

        InstructorsLogic.inst().setArchiveStatusOfInstructor("idOfInstructor4", "idOfCourseNoEvals", true);
        courseListForInstructor = coursesLogic
                .getCoursesSummaryWithoutStatsForInstructor("idOfInstructor4", true);
        assertEquals(0, courseListForInstructor.size());
        InstructorsLogic.inst().setArchiveStatusOfInstructor("idOfInstructor4", "idOfCourseNoEvals", true);

        ______TS("Instructor with 0 courses");

        courseListForInstructor = coursesLogic.getCoursesSummaryWithoutStatsForInstructor("instructorWithoutCourses", false);
        assertEquals(0, courseListForInstructor.size());

        ______TS("Null parameter");

        try {
            coursesLogic.getCoursesSummaryWithoutStatsForInstructor(null, false);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testGetCourseStudentListAsCsv() throws Exception {

        ______TS("Typical case: course with section");

        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        String csvString = coursesLogic.getCourseStudentListAsCsv(courseId, instructorId);
        String[] expectedCsvString = {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course ID,\"idOfTypicalCourse1\"",
                "Course Name,\"Typical Course 1 with 2 Evals\"",
                "",
                "",
                "Section,Team,Full Name,Last Name,Status,Email",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"Joined\",\"student1InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"",
                "\"Section 1\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"",
                "\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expectedCsvString, EOL), csvString);

        ______TS("Typical case: course without sections");

        InstructorAttributes instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2");

        instructorId = instructor1OfCourse2.googleId;
        courseId = instructor1OfCourse2.courseId;

        csvString = coursesLogic.getCourseStudentListAsCsv(courseId, instructorId);
        expectedCsvString = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course ID,\"idOfTypicalCourse2\"",
                "Course Name,\"Typical Course 2 with 1 Evals\"",
                "",
                "",
                "Team,Full Name,Last Name,Status,Email",
                "\"Team 2.1\",\"student1 In Course2\",\"Course2\",\"Joined\",\"student1InCourse2@gmail.tmt\"",
                "\"Team 2.1\",\"student2 In Course2\",\"Course2\",\"Joined\",\"student2InCourse1@gmail.tmt\"",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expectedCsvString, EOL), csvString);

        ______TS("Typical case: course with unregistered student");

        InstructorAttributes instructor5 = dataBundle.instructors.get("instructor5");

        instructorId = instructor5.googleId;
        courseId = instructor5.courseId;

        csvString = coursesLogic.getCourseStudentListAsCsv(courseId, instructorId);
        expectedCsvString = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course ID,\"idOfUnregisteredCourse\"",
                "Course Name,\"Unregistered Course\"",
                "",
                "",
                "Section,Team,Full Name,Last Name,Status,Email",
                "\"Section 1\",\"Team 1\",\"student1 In unregisteredCourse\",\"unregisteredCourse\",\"Yet to join\",\"student1InUnregisteredCourse@gmail.tmt\"",
                "\"Section 2\",\"Team 2\",\"student2 In unregisteredCourse\",\"unregisteredCourse\",\"Yet to join\",\"student2InUnregisteredCourse@gmail.tmt\"",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expectedCsvString, EOL), csvString);

        ______TS("Failure case: non existent instructor");

        try {
            coursesLogic.getCourseStudentListAsCsv(courseId, "non-existent-instructor");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("Failure case: non existent course in the list of courses of the instructor");

        try {
            coursesLogic.getCourseStudentListAsCsv("non-existent-course", instructorId);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("Failure case: null parameter");

        try {
            coursesLogic.getCourseStudentListAsCsv(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testHasIndicatedSections() throws Exception {

        ______TS("Typical case: course with sections");

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        assertTrue(coursesLogic.hasIndicatedSections(typicalCourse1.getId()));

        ______TS("Typical case: course without sections");

        CourseAttributes typicalCourse2 = dataBundle.courses.get("typicalCourse2");
        assertFalse(coursesLogic.hasIndicatedSections(typicalCourse2.getId()));

        ______TS("Failure case: course does not exists");

        try {
            coursesLogic.hasIndicatedSections("non-existent-course");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }

        ______TS("Failure case: null parameter");

        try {
            coursesLogic.hasIndicatedSections(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }

    }

    private void testCreateCourse() throws Exception {

        /*Explanation:
         * The SUT (i.e. CoursesLogic::createCourse) has only 1 path. Therefore, we
         * should typically have 1 test cases here.
         */
        ______TS("typical case");

        CourseAttributes c = CourseAttributes
                .builder("Computing101-fresh", "Basic Computing", "Asia/Singapore")
                .build();
        coursesLogic.createCourse(c.getId(), c.getName(), c.getTimeZone());
        verifyPresentInDatastore(c);
        coursesLogic.deleteCourseCascade(c.getId());
        ______TS("Null parameter");

        try {
            coursesLogic.createCourse(null, c.getName(), c.getTimeZone());
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Non-null value expected", e.getMessage());
        }
    }

    private void testCreateCourseAndInstructor() throws Exception {

        /* Explanation: SUT has 5 paths. They are,
         * path 1 - exit because the account doesn't' exist.
         * path 2 - exit because the account exists but doesn't have instructor privileges.
         * path 3 - exit because course creation failed.
         * path 4 - exit because instructor creation failed.
         * path 5 - success.
         * Accordingly, we have 5 test cases.
         */

        ______TS("fails: account doesn't exist");

        CourseAttributes c = CourseAttributes
                .builder("fresh-course-tccai", "Fresh course for tccai", "America/Los Angeles")
                .build();

        @SuppressWarnings("deprecation")
        InstructorAttributes i = InstructorAttributes
                .builder("instructor-for-tccai", c.getId(), "Instructor for tccai", "ins.for.iccai@gmail.tmt")
                .build();

        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.getId(), c.getName(), c.getTimeZone());
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("for a non-existent instructor", e.getMessage());
        }
        verifyAbsentInDatastore(c);
        verifyAbsentInDatastore(i);

        ______TS("fails: account doesn't have instructor privileges");

        AccountAttributes a = new AccountAttributes();
        a.googleId = i.googleId;
        a.name = i.name;
        a.email = i.email;
        a.institute = "TEAMMATES Test Institute 5";
        a.isInstructor = false;
        a.studentProfile = StudentProfileAttributes.builder().build();
        a.studentProfile.googleId = i.googleId;
        accountsDb.createAccount(a);
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.getId(), c.getName(), c.getTimeZone());
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("doesn't have instructor privileges", e.getMessage());
        }
        verifyAbsentInDatastore(c);
        verifyAbsentInDatastore(i);

        ______TS("fails: error during course creation");

        a.isInstructor = true;
        accountsDb.updateAccount(a);

        CourseAttributes invalidCourse = CourseAttributes
                .builder("invalid id", "Fresh course for tccai", "InvalidTimeZone")
                .build();

        String expectedError =
                "\"" + invalidCourse.getId() + "\" is not acceptable to TEAMMATES as a/an course ID because"
                + " it is not in the correct format. "
                + "A course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. "
                + "It cannot be longer than 40 characters, cannot be empty and cannot contain spaces."
                + EOL
                + "\"InvalidTimeZone\" is not acceptable to TEAMMATES as a/an course time zone because it not available "
                + "as a choice. The value must be one of the values from the time zone dropdown selector.";

        try {
            coursesLogic.createCourseAndInstructor(i.googleId, invalidCourse.getId(), invalidCourse.getName(),
                                                   invalidCourse.getTimeZone());
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(expectedError, e.getMessage());
        }
        verifyAbsentInDatastore(invalidCourse);
        verifyAbsentInDatastore(i);

        ______TS("fails: error during instructor creation due to duplicate instructor");

        CourseAttributes courseWithDuplicateInstructor = CourseAttributes
                .builder("fresh-course-tccai", "Fresh course for tccai", "UTC")
                .build();
        instructorsDb.createEntity(i); //create a duplicate instructor

        try {
            coursesLogic.createCourseAndInstructor(i.googleId, courseWithDuplicateInstructor.getId(),
                                                   courseWithDuplicateInstructor.getName(),
                                                   courseWithDuplicateInstructor.getTimeZone());
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course",
                                        e.getMessage());
        }
        verifyAbsentInDatastore(courseWithDuplicateInstructor);

        ______TS("fails: error during instructor creation due to invalid parameters");

        i.email = "ins.for.iccai.gmail.tmt";

        try {
            coursesLogic.createCourseAndInstructor(i.googleId, courseWithDuplicateInstructor.getId(),
                                                   courseWithDuplicateInstructor.getName(),
                                                   courseWithDuplicateInstructor.getTimeZone());
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course",
                                        e.getMessage());
        }
        verifyAbsentInDatastore(courseWithDuplicateInstructor);

        ______TS("success: typical case");

        i.email = "ins.for.iccai@gmail.tmt";

        //remove the duplicate instructor object from the datastore.
        instructorsDb.deleteInstructor(i.courseId, i.email);

        coursesLogic.createCourseAndInstructor(i.googleId, courseWithDuplicateInstructor.getId(),
                                               courseWithDuplicateInstructor.getName(),
                                               courseWithDuplicateInstructor.getTimeZone());
        verifyPresentInDatastore(courseWithDuplicateInstructor);
        verifyPresentInDatastore(i);

        ______TS("Null parameter");

        try {
            coursesLogic.createCourseAndInstructor(null, courseWithDuplicateInstructor.getId(),
                                                   courseWithDuplicateInstructor.getName(),
                                                   courseWithDuplicateInstructor.getTimeZone());
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private void testDeleteCourse() {

        ______TS("typical case");

        CourseAttributes course1OfInstructor = dataBundle.courses.get("typicalCourse1");
        StudentAttributes studentInCourse = dataBundle.students.get("student1InCourse1");

        // Ensure there are entities in the datastore under this course
        assertFalse(StudentsLogic.inst().getStudentsForCourse(course1OfInstructor.getId()).isEmpty());

        verifyPresentInDatastore(course1OfInstructor);
        verifyPresentInDatastore(studentInCourse);
        verifyPresentInDatastore(dataBundle.instructors.get("instructor1OfCourse1"));
        verifyPresentInDatastore(dataBundle.instructors.get("instructor3OfCourse1"));
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
        verifyPresentInDatastore(dataBundle.students.get("student5InCourse1"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse1"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session2InCourse1"));
        assertEquals(course1OfInstructor.getId(), studentInCourse.course);

        coursesLogic.deleteCourseCascade(course1OfInstructor.getId());

        // Ensure the course and related entities are deleted
        verifyAbsentInDatastore(course1OfInstructor);
        verifyAbsentInDatastore(studentInCourse);
        verifyAbsentInDatastore(dataBundle.instructors.get("instructor1OfCourse1"));
        verifyAbsentInDatastore(dataBundle.instructors.get("instructor3OfCourse1"));
        verifyAbsentInDatastore(dataBundle.students.get("student1InCourse1"));
        verifyAbsentInDatastore(dataBundle.students.get("student5InCourse1"));
        verifyAbsentInDatastore(dataBundle.feedbackSessions.get("session1InCourse1"));
        verifyAbsentInDatastore(dataBundle.feedbackSessions.get("session2InCourse1"));

        ______TS("non-existent");

        // try to delete again. Should fail silently.
        coursesLogic.deleteCourseCascade(course1OfInstructor.getId());

        ______TS("null parameter");

        try {
            coursesLogic.deleteCourseCascade(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
}
