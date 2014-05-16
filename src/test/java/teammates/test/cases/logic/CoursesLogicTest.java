package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class CoursesLogicTest extends BaseComponentTestCase {
 
    private CoursesLogic coursesLogic = new CoursesLogic();
    private CoursesDb coursesDb = new CoursesDb();
    private AccountsDb accountsDb = new AccountsDb();
    private InstructorsDb instructorsDb = new InstructorsDb();
    
    private static DataBundle dataBundle;

    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(CoursesLogic.class);
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }

    @Test
    public void testCreateCourse() throws Exception {
        
        /*Explanation:
         * The SUT (i.e. CoursesLogic::createCourse) has only 1 path. Therefore, we
         * should typically have 1 test cases here.
         */
        ______TS("typical case");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "Computing101-fresh";
        c.name = "Basic Computing";
        coursesLogic.createCourse(c.id, c.name);
        TestHelper.verifyPresentInDatastore(c);
        
    }
    
    @Test
    public void testCreateCourseAndInstructor() throws Exception {
        
        /* Explanation: SUT has 5 paths. They are,
         * path 1 - exit because the account doesn't' exist.
         * path 2 - exit because the account exists but doesn't have instructor privileges.
         * path 3 - exit because course creation failed.
         * path 4 - exit because instructor creation failed.
         * path 5 - success.
         * Accordingly, we have 5 test cases.
         */
        
        ______TS("fails: account doesn't exist");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "fresh-course-tccai";
        c.name = "Fresh course for tccai";
        
        InstructorAttributes i = new InstructorAttributes();
        i.googleId = "instructor-for-tccai";
        i.courseId = c.id;
        i.name = "Instructor for tccai";
        i.email = "ins.for.iccai@gmail.com";
        
        
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("for a non-existent instructor", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
        TestHelper.verifyAbsentInDatastore(i);
        
        
        ______TS("fails: account doesn't have instructor privileges");
        
        AccountAttributes a = new AccountAttributes();
        a.googleId = i.googleId;
        a.name = i.name;
        a.email = i.email;
        a.institute = "NUS";
        a.isInstructor = false;
        accountsDb.createAccount(a);
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("doesn't have instructor privileges", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
        TestHelper.verifyAbsentInDatastore(i);
        
        ______TS("fails: error during course creation");
        
        a.isInstructor = true;
        accountsDb.updateAccount(a);
        
        c.id = "invalid id";
        
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("not acceptable to TEAMMATES as a Course ID", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
        TestHelper.verifyAbsentInDatastore(i);
        
        ______TS("fails: error during instructor creation due to duplicate instructor");
        
        c.id = "fresh-course-tccai";
        instructorsDb.createEntity(i); //create a duplicate instructor
        
        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);

        ______TS("fails: error during instructor creation due to invalid parameters");

        i.email = "ins.for.iccai.gmail.com";

        try {
            coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course", e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(c);
       
        ______TS("success: typical case");

         i.email = "ins.for.iccai@gmail.com";

        //remove the duplicate instructor object from the datastore.
        instructorsDb.deleteInstructor(i.courseId, i.email);
        
        coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
        TestHelper.verifyPresentInDatastore(c);
        TestHelper.verifyPresentInDatastore(i);
        
    }
    
    @Test
    public void testGetCourse() throws Exception {

        ______TS("failure: course doesn't exist");

        assertNull(coursesLogic.getCourse("nonexistant-course"));

        ______TS("success: typical case");

        CourseAttributes c = new CourseAttributes();
        c.id = "Computing101-getthis";
        c.name = "Basic Computing Getting";
        coursesDb.createEntity(c);

        assertEquals(c.id, coursesLogic.getCourse(c.id).id);
        assertEquals(c.name, coursesLogic.getCourse(c.id).name);
    }
    
    @Test
    public void testGetArchivedCoursesForInstructor() throws Exception {
        
        ______TS("success: instructor with archive course");
        String instructorId = getTypicalDataBundle().instructors.get("instructorOfArchivedCourse").googleId;
        
        List<CourseAttributes> archivedCourses = coursesLogic.getArchivedCoursesForInstructor(instructorId);
        
        assertEquals(1, archivedCourses.size());
        assertEquals(true, archivedCourses.get(0).isArchived);
    
        ______TS("boundary: instructor without archive courses");
        instructorId = getTypicalDataBundle().instructors.get("instructor1OfCourse1").googleId;
        
        archivedCourses = coursesLogic.getArchivedCoursesForInstructor(instructorId);
        
        assertEquals(0, archivedCourses.size());
    }
    
    @Test
    public void testGetCoursesForInstructor() throws Exception {

        ______TS("success: instructor with present courses");
        String instructorId = getTypicalDataBundle().accounts.get("instructor3").googleId;

        List<CourseAttributes> courses = coursesLogic.getCoursesForInstructor(instructorId);

        assertEquals(2, courses.size());

        ______TS("boundary: instructor without any courses");
        instructorId = getTypicalDataBundle().accounts.get("instructorWithoutCourses").googleId;

        courses = coursesLogic.getCoursesForInstructor(instructorId);

        assertEquals(0, courses.size());
    }

    @Test
    public void testIsSampleCourse() {
        
        ______TS("typical case: not a sample course");
        CourseAttributes c = new CourseAttributes();
        c.id = "course.id";
        
        assertEquals(false, coursesLogic.isSampleCourse(c.id));
        
        ______TS("typical case: is a sample course");
        c.id = c.id.concat("-demo3");
        assertEquals(true, coursesLogic.isSampleCourse(c.id));
        
        ______TS("typical case: is a sample course with '-demo' in the middle of its id");
        c.id = c.id.concat("-demo33");
        assertEquals(true, coursesLogic.isSampleCourse(c.id));
        
    }

    @Test
    public void testIsCoursePresent() {

        ______TS("typical case: not an existent course");
        CourseAttributes c = new CourseAttributes();
        c.id = "non-existent-course";

        assertEquals(false, coursesLogic.isCoursePresent(c.id));

        ______TS("typical case: an existent course");
        c.id = "idOfTypicalCourse1";

        assertEquals(true, coursesLogic.isCoursePresent(c.id));
    }

    @Test
    public void testVerifyCourseIsPresent() {

        ______TS("typical case: verify an inexistent course");
        CourseAttributes c = new CourseAttributes();
        c.id = "non-existent-course";

        try{
            coursesLogic.verifyCourseIsPresent(c.id);
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Course does not exist: ", e.getMessage());
        }

        ______TS("typical case: verify an existent course");
        c.id = "idOfTypicalCourse1";

        try {
            coursesLogic.verifyCourseIsPresent(c.id);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("This is not expected");
        }
    }
    
    @Test
    public void testSetArchiveStatusOfCourse() throws Exception {
        
        CourseAttributes course = new CourseAttributes("CLogicT.new-course", "New course");
        coursesDb.createEntity(course);
        
        ______TS("success: archive a course");
        coursesLogic.setArchiveStatusOfCourse(course.id, true);
        
        CourseAttributes courseRetrieved = coursesLogic.getCourse(course.id);
        assertEquals(true, courseRetrieved.isArchived);
        
        ______TS("success: unarchive a course");
        coursesLogic.setArchiveStatusOfCourse(course.id, false);
        
        courseRetrieved = coursesLogic.getCourse(course.id);
        assertEquals(false, courseRetrieved.isArchived);
        
        ______TS("fail: course doesn't exist");
        coursesDb.deleteCourse(course.id);
        
        try {
            coursesLogic.setArchiveStatusOfCourse(course.id, true);
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("Course does not exist: CLogicT.new-course", e.getMessage());
        }
    }

     @Test
    public void testGetCourseSummary() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseDetailsBundle courseSummary = coursesLogic.getCourseSummary(course.id);
        assertEquals(course.id, courseSummary.course.id);
        assertEquals(course.name, courseSummary.course.name);
        assertEquals(false, courseSummary.course.isArchived);

        assertEquals(2, courseSummary.stats.teamsTotal);
        assertEquals(6, courseSummary.stats.studentsTotal);
        assertEquals(1, courseSummary.stats.unregisteredTotal);
        
        assertEquals(0, courseSummary.evaluations.size());

        assertEquals(2, courseSummary.teams.size()); 
        assertEquals("Team 1.1", courseSummary.teams.get(0).name);
        assertEquals("Team 1.2", courseSummary.teams.get(1).name);

        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        courseSummary = coursesLogic.getCourseSummary("course1");
        assertEquals("course1", courseSummary.course.id);
        assertEquals("course 1", courseSummary.course.name);
        
        assertEquals(0, courseSummary.stats.teamsTotal);
        assertEquals(0, courseSummary.stats.studentsTotal);
        assertEquals(0, courseSummary.stats.unregisteredTotal);
        
        assertEquals(0, courseSummary.evaluations.size());
        assertEquals(0, courseSummary.teams.size());
        
        coursesLogic.deleteCourseCascade("course1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummary("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }
        

        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummary(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test
    public void testGetCourseSummaryWithoutStats() throws Exception {


        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseSummaryBundle courseSummary = coursesLogic.getCourseSummaryWithoutStats(course.id);
        assertEquals(course.id, courseSummary.course.id);
        assertEquals(course.name, courseSummary.course.name);
        assertEquals(false, courseSummary.course.isArchived);

        assertEquals(0, courseSummary.evaluations.size());
        assertEquals(0, courseSummary.teams.size()); 
       
        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        courseSummary = coursesLogic.getCourseSummaryWithoutStats("course1");
        assertEquals("course1", courseSummary.course.id);
        assertEquals("course 1", courseSummary.course.name);
         
        assertEquals(0, courseSummary.evaluations.size());
        assertEquals(0, courseSummary.teams.size());
        
        coursesLogic.deleteCourseCascade("course1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseSummaryWithoutStats("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }
        

        ______TS("null parameter");

        try {
            coursesLogic.getCourseSummaryWithoutStats(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test
    public void testGetCourseDetails() throws Exception {

        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        CourseDetailsBundle courseDetails = coursesLogic.getCourseDetails(course.id);
        assertEquals(course.id, courseDetails.course.id);
        assertEquals(course.name, courseDetails.course.name);
        assertEquals(false, courseDetails.course.isArchived);

        assertEquals(2, courseDetails.stats.teamsTotal);
        assertEquals(6, courseDetails.stats.studentsTotal);
        assertEquals(1, courseDetails.stats.unregisteredTotal);
        
        assertEquals(2, courseDetails.evaluations.size());
        assertEquals("evaluation2 In Course1", courseDetails.evaluations.get(0).evaluation.name);
        assertEquals("evaluation1 In Course1", courseDetails.evaluations.get(1).evaluation.name);

        assertEquals(2, courseDetails.teams.size()); 
        assertEquals("Team 1.1", courseDetails.teams.get(0).name);
        assertEquals("Team 1.2", courseDetails.teams.get(1).name);

        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        courseDetails = coursesLogic.getCourseDetails("course1");
        assertEquals("course1", courseDetails.course.id);
        assertEquals("course 1", courseDetails.course.name);
        
        assertEquals(0, courseDetails.stats.teamsTotal);
        assertEquals(0, courseDetails.stats.studentsTotal);
        assertEquals(0, courseDetails.stats.unregisteredTotal);
        
        assertEquals(0, courseDetails.evaluations.size());
        assertEquals(0, courseDetails.teams.size());
        
        coursesLogic.deleteCourseCascade("course1");

        ______TS("non-existent");

        try {
            coursesLogic.getCourseDetails("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("The course does not exist:", e.getMessage());
        }
        

        ______TS("null parameter");

        try {
            coursesLogic.getCourseDetails(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test
    public void testGetTeamsForCourse() throws Exception {
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        List<TeamDetailsBundle> teams = coursesLogic.getTeamsForCourse(course.id);
        
        assertEquals(2, teams.size()); 
        assertEquals("Team 1.1", teams.get(0).name);
        assertEquals("Team 1.2", teams.get(1).name);


        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        teams = coursesLogic.getTeamsForCourse("course1");

        assertEquals(0, teams.size());
        
        coursesLogic.deleteCourseCascade("course1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getTeamsForCourse("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        

        ______TS("null parameter");

        try {
            coursesLogic.getTeamsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test 
    public void testGetNumberOfTeams() throws Exception {
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        int teamNum = coursesLogic.getNumberOfTeams(course.id);
        
        assertEquals(2, teamNum); 

        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        teamNum = coursesLogic.getNumberOfTeams("course1");

        assertEquals(0, teamNum);
        
        coursesLogic.deleteCourseCascade("course1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getNumberOfTeams("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        

        ______TS("null parameter");

        try {
            coursesLogic.getNumberOfTeams(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test
    public void testGetTotalEnrolledInCourse() throws Exception {
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        int enrolledNum = coursesLogic.getTotalEnrolledInCourse(course.id);
        
        assertEquals(6, enrolledNum); 

        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        enrolledNum = coursesLogic.getTotalEnrolledInCourse("course1");

        assertEquals(0, enrolledNum);
        
        coursesLogic.deleteCourseCascade("course1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getTotalEnrolledInCourse("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        

        ______TS("null parameter");

        try {
            coursesLogic.getTotalEnrolledInCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test
    public void testGetTotalUnregisteredInCourse() throws Exception {
        ______TS("typical case");

        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        int unregisteredNum = coursesLogic.getTotalUnregisteredInCourse(course.id);
        
        assertEquals(1, unregisteredNum); 

        ______TS("course without students");

        AccountsLogic.inst().createAccount(new AccountAttributes("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore"));
        coursesLogic.createCourseAndInstructor("instructor1", "course1", "course 1");
        unregisteredNum = coursesLogic.getTotalUnregisteredInCourse("course1");

        assertEquals(0, unregisteredNum);
        
        coursesLogic.deleteCourseCascade("course1");
        
        ______TS("non-existent");

        try {
            coursesLogic.getTotalUnregisteredInCourse("non-existent-course");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist", e.getMessage());
        }
        
        ______TS("null parameter");

        try {
            coursesLogic.getTotalUnregisteredInCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

    @Test
    public void testGetCoursesForStudentAccount() throws Exception {

        ______TS("student having two courses");

        StudentAttributes studentInTwoCourses = dataBundle.students
                .get("student2InCourse1");
        List<CourseAttributes> courseList = coursesLogic
                .getCoursesForStudentAccount(studentInTwoCourses.googleId);
        assertEquals(2, courseList.size());
        // For some reason, index 0 is Course2 and index 1 is Course1
        // Anyway in DataStore which follows a HashMap structure,
        // there is no guarantee on the order of Entities' storage
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse2");
        assertEquals(course1.id, courseList.get(0).id);
        assertEquals(course1.name, courseList.get(0).name);
    
        CourseAttributes course2 = dataBundle.courses.get("typicalCourse1");
        assertEquals(course2.id, courseList.get(1).id);
        assertEquals(course2.name, courseList.get(1).name);
    
        ______TS("student having one course");
    
        StudentAttributes studentInOneCourse = dataBundle.students
                .get("student1InCourse1");
        courseList = coursesLogic.getCoursesForStudentAccount(studentInOneCourse.googleId);
        assertEquals(1, courseList.size());
        course1 = dataBundle.courses.get("typicalCourse1");
        assertEquals(course1.id, courseList.get(0).id);
        assertEquals(course1.name, courseList.get(0).name);
    
        // Student having zero courses is not applicable
    
        ______TS("non-existent student");
    
        try {
            coursesLogic.getCoursesForStudentAccount("non-existent-student");
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains("does not exist",
                                         e.getMessage());
        }
    
        ______TS("null parameter");
    
        try {
            coursesLogic.getCoursesForStudentAccount(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals("Supplied parameter was null\n", e.getMessage());
        }
    }

     @Test
    public void testGetCourseDetailsListForStudent() throws Exception {

    }

    @Test
    public void testGetCourseSummariesForInstructor() throws Exception {

    }

    @Test
    public void testGetCourseDetailsForInstructor() throws Exception {

    }

    @Test
    public void testGetCoursesSummaryWithoutStatsForInstructor() throws Exception {

    }

    @Test
    public void testGetCourseStudentListAsCsv() throws Exception {

    }
}
