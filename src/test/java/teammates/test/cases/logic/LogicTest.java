package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.EVALUATION_NAME;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;
import static teammates.logic.core.TeamEvalResult.NA;
import static teammates.logic.core.TeamEvalResult.NSB;
import static teammates.logic.core.TeamEvalResult.NSU;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

import com.google.appengine.api.datastore.Text;

public class LogicTest extends BaseComponentTestCase {

    private static final Logic logic = new Logic();
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(Logic.class);
    }

    @BeforeMethod
    public void caseSetUp() throws ServletException {
        dataBundle = getTypicalDataBundle();
    }

    @SuppressWarnings("unused")
    private void ____USER_level_methods___________________________________() {
    }

    @Test
    public void testGetLoginUrl() {
        gaeSimulation.logoutUser();
        assertEquals("/_ah/login?continue=www.abc.com",
                Logic.getLoginUrl("www.abc.com"));
    }

    @Test
    public void testGetLogoutUrl() {
        gaeSimulation.loginUser("any.user");
        assertEquals("/_ah/logout?continue=www.def.com",
                Logic.getLogoutUrl("www.def.com"));
    }
    
    //TODO: test isUserLoggedIn method

    @Test
    public void testGetCurrentUser() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("admin+instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsAdmin(instructor.googleId);
        // also make this user a student
        StudentAttributes instructorAsStudent = new StudentAttributes(
                "Team 1", "Instructor As Student", "instructorasstudent@yahoo.com", "", "some-course");
        instructorAsStudent.googleId = instructor.googleId;
        logic.createStudent(instructorAsStudent);

        UserType user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(true, user.isStudent);

        ______TS("admin+instructor only");

        // this user is no longer a student
        logic.deleteStudent(instructorAsStudent.course, instructorAsStudent.email);

        user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("instructor only");
        
        // this user is no longer an admin
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        user = logic.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(true, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("unregistered");

        gaeSimulation.loginUser("unknown");

        user = logic.getCurrentUser();
        assertEquals("unknown", user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("student only");

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);

        user = logic.getCurrentUser();
        assertEquals(student.googleId, user.id);
        assertEquals(false, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(true, user.isStudent);

        ______TS("admin only");

        gaeSimulation.loginAsAdmin("any.user");

        user = logic.getCurrentUser();
        assertEquals("any.user", user.id);
        assertEquals(true, user.isAdmin);
        assertEquals(false, user.isInstructor);
        assertEquals(false, user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        gaeSimulation.logoutUser();
        assertEquals(null, logic.getCurrentUser());
    }

    @SuppressWarnings("unused")
    private void ____STUDENT_level_methods__________________________________() {
    }

    @Test
    public void testcreateStudentWithSubmissionAdjustment() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("typical case");

        // TODO: Move following to StudentsLogicTest (together with SUT -> StudentsLogic)
        
        
        
        restoreTypicalDataInDatastore();
        //reuse existing student to create a new student
        StudentAttributes newStudent = dataBundle.students.get("student1InCourse1");
        newStudent.email = "new@student.com";
        TestHelper.verifyAbsentInDatastore(newStudent);
        
        List<SubmissionAttributes> submissionsBeforeAdding = submissionsLogic.getSubmissionsForCourse(newStudent.course);
        
        logic.createStudent(newStudent);
        TestHelper.verifyPresentInDatastore(newStudent);
        
        List<SubmissionAttributes> submissionsAfterAdding = submissionsLogic.getSubmissionsForCourse(newStudent.course);
        
        //expected increase in submissions = 2*(1+4+4)
        //2 is the number of evaluations in the course
        //4 is the number of existing members in the team
        //1 is the self evaluation
        //We simply check the increase in submissions. A deeper check is 
        //  unnecessary because adjusting existing submissions should be 
        //  checked elsewhere.
        assertEquals(submissionsBeforeAdding.size()+18, submissionsAfterAdding.size());

        ______TS("duplicate student");

        // try to create the same student
        try {
            logic.createStudent(newStudent);
            Assert.fail();
        } catch (EntityAlreadyExistsException e) {
        }

        ______TS("invalid parameter");

        // Only checking that exception is thrown at logic level
        newStudent.email = "invalid email";
        
        try {
            logic.createStudent(newStudent);
            Assert.fail();
        } catch (InvalidParametersException e) {
            assertEquals(
                    String.format(EMAIL_ERROR_MESSAGE, "invalid email", REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        
        ______TS("null parameters");
        
        try {
            logic.createStudent(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }

        // other combination of invalid data should be tested against
        // StudentAttributes

    }

    @Test
    public void testGetStudentForEmail() throws Exception {
        // mostly tested in testcreateStudentWithSubmissionAdjustment

        restoreTypicalDataInDatastore();


        ______TS("null parameters");

        try {
            logic.getStudentForEmail(null, "valid@email.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }

    @Test
    public void testGetStudentsForGoogleId() throws Exception {
    
        ______TS("access control");
    
        restoreTypicalDataInDatastore();
    
        ______TS("student in one course");
    
        
    
        restoreTypicalDataInDatastore();
        StudentAttributes studentInOneCourse = dataBundle.students
                .get("student1InCourse1");
        assertEquals(1, logic.getStudentsForGoogleId(studentInOneCourse.googleId).size());
        assertEquals(studentInOneCourse.email,
                logic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).email);
        assertEquals(studentInOneCourse.name,
                logic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).name);
        assertEquals(studentInOneCourse.course,
                logic.getStudentsForGoogleId(studentInOneCourse.googleId).get(0).course);
    
        ______TS("student in two courses");
    
        // this student is in two courses, course1 and course 2.
    
        // get list using student data from course 1
        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");
        List<StudentAttributes> listReceivedUsingStudentInCourse1 = logic
                .getStudentsForGoogleId(studentInTwoCoursesInCourse1.googleId);
        assertEquals(2, listReceivedUsingStudentInCourse1.size());
    
        // get list using student data from course 2
        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        List<StudentAttributes> listReceivedUsingStudentInCourse2 = logic
                .getStudentsForGoogleId(studentInTwoCoursesInCourse2.googleId);
        assertEquals(2, listReceivedUsingStudentInCourse2.size());
    
        // check the content from first list (we assume the content of the
        // second list is similar.
    
        StudentAttributes firstStudentReceived = listReceivedUsingStudentInCourse1
                .get(0);
        // First student received turned out to be the one from course 2
        assertEquals(studentInTwoCoursesInCourse2.email,
                firstStudentReceived.email);
        assertEquals(studentInTwoCoursesInCourse2.name,
                firstStudentReceived.name);
        assertEquals(studentInTwoCoursesInCourse2.course,
                firstStudentReceived.course);
    
        // then the second student received must be from course 1
        StudentAttributes secondStudentReceived = listReceivedUsingStudentInCourse1
                .get(1);
        assertEquals(studentInTwoCoursesInCourse1.email,
                secondStudentReceived.email);
        assertEquals(studentInTwoCoursesInCourse1.name,
                secondStudentReceived.name);
        assertEquals(studentInTwoCoursesInCourse1.course,
                secondStudentReceived.course);
    
        ______TS("non existent student");
    
        assertEquals(0, logic.getStudentsForGoogleId("non-existent").size());
    
        ______TS("null parameters");
    
        try {
            logic.getStudentsForGoogleId(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }

    @Test
    public void testGetStudentForGoogleId() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("student in two courses");
    
        restoreTypicalDataInDatastore();
        
        StudentAttributes studentInTwoCoursesInCourse1 = dataBundle.students
                .get("student2InCourse1");
    
        String googleIdOfstudentInTwoCourses = studentInTwoCoursesInCourse1.googleId;
        assertEquals(studentInTwoCoursesInCourse1.email,
                logic.getStudentForGoogleId(
                        studentInTwoCoursesInCourse1.course,
                        googleIdOfstudentInTwoCourses).email);
    
        StudentAttributes studentInTwoCoursesInCourse2 = dataBundle.students
                .get("student2InCourse2");
        assertEquals(studentInTwoCoursesInCourse2.email,
                logic.getStudentForGoogleId(
                        studentInTwoCoursesInCourse2.course,
                        googleIdOfstudentInTwoCourses).email);
    
        ______TS("student in zero courses");
    
        assertEquals(null, logic.getStudentForGoogleId("non-existent",
                "random-google-id"));
    
        ______TS("null parameters");
    
        try {
            logic.getStudentForGoogleId("valid.course", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }

    @Test
    public void testGetStudentsForCourse() throws Exception {
    
        restoreTypicalDataInDatastore();
        
        ______TS("course with multiple students");
    
        restoreTypicalDataInDatastore();
    
        
    
        CourseAttributes course1OfInstructor1 = dataBundle.courses.get("typicalCourse1");
        List<StudentAttributes> studentList = logic
                .getStudentsForCourse(course1OfInstructor1.id);
        assertEquals(5, studentList.size());
        for (StudentAttributes s : studentList) {
            assertEquals(course1OfInstructor1.id, s.course);
        }
    
        ______TS("course with 0 students");
    
        CourseAttributes course2OfInstructor1 = dataBundle.courses.get("courseNoEvals");
        studentList = logic.getStudentsForCourse(course2OfInstructor1.id);
        assertEquals(0, studentList.size());
    
        ______TS("null parameter");
    
        try {
            logic.getStudentsForCourse(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent course");
    
        studentList = logic.getStudentsForCourse("non-existent");
        assertEquals(0, studentList.size());
        
    }

    @Test
    public void testGetTeamsForCourse() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        String methodName = "getTeamsForCourse";
        Class<?>[] paramTypes = new Class<?>[] { String.class };
    
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();
    
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        logic.createStudent(new StudentAttributes("t1", "s1", "s1@e", "", course.id));
        List<TeamDetailsBundle> courseAsTeams = logic.getTeamsForCourse(course.id);
        assertEquals(3, courseAsTeams.size());
    
        String team1Id = "Team 1.1";
        assertEquals(team1Id, courseAsTeams.get(0).name);
        assertEquals(4, courseAsTeams.get(0).students.size());
        assertEquals(team1Id, courseAsTeams.get(0).students.get(0).team);
        assertEquals(team1Id, courseAsTeams.get(0).students.get(1).team);
    
        String team2Id = "Team 1.2";
        assertEquals(team2Id, courseAsTeams.get(1).name);
        assertEquals(1, courseAsTeams.get(1).students.size());
        assertEquals(team2Id, courseAsTeams.get(1).students.get(0).team);
    
        ______TS("null parameters");
    
        try {
            logic.getTeamsForCourse(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("course without teams");
    
        logic.deleteCourse("course1");
        logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor("instructor1", "course1", "Course 1");
        assertEquals(0, logic.getTeamsForCourse("course1").size());

        ______TS("non-existent course");
        
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes,
                new Object[] { "non-existent" });
    }

    @Test
    public void testGetKeyForStudent() throws Exception {
        // mostly tested in testJoinCourse()
    
        restoreTypicalDataInDatastore();
    
        ______TS("null parameters");
    
        try {
            logic.getKeyForStudent("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent student");
        
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        assertEquals(null,
                logic.getKeyForStudent(student.course, "non@existent"));
    }

    @Test
    public void testupdateStudent() throws Exception {

        restoreTypicalDataInDatastore();

        

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String originalEmail = student1InCourse1.email;
                 
        ______TS("typical success case");
        student1InCourse1.name = student1InCourse1.name + "x";
        student1InCourse1.googleId = student1InCourse1.googleId + "x";
        student1InCourse1.comments = student1InCourse1.comments + "x";
        student1InCourse1.email = student1InCourse1.email + "x";
        student1InCourse1.team = "Team 1.2";
        logic.updateStudent(originalEmail, student1InCourse1);        
        TestHelper.verifyPresentInDatastore(student1InCourse1);
        
        // check for cascade
        List<SubmissionAttributes> submissionsAfterEdit = submissionsLogic.getSubmissionsForCourse(student1InCourse1.course);        
        TestHelper.verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(submissionsAfterEdit,
                student1InCourse1.course);
        
        ______TS("null parameters");

        try {
            logic.updateStudent(null, student1InCourse1);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }        
        try {
            logic.updateStudent("test@email.com", null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
    }

    @Test
    public void testJoinCourseForStudent() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        // make a student 'unregistered'
        
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        String googleId = "student1InCourse1";
        String key = logic.getKeyForStudent(student.course, student.email);
        student.googleId = "";
        logic.updateStudent(student.email, student);
        assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
    
    
        ______TS("register an unregistered student");
    
        restoreTypicalDataInDatastore();
    
        
    
        // make a student 'unregistered'
        student = dataBundle.students.get("student1InCourse1");
        googleId = "student1InCourse1";
        key = logic.getKeyForStudent(student.course, student.email);
        student.googleId = "";
        logic.updateStudent(student.email, student);
        assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
    
        // TODO: remove encrpytion - should fail test
        //Test if unencrypted key used
        logic.joinCourseForStudent(key, googleId);
        assertEquals(googleId,
                logic.getStudentForEmail(student.course, student.email).googleId);
        
        
        
        // make a student 'unregistered'
        student = dataBundle.students.get("student1InCourse1");
        googleId = "student1InCourse1";
        key = logic.getKeyForStudent(student.course, student.email);
        student.googleId = "";
        logic.updateStudent(student.email, student);
        assertEquals("", logic.getStudentForEmail(student.course, student.email).googleId);
        logic.deleteAccount(googleId);    // for testing account creation
        AccountAttributes studentAccount = logic.getAccount(googleId); // this is because student accounts are not in typical data bundle
        assertNull(studentAccount);
    
        //Test for encrypted key used
        key = StringHelper.encrypt(key);
        logic.joinCourseForStudent(key, googleId);
        assertEquals(googleId,
                logic.getStudentForEmail(student.course, student.email).googleId);
        
        // Check that an account with the student's google ID was created
        studentAccount = logic.getAccount(googleId);
        TestHelper.verifyPresentInDatastore(studentAccount); 
        AccountAttributes accountOfInstructorOfCourse = dataBundle.accounts.get("instructor1OfCourse1");
        assertEquals(accountOfInstructorOfCourse.institute, studentAccount.institute);// Test that student account was appended with the correct Institute
                
        ______TS("null parameters");
    
        try {
            logic.joinCourseForStudent(null, "valid.user");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
        try {
            logic.joinCourseForStudent(key, null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }

    @Test
    public void testEnrollStudents() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("all valid students, but contains blank lines");
    
        restoreTypicalDataInDatastore();
    
        String instructorId = "instructorForEnrollTesting";
        String courseId = "courseForEnrollTest";
        
        logic.createAccount("instructorForEnrollTesting", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor(instructorId, courseId, "Course for Enroll Testing");
        String EOL = Const.EOL;
    
        String line0 = "t1|n1|e1@g|c1";
        String line1 = " t2|  n2|  e2@g|  c2";
        String line2 = "t3|n3|e3@g|c3  ";
        String line3 = "t4|n4|  e4@g|c4";
        String line4 = "t5|n5|e5@g  |c5";
        String lines = line0 + EOL + line1 + EOL + line2 + EOL
                + "  \t \t \t \t           " + EOL + line3 + EOL + EOL + line4
                + EOL + "    " + EOL + EOL;
        List<StudentAttributes> enrollResults = logic.enrollStudents(lines, courseId);
    
        StudentAttributesFactory saf = new StudentAttributesFactory();
        assertEquals(5, enrollResults.size());
        assertEquals(5, logic.getStudentsForCourse(courseId).size());
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseId),
                enrollResults.get(0), StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseId),
                enrollResults.get(1), StudentAttributes.UpdateStatus.NEW);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line4, courseId),
                enrollResults.get(4), StudentAttributes.UpdateStatus.NEW);
        
        CourseDetailsBundle cd = logic.getCourseDetails(courseId);
        assertEquals(5, cd.stats.unregisteredTotal);
    
        ______TS("includes a mix of unmodified, modified, and new");
    
        String line0_1 = "t3|modified name|e3@g|c3";
        String line5 = "t6|n6|e6@g|c6";
        lines = line0 + EOL + line0_1 + EOL + line1 + EOL + line5;
        enrollResults = logic.enrollStudents(lines, courseId);
        assertEquals(6, enrollResults.size());
        assertEquals(6, logic.getStudentsForCourse(courseId).size());
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0, courseId),
                enrollResults.get(0), StudentAttributes.UpdateStatus.UNMODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line0_1, courseId),
                enrollResults.get(1), StudentAttributes.UpdateStatus.MODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line1, courseId),
                enrollResults.get(2), StudentAttributes.UpdateStatus.UNMODIFIED);
        TestHelper.verifyEnrollmentResultForStudent(saf.makeStudent(line5, courseId),
                enrollResults.get(3), StudentAttributes.UpdateStatus.NEW);
        assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
                enrollResults.get(4).updateStatus);
        assertEquals(StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST,
                enrollResults.get(5).updateStatus);
    
        ______TS("includes an incorrect line");
    
        // no changes should be done to the database
        String incorrectLine = "incorrectly formatted line";
        lines = "t7|n7|e7@g|c7" + EOL + incorrectLine + EOL + line2 + EOL
                + line3;
        try {
            enrollResults = logic.enrollStudents(lines, courseId);
            Assert.fail("Did not throw exception for incorrectly formatted line");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(incorrectLine));
        }
        assertEquals(6, logic.getStudentsForCourse(courseId).size());
    
        ______TS("null parameters");
    
        try {
            logic.enrollStudents("a|b|c|d", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("same student added, modified and unmodified");
    
        logic.createAccount("tes.instructor", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor("tes.instructor", "tes.course", "TES Course");
        
        String line = "t8|n8|e8@g|c1" ;
        enrollResults = logic.enrollStudents(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.NEW,
                enrollResults.get(0).updateStatus);
        
        line = "t8|n8a|e8@g|c1";
        enrollResults = logic.enrollStudents(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.MODIFIED,
                enrollResults.get(0).updateStatus);
        
        line = "t8|n8a|e8@g|c1";
        enrollResults = logic.enrollStudents(line, "tes.course");
        assertEquals(1, enrollResults.size());
        assertEquals(StudentAttributes.UpdateStatus.UNMODIFIED,
                enrollResults.get(0).updateStatus);

        ______TS("duplicated emails");
        
        String line_t9 = "t9|n9|e9@g|c9";
        String line_t10 = "t10|n10|e9@g|c10";
        try {
            logic.enrollStudents(line_t9 + EOL + line_t10, "tes.course");
        } catch (EnrollException e) {
            assertTrue(e.getMessage().contains(line_t10));
            assertTrue(e.getMessage().contains("Same email address as the student in line \""+line_t9+"\""));    
        }
    }

    @Test
    public void testSendRegistrationInviteForCourse() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("all students already registered");
    
        
    
        restoreTypicalDataInDatastore();
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
    
        // send registration key to a class in which all are registered
        List<MimeMessage> emailsSent = logic
                .sendRegistrationInviteForCourse(course1.id);
        assertEquals(0, emailsSent.size());
    
        ______TS("some students not registered");
    
        // modify two students to make them 'unregistered' and send again
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        student1InCourse1.googleId = "";
        logic.updateStudent(student1InCourse1.email, student1InCourse1);
        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        student2InCourse1.googleId = "";
        logic.updateStudent(student2InCourse1.email, student2InCourse1);
        emailsSent = logic.sendRegistrationInviteForCourse(course1.id);
        assertEquals(2, emailsSent.size());
        TestHelper.verifyJoinInviteToStudent(student2InCourse1, emailsSent.get(0));
        TestHelper.verifyJoinInviteToStudent(student1InCourse1, emailsSent.get(1));
    
        ______TS("null parameters");
    
        try {
            logic.sendRegistrationInviteForCourse(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }

    @Test
    public void testSendRegistrationInviteToStudent() throws Exception {
    
    
        ______TS("send to existing student");
    
        
    
        restoreTypicalDataInDatastore();
    
        StudentAttributes student1 = dataBundle.students.get("student1InCourse1");
    
        MimeMessage email = logic.sendRegistrationInviteToStudent(
                student1.course, student1.email);
    
        TestHelper.verifyJoinInviteToStudent(student1, email);
    
        ______TS("send to non-existing student");
    
        restoreTypicalDataInDatastore();
    
        String methodName = "sendRegistrationInviteToStudent";
        Class<?>[] paramTypes = new Class<?>[] { String.class, String.class };
    
    
        TestHelper.verifyEntityDoesNotExistException(methodName, paramTypes, new Object[] {
                student1.course, "non@existent" });
    
        ______TS("null parameters");
    
        try {
            logic.sendRegistrationInviteToStudent(null, "valid@email.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }

    @Test
    public void testSendReminderForEvaluation() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("empty class");
    
        restoreTypicalDataInDatastore();
    
        AccountsLogic.inst().deleteAccountCascade("instructor1");
        CoursesLogic.inst().deleteCourseCascade("course1");
        logic.createAccount("instructor1", "Instructor 1", true, "instructor@email.com", "National University Of Singapore");
        logic.createCourseAndInstructor("instructor1", "course1", "course 1");
        EvaluationAttributes newEval = new EvaluationAttributes();
        newEval.courseId = "course1";
        newEval.name = "new eval";
        newEval.instructions = new Text("instructions");
        newEval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        newEval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        logic.createEvaluation(newEval);
    
        List<MimeMessage> emailsSent = EvaluationsLogic.inst().sendReminderForEvaluation(
                "course1", "new eval");
        
        int numOfInstructor = logic.getInstructorsForCourse(newEval.courseId).size();
        assertEquals(0+numOfInstructor, emailsSent.size());
    
        ______TS("1 person submitted fully, 4 others have not");
    
        EvaluationAttributes eval = dataBundle.evaluations
                .get("evaluation1InCourse1");
        emailsSent = EvaluationsLogic.inst().sendReminderForEvaluation(eval.courseId, eval.name);
        
        numOfInstructor = logic.getInstructorsForCourse(eval.courseId).size();
        assertEquals(4+numOfInstructor, emailsSent.size());
        List<StudentAttributes> studentList = logic
                .getStudentsForCourse(eval.courseId);
    
        //student 1 would not recieve email 
        for (StudentAttributes s : studentList) {
            if(!s.name.equals("student1 In Course1")){
                String errorMessage = "No email sent to " + s.email;
                assertTrue(errorMessage, TestHelper.getEmailToStudent(s, emailsSent) != null);
            }
        }
    
        ______TS("some have submitted fully");
        // This student is the only member in Team 1.2. If he submits his
        // self-evaluation, he sill be considered 'fully submitted'. Only
        // student in Team 1.1 should receive emails.
        StudentAttributes singleStudnetInTeam1_2 = dataBundle.students
                .get("student5InCourse1");
        SubmissionAttributes sub = new SubmissionAttributes();
        sub.course = singleStudnetInTeam1_2.course;
        sub.evaluation = eval.name;
        sub.team = singleStudnetInTeam1_2.team;
        sub.reviewer = singleStudnetInTeam1_2.email;
        sub.reviewee = singleStudnetInTeam1_2.email;
        sub.points = 100;
        sub.justification = new Text("j");
        sub.p2pFeedback = new Text("y");
        ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
        submissions.add(sub);
        logic.updateSubmissions(submissions);
        emailsSent = EvaluationsLogic.inst().sendReminderForEvaluation(eval.courseId, eval.name);
    
        numOfInstructor = logic.getInstructorsForCourse(eval.courseId).size();
        assertEquals(3+numOfInstructor, emailsSent.size());
    
        studentList = logic.getStudentsForCourse(eval.courseId);
    
        // verify 3 students in Team 1.1 received emails.
        for (StudentAttributes s : studentList) {
            if (s.team.equals("Team 1.1") && !s.name.equals("student1 In Course1")) {
                String errorMessage = "No email sent to " + s.email;
                assertTrue(errorMessage,
                        TestHelper.getEmailToStudent(s, emailsSent) != null);
            }
        }
    
        ______TS("non-existent course/evaluation");
        try {
            EvaluationsLogic.inst().sendReminderForEvaluation("non-existent-course", "non-existent-eval");
        } catch (Exception e) {
            assertEquals("Trying to edit non-existent evaluation non-existent-course/non-existent-eval", 
                    e.getMessage());
        }
    
        ______TS("null parameter");
    
        try {
            EvaluationsLogic.inst().sendReminderForEvaluation("valid.course.id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals("Supplied parameter was null\n", a.getMessage());
        }
    }

    @Test
    public void testDeleteStudent() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("typical delete");

        restoreTypicalDataInDatastore();

        

        // this is the student to be deleted
        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        TestHelper.verifyPresentInDatastore(student2InCourse1);

        // ensure student-to-be-deleted has some submissions
        SubmissionAttributes submissionFromS1C1ToS2C1 = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");
        TestHelper.verifyPresentInDatastore(submissionFromS1C1ToS2C1);

        SubmissionAttributes submissionFromS2C1ToS1C1 = dataBundle.submissions
                .get("submissionFromS2C1ToS1C1");
        TestHelper.verifyPresentInDatastore(submissionFromS2C1ToS1C1);

        SubmissionAttributes submissionFromS1C1ToS1C1 = dataBundle.submissions
                .get("submissionFromS1C1ToS1C1");
        TestHelper.verifyPresentInDatastore(submissionFromS1C1ToS1C1);

        logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);
        TestHelper.verifyAbsentInDatastore(student2InCourse1);

        // verify that other students in the course are intact
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        TestHelper.verifyPresentInDatastore(student1InCourse1);

        // verify that submissions are deleted
        TestHelper.verifyAbsentInDatastore(submissionFromS1C1ToS2C1);
        TestHelper.verifyAbsentInDatastore(submissionFromS2C1ToS1C1);

        // verify other student's submissions are intact
        TestHelper.verifyPresentInDatastore(submissionFromS1C1ToS1C1);

        ______TS("delete non-existent student");

        // should fail silently.
        logic.deleteStudent(student2InCourse1.course, student2InCourse1.email);

        ______TS("null parameters");

        try {
            logic.deleteStudent(null, "valid@email.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }



    @SuppressWarnings("unused")
    private void ____EVALUATION_level_methods_______________________________() {

    }

    
    @Test
    public void testGetCourseStudentListAsCsv() throws Exception {
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();    
        
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        String instructorGoogleId = dataBundle.instructors.get("instructor1OfCourse1").googleId;
        
        String export = logic.getCourseStudentListAsCsv(course.id, instructorGoogleId);
        
        // This is what export should look like:
        // ==================================
        //Course ID,"idOfTypicalCourse1"
        //Course Name,"Typical Course 1 with 2 Evals"
        //
        //
        //Team,Student Name,Status,Email
        //"Team 1.1","student1 In Course1","Joined","student1InCourse1@gmail.com"
        //"Team 1.1","student2 In Course1","Joined","student2InCourse1@gmail.com"
        //"Team 1.1","student3 In Course1","Joined","student3InCourse1@gmail.com"
        //"Team 1.1","student4 In Course1","Joined","student4InCourse1@gmail.com"
        //"Team 1.2","student5 In Course1","Joined","student5InCourse1@gmail.com"
        
        String[] exportLines = export.split(Const.EOL);
        assertEquals("Course ID," + "\"" + course.id + "\"", exportLines[0]);
        assertEquals("Course Name," + "\"" + course.name + "\"", exportLines[1]);
        assertEquals("", exportLines[2]);
        assertEquals("", exportLines[3]);
        assertEquals("Team,Student Name,Status,Email", exportLines[4]);
        assertEquals("\"Team 1.1\",\"student1 In Course1\",\"Joined\",\"student1InCourse1@gmail.com\"", exportLines[5]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Joined\",\"student2InCourse1@gmail.com\"", exportLines[6]);
        assertEquals("\"Team 1.1\",\"student3 In Course1\",\"Joined\",\"student3InCourse1@gmail.com\"", exportLines[7]);
        assertEquals("\"Team 1.1\",\"student4 In Course1\",\"Joined\",\"student4InCourse1@gmail.com\"", exportLines[8]);
        assertEquals("\"Team 1.2\",\"student5 In Course1\",\"Joined\",\"student5InCourse1@gmail.com\"", exportLines[9]);

        ______TS("Null parameters");
        
        try {
            logic.getCourseStudentListAsCsv(null, instructorGoogleId);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
        }
        
        try {
            logic.getCourseStudentListAsCsv(course.id, null);
            Assert.fail();
        } catch (AssertionError ae) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, ae.getMessage());
        }
    }
    
    @SuppressWarnings("unused")
    private void ____SUBMISSION_level_methods_______________________________() {
    }

    @Test
    public void testCreateSubmission() {
        // method not implemented
    }


    @Test
    public void testGetSubmissionsForEvaluationFromStudent() throws Exception {
    
        restoreTypicalDataInDatastore();
    
        ______TS("typical case");
    
        restoreTypicalDataInDatastore();
    
        
    
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        // reuse this evaluation data to create a new one
        evaluation.name = "new evaluation";
        logic.createEvaluationWithoutSubmissionQueue(evaluation);
        // this is the student we are going to check
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
    
        List<SubmissionAttributes> submissions = logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, student.email);
        // there should be 4 submissions as this student is in a 4-person team
        assertEquals(4, submissions.size());
        // verify they all belong to this student
        for (SubmissionAttributes s : submissions) {
            assertEquals(evaluation.courseId, s.course);
            assertEquals(evaluation.name, s.evaluation);
            assertEquals(student.email, s.reviewer);
            assertEquals(student.name, s.details.reviewerName);
            assertEquals(logic.getStudentForEmail(evaluation.courseId, s.reviewee).name,
                    s.details.revieweeName);
        }
    
        ______TS("orphan submissions");
    
        //Move student to a new team
        student.team = "Team 1.3";
        logic.updateStudent(student.email, student);
        
        submissions = logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, student.email);
        //There should be 1 submission as he is now in a 1-person team.
        //   Orphaned submissions from previous team should not be returned.
                assertEquals(1, submissions.size());
                
        // Move the student out and move in again
        student.team = "Team 1.4";
        logic.updateStudent(student.email, student);
        student.team = "Team 1.3";
        logic.updateStudent(student.email, student);
        submissions = logic.getSubmissionsForEvaluationFromStudent(evaluation.courseId,
                evaluation.name, student.email);
        assertEquals(1, submissions.size());
    
        ______TS("null parameters");
        
        try {
            logic.getSubmissionsForEvaluationFromStudent("valid.course.id", "valid evaluation name", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("course/evaluation/student does not exist");
    
        
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                "non-existent", evaluation.name, student.email ).size());
    
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, "non-existent", student.email ).size());
    
        assertEquals(0, logic.getSubmissionsForEvaluationFromStudent(
                evaluation.courseId, evaluation.name, "non-existent" ).size());
    }

    @Test
    public void testHasStudentSubmittedEvaluation() throws Exception {
    
        EvaluationAttributes evaluation = dataBundle.evaluations
                .get("evaluation1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
    
        restoreTypicalDataInDatastore();
    
        ______TS("student has submitted");
    
        
    
        assertEquals(true, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, student.email));
    
        ______TS("student has not submitted");
    
        // create a new evaluation reusing data from previous one
        evaluation.name = "New evaluation";
        logic.createEvaluation(evaluation);
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, student.email));
    
        ______TS("null parameters");
    
        try {
            logic.hasStudentSubmittedEvaluation("valid.course.id", "valid evaluation name", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    
        ______TS("non-existent course/evaluation/student");
    
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                "non-existent-course", evaluation.name, student.email));
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, "non-existent-eval", student.email));
        assertEquals(false, logic.hasStudentSubmittedEvaluation(
                evaluation.courseId, evaluation.name, "non-existent@student"));
    
    }

    @Test
    public void testUpdateSubmissions() throws Exception {

        ______TS("typical cases");

        restoreTypicalDataInDatastore();
        

        ArrayList<SubmissionAttributes> submissionContainer = new ArrayList<SubmissionAttributes>();

        // try without empty list. Nothing should happen
        logic.updateSubmissions(submissionContainer);

        SubmissionAttributes sub1 = dataBundle.submissions
                .get("submissionFromS1C1ToS2C1");

        SubmissionAttributes sub2 = dataBundle.submissions
                .get("submissionFromS2C1ToS1C1");

        // checking editing of one of the submissions
        TestHelper.alterSubmission(sub1);

        submissionContainer.add(sub1);
        logic.updateSubmissions(submissionContainer);

        TestHelper.verifyPresentInDatastore(sub1);
        TestHelper.verifyPresentInDatastore(sub2);

        // check editing both submissions
        TestHelper.alterSubmission(sub1);
        TestHelper.alterSubmission(sub2);

        submissionContainer = new ArrayList<SubmissionAttributes>();
        submissionContainer.add(sub1);
        submissionContainer.add(sub2);
        logic.updateSubmissions(submissionContainer);

        TestHelper.verifyPresentInDatastore(sub1);
        TestHelper.verifyPresentInDatastore(sub2);

        ______TS("non-existent evaluation");

        // already tested under testUpdateSubmission()

        ______TS("null parameter");

        try {
            logic.updateSubmissions(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }



    @Test
    public void testDeleteSubmission() {
        // method not implemented
    }
    
    /* TODO: implement tests for the following :
     * 1. getFeedbackSessionDetails()
     * 2. getFeedbackSessionsListForInstructor()
     */
    
    @SuppressWarnings("unused")
    private void ____COMMENT_level_methods_____________________________() {
        //The tests here are only for null params check,
        //the rest are done in CommentsLogicTest
    }
    
    @Test
    public void testCreateComment() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.createComment(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
    }
    
    @Test
    public void testUpdateComment() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.updateComment(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testDeleteComment() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.deleteComment(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testGetCommentsForGiver() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.getCommentsForGiver(null, "giver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForGiver("course-id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testGetCommentsForReceiver() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.getCommentsForReceiver(null, "receiver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForReceiver("course-id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testGetCommentsForGiverAndReceiver() throws Exception{
        ______TS("null parameters");
        
        try {
            logic.getCommentsForGiverAndReceiver(null, "giver@mail.com", "receiver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForGiverAndReceiver("course-id", null, "receiver@mail.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        try {
            logic.getCommentsForGiverAndReceiver("course-id", "giver@mail.com", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        turnLoggingDown(Logic.class);
    }

}
