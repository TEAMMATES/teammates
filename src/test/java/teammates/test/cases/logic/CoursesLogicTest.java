package teammates.test.cases.logic;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
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

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testUpdateCourseCascade_shouldCascadeUpdateTimezoneOfFeedbackSessions() throws Exception {
        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        assertNotEquals(ZoneId.of("UTC"), typicalCourse1.getTimeZone());

        coursesLogic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(typicalCourse1.getId())
                        .withTimezone(ZoneId.of("UTC"))
                        .build());

        List<FeedbackSessionAttributes> sessionsOfCourse = logic.getFeedbackSessionsForCourse(typicalCourse1.getId());
        assertFalse(sessionsOfCourse.isEmpty());
        assertTrue(sessionsOfCourse.stream().allMatch(s -> s.getTimeZone().equals(ZoneId.of("UTC"))));
    }

    @Test
    public void testAll() throws Exception {
        testGetCourse();
        testGetCoursesForInstructor();
        testGetSoftDeletedCoursesForInstructors();
        testGetSoftDeletedCourseForInstructor();
        testIsSampleCourse();
        testIsCoursePresent();
        testVerifyCourseIsPresent();
        testGetSectionsNameForCourse();
        testGetCourseSummary();
        testGetCourseSummaryWithoutStats();
        testGetCourseDetails();
        testGetTeamsForCourse();
        testGetCoursesForStudentAccount();
        testGetCourseSummariesForInstructor();
        testGetCoursesSummaryWithoutStatsForInstructor();
        testHasIndicatedSections();
        testCreateCourse();
        testCreateCourseAndInstructor();
        testMoveCourseToRecycleBin();
        testRestoreCourseFromRecycleBin();
        testRestoreAllCoursesFromRecycleBin();
        testUpdateCourseCascade();
    }

    private void testGetCourse() throws Exception {

        ______TS("failure: course doesn't exist");

        assertNull(coursesLogic.getCourse("nonexistant-course"));

        ______TS("success: typical case");

        CourseAttributes c = CourseAttributes
                .builder("Computing101-getthis")
                .withName("Basic Computing Getting")
                .withTimezone(ZoneId.of("UTC"))
                .build();
        coursesDb.createEntity(c);

        assertEquals(c.getId(), coursesLogic.getCourse(c.getId()).getId());
        assertEquals(c.getName(), coursesLogic.getCourse(c.getId()).getName());

        coursesDb.deleteCourse(c.getId());
        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getCourse(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getCoursesForInstructor((String) null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> coursesLogic.getCoursesForInstructor((List<InstructorAttributes>) null));
        assertEquals("Supplied parameter was null", ae.getMessage());
    }

    private void testGetSoftDeletedCoursesForInstructors() {

        ______TS("success: instructors with deleted courses");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse3");

        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(instructor);

        List<CourseAttributes> courses = coursesLogic.getSoftDeletedCoursesForInstructors(instructors);

        assertEquals(1, courses.size());

        ______TS("boundary: instructor without any courses");

        instructors.remove(0);
        instructor = dataBundle.instructors.get("instructor5");
        instructors.add(instructor);

        courses = coursesLogic.getSoftDeletedCoursesForInstructors(instructors);

        assertEquals(0, courses.size());

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getSoftDeletedCoursesForInstructors(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testGetSoftDeletedCourseForInstructor() {

        ______TS("success: instructor with deleted course");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse3");

        CourseAttributes course = coursesLogic.getSoftDeletedCourseForInstructor(instructor);

        assertNotNull(course);

        ______TS("boundary: instructor without any deleted courses");

        instructor = dataBundle.instructors.get("instructor5");

        course = coursesLogic.getSoftDeletedCourseForInstructor(instructor);

        assertNull(course);

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getSoftDeletedCourseForInstructor(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testIsSampleCourse() {

        ______TS("typical case: not a sample course");

        CourseAttributes notSampleCourse = CourseAttributes
                .builder("course.id")
                .withName("not sample course")
                .withTimezone(ZoneId.of("UTC"))
                .build();

        assertFalse(coursesLogic.isSampleCourse(notSampleCourse.getId()));

        ______TS("typical case: is a sample course");

        CourseAttributes sampleCourse = CourseAttributes
                .builder("course.id-demo3")
                .withName("sample course")
                .withTimezone(ZoneId.of("UTC"))
                .build();
        assertTrue(coursesLogic.isSampleCourse(sampleCourse.getId()));

        ______TS("typical case: is a sample course with '-demo' in the middle of its id");

        CourseAttributes sampleCourse2 = CourseAttributes
                .builder("course.id-demo3-demo33")
                .withName("sample course with additional -demo")
                .withTimezone(ZoneId.of("UTC"))
                .build();
        assertTrue(coursesLogic.isSampleCourse(sampleCourse2.getId()));

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.isSampleCourse(null));
        assertEquals("Course ID is null", ae.getMessage());
    }

    private void testIsCoursePresent() {

        ______TS("typical case: not an existent course");

        CourseAttributes nonExistentCourse = CourseAttributes
                .builder("non-existent-course")
                .withName("non existent course")
                .withTimezone(ZoneId.of("UTC"))
                .build();

        assertFalse(coursesLogic.isCoursePresent(nonExistentCourse.getId()));

        ______TS("typical case: an existent course");

        CourseAttributes existingCourse = CourseAttributes
                .builder("idOfTypicalCourse1")
                .withName("existing course")
                .withTimezone(ZoneId.of("UTC"))
                .build();

        assertTrue(coursesLogic.isCoursePresent(existingCourse.getId()));

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.isCoursePresent(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testVerifyCourseIsPresent() throws Exception {

        ______TS("typical case: verify a non-existent course");

        CourseAttributes nonExistentCourse = CourseAttributes
                .builder("non-existent-course")
                .withName("non existent course")
                .withTimezone(ZoneId.of("UTC"))
                .build();

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.verifyCourseIsPresent(nonExistentCourse.getId()));
        AssertHelper.assertContains("Course does not exist: ", ednee.getMessage());

        ______TS("typical case: verify an existent course");

        CourseAttributes existingCourse = CourseAttributes
                .builder("idOfTypicalCourse1")
                .withName("existing course")
                .withTimezone(ZoneId.of("UTC"))
                .build();
        coursesLogic.verifyCourseIsPresent(existingCourse.getId());

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.verifyCourseIsPresent(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testGetSectionsNameForCourse() throws Exception {

        ______TS("Typical case: course with sections");

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        assertEquals(2, coursesLogic.getSectionsNameForCourse(typicalCourse1.getId()).size());
        assertEquals("Section 1", coursesLogic.getSectionsNameForCourse(typicalCourse1.getId()).get(0));
        assertEquals("Section 2", coursesLogic.getSectionsNameForCourse(typicalCourse1.getId()).get(1));

        ______TS("Typical case: course without sections");

        CourseAttributes typicalCourse2 = dataBundle.courses.get("typicalCourse2");
        assertTrue(coursesLogic.getSectionsNameForCourse(typicalCourse2.getId()).isEmpty());

        ______TS("Failure case: course does not exists");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getSectionsNameForCourse("non-existent-course"));
        AssertHelper.assertContains("does not exist", ednee.getMessage());

        ______TS("Failure case: null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getSectionsNameForCourse(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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

        AccountsLogic.inst().createAccount(AccountAttributes.builder("instructor1")
                .withName("Instructor 1")
                .withEmail("instructor@email.tmt")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .build());
        coursesLogic.createCourseAndInstructor("instructor1",
                CourseAttributes.builder("course1")
                        .withName("course 1")
                        .withTimezone(ZoneId.of("Asia/Singapore"))
                        .build());
        courseSummary = coursesLogic.getCourseSummary("course1");
        assertEquals("course1", courseSummary.course.getId());
        assertEquals("course 1", courseSummary.course.getName());
        assertEquals("Asia/Singapore", courseSummary.course.getTimeZone().getId());

        assertEquals(0, courseSummary.stats.teamsTotal);
        assertEquals(0, courseSummary.stats.studentsTotal);
        assertEquals(0, courseSummary.stats.unregisteredTotal);

        assertEquals(0, courseSummary.sections.size());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getCourseSummary("non-existent-course"));
        AssertHelper.assertContains("The course does not exist:", ednee.getMessage());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getCourseSummary((String) null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class, () -> coursesLogic.getCourseSummary((CourseAttributes) null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testGetCourseSummaryWithoutStats() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseSummaryBundle courseSummary = coursesLogic.getCourseSummaryWithoutStats(course.getId());
        assertEquals(course.getId(), courseSummary.course.getId());
        assertEquals(course.getName(), courseSummary.course.getName());

        ______TS("course without students");

        AccountsLogic.inst().createAccount(AccountAttributes.builder("instructor1")
                .withName("Instructor 1")
                .withEmail("instructor@email.tmt")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .build());
        coursesLogic.createCourseAndInstructor("instructor1",
                CourseAttributes.builder("course1")
                        .withName("course 1")
                        .withTimezone(ZoneId.of("America/Los_Angeles"))
                        .build());
        courseSummary = coursesLogic.getCourseSummaryWithoutStats("course1");
        assertEquals("course1", courseSummary.course.getId());
        assertEquals("course 1", courseSummary.course.getName());
        assertEquals("America/Los_Angeles", courseSummary.course.getTimeZone().getId());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getCourseSummaryWithoutStats("non-existent-course"));
        AssertHelper.assertContains("The course does not exist:", ednee.getMessage());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> coursesLogic.getCourseSummaryWithoutStats((CourseAttributes) null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class, () -> coursesLogic.getCourseSummaryWithoutStats((String) null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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

        AccountsLogic.inst().createAccount(AccountAttributes.builder("instructor1")
                .withName("Instructor 1")
                .withEmail("instructor@email.tmt")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .build());
        coursesLogic.createCourseAndInstructor("instructor1",
                CourseAttributes.builder("course1")
                        .withName("course 1")
                        .withTimezone(ZoneId.of("Australia/Adelaide"))
                        .build());
        courseDetails = coursesLogic.getCourseSummary("course1");
        assertEquals("course1", courseDetails.course.getId());
        assertEquals("course 1", courseDetails.course.getName());
        assertEquals("Australia/Adelaide", courseDetails.course.getTimeZone().getId());

        assertEquals(0, courseDetails.stats.teamsTotal);
        assertEquals(0, courseDetails.stats.studentsTotal);
        assertEquals(0, courseDetails.stats.unregisteredTotal);

        assertEquals(0, courseDetails.sections.size());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getCourseSummary("non-existent-course"));
        AssertHelper.assertContains("The course does not exist:", ednee.getMessage());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getCourseSummary((String) null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testGetTeamsForCourse() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        List<TeamDetailsBundle> teams = coursesLogic.getTeamsForCourse(course.getId());

        assertEquals(2, teams.size());
        assertEquals("Team 1.1</td></div>'\"", teams.get(0).name);
        assertEquals("Team 1.2", teams.get(1).name);

        ______TS("course without students");

        AccountsLogic.inst().createAccount(AccountAttributes.builder("instructor1")
                .withName("Instructor 1")
                .withEmail("instructor@email.tmt")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .build());
        coursesLogic.createCourseAndInstructor("instructor1",
                CourseAttributes.builder("course1")
                        .withName("course 1")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());
        teams = coursesLogic.getTeamsForCourse("course1");

        assertEquals(0, teams.size());

        coursesLogic.deleteCourseCascade("course1");
        accountsDb.deleteAccount("instructor1");

        ______TS("non-existent");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getTeamsForCourse("non-existent-course"));
        AssertHelper.assertContains("does not exist", ednee.getMessage());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getTeamsForCourse(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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

        courseList = coursesLogic.getCoursesForStudentAccount("non-existent-student");
        assertEquals(0, courseList.size());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.getCoursesForStudentAccount(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getCourseSummariesForInstructor("non-existent-instructor", false));
        AssertHelper.assertContains("does not exist", ednee.getMessage());

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> coursesLogic.getCourseSummariesForInstructor(null, false));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> coursesLogic.getCoursesSummaryWithoutStatsForInstructor(null, false));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testHasIndicatedSections() throws Exception {

        ______TS("Typical case: course with sections");

        CourseAttributes typicalCourse1 = dataBundle.courses.get("typicalCourse1");
        assertTrue(coursesLogic.hasIndicatedSections(typicalCourse1.getId()));

        ______TS("Typical case: course without sections");

        CourseAttributes typicalCourse2 = dataBundle.courses.get("typicalCourse2");
        assertFalse(coursesLogic.hasIndicatedSections(typicalCourse2.getId()));

        ______TS("Failure case: course does not exists");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.hasIndicatedSections("non-existent-course"));
        AssertHelper.assertContains("does not exist", ednee.getMessage());

        ______TS("Failure case: null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.hasIndicatedSections(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

    }

    private void testCreateCourse() throws Exception {

        ______TS("typical case");

        CourseAttributes c = CourseAttributes
                .builder("Computing101-fresh")
                .withName("Basic Computing")
                .withTimezone(ZoneId.of("Asia/Singapore"))
                .build();
        coursesLogic.createCourse(
                CourseAttributes.builder(c.getId())
                        .withName(c.getName())
                        .withTimezone(c.getTimeZone())
                        .build());
        verifyPresentInDatastore(c);
        coursesLogic.deleteCourseCascade(c.getId());

        ______TS("Null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> coursesLogic.createCourse(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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
                .builder("fresh-course-tccai")
                .withName("Fresh course for tccai")
                .withTimezone(ZoneId.of("America/Los_Angeles"))
                .build();

        InstructorAttributes i = InstructorAttributes
                .builder(c.getId(), "ins.for.iccai@gmail.tmt")
                .withGoogleId("instructor-for-tccai")
                .withName("Instructor for tccai")
                .build();

        AssertionError ae = assertThrows(AssertionError.class,
                () -> coursesLogic.createCourseAndInstructor(i.googleId,
                        CourseAttributes.builder(c.getId())
                                .withName(c.getName())
                                .withTimezone(c.getTimeZone())
                                .build()));
        AssertHelper.assertContains("for a non-existent instructor", ae.getMessage());
        verifyAbsentInDatastore(c);
        verifyAbsentInDatastore(i);

        ______TS("fails: account doesn't have instructor privileges");

        AccountAttributes a = AccountAttributes.builder(i.googleId)
                .withName(i.name)
                .withIsInstructor(false)
                .withEmail(i.email)
                .withInstitute("TEAMMATES Test Institute 5")
                .build();

        accountsDb.createEntity(a);
        ae = assertThrows(AssertionError.class,
                () -> coursesLogic.createCourseAndInstructor(i.googleId,
                        CourseAttributes.builder(c.getId())
                                .withName(c.getName())
                                .withTimezone(c.getTimeZone())
                                .build()));
        AssertHelper.assertContains("doesn't have instructor privileges", ae.getMessage());
        verifyAbsentInDatastore(c);
        verifyAbsentInDatastore(i);

        ______TS("fails: error during course creation");

        accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(a.googleId)
                        .withIsInstructor(true)
                        .build()
        );

        CourseAttributes invalidCourse = CourseAttributes
                .builder("invalid id")
                .withName("Fresh course for tccai")
                .withTimezone(ZoneId.of("UTC"))
                .build();

        String expectedError =
                "\"" + invalidCourse.getId() + "\" is not acceptable to TEAMMATES as a/an course ID because"
                + " it is not in the correct format. "
                + "A course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. "
                + "It cannot be longer than 40 characters, cannot be empty and cannot contain spaces.";

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.createCourseAndInstructor(i.googleId,
                        CourseAttributes.builder(invalidCourse.getId())
                                .withName(invalidCourse.getName())
                                .withTimezone(invalidCourse.getTimeZone())
                                .build()));
        assertEquals(expectedError, ipe.getMessage());
        verifyAbsentInDatastore(invalidCourse);
        verifyAbsentInDatastore(i);

        ______TS("fails: error during instructor creation due to duplicate instructor");

        CourseAttributes courseWithDuplicateInstructor = CourseAttributes
                .builder("fresh-course-tccai")
                .withName("Fresh course for tccai")
                .withTimezone(ZoneId.of("UTC"))
                .build();
        instructorsDb.createEntity(i); //create a duplicate instructor

        ae = assertThrows(AssertionError.class,
                () -> coursesLogic.createCourseAndInstructor(i.googleId,
                        CourseAttributes.builder(courseWithDuplicateInstructor.getId())
                                .withName(courseWithDuplicateInstructor.getName())
                                .withTimezone(courseWithDuplicateInstructor.getTimeZone())
                                .build()));
        AssertHelper.assertContains(
                "Unexpected exception while trying to create instructor for a new course",
                ae.getMessage());
        verifyAbsentInDatastore(courseWithDuplicateInstructor);

        ______TS("fails: error during instructor creation due to invalid parameters");

        i.email = "ins.for.iccai.gmail.tmt";

        ae = assertThrows(AssertionError.class,
                () -> coursesLogic.createCourseAndInstructor(i.googleId,
                        CourseAttributes.builder(courseWithDuplicateInstructor.getId())
                                .withName(courseWithDuplicateInstructor.getName())
                                .withTimezone(courseWithDuplicateInstructor.getTimeZone())
                                .build()));
        AssertHelper.assertContains(
                "Unexpected exception while trying to create instructor for a new course",
                ae.getMessage());
        verifyAbsentInDatastore(courseWithDuplicateInstructor);

        ______TS("success: typical case");

        i.email = "ins.for.iccai@gmail.tmt";

        //remove the duplicate instructor object from the datastore.
        instructorsDb.deleteInstructor(i.courseId, i.email);

        coursesLogic.createCourseAndInstructor(i.googleId,
                CourseAttributes.builder(courseWithDuplicateInstructor.getId())
                        .withName(courseWithDuplicateInstructor.getName())
                        .withTimezone(courseWithDuplicateInstructor.getTimeZone())
                        .build());
        verifyPresentInDatastore(courseWithDuplicateInstructor);
        verifyPresentInDatastore(i);

        ______TS("Null parameter");

        ae = assertThrows(AssertionError.class,
                () -> coursesLogic.createCourseAndInstructor(null,
                        CourseAttributes.builder(courseWithDuplicateInstructor.getId())
                                .withName(courseWithDuplicateInstructor.getName())
                                .withTimezone(courseWithDuplicateInstructor.getTimeZone())
                                .build()));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testMoveCourseToRecycleBin() throws InvalidParametersException, EntityDoesNotExistException {

        ______TS("typical case");

        CourseAttributes course1OfInstructor = dataBundle.courses.get("typicalCourse1");

        // Ensure there are entities in the datastore under this course
        verifyPresentInDatastore(course1OfInstructor);
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
        verifyPresentInDatastore(dataBundle.students.get("student5InCourse1"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse1"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session2InCourse1"));

        // Ensure the course is not in Recycle Bin
        assertFalse(course1OfInstructor.isCourseDeleted());

        Instant deletedAt = coursesLogic.moveCourseToRecycleBin(course1OfInstructor.getId());
        course1OfInstructor.deletedAt = deletedAt;

        // Ensure the course and related entities still exist in datastore
        verifyPresentInDatastore(course1OfInstructor);
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse1"));
        verifyPresentInDatastore(dataBundle.students.get("student5InCourse1"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse1"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session2InCourse1"));

        // Ensure the course is moved to Recycle Bin
        assertTrue(course1OfInstructor.isCourseDeleted());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.moveCourseToRecycleBin(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testRestoreCourseFromRecycleBin() throws InvalidParametersException, EntityDoesNotExistException {

        ______TS("typical case");

        CourseAttributes course3OfInstructor = dataBundle.courses.get("typicalCourse3");

        // Ensure there are entities in the datastore under this course
        verifyPresentInDatastore(course3OfInstructor);
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse3"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse3"));

        // Ensure the course is currently in Recycle Bin
        assertTrue(course3OfInstructor.isCourseDeleted());

        coursesLogic.restoreCourseFromRecycleBin(course3OfInstructor.getId());
        course3OfInstructor.resetDeletedAt();

        // Ensure the course and related entities still exist in datastore
        verifyPresentInDatastore(course3OfInstructor);
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse3"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse3"));

        // Ensure the course is restored from Recycle Bin
        assertFalse(course3OfInstructor.isCourseDeleted());

        // Move the course back to Recycle Bin for further testing
        coursesLogic.moveCourseToRecycleBin(course3OfInstructor.getId());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.restoreCourseFromRecycleBin(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testRestoreAllCoursesFromRecycleBin() throws InvalidParametersException, EntityDoesNotExistException {

        ______TS("typical case");

        InstructorAttributes instructor1OfCourse3 = dataBundle.instructors.get("instructor1OfCourse3");
        CourseAttributes course3OfInstructor = coursesLogic.getSoftDeletedCourseForInstructor(instructor1OfCourse3);

        List<InstructorAttributes> instructors = new ArrayList<>();
        instructors.add(instructor1OfCourse3);

        // Ensure there are entities in the datastore under this course
        verifyPresentInDatastore(course3OfInstructor);
        verifyPresentInDatastore(dataBundle.instructors.get("instructor1OfCourse3"));
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse3"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse3"));

        // Ensure the course is currently in Recycle Bin
        assertTrue(course3OfInstructor.isCourseDeleted());

        coursesLogic.restoreAllCoursesFromRecycleBin(instructors);
        course3OfInstructor.resetDeletedAt();

        // Ensure the course and related entities still exist in datastore
        verifyPresentInDatastore(course3OfInstructor);
        verifyPresentInDatastore(dataBundle.instructors.get("instructor1OfCourse3"));
        verifyPresentInDatastore(dataBundle.students.get("student1InCourse3"));
        verifyPresentInDatastore(dataBundle.feedbackSessions.get("session1InCourse3"));

        // Ensure the courses are restored from Recycle Bin
        assertFalse(course3OfInstructor.isCourseDeleted());

        // Move the course back to Recycle Bin for further testing
        coursesLogic.moveCourseToRecycleBin(course3OfInstructor.getId());

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> coursesLogic.restoreAllCoursesFromRecycleBin(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    @Test
    public void testDeleteCourseCascade() {

        ______TS("non-existent");

        coursesLogic.deleteCourseCascade("not_exist");

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
        verifyPresentInDatastore(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        FeedbackResponseAttributes typicalResponse = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        FeedbackQuestionAttributes typicalQuestion =
                FeedbackQuestionsLogic.inst()
                        .getFeedbackQuestion(typicalResponse.feedbackSessionName, typicalResponse.courseId,
                                Integer.parseInt(typicalResponse.feedbackQuestionId));
        typicalResponse = FeedbackResponsesLogic.inst()
                .getFeedbackResponse(typicalQuestion.getId(), typicalResponse.giver, typicalResponse.recipient);
        verifyPresentInDatastore(typicalResponse);
        FeedbackResponseCommentAttributes typicalComment =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        typicalComment = FeedbackResponseCommentsLogic.inst()
                .getFeedbackResponseComment(typicalResponse.getId(),
                        typicalComment.commentGiver, typicalComment.createdAt);
        verifyPresentInDatastore(typicalComment);

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
        verifyAbsentInDatastore(dataBundle.feedbackQuestions.get("qn1InSession1InCourse1"));
        verifyAbsentInDatastore(typicalQuestion);
        verifyAbsentInDatastore(typicalResponse);
        verifyAbsentInDatastore(typicalComment);

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> coursesLogic.deleteCourseCascade(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void testUpdateCourseCascade() throws Exception {
        CourseAttributes c = CourseAttributes
                .builder("Computing101-getthis")
                .withName("Basic Computing Getting")
                .withTimezone(ZoneId.of("UTC"))
                .build();
        coursesDb.createEntity(c);

        ______TS("Typical case");
        String newName = "New Course Name";
        String validTimeZone = "Asia/Singapore";
        CourseAttributes updateCourse = coursesLogic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(c.getId())
                        .withName(newName)
                        .withTimezone(ZoneId.of(validTimeZone))
                        .build()
        );
        c.setName(newName);
        c.setTimeZone(ZoneId.of(validTimeZone));
        verifyPresentInDatastore(c);
        assertEquals(newName, updateCourse.getName());
        assertEquals(validTimeZone, updateCourse.getTimeZone().getId());

        ______TS("Invalid name (empty course name)");

        String emptyName = "";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.updateCourseCascade(
                        CourseAttributes.updateOptionsBuilder(c.getId())
                                .withName(emptyName)
                                .build()
                ));
        String expectedErrorMessage =
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals(expectedErrorMessage, ipe.getMessage());
        verifyPresentInDatastore(c);
    }

}
